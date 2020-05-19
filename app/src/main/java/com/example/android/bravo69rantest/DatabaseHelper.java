package com.example.android.bravo69rantest;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class DatabaseHelper extends SQLiteOpenHelper
{
    public static String DATABASE_NAME = "Student.db";
    public static String TABLE_CODING_DATA = "codingdata";
    public static String COL_1 = "ID";
    public static String COL_2 = "OBJECTID";
    public static String COL_3 = "OBJECTTYPE";
    public static String COL_4 = "CODINGMASTERID";

    public String[][] arCodingDataDef = {
            {"Id", "primary key"},
            {"ObjectId", "Integer"},
            {"ObjectType", ""},
            {"CodingMasterId", "Integer"},
            {"DataType", ""},
            {"Value", ""},
            {"Timestamp", ""}
    };


    String[][][] arTableDefs = {arCodingDataDef};

    public static int VERSION = 1;
//    public SQLiteDatabase myDb;

    public DatabaseHelper(@Nullable Context context)
    {
        super(context, DATABASE_NAME, null, VERSION   );
//        myDb = this.getWritableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase db)
    {
        String strThis = "[" + this.getClass() + ".onCreate] ";
//        myDb = db;
        int iVersion = db.getVersion();

        System.out.println(strThis + "iVersion=" + iVersion + ", VERSION=" + VERSION);

        SchemaContract.createSchema(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        String strThis = "[" + this.getClass() + ".onUpgrade] ";
        int iVersion = db.getVersion(); // This will be the old version until after exit onUpgrade();
        System.out.println(strThis + "iVersion=" + iVersion + ", VERSION=" + VERSION);

        System.out.println(strThis + "iVersion=" + iVersion);

        SchemaContract.dropSchema(db);

        onCreate(db);
    }
}
