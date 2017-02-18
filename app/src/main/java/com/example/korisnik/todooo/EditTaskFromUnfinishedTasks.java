package com.example.korisnik.todooo;


import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.DialogFragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.korisnik.todooo.db.ListaHelper;
import com.example.korisnik.todooo.db.Task;

import java.text.SimpleDateFormat;
import java.util.Date;

public class EditTaskFromUnfinishedTasks extends AppCompatActivity implements View.OnClickListener {
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private NavigationView nvDrawer;

    private static final String TAG = "EditTaskFromAllTasks";
    private ListaHelper listaHelper;

    // GUI components
    private AutoCompleteTextView taskName;		// Text field
    private FloatingActionButton editTaskButton;	// Add new button
    private Spinner taskPriority;
    private Spinner taskStatus;
    private EditText taskNote;
    private Button taskDate;
    private Button taskTime;
    private int prioritet = 4; //u bazi se cuva int, 1 - high, 2 - medium, 3 - low, 4 - nije oznaceno
    private String task_priority;
    private String task_status;

    //pocetne info
    private String pocetniNaziv;
    private String pocetniDatum;
    private String pocetnoVrijeme;
    private int pocetniPrioritet;
    private String pocetniStatus;
    private String pocetnaNote;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_task);

        listaHelper = new ListaHelper(this);

        taskName 		= (AutoCompleteTextView) findViewById(R.id.editTaskName);
        taskDate = (Button)findViewById(R.id.button_date);
        taskTime = (Button)findViewById(R.id.button_time);
        taskPriority = (Spinner)findViewById(R.id.edit_spinner_priority);
        taskStatus = (Spinner)findViewById(R.id.edit_spinner_status);
        taskNote = (EditText)findViewById(R.id.edit_textnote);
        editTaskButton 	= (FloatingActionButton)findViewById(R.id.btnEditTaskTypeOne);

        editTaskButton.setOnClickListener(this);

        //za navigation drawer
        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Edit task");
        drawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this,drawerLayout,toolbar,R.string.drawer_open,
                R.string.drawer_close);
        drawerLayout.setDrawerListener(actionBarDrawerToggle);
        nvDrawer = (NavigationView) findViewById(R.id.navigation_view);

        //opcije change list name, delete list i delete finished tasks nisu dostupne na pocetnom meniju
        Menu menuNav = nvDrawer.getMenu();
        MenuItem item2 = menuNav.findItem(R.id.id_changeListName);
        item2.setEnabled(false);
        MenuItem item3 = menuNav.findItem(R.id.id_deleteList);
        item3.setEnabled(false);
        MenuItem item4 = menuNav.findItem(R.id.id_delete_completed);
        item4.setEnabled(false);

        setupDrawerContent(nvDrawer);

        //za padajucu listu priority
        Spinner dropdown = (Spinner)findViewById(R.id.edit_spinner_priority);
        String[] priority = new String[] {"Priority\n","high\n", "medium\n", "low\n"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,R.layout.spinner_item,priority);
        dropdown.setAdapter(adapter);

        //za padajucu listu status
        Spinner dropdown2 = (Spinner)findViewById(R.id.edit_spinner_status);
        String[] status = new String[] {"Status\n","to do\n","done\n"};
        ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(this,R.layout.spinner_item,status);
        dropdown2.setAdapter(adapter2);

        //dobavljanje informacija o tasku
        String nazivTaska = getIntent().getExtras().getString("nazivTaska");
        pocetniNaziv = nazivTaska;
        String datumTaska = getIntent().getExtras().getString("datumTaska");
        pocetniDatum = datumTaska;
        //konverzija datuma za spremanje u bazu u formatu yyyy-mm-dd
        SimpleDateFormat format1 = new SimpleDateFormat("dd-MM-yyyy");
        SimpleDateFormat format2 = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        Date datum2 = null;
        try {
            datum2 = format2.parse(datumTaska);
        }
        catch (Exception ex) {
            Toast.makeText(getApplicationContext(), "Something went wrong", Toast.LENGTH_LONG).show();
        }
        String datumPrikaz = format1.format(datum2);
        String vrijemeTaska = getIntent().getExtras().getString("vrijemeTaska");
        pocetnoVrijeme = vrijemeTaska;
        String prioritetTaska = getIntent().getExtras().getString("prioritetTaska");
        int pTaska = Integer.parseInt(prioritetTaska);
        pocetniPrioritet = pTaska;
        String statusTaska = getIntent().getExtras().getString("statusTaska");
        pocetniStatus = statusTaska;
        String noteTaska = getIntent().getExtras().getString("noteTaska");
        pocetnaNote = noteTaska;

        //postavljanje dobivenih informacija na formu
        taskName.setText(nazivTaska);
        taskDate.setText(datumPrikaz);
        taskTime.setText(vrijemeTaska);
        if(pTaska == 1)
            taskPriority.setSelection(1);
        else if(pTaska == 2)
            taskPriority.setSelection(2);
        else if(pTaska == 3)
            taskPriority.setSelection(3);
        else taskPriority.setSelection(0);
        if(statusTaska.equals("to do\n"))
            taskStatus.setSelection(1);
        else if(statusTaska.equals("done\n"))
            taskStatus.setSelection(2);
        else taskStatus.setSelection(0);
        taskNote.setText(noteTaska);

    }

    @Override
    public void onClick(View v) {
        String idTaska = getIntent().getExtras().getString("idTaska");
        String idListeTaska = getIntent().getExtras().getString("idListeTaska");

        // If add button was clicked
        if (editTaskButton.isPressed()) {
            // Get entered text
            String taskTextValue = taskName.getText().toString();
            //date
            String datum = taskDate.getText().toString(); // format dd-mm-yyyy se prikazuje
            //konverzija datuma za spremanje u bazu u formatu yyyy-mm-dd
            SimpleDateFormat format1 = new SimpleDateFormat("dd-MM-yyyy");
            SimpleDateFormat format2 = new SimpleDateFormat("yyyy-MM-dd");
            Date datum2 = null;
            try {
                datum2 = format1.parse(datum);
            } catch (Exception ex) {
                Toast.makeText(getApplicationContext(), "Something went wrong", Toast.LENGTH_LONG).show();
            }
            //time
            String vrijeme = taskTime.getText().toString();
            //u date tabeli sacuvan format yyyy-mm-dd hh:mm:ss
            String datumUBazu = format2.format(datum2) + " " + vrijeme + ":00";
            //priority
            task_priority = taskPriority.getSelectedItem().toString();
            if (task_priority == "high\n")
                prioritet = 1;
            else if (task_priority == "medium\n")
                prioritet = 2;
            else if (task_priority == "low\n")
                prioritet = 3;
            else prioritet = 4;
            //status
            task_status = taskStatus.getSelectedItem().toString();
            //note
            String taskNoteValue = taskNote.getText().toString();

            if (!(pocetniNaziv.equals(taskTextValue)) || !(pocetniDatum.equals(datumUBazu)) || !(pocetnoVrijeme.equals(vrijeme)) || (pocetniPrioritet != prioritet) || !(pocetniStatus.equals(task_status)) || !(pocetnaNote.equals(taskNoteValue))) {
                SQLiteDatabase db = listaHelper.getWritableDatabase();
                ContentValues values = new ContentValues();
                values.put(Task.TaskEntry.COL_TASK_NAME, taskTextValue);
                values.put(Task.TaskEntry.COL_TASK_DATE, datumUBazu);
                values.put(Task.TaskEntry.COL_TASK_TIME, vrijeme);
                values.put(Task.TaskEntry.COL_TASK_PRIORITY, prioritet);
                values.put(Task.TaskEntry.COL_TASK_STATUS, task_status);
                values.put(Task.TaskEntry.COL_TASK_NOTE, taskNoteValue);
                values.put(Task.TaskEntry.COL_TASK_ID_LISTA, idListeTaska);
                db.update(Task.TaskEntry.TABLE, values, "_id = " + idTaska, null);

                Toast.makeText(getApplicationContext(), "Task successfully edited!", Toast.LENGTH_LONG).show();
                db.close();
            } else {
                Toast.makeText(getApplicationContext(), "You didn't make any changes", Toast.LENGTH_LONG).show();
            }
            Intent intent = new Intent(this, UnfinishedTasks.class);
            startActivity(intent);
        }

    }

    //sve dalje metode su za navigation drawer :)
    @Override
    protected void onPostCreate(Bundle savedInstanceState){
        super.onPostCreate(savedInstanceState);
        actionBarDrawerToggle.syncState();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.my_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                drawerLayout.openDrawer(GravityCompat.START);
                return true;
            case R.id.id_help:
                startActivity(new Intent(getApplicationContext(), HelpActivity.class));
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setupDrawerContent(NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        selectDrawerItem(menuItem);
                        return true;
                    }
                });
    }

    public void selectDrawerItem(MenuItem menuItem) {
        switch(menuItem.getItemId()) {
            case R.id.viewLists:
                startActivity(new Intent(getApplicationContext(),MainActivity.class));
                break;
            case R.id.viewTasks:
                startActivity(new Intent(getApplicationContext(),AllTasks.class));
                break;
            case R.id.id_priority:
                startActivity(new Intent(getApplicationContext(), AllTaskSortByPriority.class));
                break;
            case R.id.id_dueDate:
                startActivity(new Intent(getApplicationContext(),AllTasks.class));
                break;
            case R.id.id_unfinished:
                startActivity(new Intent(getApplicationContext(), UnfinishedTasks.class));
                break;
            case R.id.id_completed:
                startActivity(new Intent(getApplicationContext(), FinishedTasks.class));
                break;
            default:
                startActivity(new Intent(getApplicationContext(),MainActivity.class));
        }
        menuItem.setChecked(true);
        setTitle(menuItem.getTitle());
        drawerLayout.closeDrawers();
    }
    //ovdje zavrsavaju metode za navigation drawer

    //prozorcic za biranje datuma
    public void showDatePickerDialog(View v) {
        DialogFragment newFragment = new DatePickerFragment();
        newFragment.show(getSupportFragmentManager(), "datePicker");
    }

    //prozorcic za biranje vremena
    public void showTimePickerDialog(View v) {
        DialogFragment newFragment = new TimePickerFragment();
        newFragment.show(getSupportFragmentManager(), "timePicker");
    }


}
