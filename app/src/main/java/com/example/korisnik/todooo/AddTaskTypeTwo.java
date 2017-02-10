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

import com.example.korisnik.todooo.db.Lista;
import com.example.korisnik.todooo.db.ListaHelper;
import com.example.korisnik.todooo.db.Task;

public class AddTaskTypeTwo extends AppCompatActivity implements View.OnClickListener{
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private NavigationView nvDrawer;

    private static final String TAG = "AddTaskTypeTwo";
    private ListaHelper listaHelper;

    // GUI components
    private AutoCompleteTextView taskName;		// Text field
    private FloatingActionButton addNewItemButton;	// Add new button

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_item);

        listaHelper = new ListaHelper(this);

        taskName = (AutoCompleteTextView) findViewById(R.id.itemname);
        addNewItemButton = (FloatingActionButton) findViewById(R.id.btnAddNewItem);

        addNewItemButton.setOnClickListener(this);

        //za navigation drawer
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //toolbar.setTitle("To-Do lists");
        setSupportActionBar(toolbar);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.drawer_open,
                R.string.drawer_close);
        drawerLayout.setDrawerListener(actionBarDrawerToggle);
        nvDrawer = (NavigationView) findViewById(R.id.navigation_view);
        setupDrawerContent(nvDrawer);
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
            if (addNewItemButton.isPressed()) {
                // Get entered text
                String taskTextValue = taskName.getText().toString();
                taskName.setText("");

                // Add text to the database
                SQLiteDatabase db = listaHelper.getWritableDatabase();
                ContentValues values = new ContentValues();
                values.put(Task.TaskEntry.COL_TASK_NAME, taskTextValue);
                values.put(Task.TaskEntry.COL_TASK_ID_LISTA, idListe);
                db.insertWithOnConflict(Task.TaskEntry.TABLE, null, values, SQLiteDatabase.CONFLICT_REPLACE);
                db.close();

                // Display success information
                Toast.makeText(getApplicationContext(), "New Item added!", Toast.LENGTH_LONG).show();
                // Close the database
                db.close();

                Intent intent = new Intent(this, ListTask.class);
                intent.putExtra("nazivListe", passedArg);
                startActivity(intent);

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
}
