package com.amsavarthan.dude.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.HashMap;

public class ContactsDatabase extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "contacts.db";
    public static final String CONTACTS_TABLE_NAME = "contacts";
    public static final String CONTACTS_COLUMN_ID = "id";
    public static final String CONTACTS_COLUMN_NAME = "name";
    public static final String CONTACTS_COLUMN_PHONE = "phone";

    private HashMap hp;

    public ContactsDatabase(Context context) {
        super(context, DATABASE_NAME , null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(
                "create table contacts " + "(id integer primary key,name text,phone text)"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS contacts");
        onCreate(db);
    }

    public void insertContact(String name,String phone) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("name", name);
        contentValues.put("phone", phone);
        db.insert(CONTACTS_TABLE_NAME, null, contentValues);
    }

    public Cursor getData(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery( "select * from "+CONTACTS_TABLE_NAME+" where id="+id+"", null );
    }

    public boolean updateContact(Integer id,String name,String phone) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("name", name);
        contentValues.put("phone", phone);
        db.update(CONTACTS_TABLE_NAME, contentValues, "id = ? ", new String[] { Integer.toString(id) } );
        return true;
    }

    public void updateContactName(Integer id, String name) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("name", name);
        db.update(CONTACTS_TABLE_NAME, contentValues, "id = ? ", new String[] { Integer.toString(id) } );
    }


    public void updateContactPhone(Integer id, String phone) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("phone", phone);
        db.update(CONTACTS_TABLE_NAME, contentValues, "id = ? ", new String[] { Integer.toString(id) } );
    }

    public Cursor getinformation(SQLiteDatabase db){
        Cursor cursor;
        String[] projections={CONTACTS_COLUMN_NAME,CONTACTS_COLUMN_PHONE};
        cursor= db.query(CONTACTS_TABLE_NAME, projections, null, null, null, null, null);
        return cursor;
    }


    public Integer deleteContact (Integer id) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(CONTACTS_TABLE_NAME,
                "id = ? ",
                new String[] { Integer.toString(id) });
    }

    public void deleteAll() {

        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(CONTACTS_TABLE_NAME,null,null);
        db.execSQL("delete from "+ CONTACTS_TABLE_NAME);
        db.close();

    }
}