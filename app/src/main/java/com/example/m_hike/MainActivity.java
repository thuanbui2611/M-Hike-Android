package com.example.m_hike;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class MainActivity extends AppCompatActivity {

    //views
    private FloatingActionButton addHikeBtn;
    private RecyclerView hikesRV;

    //db helper
    private MyDbHelper dbHelper;
    //action bar
    ActionBar actionBar;
    //sort options
    String orderByNewest = Constants.C_CREATED_AT + " DESC";
    String orderByOldest = Constants.C_CREATED_AT + " ASC";
    String orderByNewestUpdated = Constants.C_LAST_UPDATED + " DESC";
    String orderByOldestUpdated = Constants.C_LAST_UPDATED + " ASC";
    String orderByNameAsc = Constants.C_NAME + " ASC";
    String orderByNameDesc = Constants.C_NAME + " DESC";

    String currentSortOption = orderByNewest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        actionBar = getSupportActionBar();
        //actionBar.setTitle("All Hikes");
        //init views
        addHikeBtn = findViewById(R.id.addHikeBtn);
        hikesRV = findViewById(R.id.hikesRV);

        //init db
        dbHelper = new MyDbHelper(this);

        //load all hikes
        loadHikes(orderByNewest);
        //click to start add hike activity
        addHikeBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(MainActivity.this, AddUpdateHikesActivity.class);
                intent.putExtra("isEditMode", false);
                startActivity(intent);
            }
        });
    }

    private void loadHikes(String orderBy){
        currentSortOption = orderBy;
        HikeAdapter hikeAdapter = new HikeAdapter(MainActivity.this,
                dbHelper.getAllHikes(orderBy));
        hikesRV.setAdapter(hikeAdapter);

        actionBar.setSubtitle("Total: "+dbHelper.getHikesCount());
    }

    private void searchHike(String query){
        HikeAdapter hikeAdapter = new HikeAdapter(MainActivity.this,
                dbHelper.searchHike(query));
        hikesRV.setAdapter(hikeAdapter);
    }

    @Override
    protected void onResume(){
        super.onResume();
        loadHikes(currentSortOption); //refresh list hikes
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
                searchHike(query);
                return true;
            }
            @Override
            public boolean onQueryTextChange(String newText) {
                //search as user type
                searchHike(newText);
                return true;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item){
        //handle menu items
        int id = item.getItemId();
        if(id==R.id.action_sort)
        {
            sortOptionDialog();
        }
        else if(id==R.id.action_deleteAllHikes){
            deleteAllOptionDialog();

        }
        return super.onOptionsItemSelected(item);
    }

    private void sortOptionDialog() {
        String[] options = {"A-Z", "Z-A", "Newest", "Oldest", "Latest updated", "Oldest updated"};
        AlertDialog.Builder builder =  new AlertDialog.Builder(this);
        builder.setTitle("Sort By")
                .setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if(i == 0){
                            //A-Z
                            loadHikes(orderByNameAsc);
                        }
                        else if(i == 1){
                            //Z-A
                            loadHikes(orderByNameDesc);
                        }
                        else if(i == 2){
                            //Newest
                            loadHikes(orderByNewest);
                        }
                        else if(i == 3){
                            //Oldest
                            loadHikes(orderByOldest);
                        }
                        else if(i == 4){
                            //Latest updated
                            loadHikes(orderByNewestUpdated);
                        }
                        else if(i == 5){
                            //Oldest updated
                            loadHikes(orderByOldestUpdated);
                        }
                    }
                })
                .create().show();
    }

    private void deleteAllOptionDialog() {
        String[] options = {"OK, delete all hikes", "Cancel"};
        AlertDialog.Builder builder =  new AlertDialog.Builder(this);
        builder.setTitle("Confirm Delete")
                .setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if(i == 0){
                            dbHelper.deleteAllHikes();
                            Toast.makeText(MainActivity.this, "Delete All Hikes Successfully!", Toast.LENGTH_SHORT).show();
                            loadHikes(currentSortOption);
                        }
                        else if(i == 1){

                        }

                    }
                })
                .create().show();
    }
}