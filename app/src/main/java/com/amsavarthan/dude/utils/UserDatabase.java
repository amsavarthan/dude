package com.amsavarthan.dude.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.HashMap;

public class UserDatabase extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "users.db";
    public static final String CONTACTS_TABLE_NAME = "user";
    public static final String CONTACTS_COLUMN_ID = "id";
    public static final String CONTACTS_COLUMN_NAME = "name";
    public static final String CONTACTS_COLUMN_PHONE = "phone";
    public static final String CONTACTS_COLUMN_CITY = "city";
    public static final String CONTACTS_COLUMN_STATE = "state";
    public static final String CONTACTS_COLUMN_AGE = "age";
    public static final String CONTACTS_COLUMN_LID = "location_id";
    public static final String CONTACTS_COLUMN_BGROUP = "blood_group";
    public static final String CONTACTS_COLUMN_ALLERGY = "allergy";
    public static final String CONTACTS_COLUMN_DONOR = "donor";

    private HashMap hp;

    public UserDatabase(Context context) {
        super(context, DATABASE_NAME , null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(
                "create table user " + "(id integer primary key,name text,phone text,city text,state text,age text,blood_group text,allergy text,donor text,location_id text)"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS user");
        onCreate(db);
    }

    public void insertContact(String name,String phone,String city,String state,String age,String location_id,String blood_group,String allergy,String donor) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("name", name);
        contentValues.put("phone", phone);
        contentValues.put("city", city);
        contentValues.put("state", state);
        contentValues.put("age", age);
        contentValues.put("location_id", location_id);
        contentValues.put("blood_group",blood_group);
        contentValues.put("allergy",allergy);
        contentValues.put("donor",donor);
        db.insert("user", null, contentValues);
    }

    public Cursor getData(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery( "select * from user where id="+id+"", null );
    }

    public boolean updateContact(Integer id,String name,String phone,String city,String state,String age,String location_id,String blood_group,String allergy,String donor) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("name", name);
        contentValues.put("phone", phone);
        contentValues.put("city", city);
        contentValues.put("state", state);
        contentValues.put("age", age);
        contentValues.put("location_id", location_id);
        contentValues.put("blood_group",blood_group);
        contentValues.put("allergy",allergy);
        contentValues.put("donor",donor);
        db.update("user", contentValues, "id = ? ", new String[] { Integer.toString(id) } );
        return true;
    }


    public void updateContactName(Integer id, String name) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("name", name);
        db.update("user", contentValues, "id = ? ", new String[] { Integer.toString(id) } );
    }


    public void updateContactPhone(Integer id, String phone) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("phone", phone);
        db.update("user", contentValues, "id = ? ", new String[] { Integer.toString(id) } );
    }

    public void updateContactAllergy(Integer id, String allergy) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("allergy",allergy);
        db.update("user", contentValues, "id = ? ", new String[] { Integer.toString(id) } );
    }

    public void updateContactDonor(Integer id, String donor) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("donor",donor);
        db.update("user", contentValues, "id = ? ", new String[] { Integer.toString(id) } );
    }

    public void updateContactBGroup(Integer id, String blood_group) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("blood_group",blood_group);
        db.update("user", contentValues, "id = ? ", new String[] { Integer.toString(id) } );
    }

    public void updateContactCity(Integer id, String city) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("city", city);
        db.update("user", contentValues, "id = ? ", new String[] { Integer.toString(id) } );
    }

    public void updateContactState(Integer id, String state) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("state", state);
        db.update("user", contentValues, "id = ? ", new String[] { Integer.toString(id) } );
    }

    public void updateContactAge(Integer id, String age) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("age", age);
        db.update("user", contentValues, "id = ? ", new String[] { Integer.toString(id) } );
    }

    public Integer deleteContact (Integer id) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete("user",
                "id = ? ",
                new String[] { Integer.toString(id) });
    }

}