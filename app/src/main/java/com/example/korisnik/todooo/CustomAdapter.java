package com.example.korisnik.todooo;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
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

public class CustomAdapter extends ArrayAdapter<Integer> {

    private Context context;
    private ArrayList<Integer> idevi;
    private ListaHelper listaHelper;

    public CustomAdapter(Context context, ArrayList<Integer> idevi){
        super(context, R.layout.task_prikaz, idevi);
        listaHelper = new ListaHelper(context);
        this.context = context;
        this.idevi = new ArrayList<Integer>();
        this.idevi = idevi;
    }


    @Override
    public View getView(final int position, View convertView, ViewGroup parent){
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.task_prikaz, parent, false);
        TextView skriveniID = (TextView)rowView.findViewById(R.id.task_id);
        TextView nazivTaska = (TextView) rowView.findViewById(R.id.task_name);
        ImageView uzvicnik = (ImageView) rowView.findViewById(R.id.uzvicnik);
        final Button btnBrisi = (Button) rowView.findViewById(R.id.btnDelete);
        int pTaska = 0; //prioritet
        String nTaska = ""; //naziv

        int idTaska = idevi.get(position);
//        nazivTaska.setText(nazivi.get(position));
        String tekst = String.valueOf(idTaska);
        btnBrisi.setTag(tekst);
        //brisanje taska
        btnBrisi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String tag_del = (String) view.getTag();
                idevi.remove(position);
                SQLiteDatabase db = listaHelper.getWritableDatabase();
                db.delete(Task.TaskEntry.TABLE, Task.TaskEntry._ID + " = ?", new String[]{tag_del});
                db.close();
                CustomAdapter.super.notifyDataSetChanged();
            }
        });

        try {
            SQLiteDatabase db = listaHelper.getReadableDatabase();
            Cursor cursor = db.rawQuery("SELECT  " + Task.TaskEntry.COL_TASK_NAME + ", " + Task.TaskEntry.COL_TASK_PRIORITY + " FROM Task WHERE " + Task.TaskEntry._ID + " = " + idTaska, new String[]{});
            if (cursor.moveToFirst()) {
                do {
                    nTaska = cursor.getString(0);
                    pTaska = cursor.getInt(1);
                } while (cursor.moveToNext());
            }
            cursor.close();
            db.close();
        }
        catch (Exception ex){
            Log.d("Dina", "Greska iz custom adaptera");
        }

        skriveniID.setText(tekst);
        nazivTaska.setText(nTaska);

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
