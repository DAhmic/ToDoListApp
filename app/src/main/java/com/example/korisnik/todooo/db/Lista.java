package com.example.korisnik.todooo.db;


import android.provider.BaseColumns;

public class Lista {
    public static final String DB_NAME = "com.example.korisnik.todooo.db";
    //db verzija - ne zabpravi promijeniti svaki put kada nesto promijenis u bazi
    public static final  int DB_VERSION = 12;

    public class ListaEntry implements BaseColumns{
        public static final String TABLE = "Lista";
        public static final String COL_LISTA_NAME = "name";
        public static final String COL_LISTA_TYPE = "type";
        public static final String COL_LISTA_DELETED = "deleted";
    }

}
