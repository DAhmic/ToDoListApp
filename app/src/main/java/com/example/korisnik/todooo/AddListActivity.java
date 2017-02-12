package com.example.korisnik.todooo;


import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.PorterDuff;
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
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import com.example.korisnik.todooo.db.Lista;
import com.example.korisnik.todooo.db.ListaHelper;

public class AddListActivity extends AppCompatActivity implements View.OnClickListener {
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private NavigationView nvDrawer;

    private static final String TAG = "AddListActivity";
    private ListaHelper listaHelper;
    private int odabrani=0; // 1-task, 2-item

    // GUI components
    private AutoCompleteTextView listaName;		// Text field
    private FloatingActionButton addNewListaButton;	// Add new button
    //private FloatingActionButton backButton;		// Back button
    private ImageButton taskTemplate;
    private ImageButton itemTemplate;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_lista);

        listaHelper = new ListaHelper(this);

        listaName 		= (AutoCompleteTextView) findViewById(R.id.newListaName);
        addNewListaButton 	= (FloatingActionButton)findViewById(R.id.addNewListaButton);
        //backButton		= (FloatingActionButton)findViewById(R.id.menuGoBackButton);

        addNewListaButton.setOnClickListener(this);
        //backButton.setOnClickListener(this);

        //za Image Buttone
        addListenerOnButton();

        //za navigation drawer
        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        //toolbar.setTitle("To-Do lists");
        setSupportActionBar(toolbar);
        drawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this,drawerLayout,toolbar,R.string.drawer_open,
                R.string.drawer_close);
        drawerLayout.setDrawerListener(actionBarDrawerToggle);
        nvDrawer = (NavigationView) findViewById(R.id.navigation_view);

        //opcije change list name i delete list nisu dostupne na pocetnom meniju
        Menu menuNav = nvDrawer.getMenu();
        MenuItem item2 = menuNav.findItem(R.id.id_changeListName);
        item2.setEnabled(false);
        MenuItem item3 = menuNav.findItem(R.id.id_deleteList);
        item3.setEnabled(false);

        setupDrawerContent(nvDrawer);
    }

    @Override
    public void onClick(View v) {
        // If add button was clicked
        if (addNewListaButton.isPressed()) {
            // Get entered text
            String listaTextValue = listaName.getText().toString();
            //template
            if(odabrani == 0){
                Toast.makeText(getApplicationContext(), "Choose template for your list", Toast.LENGTH_LONG).show();

            }
            else {
                // Add text to the database
                SQLiteDatabase db = listaHelper.getWritableDatabase();
                ContentValues values = new ContentValues();
                values.put(Lista.ListaEntry.COL_LISTA_NAME, listaTextValue);
                values.put(Lista.ListaEntry.COL_LISTA_TYPE, odabrani);
                db.insertWithOnConflict(Lista.ListaEntry.TABLE, null, values, SQLiteDatabase.CONFLICT_REPLACE);
                db.close();

                // Display success information
                itemTemplate.getBackground().clearColorFilter();
                taskTemplate.getBackground().clearColorFilter();
                listaName.setText("");
                Toast.makeText(getApplicationContext(), "New List added!", Toast.LENGTH_LONG).show();
                // Close the database
                db.close();

                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
            }

        }
//        else if (backButton.isPressed()) {
//            // When back button is pressed
//            // Create an intent
//            Intent intent = new Intent(this, MainActivity.class);
//            // Start activity
//            startActivity(intent);
//            // Finish this activity
//            this.finish();
//
//
//        }

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


    //click on Image Buttons
    public void addListenerOnButton() {
        taskTemplate = (ImageButton) findViewById(R.id.tasktemplate);
        itemTemplate = (ImageButton) findViewById(R.id.itemtemplate);

        //klik na template za taskove
//        taskTemplate.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View arg0) {
//                Toast.makeText(AddListActivity.this,
//                        "Task button is clicked!", Toast.LENGTH_SHORT).show();
//            }
//        });

        //border za template za taskove
        taskTemplate.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN: {
                        ImageButton view = (ImageButton) v;
                        view.getBackground().setColorFilter(0x77000000, PorterDuff.Mode.SRC_ATOP);
                        v.invalidate();
                        break;
                    }
                    case MotionEvent.ACTION_UP: {
                        // Your action here on button click
                        ImageButton view = (ImageButton) v;
                        view.getBackground().setColorFilter(0x77000000, PorterDuff.Mode.SRC_ATOP);
                        v.invalidate();

                        itemTemplate.getBackground().clearColorFilter();
                        odabrani = 1;

                        Toast.makeText(AddListActivity.this,
                                "Task button is clicked!", Toast.LENGTH_SHORT).show();

                        break;
                    }
                    case MotionEvent.ACTION_CANCEL: {
                        ImageButton view = (ImageButton) v;
                        view.getBackground().clearColorFilter();
                        view.invalidate();
                        break;
                    }
                }
                return true;
            }
        });

        //klik na template za iteme
        itemTemplate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                Toast.makeText(AddListActivity.this,
                        "Item button is clicked!", Toast.LENGTH_SHORT).show();
            }
        });

        //border za template za iteme
        itemTemplate.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN: {
                        ImageButton view = (ImageButton) v;
                        view.getBackground().setColorFilter(0x77000000, PorterDuff.Mode.SRC_ATOP);
                        v.invalidate();
                        break;
                    }
                    case MotionEvent.ACTION_UP: {
                        // Your action here on button click
                        ImageButton view = (ImageButton) v;
                        view.getBackground().setColorFilter(0x77000000, PorterDuff.Mode.SRC_ATOP);
                        v.invalidate();

                        taskTemplate.getBackground().clearColorFilter();
                        odabrani = 2;

                        Toast.makeText(AddListActivity.this,
                                "Item button is clicked!", Toast.LENGTH_SHORT).show();

                        break;
                    }
                    case MotionEvent.ACTION_CANCEL: {
                        ImageButton view = (ImageButton) v;
                        view.getBackground().clearColorFilter();
                        view.invalidate();
                        break;
                    }
                }
                return true;
            }
        });

    }


}
