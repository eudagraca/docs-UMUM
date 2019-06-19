package com.example.umum;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.umum.fragments.CagFragment;
import com.example.umum.fragments.CedFragment;
import com.example.umum.fragments.EitFragment;
import com.example.umum.fragments.MyBooks;
import com.example.umum.fragments.TeoFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.mikhaellopez.circularimageview.CircularImageView;

import java.util.Objects;

public class Home extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private FirebaseAuth mAuth;
    @Nullable
    private
    FirebaseUser correntUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mAuth = FirebaseAuth.getInstance();
        correntUser = mAuth.getCurrentUser();

        getSupportFragmentManager().beginTransaction().replace(R.id.fragment, new CedFragment()).commit();

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        updateNaveHeader();

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_cag) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment, new CagFragment()).commit();

        } else if (id == R.id.nav_ced) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment, new CedFragment()).commit();


        } else if (id == R.id.nav_eit) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment, new EitFragment()).commit();


        } else if (id == R.id.nav_teolog) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment, new TeoFragment()).commit();


        } else if (id == R.id.nav_out) {

        } else if (id == R.id.nav_meus) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment, new MyBooks()).commit();

        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void updateNaveHeader() {
        NavigationView navigationView = findViewById(R.id.nav_view);
        View headerView = navigationView.getHeaderView(0);
        TextView navUserName = headerView.findViewById(R.id.user_nome);
        TextView navUserMail = headerView.findViewById(R.id.user_email);
        CircularImageView navUserPhoto = headerView.findViewById(R.id.imageUser);

        navUserName.setText(Objects.requireNonNull(correntUser).getDisplayName());
        navUserMail.setText(correntUser.getEmail());
        Glide.with(this).load(correntUser.getPhotoUrl()).into(navUserPhoto);

    }

}
