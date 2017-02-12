package com.example.korisnik.todooo;


import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
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
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.korisnik.todooo.db.ListaHelper;
import com.example.korisnik.todooo.db.Task;

import java.util.ArrayList;

public class AllTaskSortByPriority extends AppCompatActivity {

    DrawerLayout drawerLayout;
    ActionBarDrawerToggle actionBarDrawerToggle;
    private NavigationView nvDrawer;

    private static final String TAG = "AllTaskSortByPriority";
    private ListaHelper listaHelper;
    private ListView taskoviView;
    //private ArrayAdapter<String> mAdapter;
    private CustomAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.all_tasks_sort_by_priority);

        listaHelper = new ListaHelper(this);
        taskoviView = (ListView) findViewById(R.id.taskovi_po_prioritetu);

        //za navigation drawer
        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        //toolbar.setTitle("To-Do lists");
        setSupportActionBar(toolbar);
        drawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this,drawerLayout,toolbar,R.string.drawer_open,
                R.string.drawer_close);
        drawerLayout.setDrawerListener(actionBarDrawerToggle);
        nvDrawer = (NavigationView) findViewById(R.id.navigation_view);

        //opcije change list name i delete list nisu dostupne
        Menu menuNav = nvDrawer.getMenu();
        MenuItem item2 = menuNav.findItem(R.id.id_changeListName);
        item2.setEnabled(false);
        MenuItem item3 = menuNav.findItem(R.id.id_deleteList);
        item3.setEnabled(false);

        setupDrawerContent(nvDrawer);

        updateUI();

        //klik na task otvara edit taska
        taskoviView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String naziv_taska = ((TextView)(view.findViewById(R.id.task_name))).getText().toString();
                String idT = ((TextView)(view.findViewById(R.id.task_id))).getText().toString();
                int idTaska = Integer.parseInt(idT);
                String datumTaska = "";
                String vrijemeTaska = "";
                int prioritetTaska = 0;
                String statusTaska = "";
                String noteTaska = "";
                int idListeTaska = 0;
                SQLiteDatabase db3 = listaHelper.getReadableDatabase();
                Cursor cursor3 = db3.rawQuery("SELECT " + Task.TaskEntry.COL_TASK_DATE + ", " + Task.TaskEntry.COL_TASK_TIME + ", "
                        + Task.TaskEntry.COL_TASK_PRIORITY + ", " + Task.TaskEntry.COL_TASK_STATUS + ", " + Task.TaskEntry.COL_TASK_NOTE +
                        ", " + Task.TaskEntry.COL_TASK_ID_LISTA + " FROM Task WHERE " + Task.TaskEntry._ID + " = " + idTaska, new String[] {});
                if (cursor3.moveToFirst()){
                    do{
                        datumTaska = cursor3.getString(0);
                        vrijemeTaska = cursor3.getString(1);
                        prioritetTaska = cursor3.getInt(2);
                        statusTaska = cursor3.getString(3);
                        noteTaska = cursor3.getString(4);
                        idListeTaska = cursor3.getInt(5);
                    }while (cursor3.moveToNext());
                }
                cursor3.close();
                db3.close();

                Intent intent = new Intent(AllTaskSortByPriority.this, EditTaskFromAllTaskSortByPriority.class);
                intent.putExtra("idTaska", idT);
                intent.putExtra("nazivTaska", naziv_taska);
                intent.putExtra("datumTaska", datumTaska);
                intent.putExtra("vrijemeTaska", vrijemeTaska);
                intent.putExtra("prioritetTaska", String.valueOf(prioritetTaska));
                intent.putExtra("statusTaska", statusTaska);
                intent.putExtra("noteTaska", noteTaska);
                intent.putExtra("idListeTaska", String.valueOf(idListeTaska));
                startActivity(intent);

            }
        });
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

    private void updateUI(){
        //ArrayList<String> imenaTaskova = new ArrayList<>();
        ArrayList<Integer> idTaskova = new ArrayList<>();
        SQLiteDatabase db = listaHelper.getReadableDatabase();
        // Query the database
        //Cursor cursor = db.query(Task.TaskEntry.TABLE, new String[] {Task.TaskEntry._ID, Task.TaskEntry.COL_TASK_NAME}, null, null, null, null, null);
        Cursor cursor = db.rawQuery("SELECT  " + Task.TaskEntry._ID + " FROM Task WHERE priority IS NOT NULL ORDER BY priority", new String[]{});

        // Iterate the results
        while (cursor.moveToNext()) {
            int indeksi = cursor.getColumnIndex(Task.TaskEntry._ID);
            idTaskova.add(cursor.getInt(indeksi));
        }
        if(mAdapter==null){
            //mAdapter = new ArrayAdapter<>(this,R.layout.task_prikaz, R.id.task_name, imenaTaskova);
            mAdapter = new CustomAdapter(this, idTaskova);
            taskoviView.setAdapter(mAdapter);
        }
        else {
            mAdapter.clear();
            mAdapter.addAll(idTaskova);
            mAdapter.notifyDataSetChanged();
        }
        cursor.close();
        db.close();

    }

}
