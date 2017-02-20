package com.example.korisnik.todooo;


import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
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

import com.example.korisnik.todooo.db.Lista;
import com.example.korisnik.todooo.db.Task;
import com.example.korisnik.todooo.db.ListaHelper;

import java.text.SimpleDateFormat;
import java.util.Date;

public class AddTaskTypeOne extends AppCompatActivity implements View.OnClickListener {

    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private NavigationView nvDrawer;

    private static final String TAG = "AddTaskTypeOne";
    private ListaHelper listaHelper;

    // GUI components
    private AutoCompleteTextView taskName;		// Text field
    private FloatingActionButton addNewTaskButton;	// Add new button
    private String taskPriority;
    private String taskStatus;
    private EditText taskNote;
    private Button taskDate;
    private Button taskTime;
    private int prioritet = 4; //u bazi se cuva int, 1 - high, 2 - medium, 3 - low, 4 - nije oznaceno

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_task);

        listaHelper = new ListaHelper(this);

        taskName 		= (AutoCompleteTextView) findViewById(R.id.newTaskName);
        addNewTaskButton 	= (FloatingActionButton)findViewById(R.id.btnAddTaskTypeOne);
        taskNote = (EditText)findViewById(R.id.textnote);

        addNewTaskButton.setOnClickListener(this);

        //za navigation drawer
        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Add new task");
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
        Spinner dropdown = (Spinner)findViewById(R.id.spinner_priority);
        String[] priority = new String[] {" Priority\n"," high\n", " medium\n", " low\n"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,R.layout.spinner_item,priority);
        dropdown.setAdapter(adapter);

        //za padajucu listu status
        Spinner dropdown2 = (Spinner)findViewById(R.id.spinner_status);
        String[] status = new String[] {" Status\n"," to do\n"," done\n"};
        ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(this,R.layout.spinner_item,status);
        dropdown2.setAdapter(adapter2);

    }

    @Override
    public void onClick(View v) {
        //trazi id liste u koju se dodaje task
        int idListe = 0;
        SQLiteDatabase db2 = listaHelper.getReadableDatabase();
        String passedArg = getIntent().getExtras().getString("nazivListe");
        Cursor cursor = db2.rawQuery("SELECT "+ Lista.ListaEntry._ID +" FROM Lista WHERE name = ?", new String[] {passedArg});
        if (cursor.moveToFirst()){
            do{
                idListe = cursor.getInt(0);
            }while (cursor.moveToNext());
        }
        cursor.close();
        db2.close();

        // If add button was clicked
        if (addNewTaskButton.isPressed()) {
            // Get entered text
            String taskTextValue = taskName.getText().toString();
            taskTextValue = taskTextValue.trim();
            taskName.setText("");
            if(taskTextValue.equals("") || taskTextValue.equals(null)){
                Toast.makeText(getApplicationContext(), "Enter the task name", Toast.LENGTH_LONG).show();
            }
            else {
                //date
                taskDate = (Button) findViewById(R.id.button_date);
                String datum = taskDate.getText().toString(); // format dd-mm-yyyy se prikazuje
                //konverzija datuma za spremanje u bazu u formatu yyyy-mm-dd
                SimpleDateFormat format1 = new SimpleDateFormat("dd-MM-yyyy");
                SimpleDateFormat format2 = new SimpleDateFormat("yyyy-MM-dd");
                String datumUBazu = "";
                Date datum2 = null;
                if (datum.equals(null) || datum.equals("") || datum.equals("Date")) {
                    datumUBazu = "";
                }
                else {
                    try {
                        datum2 = format1.parse(datum);
                    } catch (Exception ex) {
                        Toast.makeText(getApplicationContext(), "Something went wrong", Toast.LENGTH_LONG).show();
                    }
                    taskTime = (Button) findViewById(R.id.button_time);
                    String vrijeme2 = taskTime.getText().toString();
                    if (vrijeme2.equals("Time") || vrijeme2.equals(null) || vrijeme2.equals("")) {
                        vrijeme2 = "00:00";
                    }
                    //u date tabeli sacuvan format yyyy-mm-dd hh:mm:ss
                    datumUBazu = format2.format(datum2) + " " + vrijeme2 + ":00";
                }
                //String datumUBazu = format2.format(datum2);
                taskDate.setText("");
                //time
                taskTime = (Button) findViewById(R.id.button_time);
                String vrijeme = taskTime.getText().toString();
                String vrijemeUBazu = "";
                if (vrijeme.equals("Time") || vrijeme.equals(null) || vrijeme.equals("")) {
                    vrijemeUBazu = "";
                } else {
                    vrijemeUBazu = vrijeme;
                }
                //u date tabeli sacuvan format yyyy-mm-dd hh:mm:ss
                //datumUBazu = format2.format(datum2) + " " + vrijeme + ":00";
                taskTime.setText("");
                //priority
                taskPriority = ((Spinner) findViewById(R.id.spinner_priority)).getSelectedItem().toString();
                if (taskPriority == " high\n")
                    prioritet = 1;
                else if (taskPriority == " medium\n")
                    prioritet = 2;
                else if (taskPriority == " low\n")
                    prioritet = 3;
                else prioritet = 4;
                ((Spinner) findViewById(R.id.spinner_priority)).setSelection(0);
                //status
                taskStatus = ((Spinner) findViewById(R.id.spinner_status)).getSelectedItem().toString();
                if (taskStatus.equals(" Status\n") || taskStatus.equals(null) || taskStatus.equals("")) {
                    taskStatus = " to do\n";
                }
                ((Spinner) findViewById(R.id.spinner_status)).setSelection(0);
                //note
                String taskNoteValue = taskNote.getText().toString();
                taskNote.setText("");

                // Add text to the database
                SQLiteDatabase db = listaHelper.getWritableDatabase();
                ContentValues values = new ContentValues();
                values.put(Task.TaskEntry.COL_TASK_NAME, taskTextValue);
                values.put(Task.TaskEntry.COL_TASK_DATE, datumUBazu);
                values.put(Task.TaskEntry.COL_TASK_TIME, vrijemeUBazu);
                values.put(Task.TaskEntry.COL_TASK_PRIORITY, prioritet);
                values.put(Task.TaskEntry.COL_TASK_STATUS, taskStatus);
                values.put(Task.TaskEntry.COL_TASK_NOTE, taskNoteValue);
                values.put(Task.TaskEntry.COL_TASK_ID_LISTA, idListe);
                db.insertWithOnConflict(Task.TaskEntry.TABLE, null, values, SQLiteDatabase.CONFLICT_REPLACE);
                //db.close();

                // Display success information
                Toast.makeText(getApplicationContext(), "New task added", Toast.LENGTH_LONG).show();
                // Close the database
                db.close();

                Intent intent = new Intent(this, ListTask.class);
                intent.putExtra("nazivListe", passedArg);
                startActivity(intent);
                this.finish();
            }
        }
        //ovdje bi isao else da je kliknut back button

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
