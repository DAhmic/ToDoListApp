package com.example.korisnik.todooo;


import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.example.korisnik.todooo.db.Lista;
import com.example.korisnik.todooo.db.Task;
import com.example.korisnik.todooo.db.ListaHelper;

import java.util.ArrayList;

public class ListTask extends AppCompatActivity implements View.OnClickListener{

    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private NavigationView nvDrawer;

    private static final String TAG = "ListTask";
    private ListaHelper listaHelper;
    private ListView taskoviView;
    private ArrayAdapter<String> mAdapter;
    private FloatingActionButton btnDodaj;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_task);

        listaHelper = new ListaHelper(this);
        taskoviView = (ListView) findViewById(R.id.taskovi_todo);
        btnDodaj = (FloatingActionButton)findViewById(R.id.btnAddTask);

        btnDodaj.setOnClickListener(this);

        //za navigation drawer
        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        //toolbar.setTitle("To-Do lists");
        setSupportActionBar(toolbar);
        drawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this,drawerLayout,toolbar,R.string.drawer_open,
                R.string.drawer_close);
        drawerLayout.setDrawerListener(actionBarDrawerToggle);
        nvDrawer = (NavigationView) findViewById(R.id.navigation_view);
        setupDrawerContent(nvDrawer);

        updateUI();
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
            default:
                startActivity(new Intent(getApplicationContext(),MainActivity.class));
        }
        menuItem.setChecked(true);
        setTitle(menuItem.getTitle());
        drawerLayout.closeDrawers();
    }
    //ovdje zavrsavaju metode za navigation drawer

    @Override
    public void onClick(View v) {
        // If add button was clicked
        if (btnDodaj.isPressed()) {
            int odabrani_template = 0;
            SQLiteDatabase db = listaHelper.getReadableDatabase();
            String passedArg = getIntent().getExtras().getString("nazivListe");
            //mislim da ovdje pada kada se pokusaju dodati dva taska zaredom
            //probala rijesiti sa prosljedjivanje naziva liste na sve ekrane koji mogu doci na ovaj
            Cursor cursor = db.rawQuery("SELECT type FROM Lista WHERE name = ?", new String[] {passedArg});
            if (cursor.moveToFirst()){
                do{
                    odabrani_template = cursor.getInt(0);
                }while (cursor.moveToNext());
            }
            cursor.close();
            db.close();

//            String test = Integer.toString(odabrani_template);
//            Toast.makeText(getApplicationContext(), "Template liste je "+test, Toast.LENGTH_LONG).show();

            if(odabrani_template == 1) {
                Intent intent = new Intent(this, AddTaskTypeOne.class);
                intent.putExtra("nazivListe", passedArg);
                // Start activity
                startActivity(intent);
                // Finish this activity
                this.finish();
            }
            else if(odabrani_template == 2) {
                Intent intent = new Intent(this, AddTaskTypeTwo.class);
                intent.putExtra("nazivListe", passedArg);
                startActivity(intent);
                this.finish();
            }
            else {
                Toast.makeText(getApplicationContext(), "Something went wrong", Toast.LENGTH_LONG).show();
            }
        }

    }

    private void updateUI(){
        //prvo naci id liste ciji su taskovi
        int idListe = 0;
        SQLiteDatabase db2 = listaHelper.getReadableDatabase();
        String passedArg = getIntent().getExtras().getString("nazivListe");
        Cursor cursor2 = db2.rawQuery("SELECT "+ Lista.ListaEntry._ID +" FROM Lista WHERE name = ?", new String[] {passedArg});
        if (cursor2.moveToFirst()){
            do{
                idListe = cursor2.getInt(0);
            }while (cursor2.moveToNext());
        }
        cursor2.close();
        db2.close();
        String idListeQuery = String.valueOf(idListe);

        ArrayList<String> imenaTaskova = new ArrayList<>();
        SQLiteDatabase db = listaHelper.getReadableDatabase();
        // Query the database
        //Cursor cursor = db.query(Task.TaskEntry.TABLE, new String[] {Task.TaskEntry._ID, Task.TaskEntry.COL_TASK_NAME}, null, null, null, null, null);
        Cursor cursor = db.rawQuery("SELECT  " + Task.TaskEntry.COL_TASK_NAME + " FROM  Task WHERE " + Task.TaskEntry.COL_TASK_ID_LISTA + " = ?", new String[] {idListeQuery});

        // Iterate the results
        while (cursor.moveToNext()) {
            int indeksi = cursor.getColumnIndex(Task.TaskEntry.COL_TASK_NAME);
            imenaTaskova.add(cursor.getString(indeksi));

        }
        if(mAdapter==null){
            mAdapter = new ArrayAdapter<>(this,R.layout.task_prikaz, R.id.task_name, imenaTaskova);
            taskoviView.setAdapter(mAdapter);
        }
        else {
            mAdapter.clear();
            mAdapter.addAll(imenaTaskova);
            mAdapter.notifyDataSetChanged();
        }
        cursor.close();
        db.close();

    }

}
