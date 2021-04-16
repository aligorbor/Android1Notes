package ru.geekbrains.android1.notes;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentManager;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;

import ru.geekbrains.android1.notes.observe.Publisher;
import ru.geekbrains.android1.notes.ui.ListNotesFragment;
import ru.geekbrains.android1.notes.ui.SettingsFragment;


public class MainActivity extends AppCompatActivity {
    private final static String KEY_NOTES = "Notes";

    private Navigation navigation;
    private final Publisher publisher = new Publisher();
    //  private NotesSourceImpl data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        navigation = new Navigation(getSupportFragmentManager());
        //     if(savedInstanceState ==null)
        //           data = new NotesSourceImpl(getResources()).init();
        initView();

        initFragments();
    }

    private void initView() {
        Toolbar toolbar = initToolbar();
        initDrawer(toolbar);
    }

    private Toolbar initToolbar() {
        Toolbar toolbar = findViewById(R.id.activity_toolbar);
        setSupportActionBar(toolbar);
        return toolbar;
    }

    private void initDrawer(Toolbar toolbar) {
        final DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();
            if (navigateNavigationMenu(id)) {
                drawer.closeDrawer(GravityCompat.START);
                return true;
            }
            return false;
        });

        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.addOnBackStackChangedListener(() -> {
            if (fragmentManager.getBackStackEntryCount() > 0) {
                // show back button
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                getSupportActionBar().setHomeButtonEnabled(true);
                toolbar.setNavigationOnClickListener(v -> onBackPressed());
            } else {
                //show hamburger
                getSupportActionBar().setDisplayHomeAsUpEnabled(false);
                getSupportActionBar().setHomeButtonEnabled(false);
                toggle.syncState();
                toolbar.setNavigationOnClickListener(v -> drawer.openDrawer(GravityCompat.START));
            }
        });
    }

    @SuppressLint("NonConstantResourceId")
    private boolean navigateNavigationMenu(int id) {
        switch (id) {
            case R.id.action_settings:
                getNavigation().addFragment(SettingsFragment.newInstance(), true);
                return true;
            case R.id.action_tag:
                Toast.makeText(MainActivity.this, "action_tag", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.action_favorite:
                Toast.makeText(MainActivity.this, "action_favorite", Toast.LENGTH_SHORT).show();
                return true;
        }
        return false;
    }

    private void initFragments() {
        getNavigation().addFragment(ListNotesFragment.newInstance(), false);
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        //    outState.putSerializable(KEY_NOTES, data);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        //    data = (NotesSourceImpl) savedInstanceState.getSerializable(KEY_NOTES);
        //  initFragments();
    }

    public Publisher getPublisher() {
        return publisher;
    }

    public Navigation getNavigation() {
        return navigation;
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        //https://ru.stackoverflow.com/questions/250093
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if (v instanceof EditText) {
                v.clearFocus();
                InputMethodManager imm = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
            }
        }
        return super.dispatchTouchEvent(ev);
    }
}