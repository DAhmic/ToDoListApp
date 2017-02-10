package com.example.korisnik.todooo;

import android.app.DatePickerDialog;
import android.content.Context;
import android.widget.DatePicker;
import android.widget.Toast;


public class DateSettings implements DatePickerDialog.OnDateSetListener {

    Context context;
    public DateSettings(Context context){
        this.context = context;
    }

    @Override
    public void onDateSet(DatePicker datePicker, int day, int month, int year) {
        Toast.makeText(context,"Selected date: " + day + "/" + month + "/" + year,Toast.LENGTH_LONG).show();
    }


}
