package com.example.korisnik.todooo;


import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
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
    //private ArrayAdapter<String> mAdapter;
    private CustomAdapter mAdapter;
    private FloatingActionButton btnDodaj;
    private Button btnBrisi;

    private int idListe = 0;
    private int odabrani_template = 0;
    private String passedArg;
    private String promjenaImena = ""; //novo ime za listu, opcija change liste name

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_task);

        listaHelper = new ListaHelper(this);
        taskoviView = (ListView) findViewById(R.id.taskovi_todo);
        btnDodaj = (FloatingActionButton)findViewById(R.id.btnAddTask);
        btnBrisi = (Button)findViewById(R.id.btnDelete);

        btnDodaj.setOnClickListener(this);

        //za navigation drawer
        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar); //naziv je podesen kad se dobavi passedarg
        drawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this,drawerLayout,toolbar,R.string.drawer_open,
                R.string.drawer_close);
        drawerLayout.setDrawerListener(actionBarDrawerToggle);
        nvDrawer = (NavigationView) findViewById(R.id.navigation_view);
        setupDrawerContent(nvDrawer);

        updateUI();

        //trazi template liste
        SQLiteDatabase db = listaHelper.getReadableDatabase();
        passedArg = getIntent().getExtras().getString("nazivListe");
        getSupportActionBar().setTitle(passedArg + " list");
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

        //opcije sortiranja nisu dostupne za liste tipa dva - samo itemi
        if(odabrani_template == 2) {
            Menu menuNav = nvDrawer.getMenu();
            MenuItem item2 = menuNav.findItem(R.id.id_dueDate);
            item2.setEnabled(false);
            MenuItem item3 = menuNav.findItem(R.id.id_priority);
            item3.setEnabled(false);
        }

        //klik na task otvara edit taska
        taskoviView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if(odabrani_template == 1) {
                    String idListeQuery = String.valueOf(idListe);
                    String naziv_taska = ((TextView) (view.findViewById(R.id.task_name))).getText().toString();
                    String idT = ((TextView) (view.findViewById(R.id.task_id))).getText().toString();
                    int idTaska = Integer.parseInt(idT);
                    String datumTaska = "";
                    String vrijemeTaska = "";
                    int prioritetTaska = 0;
                    String statusTaska = "";
                    String noteTaska = "";
                    SQLiteDatabase db3 = listaHelper.getReadableDatabase();
                    Cursor cursor3 = db3.rawQuery("SELECT " + Task.TaskEntry.COL_TASK_DATE + ", " + Task.TaskEntry.COL_TASK_TIME + ", "
                            + Task.TaskEntry.COL_TASK_PRIORITY + ", " + Task.TaskEntry.COL_TASK_STATUS + ", " + Task.TaskEntry.COL_TASK_NOTE + " FROM Task WHERE " + Task.TaskEntry._ID + " = " + idTaska, new String[]{});
                    if (cursor3.moveToFirst()) {
                        do {
                            datumTaska = cursor3.getString(0);
                            vrijemeTaska = cursor3.getString(1);
                            prioritetTaska = cursor3.getInt(2);
                            statusTaska = cursor3.getString(3);
                            noteTaska = cursor3.getString(4);
                        } while (cursor3.moveToNext());
                    }
                    cursor3.close();
                    db3.close();

                    Intent intent = new Intent(ListTask.this, EditTaskTypeOne.class);
                    intent.putExtra("idTaska", idT);
                    intent.putExtra("nazivTaska", naziv_taska);
                    intent.putExtra("datumTaska", datumTaska);
                    intent.putExtra("vrijemeTaska", vrijemeTaska);
                    intent.putExtra("prioritetTaska", String.valueOf(prioritetTaska));
                    intent.putExtra("statusTaska", statusTaska);
                    intent.putExtra("noteTaska", noteTaska);
                    intent.putExtra("idListeTaska", idListeQuery);
                    startActivity(intent);
                }
                else if(odabrani_template == 2){
                    String idListeQuery2 = String.valueOf(idListe);
                    String naziv_taska2 = ((TextView) (view.findViewById(R.id.task_name))).getText().toString();
                    String idT2 = ((TextView) (view.findViewById(R.id.task_id))).getText().toString();
                    Intent intent = new Intent(ListTask.this, EditTaskTypeTwo.class);
                    intent.putExtra("idTaska", idT2);
                    intent.putExtra("nazivTaska", naziv_taska2);
                    intent.putExtra("idListeTaska", idListeQuery2);
                    startActivity(intent);
                }
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
            case R.id.id_changeListName:
                AlertDialog.Builder builder3 = new AlertDialog.Builder(this);
                builder3.setTitle("Change list name");
                final EditText newName = new EditText(this);
                newName.setInputType(InputType.TYPE_CLASS_TEXT);
                builder3.setView(newName);
                builder3.setMessage("\nEnter the new name for this list:")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener(){
                            public void onClick(DialogInterface dialog, int id){
                                promjenaImena = newName.getText().toString();
                                promjenaImena = promjenaImena.trim();
                                SQLiteDatabase db = listaHelper.getWritableDatabase();
                                ContentValues values = new ContentValues();
                                values.put(Lista.ListaEntry.COL_LISTA_NAME, promjenaImena);
                                db.update(Lista.ListaEntry.TABLE, values, "_id = " + idListe, null);
                                Toast.makeText(getApplicationContext(), "List name successfully changed!", Toast.LENGTH_LONG).show();
                                db.close();
                                startActivity(new Intent(getApplicationContext(),MainActivity.class));
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener(){
                            public void onClick(DialogInterface dialog, int id){
                                dialog.cancel();
                            }
                        });
                AlertDialog alert3 = builder3.create();
                alert3.show();
                break;
            case R.id.id_deleteList:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Delete list");
                builder.setMessage("Are you sure you want to delete this list?")
                        .setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener(){
                public void onClick(DialogInterface dialog, int id){
                    deleteLista(idListe);
                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                }
            })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id){
                    dialog.cancel();
                }
            });
                AlertDialog alert = builder.create();
                alert.show();
                break;
            case R.id.id_priority:
                sortByPriority();
                //startActivity(new Intent(getApplicationContext(), AllTaskSortByPriority.class));
                break;
            case R.id.id_dueDate:
                sortByDueDate();
                //startActivity(new Intent(getApplicationContext(),AllTasks.class));
                break;
            case R.id.id_unfinished:
                viewUnfinished();
                //startActivity(new Intent(getApplicationContext(), UnfinishedTasks.class));
                break;
            case R.id.id_completed:
                viewFinished();
                //startActivity(new Intent(getApplicationContext(), FinishedTasks.class));
                break;
            case R.id.id_delete_completed:
                AlertDialog.Builder builder2 = new AlertDialog.Builder(this);
                builder2.setMessage("Are you sure you want to delete finished tasks from this list?")
                        .setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener(){
                            public void onClick(DialogInterface dialog, int id){
                                deleteFinishedTasks(idListe);
                                updateUI();
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id){
                                dialog.cancel();
                            }
                        });
                AlertDialog alert2 = builder2.create();
                alert2.show();
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
        //int idListe = 0;
        SQLiteDatabase db2 = listaHelper.getReadableDatabase();
        passedArg = getIntent().getExtras().getString("nazivListe");
        Cursor cursor2 = db2.rawQuery("SELECT "+ Lista.ListaEntry._ID +" FROM Lista WHERE name = ?", new String[] {passedArg});
        if (cursor2.moveToFirst()){
            do{
                idListe = cursor2.getInt(0);
            }while (cursor2.moveToNext());
        }
        cursor2.close();
        db2.close();
        String idListeQuery = String.valueOf(idListe);

        //ArrayList<String> imenaTaskova = new ArrayList<>();
        ArrayList<Integer> idTaskova = new ArrayList<>();
        SQLiteDatabase db = listaHelper.getReadableDatabase();
        // Query the database
        //Cursor cursor = db.query(Task.TaskEntry.TABLE, new String[] {Task.TaskEntry._ID, Task.TaskEntry.COL_TASK_NAME}, null, null, null, null, null);
        //Cursor cursor = db.rawQuery("SELECT  " + Task.TaskEntry._ID + ", " + Task.TaskEntry.COL_TASK_NAME + " FROM  Task WHERE " + Task.TaskEntry.COL_TASK_ID_LISTA + " = ?", new String[] {idListeQuery});
        Cursor cursor = db.rawQuery("SELECT  " + Task.TaskEntry._ID + " FROM  Task WHERE " + Task.TaskEntry.COL_TASK_ID_LISTA + " = ? ORDER BY CASE WHEN date = '' THEN 2 ELSE 1 END, date", new String[] {idListeQuery});

        // Iterate the results
        while (cursor.moveToNext()) {
            int indeksi = cursor.getColumnIndex(Task.TaskEntry._ID);
            idTaskova.add(cursor.getInt(indeksi));
        }
        if(mAdapter==null){
            //mAdapter = new ArrayAdapter<>(this,R.layout.task_prikaz, R.id.task_name, imenaTaskova);
            mAdapter = new CustomAdapter(this, idTaskova); //mAdapter = new CustomAdapter(this, imenaTaskova);
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

    public void deleteLista(int idL){
        String idListee = String.valueOf(idL);
        SQLiteDatabase db = listaHelper.getWritableDatabase();
        db.delete(Task.TaskEntry.TABLE, Task.TaskEntry.COL_TASK_ID_LISTA + " = ?", new String[]{idListee});
        db.delete(Lista.ListaEntry.TABLE, Lista.ListaEntry._ID + " = ?", new String[] {idListee});
        db.close();
    }

    public void deleteFinishedTasks(int idL){
        String idListee = String.valueOf(idListe);
        SQLiteDatabase db = listaHelper.getWritableDatabase();
        db.delete(Task.TaskEntry.TABLE, Task.TaskEntry.COL_TASK_ID_LISTA + " = ? AND " + Task.TaskEntry.COL_TASK_STATUS + " = ' done\n'", new String[]{idListee});
        db.close();
    }

//    public void deleteTask(int idT){
//        String idTaskaa = String.valueOf(idT);
//        SQLiteDatabase db = listaHelper.getWritableDatabase();
//        db.delete(Task.TaskEntry.TABLE, Task.TaskEntry._ID + " = ?", new String[]{idTaskaa});
//        db.close();
//    }

    private void sortByPriority(){
        //prvo naci id liste ciji su taskovi
        SQLiteDatabase db2 = listaHelper.getReadableDatabase();
        passedArg = getIntent().getExtras().getString("nazivListe");
        Cursor cursor2 = db2.rawQuery("SELECT "+ Lista.ListaEntry._ID +" FROM Lista WHERE name = ?", new String[] {passedArg});
        if (cursor2.moveToFirst()){
            do{
                idListe = cursor2.getInt(0);
            }while (cursor2.moveToNext());
        }
        cursor2.close();
        db2.close();
        String idListeQuery = String.valueOf(idListe);

        ArrayList<Integer> idTaskova = new ArrayList<>();
        SQLiteDatabase db = listaHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT  " + Task.TaskEntry._ID + " FROM  Task WHERE " + Task.TaskEntry.COL_TASK_ID_LISTA + " = ? ORDER BY priority", new String[] {idListeQuery});

        // Iterate the results
        while (cursor.moveToNext()) {
            int indeksi = cursor.getColumnIndex(Task.TaskEntry._ID);
            idTaskova.add(cursor.getInt(indeksi));
        }
        if(mAdapter==null){
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

    private void sortByDueDate(){
        //prvo naci id liste ciji su taskovi
        SQLiteDatabase db2 = listaHelper.getReadableDatabase();
        passedArg = getIntent().getExtras().getString("nazivListe");
        Cursor cursor2 = db2.rawQuery("SELECT "+ Lista.ListaEntry._ID +" FROM Lista WHERE name = ?", new String[] {passedArg});
        if (cursor2.moveToFirst()){
            do{
                idListe = cursor2.getInt(0);
            }while (cursor2.moveToNext());
        }
        cursor2.close();
        db2.close();
        String idListeQuery = String.valueOf(idListe);

        ArrayList<Integer> idTaskova = new ArrayList<>();
        SQLiteDatabase db = listaHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT  " + Task.TaskEntry._ID + " FROM  Task WHERE " + Task.TaskEntry.COL_TASK_ID_LISTA + " = ? ORDER BY CASE WHEN date = '' THEN 2 ELSE 1 END, date", new String[] {idListeQuery});

        while (cursor.moveToNext()) {
            int indeksi = cursor.getColumnIndex(Task.TaskEntry._ID);
            idTaskova.add(cursor.getInt(indeksi));
        }
        if(mAdapter==null){
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

    private void viewUnfinished(){
        //prvo naci id liste ciji su taskovi
        SQLiteDatabase db2 = listaHelper.getReadableDatabase();
        passedArg = getIntent().getExtras().getString("nazivListe");
        Cursor cursor2 = db2.rawQuery("SELECT "+ Lista.ListaEntry._ID +" FROM Lista WHERE name = ?", new String[] {passedArg});
        if (cursor2.moveToFirst()){
            do{
                idListe = cursor2.getInt(0);
            }while (cursor2.moveToNext());
        }
        cursor2.close();
        db2.close();
        String idListeQuery = String.valueOf(idListe);

        ArrayList<Integer> idTaskova = new ArrayList<>();
        SQLiteDatabase db = listaHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT  " + Task.TaskEntry._ID + " FROM  Task WHERE " + Task.TaskEntry.COL_TASK_ID_LISTA + " = ? AND " + Task.TaskEntry.COL_TASK_STATUS + " = ' to do\n' " + "ORDER BY date == ''", new String[] {idListeQuery});

        while (cursor.moveToNext()) {
            int indeksi = cursor.getColumnIndex(Task.TaskEntry._ID);
            idTaskova.add(cursor.getInt(indeksi));
        }
        if(mAdapter==null){
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

    private void viewFinished(){
        //prvo naci id liste ciji su taskovi
        SQLiteDatabase db2 = listaHelper.getReadableDatabase();
        passedArg = getIntent().getExtras().getString("nazivListe");
        Cursor cursor2 = db2.rawQuery("SELECT "+ Lista.ListaEntry._ID +" FROM Lista WHERE name = ?", new String[] {passedArg});
        if (cursor2.moveToFirst()){
            do{
                idListe = cursor2.getInt(0);
            }while (cursor2.moveToNext());
        }
        cursor2.close();
        db2.close();
        String idListeQuery = String.valueOf(idListe);

        ArrayList<Integer> idTaskova = new ArrayList<>();
        SQLiteDatabase db = listaHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT  " + Task.TaskEntry._ID + " FROM  Task WHERE " + Task.TaskEntry.COL_TASK_ID_LISTA + " = ? AND " + Task.TaskEntry.COL_TASK_STATUS + " = ' done\n' " + "ORDER BY date == ''", new String[] {idListeQuery});

        while (cursor.moveToNext()) {
            int indeksi = cursor.getColumnIndex(Task.TaskEntry._ID);
            idTaskova.add(cursor.getInt(indeksi));
        }
        if(mAdapter==null){
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
