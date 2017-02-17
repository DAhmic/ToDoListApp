package com.example.korisnik.todooo.db;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class ListaHelper extends SQLiteOpenHelper {

    public ListaHelper(Context context){
        super(context,Lista.DB_NAME, null, Lista.DB_VERSION);

    }

    //create table
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("PRAGMA foreign_keys=ON;");
        String createTable = "CREATE TABLE " + Lista.ListaEntry.TABLE + "( "
                                             + Lista.ListaEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                                             + Lista.ListaEntry.COL_LISTA_NAME + " TEXT NOT NULL UNIQUE, "
                                             + Lista.ListaEntry.COL_LISTA_TYPE + " INTEGER NOT NULL, "
                                             + Lista.ListaEntry.COL_LISTA_DELETED + " BOOLEAN DEFAULT FALSE);";

        String createTable2 = "CREATE TABLE " + Task.TaskEntry.TABLE + "( "
                + Task.TaskEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + Task.TaskEntry.COL_TASK_NAME + " TEXT NOT NULL, "
                + Task.TaskEntry.COL_TASK_DATE + " TEXT DEFAULT NULL, "
                + Task.TaskEntry.COL_TASK_TIME + " TEXT DEFAULT NULL, "
                + Task.TaskEntry.COL_TASK_PRIORITY + " INTEGER DEFAULT NULL, "
                + Task.TaskEntry.COL_TASK_STATUS + " TEXT DEFAULT 'to do\n', "
                + Task.TaskEntry.COL_TASK_NOTE + " TEXT DEFAULT NULL, "
                + Task.TaskEntry.COL_TASK_DELETED + " BOOLEAN DEFAULT FALSE, "
                + Task.TaskEntry.COL_TASK_ID_LISTA + " INTEGER NOT NULL, "
                + "FOREIGN KEY(" + Task.TaskEntry.COL_TASK_ID_LISTA +") REFERENCES Lista(" + Lista.ListaEntry._ID + ") );";

        db.execSQL(createTable);
        db.execSQL(createTable2);
    }

    //recreates table
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVer, int newVer) {
        // DROP table
        db.execSQL("DROP TABLE IF EXISTS " + Lista.ListaEntry.TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + Task.TaskEntry.TABLE);
        // Recreate table
        onCreate(db);
    }

}
