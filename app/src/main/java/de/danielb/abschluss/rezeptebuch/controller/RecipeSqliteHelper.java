package de.danielb.abschluss.rezeptebuch.controller;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.NonNull;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import de.danielb.abschluss.rezeptebuch.model.Recipe;

/**
 * Created by Daniel B. on 04.09.2018.
 */

/**
 * RecipeSqliteHelper is a combination of SqliteDatabaseHelper and List<Recipe>
 * the idea is, to make an sql request by using usual List-Methods.
 * Only if SQL-Query was successfull, the current working object will be added to
 * local cached list.
 * What is the main advantage?
 * - only one pull query is needed at the beginning,
 * - any ListAdapter will be able to work with local list
 */
public class RecipeSqliteHelper {
    public static final int ACTIVITY_EDIT_RECIPE = 0x4711;

    private SQLiteOpenHelper sqLiteOpenHelper;

    private String sqlTableName;
    private String sqlTableDesc;
    private String sqlTableFieldString;
    private String[] sqlTableFields;
    private String sqlQueryTable;

    private List<Recipe> recipeList;

    //========Constructor
    public RecipeSqliteHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        generateSqlStrings();

        sqLiteOpenHelper = new SQLiteOpenHelper(context, name, factory, version) {
            //when database file is about to be created
            @Override
            public void onCreate(SQLiteDatabase sqLiteDatabase) {
                Log.d(this.getClass().getSimpleName(), "OnCreate executed");
                if (!sqlTableName.isEmpty()) {
                    sqLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS " + sqlTableName + " (" + sqlTableDesc + ")");
                } else {
                    Log.d(this.getClass().getSimpleName(), "No table name.");
                }
            }

            //for future use ... maybe
            @Override
            public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

            }
        };
    }

    //region =========Sqlite Commands
    //generates table description based on the class fields
    private void generateSqlStrings() {
        sqlTableName = "Recipes";
        sqlTableDesc = "_id INTEGER PRIMARY KEY AUTOINCREMENT,"
                + " title VARCHAR,"
                + " category VARCHAR,"
                + " duration VARCHAR,"
                + " ingredients VARCHAR,"
                + " instructions VARCHAR,"
                + " pathToImage VARCHAR";
        //string representation
        sqlTableFieldString = "_id, title, category, duration, ingredients, instructions, pathToImage";

        //array representation
        sqlTableFields = sqlTableFieldString.split(",");

        //region debug messages
        Log.d(this.getClass().getSimpleName(), "Table: " + sqlTableName);
        Log.d(this.getClass().getSimpleName(), "TableDesc: " + sqlTableDesc);
        Log.d(this.getClass().getSimpleName(), "TableFieldString: " + sqlTableFieldString);
        //endregion
    }

    /**
     * insertRecipe
     * returns null, if insert was failed
     * @param recipe
     * @return
     */
    public Recipe insertRecipe(@NonNull Recipe recipe) {
        //get write access
        SQLiteDatabase sqLiteDatabase = this.sqLiteOpenHelper.getWritableDatabase();

        //only nonnull recipe can be added
        if (recipe != null) {
            ContentValues contentValues = new ContentValues();
            contentValues.putNull(sqlTableFields[0].trim());
            contentValues.put(sqlTableFields[1].trim(), recipe.getTitle());
            contentValues.put(sqlTableFields[2].trim(), recipe.getCategory());
            contentValues.put(sqlTableFields[3].trim(), recipe.getDuration());
            contentValues.put(sqlTableFields[4].trim(), recipe.getIngredients());
            contentValues.put(sqlTableFields[5].trim(), recipe.getInstructions());
            contentValues.put(sqlTableFields[6].trim(), recipe.getPathToImage());

            //insert to table
            long rowid = sqLiteDatabase.insert(sqlTableName, null, contentValues);

            if (rowid >= 0) {
                recipe.setId(rowid);
            } else {
                recipe = null;
            }
        }

        if(recipe != null && recipeList != null) {
            refreshNew(recipe.get_id());
        }

        //returns current recipe with new rowid or null
        return recipe;
    }

    public boolean modifyRecipe(@NonNull Recipe recipe) {
        //prepare write access
        SQLiteDatabase sqLiteDatabase = this.sqLiteOpenHelper.getWritableDatabase();
        int result = -1;

        if (recipe != null) {
            ContentValues contentValues = new ContentValues();
            contentValues.put(sqlTableFields[0].trim(), recipe.get_id());
            contentValues.put(sqlTableFields[1].trim(), recipe.getTitle());
            contentValues.put(sqlTableFields[2].trim(), recipe.getCategory());
            contentValues.put(sqlTableFields[3].trim(), recipe.getDuration());
            contentValues.put(sqlTableFields[4].trim(), recipe.getIngredients());
            contentValues.put(sqlTableFields[5].trim(), recipe.getInstructions());
            contentValues.put(sqlTableFields[6].trim(), recipe.getPathToImage());

            //insert to table
            result = sqLiteDatabase.update(sqlTableName, contentValues, sqlTableFields[0] + "=" + recipe.get_id(), null);
        }
        return result == 1;
    }

    public boolean deleteRecipeByRowid(long rowid) {
        //prepare write access
        SQLiteDatabase sqLiteDatabase = this.sqLiteOpenHelper.getWritableDatabase();

        //execute delete command
        int result = sqLiteDatabase.delete(sqlTableName, sqlTableFields[0] + "=" + rowid, null);

        //if one item was successfully deleted, true
        if(result >= 1 && recipeList != null) {
            return refreshDelete(rowid);
        } else {
            return false;
        }
    }

    public Recipe queryRecipeByRowid(long rowid) {
        //prepare readonly access
        SQLiteDatabase sqLiteDatabase = this.sqLiteOpenHelper.getReadableDatabase();

        //return field
        Recipe recipe = null;

        //query for index
        Cursor cursor = sqLiteDatabase.query(sqlTableName, null, sqlTableFields[0] + "=" + rowid, null, null, null, null);

        //there should be only one result, because id is unique
        if (cursor.getCount() == 1 && cursor.moveToFirst()) {
            recipe = new Recipe(cursor.getInt(cursor.getColumnIndexOrThrow(sqlTableFields[0].trim())),
                    cursor.getString(cursor.getColumnIndexOrThrow(sqlTableFields[1].trim())), //Title
                    cursor.getString(cursor.getColumnIndexOrThrow(sqlTableFields[2].trim())), //Category
                    cursor.getString(cursor.getColumnIndexOrThrow(sqlTableFields[3].trim())), //Duration
                    cursor.getString(cursor.getColumnIndexOrThrow(sqlTableFields[4].trim())), //Ingredients
                    cursor.getString(cursor.getColumnIndexOrThrow(sqlTableFields[5].trim())), //Instructions
                    cursor.getString(cursor.getColumnIndexOrThrow(sqlTableFields[6].trim()))); //PathToImage
        }
        //returns recipe or null
        return recipe;
    }

    public List<Recipe> getRecipeList() {
        if (this.recipeList == null) {
            queryRecipeList();
        }
        return this.recipeList;
    }

    /**
     * queryRecipeList is destructive!
     * locally cached list will be deleted, if existing.
     *
     * @return
     */
    private List<Recipe> queryRecipeList() {
        SQLiteDatabase sqLiteDatabase = sqLiteOpenHelper.getReadableDatabase();

        //prepare empty List
        this.recipeList = new ArrayList<Recipe>();

        Recipe currentRecipe = null;

        Cursor cursor = sqLiteDatabase.query(sqlTableName, null, null, null, null, null, null);

        //while table contains data rows...
        while (cursor.moveToNext()) {
            //create recipe from Tableentry
            currentRecipe = new Recipe(cursor.getInt(cursor.getColumnIndexOrThrow(sqlTableFields[0].trim())),
                    cursor.getString(cursor.getColumnIndexOrThrow(sqlTableFields[1].trim())), //Title
                    cursor.getString(cursor.getColumnIndexOrThrow(sqlTableFields[2].trim())), //Category
                    cursor.getString(cursor.getColumnIndexOrThrow(sqlTableFields[3].trim())), //Duration
                    cursor.getString(cursor.getColumnIndexOrThrow(sqlTableFields[4].trim())), //Ingredients
                    cursor.getString(cursor.getColumnIndexOrThrow(sqlTableFields[5].trim())), //Instructions
                    cursor.getString(cursor.getColumnIndexOrThrow(sqlTableFields[6].trim()))); //PathToImage

            //add recipe to list
            this.recipeList.add(currentRecipe);
        }
        return this.recipeList;
    }

    //======= Handle on local list only
    public void refreshNew(long rowid) {
        Recipe recipe = queryRecipeByRowid(rowid);

        if(recipe != null && recipeList != null) {
            if(findRecipeByRowid(recipe.get_id()) == null) {
                recipeList.add(recipe);
            }
        }
    }

    public void refreshOld(long rowid) {
        Recipe recipe = queryRecipeByRowid(rowid);

        if(recipe != null  && recipeList != null) {
            Recipe oldRecipe = findRecipeByRowid(rowid);
            if(oldRecipe != null) {
                if(oldRecipe.get_id() == recipe.get_id()) {
                    oldRecipe.setTitle(recipe.getTitle());
                    oldRecipe.setCategory(recipe.getCategory());
                    oldRecipe.setDuration(recipe.getDuration());
                    oldRecipe.setIngredients(recipe.getIngredients());
                    oldRecipe.setInstructions(recipe.getInstructions());
                    oldRecipe.setPathToImage(recipe.getPathToImage());
                }
            }
        }
    }

    public boolean refreshDelete(long rowid) {
        boolean success = false;
        Recipe recipe = queryRecipeByRowid(rowid);

        //if deletion is valid in database, result should be null
        if(recipe == null && recipeList != null) {
            //find recipe in local list and validate deletion
            Recipe oldRecipe = findRecipeByRowid(rowid);
            if (oldRecipe != null) {
                success = recipeList.remove(oldRecipe);
            }
        }
        return success;
    }

    public Recipe findRecipeByRowid(long rowid) {
        Recipe recipe = null;

        for (Recipe r : recipeList) {
            if(r.get_id() == rowid) {
                recipe = r;
                break;
            }
        }
        return recipe;
    }
    //endregion

    public void printPath() {
        Log.d(this.getClass().getSimpleName(), this.sqLiteOpenHelper.getReadableDatabase().getPath().toString());
    }
}
