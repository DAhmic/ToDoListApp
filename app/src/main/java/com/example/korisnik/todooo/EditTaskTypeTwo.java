package com.example.korisnik.todooo;


import android.content.ContentValues;
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
import android.widget.AutoCompleteTextView;
import android.widget.Toast;

import com.example.korisnik.todooo.db.ListaHelper;
import com.example.korisnik.todooo.db.Task;

public class EditTaskTypeTwo extends AppCompatActivity implements View.OnClickListener {
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private NavigationView nvDrawer;

    private static final String TAG = "EditTaskTypeTwo";
    private ListaHelper listaHelper;

    // GUI components
    private AutoCompleteTextView itemName;		// Text field
    private FloatingActionButton editItemButton;

    //pocetne info
    private String pocetniNaziv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_item);

        listaHelper = new ListaHelper(this);
        itemName = (AutoCompleteTextView) findViewById(R.id.edititemname);
        editItemButton 	= (FloatingActionButton)findViewById(R.id.btnEditItem);
        editItemButton.setOnClickListener(this);

        //za navigation drawer
        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        //toolbar.setTitle("To-Do lists");
        setSupportActionBar(toolbar);
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

        //dobavljanje informacija o tasku
        String nazivTaska = getIntent().getExtras().getString("nazivTaska");
        pocetniNaziv = nazivTaska;

        //postavljanje dobivenih informacija na formu
        itemName.setText(nazivTaska);
        int pos = itemName.getText().length();
        itemName.setSelection(pos);

    }

    @Override
    public void onClick(View v) {
        String idTaska = getIntent().getExtras().getString("idTaska");
        String idListeTaska = getIntent().getExtras().getString("idListeTaska");
        String nazivListe = "";

        //nadji naziv liste
        SQLiteDatabase db2 = listaHelper.getReadableDatabase();
        Cursor cursor = db2.rawQuery("SELECT name FROM Lista WHERE _id = ?", new String[] {idListeTaska});
        if (cursor.moveToFirst()){
            do{
                nazivListe = cursor.getString(0);
            }while (cursor.moveToNext());
        }
        cursor.close();
        db2.close();

        // If add button was clicked
        if (editItemButton.isPressed()) {
            // Get entered text
            String taskTextValue = itemName.getText().toString();

            if(!(pocetniNaziv.equals(taskTextValue))){
                SQLiteDatabase db = listaHelper.getWritableDatabase();
                ContentValues values = new ContentValues();
                values.put(Task.TaskEntry.COL_TASK_NAME, taskTextValue);
                values.put(Task.TaskEntry.COL_TASK_ID_LISTA, idListeTaska);
                db.update(Task.TaskEntry.TABLE, values, "_id = " + idTaska, null);
                // Display success information
                Toast.makeText(getApplicationContext(), "Task successfully edited!", Toast.LENGTH_LONG).show();
                // Close the database
                db.close();
            }
            else {
                Toast.makeText(getApplicationContext(), "You didn't make any changes", Toast.LENGTH_LONG).show();
            }
            Intent intent = new Intent(this, ListTask.class);
            intent.putExtra("nazivListe", nazivListe);
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

}
