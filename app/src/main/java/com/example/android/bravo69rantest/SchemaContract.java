package com.example.android.bravo69rantest;

import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class SchemaContract
{
    public static String TAG = "SchemaContractDriverTraining";

    public SchemaContract() {} // private so never called by mistake.

    public static List<TableDef> listTableDefs;

    // Static block defining all the schema tables as a list of TableDef objects.
    static {
        listTableDefs = new ArrayList<>();

        listTableDefs.add(
                new TableDef("CodingData",
                new String[][]
                        {
                            {"Id", "TEXT"},
                            {"ObjectId", "INTEGER"},
                            {"ObjectType", "TEXT"},
                            {"CodingMasterId", "INTEGER"},
                            {"DataType", "INTEGER"},
                            {"StringVal", "TEXT"},
                            {"NumberVal", "REAL"},
                            {"Timestamp", "TEXT"}
                        }
                    ));

        listTableDefs.add(
                new TableDef("CodingMaster",
                        new String[][]
                                {
                                        {"Id", "INTEGER"},
                                        {"DataType", "INTEGER"},
                                        {"DisplayName", "TEXT"}
                                }
                ));

        listTableDefs.add(
                new TableDef("ObjectType",
                        new String[][]
                                {
                                        {"Type", "TEXT"},
                                        {"DisplayType", "TEXT"}
                                }
                ));
    }

    public static void createSchema(SQLiteDatabase db)
    {
//        SchemaContractDriverTraining fake = new SchemaContractDriverTraining();

        Log.d(TAG, ".createSchema(),  listTableDefs.size()=" + (listTableDefs == null ? "(NULL)" : listTableDefs.size()));

        for (TableDef tbl : listTableDefs)
        {
            db.execSQL(tbl.getCreateTableSql());
        }
    }

    public static void dropSchema(SQLiteDatabase db)
    {
//        SchemaContractDriverTraining fake = new SchemaContractDriverTraining();

        Log.d(TAG, ".dropSchema(),  listTableDefs.size()=" + (listTableDefs == null ? "(NULL)" : listTableDefs.size()));

        for (TableDef tbl : listTableDefs)
        {
            db.execSQL(tbl.getDropTableSql());
        }
    }
}
