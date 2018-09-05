package de.danielb.abschluss.rezeptebuch.controller;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.ListAdapter;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.RecursiveAction;

import de.danielb.abschluss.rezeptebuch.model.Recipe;

/**
 * Created by Daniel B. on 04.09.2018.
 */

public class RecipeSqliteHelper extends SQLiteOpenHelper {
    public static final int ACTIVITY_EDIT_RECIPE = 0x4711;

    private String sqlTableName;
    private String sqlTableDesc;
    private String sqlTableFieldString;
    private String[] sqlTableFields;
    private String sqlQueryTable;

    private List<Recipe> recipeList;

    public RecipeSqliteHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
        generateSqlStrings();
    }


    //generates table description based on the class fields using reflections
    private void generateSqlStrings() {
        sqlTableName = "Recipes";
        sqlTableDesc = "_id INTEGER PRIMARY KEY AUTOINCREMENT,"
                + " title VARCHAR,"
                + " category VARCHAR,"
                + " duration VARCHAR,"
                + " ingredients VARCHAR,"
                + " instructions VARCHAR,"
                + " pathToImage VARCHAR";
        sqlTableFieldString = "_id, title, category, duration, ingredients, instructions, pathToImage";

        sqlTableFields = sqlTableFieldString.split(",");

        Log.d(this.getClass().getSimpleName(), "Table: " + sqlTableName);
        Log.d(this.getClass().getSimpleName(), "TableDesc: " + sqlTableDesc);
        Log.d(this.getClass().getSimpleName(), "TableFieldString: " + sqlTableFieldString);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        Log.d(this.getClass().getSimpleName(), "OnCreate executed");
        if (!sqlTableName.isEmpty()) {
            sqLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS " + sqlTableName + " (" + sqlTableDesc + ")");
        } else {
            Log.d(this.getClass().getSimpleName(), "No table name.");
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    public Recipe addNewRecipe(Recipe recipe) {
        //prepared return field for success
        boolean success = false;
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        if(recipe != null && recipe.isValid()) {
            ContentValues contentValues = new ContentValues();
            contentValues.putNull(sqlTableFields[0].trim());
            contentValues.put(sqlTableFields[1].trim(), recipe.getTitle());
            contentValues.put(sqlTableFields[2].trim(), recipe.getCategory());
            contentValues.put(sqlTableFields[3].trim(), recipe.getDuration());
            contentValues.put(sqlTableFields[4].trim(), recipe.getIngredients());
            contentValues.put(sqlTableFields[5].trim(), recipe.getInstructions());
            contentValues.put(sqlTableFields[6].trim(), recipe.getPathToImage());

            long rowid = sqLiteDatabase.insert(sqlTableName, null, contentValues);
            if(rowid >= 0) {
                recipe = getRecipe(rowid);
            }
        } else {
            recipe = null;
        }
        return recipe;
    }

    public void editRecipe(Recipe recipe) {

    }

    public void removeRecipe(Recipe recipe) {

    }


    public Recipe getRecipe(long id) {
        Recipe recipe = null;
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();

        //query for index
        Cursor cursor = sqLiteDatabase.query(sqlTableName, null, sqlTableFields[0] + "=" + id, null, null, null, null);

        //there should be only one result
        if (cursor.getCount() == 1 && cursor.moveToFirst()) {
            recipe = new Recipe(cursor.getInt(cursor.getColumnIndexOrThrow(sqlTableFields[0].trim())),
                    cursor.getString(cursor.getColumnIndexOrThrow(sqlTableFields[1].trim())), //Title
                    cursor.getString(cursor.getColumnIndexOrThrow(sqlTableFields[2].trim())), //Category
                    cursor.getString(cursor.getColumnIndexOrThrow(sqlTableFields[3].trim())), //Duration
                    cursor.getString(cursor.getColumnIndexOrThrow(sqlTableFields[4].trim())), //Ingredients
                    cursor.getString(cursor.getColumnIndexOrThrow(sqlTableFields[5].trim())), //Instructions
                    cursor.getString(cursor.getColumnIndexOrThrow(sqlTableFields[6].trim()))); //PathToImage
        } else {
            recipe = null;
        }

        return recipe;
    }

    public List<Recipe> getRecipeList() {
        if (this.recipeList == null) {
            refreshRecipeList();
        }
        return this.recipeList;
    }

    public List<Recipe> refreshRecipeList() {
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();

        this.recipeList = null;
        Recipe currentRecipe = null;

        Cursor cursor = sqLiteDatabase.query(sqlTableName, null, null, null, null, null, null);

        while (cursor.moveToNext()) {
            // create List once the query contains data
            if (this.recipeList == null) {
                this.recipeList = new ArrayList<Recipe>();
            }

            //create recipe from Tableentry
            currentRecipe = new Recipe(cursor.getInt(cursor.getColumnIndexOrThrow(sqlTableFields[0].trim())),
                    cursor.getString(cursor.getColumnIndexOrThrow(sqlTableFields[1].trim())), //Title
                    cursor.getString(cursor.getColumnIndexOrThrow(sqlTableFields[2].trim())), //Category
                    cursor.getString(cursor.getColumnIndexOrThrow(sqlTableFields[3].trim())), //Duration
                    cursor.getString(cursor.getColumnIndexOrThrow(sqlTableFields[4].trim())), //Ingredients
                    cursor.getString(cursor.getColumnIndexOrThrow(sqlTableFields[5].trim())), //Instructions
                    cursor.getString(cursor.getColumnIndexOrThrow(sqlTableFields[6].trim()))); //PathToImage

            //add recipe to list
            //this.recipeList.add(cursor.getInt(cursor.getColumnIndexOrThrow(sqlTableFields[0].trim())), currentRecipe);
            this.recipeList.add(currentRecipe);
        }
        return this.recipeList;
    }

    public void printPath() {
        Log.d(this.getClass().getSimpleName(), this.getReadableDatabase().getPath().toString());
    }
}
