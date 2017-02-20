package com.example.korisnik.todooo;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.Drawable;
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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


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
        TextView datumTaska = (TextView) rowView.findViewById(R.id.task_date);
        ImageView uzvicnik = (ImageView) rowView.findViewById(R.id.uzvicnik);
        final Button btnBrisi = (Button) rowView.findViewById(R.id.btnDelete);
        int pTaska = 0; //prioritet
        String nTaska = ""; //naziv
        String dTaska = ""; //datum
        String sTaska = ""; //status
        SimpleDateFormat format1 = new SimpleDateFormat("dd-MM-yyyy");
        SimpleDateFormat format2 = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        Date datum2 = null;

        int idTaska = idevi.get(position);
//        nazivTaska.setText(nazivi.get(position));
        final String tekst = String.valueOf(idTaska);

        try {
            SQLiteDatabase db = listaHelper.getReadableDatabase();
            Cursor cursor = db.rawQuery("SELECT  " + Task.TaskEntry.COL_TASK_NAME + ", " + Task.TaskEntry.COL_TASK_DATE + ", " + Task.TaskEntry.COL_TASK_PRIORITY + ", " + Task.TaskEntry.COL_TASK_STATUS + " FROM Task WHERE " + Task.TaskEntry._ID + " = " + idTaska, new String[]{});
            if (cursor.moveToFirst()) {
                do {
                    nTaska = cursor.getString(0);
                    dTaska = cursor.getString(1);
                    pTaska = cursor.getInt(2);
                    sTaska = cursor.getString(3);
                } while (cursor.moveToNext());
            }
            cursor.close();
            db.close();
        }
        catch (Exception ex){
            Toast.makeText(getContext().getApplicationContext(), "Error while reading from database", Toast.LENGTH_LONG).show();
        }

        final String statusTaska = sTaska;
        final boolean[] isPressed = {false};
        //oznacavanje taska kao done ili to do
        btnBrisi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isPressed[0]) {
                    btnBrisi.setBackgroundResource(R.drawable.notdonee);
                    //Toast.makeText(getContext().getApplicationContext(), "Nista", Toast.LENGTH_LONG).show();
                }
                else {
                    SQLiteDatabase db = listaHelper.getWritableDatabase();
                    if(statusTaska.equals(" to do\n")) {
                        btnBrisi.setBackgroundResource(R.drawable.notdonee);
                        ContentValues values = new ContentValues();
                        values.put(Task.TaskEntry.COL_TASK_STATUS, " done\n");
                        db.update(Task.TaskEntry.TABLE, values, "_id = " + tekst, null);
                    }
                    else if(statusTaska.equals(" done\n")) {
                        btnBrisi.setBackgroundResource(R.drawable.done);
                        ContentValues values = new ContentValues();
                        values.put(Task.TaskEntry.COL_TASK_STATUS, " to do\n");
                        db.update(Task.TaskEntry.TABLE, values, "_id = " + tekst, null);
                    }
                    else Toast.makeText(getContext().getApplicationContext(), "Error loading", Toast.LENGTH_LONG).show();
                    CustomAdapter.super.notifyDataSetChanged();
                    db.close();
                }
                isPressed[0] = !isPressed[0];
            }
        });

        skriveniID.setText(tekst);
        nazivTaska.setText(nTaska);

        //prikaz datuma
        if((dTaska.equals(null)) || (dTaska.equals("")) || (dTaska == null) || (dTaska == "")) {
            datumTaska.setText("");
        }
        else {
            try {
                datum2 = format2.parse(dTaska);
            }
            catch (Exception ex) {
                Toast.makeText(getContext().getApplicationContext(), "Something went wrong", Toast.LENGTH_LONG).show();
            }
            String datumPrikaz = format1.format(datum2);
            datumTaska.setText(datumPrikaz);
        }

        //podesi slicicu u zavisnosti od prioriteta
        if(pTaska == 1)
            uzvicnik.setImageResource(R.drawable.highpriority);
        else if(pTaska == 2)
            uzvicnik.setImageResource(R.drawable.mediumpriority);
        else if(pTaska == 3)
            uzvicnik.setImageResource(R.drawable.lowpriority);
        else uzvicnik.setImageResource(R.drawable.priority);

        //podesi status taska
        if(sTaska.equals(" to do\n"))
            btnBrisi.setBackgroundResource(R.drawable.notdonee);
        else if(sTaska.equals(" done\n"))
            btnBrisi.setBackgroundResource(R.drawable.done);
        else btnBrisi.setBackgroundResource(R.drawable.notdonee); //ovo je zbog liste itema, kasnije popravi

        return rowView;

    }
}
