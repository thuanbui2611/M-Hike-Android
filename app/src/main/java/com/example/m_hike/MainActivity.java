package com.example.m_hike;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class MainActivity extends AppCompatActivity {

    //views
    private FloatingActionButton addContactBtn;
    private RecyclerView contactsRV;
    //db helper
    private MyDbHelper dbHelper;
    //action bar
    ActionBar actionBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        actionBar = getSupportActionBar();
        //init views
        addContactBtn = findViewById(R.id.addContactBtn);
        contactsRV = findViewById(R.id.contactsRV);

        //init db
        dbHelper = new MyDbHelper(this);

        //load all contacts
        loadContacts();
        //click to start add activity
        addContactBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(MainActivity.this, AddUpdateContactsActivity.class);
                intent.putExtra("isEditMode", false);
                startActivity(intent);
            }
        });
    }

    private void loadContacts(){
        ContactAdapter contactAdapter = new ContactAdapter(MainActivity.this,
                dbHelper.getAllContacts());
        contactsRV.setAdapter(contactAdapter);
    }

    private void searchContact(String query){
        ContactAdapter contactAdapter = new ContactAdapter(MainActivity.this,
                dbHelper.searchContact(query));
        contactsRV.setAdapter(contactAdapter);
    }

    @Override
    protected void onResume(){
        super.onResume();
        loadContacts();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        //inflate menu
        getMenuInflater().inflate(R.menu.menu_main, menu);
        //search view
        MenuItem item = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) item.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                //search when search button on keyboard clicked
                searchContact(query);
                return true;
            }
            @Override
            public boolean onQueryTextChange(String newText) {
                //search as user type
                searchContact(newText);
                return true;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item){
        //handle menu items
        int id = item.getItemId();
        if(id==R.id.action_deleteAllContacts){
            dbHelper.deleteAllContacts();
            Toast.makeText(this, "Delete All Contacts Successfully!", Toast.LENGTH_SHORT).show();
            loadContacts();
        }
        return super.onOptionsItemSelected(item);
    }
}