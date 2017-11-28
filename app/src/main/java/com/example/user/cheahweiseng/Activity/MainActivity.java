package com.example.user.cheahweiseng.Activity;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;

import com.example.user.cheahweiseng.Activity.Chat.ChatListActivity;
import com.example.user.cheahweiseng.Activity.Lodge.HistoryUploadActivity;
import com.example.user.cheahweiseng.Activity.Lodge.LodgeDetailActivity;
import com.example.user.cheahweiseng.Activity.Lodge.UploadLodgeActivity;
import com.example.user.cheahweiseng.Activity.LodgeProvider.EditProfileActivity;
import com.example.user.cheahweiseng.Activity.LodgeProvider.LodgeProviderActivity;
import com.example.user.cheahweiseng.Adapter.LodgeAdapter;
import com.example.user.cheahweiseng.Class.Lodge;
import com.example.user.cheahweiseng.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private FirebaseAuth firebaseAuth;
    private DatabaseReference myRefLodge;
    private TextView profileName;
    private TextView profileEmail;
    private ImageView profileImage;
    private RecyclerView recyclerView;
    private DatabaseReference mUserDatabase;
    private ArrayList<String> prodKey = new ArrayList<String>();

    private MenuItem searchItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        System.out.println("LeeZX : " + new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
        firebaseAuth = FirebaseAuth.getInstance();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        recyclerView = (RecyclerView) findViewById(R.id.recycle_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        BackgroundWorker worker = new BackgroundWorker(MainActivity.this, recyclerView);
        worker.execute();

        searchItem = (MenuItem) findViewById(R.id.search);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        View header = navigationView.getHeaderView(0);

        profileName = (TextView) header.findViewById(R.id.profileName);
        profileEmail = (TextView) header.findViewById(R.id.profileEmail);
        profileImage = (ImageView) header.findViewById(R.id.profileImage);

        if (firebaseAuth.getCurrentUser() != null) {
            mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Lodge Provider").child(firebaseAuth.getCurrentUser().getUid());

            mUserDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    final String name = dataSnapshot.child("firstName").getValue().toString() + " " + dataSnapshot.child("lastName").getValue().toString();
                    final String email = dataSnapshot.child("email").getValue().toString();

                    profileName.setText(name);
                    profileEmail.setText(email);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }

        myRefLodge = FirebaseDatabase.getInstance().getReference().child("Lodge");

        recyclerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, LodgeDetailActivity.class);
                startActivity(intent);

            }

        });

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.uploadLodge);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, UploadLodgeActivity.class);
                startActivity(intent);
            }
        });

        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, EditProfileActivity.class));
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser currentUser = firebaseAuth.getCurrentUser();

        if (currentUser == null) {
            sendToStart();
        } else {
            mUserDatabase.child("online").setValue("true");
        }
    }

    private void sendToStart() {
        Intent startIntent = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(startIntent);
        finish();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);

        // Associate searchable configuration with the SearchView
        SearchManager searchManager =
                (SearchManager) getSystemService(SEARCH_SERVICE);
        SearchView searchView =
                (SearchView) menu.findItem(R.id.search).getActionView();
        searchView.setSearchableInfo(
                searchManager.getSearchableInfo(getComponentName()));
        searchView.setFocusable(true);
//        searchView.setQueryHint("Search Lodge");
        searchView.setIconified(false);
        searchView.requestFocusFromTouch();

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.editProfile:
                startActivity(new Intent(this, EditProfileActivity.class));
                return true;
            case R.id.logOut:
                firebaseAuth.signOut();
                startActivity(new Intent(this, LoginActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_lodge_provider) {
            startActivity(new Intent(MainActivity.this, LodgeProviderActivity.class));
        } else if (id == R.id.nav_chat) {
            startActivity(new Intent(MainActivity.this, ChatListActivity.class));
        } else if (id == R.id.nav_history_upload) {
            startActivity(new Intent(MainActivity.this, HistoryUploadActivity.class));
        } else if (id == R.id.nav_about) {
        } else if (id == R.id.nav_logout) {
            firebaseAuth.signOut();
            startActivity(new Intent(this, LoginActivity.class));
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onStop() {
        super.onStop();
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();

        if (currentUser != null) {
            mUserDatabase.child("online").setValue(ServerValue.TIMESTAMP);
        }
    }

    class BackgroundWorker extends AsyncTask<String, Void, String> {

        private Context ctx;
        RecyclerView lodgeView;
        LodgeAdapter lodgeDetail;
        ArrayList<Lodge> lodgeList = new ArrayList<>();

        public BackgroundWorker(Context ctx, RecyclerView rv) {
            this.ctx = ctx;
            this.lodgeView = rv;
        }

        @Override
        protected String doInBackground(String... strings) {
            String all_lodge_url = "https://2f766948.ngrok.io/LodgeServiceSystem/database/lodge/display_all_lodge.php";

            try {
                URL url = new URL(all_lodge_url);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setDoOutput(true);

                InputStream inputStream = connection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "iso-8859-1"));

                String result = "";
                String line = "";

                while ((line = bufferedReader.readLine()) != null) {
                    result += line;
                }

                bufferedReader.close();
                inputStream.close();
                connection.disconnect();

                return result;

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected void onPostExecute(final String s) {
            try {
                JSONArray jsonArray = new JSONArray(s);
                JSONObject object;
                for (int i = 0; i < jsonArray.length(); i++) {
                    object = jsonArray.getJSONObject(i);
                    String id = object.getString("lodgeID");
                    String title = object.getString("lodgeTitle");
                    String description = object.getString("lodgeDescription");
                    String price = object.getString("lodgePrice");
                    String image = object.getString("lodgeImage");
                    String provider_id = object.getString("lodgeProviderID");
                    lodgeList.add(new Lodge(id, title, description, price, image, provider_id));
                }
                lodgeDetail = new LodgeAdapter(ctx, lodgeList, lodgeView);
                lodgeView.setAdapter(lodgeDetail);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }
    }
}
