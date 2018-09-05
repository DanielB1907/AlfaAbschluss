package de.danielb.abschluss.rezeptebuch.controller;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Daniel B. on 04.09.2018.
 */

public class GenericSqliteHelper<TABLECLASS> extends SQLiteOpenHelper{
    private Class tableClass;
    private Field[] tableFields;
    private String sqlTableName;
    private String sqlTableDesc;
    private String[] sqlTableFields;
    private String sqlTableFieldString;
    private String sqlQueryTable;

    public GenericSqliteHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version, Class<TABLECLASS> tableClass) {
        super(context, name, factory, version);
        this.tableClass = tableClass;
        generateSqlStrings();
    }


    //generates table description based on the class fields using reflections
    private void generateSqlStrings() {
        sqlTableDesc = "";
        sqlTableFieldString = "";
        sqlTableName = tableClass.getSimpleName();

        tableFields = tableClass.getDeclaredFields();

        for (Field field : this.tableFields) {
            Log.d(this.getClass().getSimpleName(), field.getName() + ":" + field.getType().getName());

            // _id of integer indicates primary key with autoincrement
            if(field.getName().equals("_id") && field.getType().getName().equals("int")) {
                sqlTableDesc += field.getName();
                sqlTableDesc += " INTEGER";
                sqlTableDesc += " PRIMARY KEY AUTOINCREMENT,";

                sqlTableFieldString += field.getName();
                sqlTableFieldString += ", ";
            } else if(field.getType().getName().equals("int")) {
                sqlTableDesc += field.getName();
                sqlTableDesc += " INTEGER,";

                sqlTableFieldString += field.getName();
                sqlTableFieldString += ", ";
            }else if(field.getType().getName().equals("java.lang.String")) {
                sqlTableDesc += field.getName();
                sqlTableDesc += " VARCHAR,";

                sqlTableFieldString += field.getName();
                sqlTableFieldString += ", ";
            }
        }
        sqlTableDesc = sqlTableDesc.subSequence(0, sqlTableDesc.length() - 1).toString();
        sqlTableFieldString = sqlTableFieldString.subSequence(0, sqlTableFieldString.length() - 1).toString();
        sqlTableFields = sqlTableFieldString.split(",");

        Log.d(this.getClass().getSimpleName(), sqlTableName);
        Log.d(this.getClass().getSimpleName(), sqlTableDesc);
        Log.d(this.getClass().getSimpleName(), sqlTableFieldString);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        Log.d(this.getClass().getSimpleName(), "OnCreate executed");
        if(!sqlTableName.isEmpty()) {
            sqLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS " + sqlTableName + " (" + sqlTableDesc + ")");
        } else {
            Log.d(this.getClass().getSimpleName(), "No table name.");
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    public void addItem(TABLECLASS item){

    }

    public void editItem(TABLECLASS item) {

    }

    public void removeItem(TABLECLASS item) {

    }

    public void getItem(TABLECLASS item) {

    }

    public List<TABLECLASS> getItemList() {
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        List<TABLECLASS> itemList = new ArrayList<TABLECLASS>();

        Cursor cursor = sqLiteDatabase.query(sqlTableName, null,null,null,null,null,null);
        cursor.moveToFirst();
        int colums = cursor.getColumnCount();
        int currentColumn;

        do{
            for (int i = 0; i < colums; i++) {
                currentColumn = cursor.getColumnIndex(tableFields[i].getName());
                if(cursor.getType(currentColumn) == Cursor.FIELD_TYPE_INTEGER &&
                        tableFields[i].getType().toString().equals("int")){

                }
            }
        } while(cursor.moveToNext());

        return null;
    }

    public List<TABLECLASS> getItemList(String whereClause) {
        return null;
    }

    public void printPath() {
        Log.d(this.getClass().getSimpleName(), this.getReadableDatabase().getPath().toString());
    }
}
