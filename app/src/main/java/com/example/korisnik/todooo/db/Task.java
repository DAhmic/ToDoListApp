package com.example.korisnik.todooo.db;


import android.provider.BaseColumns;

public class Task {


    public class TaskEntry implements BaseColumns {
        public static final String TABLE = "Task";
        public static final String COL_TASK_NAME = "name";
        public static final String COL_TASK_DATE = "date";
        public static final String COL_TASK_TIME = "time";
        public static final String COL_TASK_PRIORITY = "priority";
        public static final String COL_TASK_STATUS = "status";
        public static final String COL_TASK_NOTE = "note";
        public static final String COL_TASK_DELETED = "deleted";
        public static final String COL_TASK_ID_LISTA = "id_lista"; //id liste kojoj pripada task
    }

}
