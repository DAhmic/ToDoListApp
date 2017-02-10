//package com.example.korisnik.todooo.db;
//
//
//import android.content.Context;
//import android.database.sqlite.SQLiteDatabase;
//import android.database.sqlite.SQLiteOpenHelper;
//
//public class TaskHelper extends SQLiteOpenHelper {
//
//    public TaskHelper(Context context){
//        super(context,Task.DB_NAME, null, Task.DB_VERSION);
//
//    }
//
//    //create table
//    @Override
//    public void onCreate(SQLiteDatabase db) {
//        String createTable = "CREATE TABLE " + Task.TaskEntry.TABLE + "( "
//                + Task.TaskEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
//                + Task.TaskEntry.COL_TASK_NAME + " TEXT NOT NULL );";
//        db.execSQL(createTable);
//    }
//
//    //recreates table
//    @Override
//    public void onUpgrade(SQLiteDatabase db, int oldVer, int newVer) {
//        // DROP table
//        db.execSQL("DROP TABLE IF EXISTS " + Task.TaskEntry.TABLE);
//        // Recreate table
//        onCreate(db);
//    }
//
//}
