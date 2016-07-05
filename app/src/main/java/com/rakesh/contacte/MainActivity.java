package com.rakesh.contacte;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    public Toolbar toolbar;
    ArrayList<Contact> contact_data = new ArrayList<>();
    ArrayList<Contact> search_data = new ArrayList<>();
    List<Contact> contacts;
    Intent j = null;
    DataBaseHandler db;
    SearchView searchView;
    ListAdapter adapter, searchAdapter;
    ListView contactList;
    Thread searchThread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);

        j = new Intent(this, AddEdit.class);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(j);
                finish();
            }
        });
        db = new DataBaseHandler(this);
        contacts = db.getAllContacts();
        for (Contact cn : contacts) {
            contact_data.add(cn);
        }
        contactList = (ListView) findViewById(R.id.contactList);
        adapter = new ContactAdapter(this, R.layout.custom_row, contact_data);
        contactList.setAdapter(adapter);

        contactList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                j.putExtra("id", contacts.get(i).getID());
                j.putExtra("name", contacts.get(i).getName());
                j.putExtra("number", contacts.get(i).getNumber());
                j.putExtra("image", contacts.get(i).getImage());
                startActivity(j);
                finish();
            }
        });
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if(search_data!=null) {
                searchAdapter = new ContactAdapter(MainActivity.this, R.layout.custom_row, search_data);
                contactList.setAdapter(searchAdapter);
            } else {
                contactList.setAdapter(null);
            }
            searchThread.interrupt();
        }
    };

    private void displayResults(final String query) {
        Runnable show = new Runnable() {
            @Override
            public void run() {
                search_data = null;
                for (Contact cnt : contacts) {
                    if (cnt.getName().toLowerCase().contains(query.toLowerCase())||cnt.getNumber().contains(query.toLowerCase())) {
                        search_data.add(cnt);
                    }
                }
                handler.sendEmptyMessage(0);
            }
        };
        searchThread = new Thread(show);
        searchThread.start();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        searchView = (SearchView) searchItem.getActionView();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                displayResults(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (!newText.isEmpty()){
                    displayResults(newText);
                } else {
                    contactList.setAdapter(adapter);
                }
                return false;
            }
        });

        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                contactList.setAdapter(adapter);
                searchView.setQuery("",true);
                return false;
            }
        });

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getOrder()==200) {
            System.exit(0);
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onStop(){
        db.close();
        super.onStop();
    }
}
