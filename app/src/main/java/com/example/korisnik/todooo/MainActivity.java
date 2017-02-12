package com.example.korisnik.todooo;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
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
import com.example.korisnik.todooo.db.ListaHelper;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private NavigationView nvDrawer;

    private static final String TAG = "MainActivity";
    private ListaHelper listaHelper;
    private ListView listeView;
    private ArrayAdapter<String> mAdapter;
    //private Button btnDodaj;
    private FloatingActionButton btnDodaj;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listaHelper = new ListaHelper(this);
        listeView = (ListView) findViewById(R.id.lista_todo);
        btnDodaj 	= (FloatingActionButton)findViewById(R.id.btnAddLista);

        btnDodaj.setOnClickListener(this);
        //klik na listu otvara njene taskove
        listeView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String naziv_liste = ((TextView)(view.findViewById(R.id.lista_name))).getText().toString();

                //Toast.makeText(getApplicationContext(), "Kliknuta je lista "+test, Toast.LENGTH_LONG).show();
                Intent intent = new Intent(MainActivity.this, ListTask.class);
                intent.putExtra("nazivListe", naziv_liste);
                startActivity(intent);
            }
        });

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

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu){
//        getMenuInflater().inflate(R.menu.main_menu, menu);
//        return super.onCreateOptionsMenu(menu);
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item){
//        switch (item.getItemId()){
//            case R.id.action_add_lista:
//                final EditText listaName = new EditText(this);
//                AlertDialog dialog = new AlertDialog.Builder(this)
//                        .setTitle("New List")
//                        .setMessage("Add a new list")
//                        .setView(listaName)
//                        .setPositiveButton("Add", new DialogInterface.OnClickListener(){
//                            @Override
//                            public void onClick(DialogInterface dialog, int wh){
//                                String lista = String.valueOf(listaName.getText());
//                                SQLiteDatabase db = listaHelper.getWritableDatabase();
//                                ContentValues values = new ContentValues();
//                                values.put(Lista.ListaEntry.COL_LISTA_NAME, lista);
//                                db.insertWithOnConflict(Lista.ListaEntry.TABLE, null, values, SQLiteDatabase.CONFLICT_REPLACE);
//                                db.close();
//                                updateUI();
//                            }
//                        })
//
//                        .setNegativeButton("Cancel", null)
//                        .create();
//                dialog.show();
//                return true;
//            default:
//                return super.onOptionsItemSelected(item);
//        }
//    }

    private void updateUI(){
        ArrayList<String> imenaListi = new ArrayList<>();
        SQLiteDatabase db = listaHelper.getReadableDatabase();
        // Query the database
        Cursor cursor = db.query(Lista.ListaEntry.TABLE, new String[] {Lista.ListaEntry._ID, Lista.ListaEntry.COL_LISTA_NAME}, null, null, null, null, null);

        // Iterate the results
        while (cursor.moveToNext()) {
            int indeksi = cursor.getColumnIndex(Lista.ListaEntry.COL_LISTA_NAME);
            imenaListi.add(cursor.getString(indeksi));

        }
        if(mAdapter==null){
            mAdapter = new ArrayAdapter<>(this,R.layout.lista_prikaz, R.id.lista_name, imenaListi);
            listeView.setAdapter(mAdapter);
        }
        else {
            mAdapter.clear();
            mAdapter.addAll(imenaListi);
            mAdapter.notifyDataSetChanged();
        }
        cursor.close();
        db.close();

    }

    //delete Liste
//    public void deleteLista(View view){
//        View parent = (View) view.getParent();
//        TextView listaTextView = (TextView) parent.findViewById(R.id.lista_name);
//        String lista = String.valueOf(listaTextView.getText());
//        SQLiteDatabase db = listaHelper.getWritableDatabase();
//        db.delete(Lista.ListaEntry.TABLE, Lista.ListaEntry.COL_LISTA_NAME + " = ?", new String[] {lista});
//        db.close();
//        updateUI();
//
//    }

    @Override
    public void onClick(View v) {
        // If add button was clicked
        if (btnDodaj.isPressed()) {
            Intent intent = new Intent(this, AddListActivity.class);
            // Start activity
            startActivity(intent);
            // Finish this activity
            this.finish();
        }

    }



}
