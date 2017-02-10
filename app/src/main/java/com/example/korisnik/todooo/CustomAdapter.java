package com.example.korisnik.todooo;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.korisnik.todooo.db.ListaHelper;
import com.example.korisnik.todooo.db.Task;

import java.io.Console;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Korisnik on 10.2.2017..
 */

public class CustomAdapter extends ArrayAdapter<String> {

    private Context context;
    private ArrayList<String> nazivi;
    private ListaHelper listaHelper;

    public CustomAdapter(Context context, ArrayList<String> nazivi){
        super(context, R.layout.task_prikaz, nazivi);
        listaHelper = new ListaHelper(context);
        this.context = context;
        this.nazivi = new ArrayList<String>();
        this.nazivi = nazivi;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.task_prikaz, parent, false);
        TextView nazivTaska = (TextView) rowView.findViewById(R.id.task_name);
        ImageView uzvicnik = (ImageView) rowView.findViewById(R.id.uzvicnik);
        int pTaska = 0; //prioritet

        nazivTaska.setText(nazivi.get(position));
        String tekst = nazivTaska.getText().toString();

        try {
            SQLiteDatabase db = listaHelper.getReadableDatabase();
            Cursor cursor = db.rawQuery("SELECT  " + Task.TaskEntry.COL_TASK_PRIORITY + " FROM Task WHERE " + Task.TaskEntry.COL_TASK_NAME + " = ?", new String[]{tekst});
            if (cursor.moveToFirst()) {
                do {
                    pTaska = cursor.getInt(0);
                } while (cursor.moveToNext());
            }
            cursor.close();
            db.close();
        }
        catch (Exception ex){
            Log.d("Dina", "Koji ti je djavo");
        }

        //podesi slicicu u zavisnosti od prioriteta
        if(pTaska == 1)
            uzvicnik.setImageResource(R.drawable.highpriority);
        else if(pTaska == 2)
            uzvicnik.setImageResource(R.drawable.mediumpriority);
        else if(pTaska == 3)
            uzvicnik.setImageResource(R.drawable.lowpriority);
        else uzvicnik.setImageResource(R.drawable.priority);

        return rowView;

    }
}
