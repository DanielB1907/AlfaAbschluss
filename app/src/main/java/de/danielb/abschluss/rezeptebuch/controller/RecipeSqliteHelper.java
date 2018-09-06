package de.danielb.abschluss.rezeptebuch.controller;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.ListAdapter;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.concurrent.RecursiveAction;

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
public class RecipeSqliteHelper implements List<Recipe> {
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

        getRecipeList();
    }

    //region ========List Interface
    @Override
    public int size() {
        return recipeList.size();
    }

    @Override
    public boolean isEmpty() {
        return recipeList.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        return recipeList.contains(o);
    }

    @NonNull
    @Override
    public Iterator<Recipe> iterator() {
        return recipeList.iterator();
    }

    @NonNull
    @Override
    public Object[] toArray() {
        return recipeList.toArray();
    }

    @NonNull
    @Override
    public <T> T[] toArray(@NonNull T[] a) {
        return recipeList.toArray(a);
    }

    @Override
    public boolean add(Recipe recipe) {
        recipe = insertRecipe(recipe);
        if(recipe != null) {
            return recipeList.add(recipe);
        } else {
            return false;
        }
    }

    @Override
    public boolean remove(Object o) {
        if(deleteRecipeByRowid(((Recipe) o).get_id())) {
            return recipeList.remove(o);
        } else {
            return false;
        }
    }

    @Override
    public boolean containsAll(@NonNull Collection<?> c) {
        return recipeList.containsAll(c);
    }

    @Override
    public boolean addAll(@NonNull Collection<? extends Recipe> c) {
        return recipeList.addAll(c);
    }

    @Override
    public boolean addAll(int index, @NonNull Collection<? extends Recipe> c) {
        return recipeList.addAll(index, c);
    }

    @Override
    public boolean removeAll(@NonNull Collection<?> c) {
        return recipeList.removeAll(c);
    }

    @Override
    public boolean retainAll(@NonNull Collection<?> c) {
        return recipeList.retainAll(c);
    }

    @Override
    public void clear() {
        recipeList.clear();
    }

    @Override
    public Recipe get(int index) {
        return recipeList.get(index);
    }

    @Override
    public Recipe set(int index, Recipe element) {
        return recipeList.set(index, element);
    }

    @Override
    public void add(int index, Recipe element) {
        //recipeSqliteHelper.add(index, element);
    }

    @Override
    public Recipe remove(int index) {
        return recipeList.remove(index);
    }

    @Override
    public int indexOf(Object o) {
        return recipeList.indexOf(o);
    }

    @Override
    public int lastIndexOf(Object o) {
        return recipeList.lastIndexOf(o);
    }

    @NonNull
    @Override
    public ListIterator<Recipe> listIterator() {
        return recipeList.listIterator();
    }

    @NonNull
    @Override
    public ListIterator<Recipe> listIterator(int index) {
        return recipeList.listIterator(index);
    }

    @NonNull
    @Override
    public List<Recipe> subList(int fromIndex, int toIndex) {
        return recipeList.subList(fromIndex, toIndex);
    }
    //endregion

    //region =========Sqlite Commands
    //generates table description based on the class fields  TODO: using reflections in future
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
    private Recipe insertRecipe(@NonNull Recipe recipe) {
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
        //returns current recipe with new rowid or null
        return recipe;
    }

    public boolean modifyRecipe(Recipe recipe) {
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

    private boolean deleteRecipeByRowid(long rowid) {
        //prepare write access
        SQLiteDatabase sqLiteDatabase = this.sqLiteOpenHelper.getWritableDatabase();

        //execute delete command
        int result = sqLiteDatabase.delete(sqlTableName, sqlTableFields[0] + "=" + rowid, null);

        //if one item was successfully deleted, true
        return result >= 1;
    }

    private Recipe getRecipeByRowid(long rowid) {
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

    private List<Recipe> getRecipeList() {
        if (this.recipeList == null) {
            refreshRecipeList();
        }
        return this.recipeList;
    }

    /**
     * refreshRecipeList is destructive!
     * locally cached list will be deleted, if existing.
     *
     * @return
     */
    private List<Recipe> refreshRecipeList() {
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
    //endregion

    public void printPath() {
        Log.d(this.getClass().getSimpleName(), this.sqLiteOpenHelper.getReadableDatabase().getPath().toString());
    }
}
