package ru.geekbrains.android1.notes;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;

import java.util.List;

public class MainActivity extends AppCompatActivity implements PublisherGetter, NotesGetter {
    private final static String KEY_NOTES = "Notes";
    private final Publisher publisher = new Publisher();
    private Notes notes = new Notes();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (savedInstanceState == null)
            initFragments();
    }

    private void initFragments() {
        //     MainFragment mainFragment = new MainFragment();
        ListNotesFragment listFragment = new ListNotesFragment();
        publisher.subscribe(listFragment);
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        fragmentTransaction.replace(R.id.fragment_list, listFragment);
        fragmentTransaction.commit();
    }

    private Fragment getVisibleFragment(FragmentManager fragmentManager) {
        List<Fragment> fragments = fragmentManager.getFragments();
        int countFragments = fragments.size();
        for (int i = countFragments - 1; i >= 0; i--) {
            Fragment fragment = fragments.get(i);
            if (fragment.isVisible())
                return fragment;
        }
        return null;
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable(KEY_NOTES, notes);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        notes = (Notes) savedInstanceState.getSerializable(KEY_NOTES);
        initFragments();
    }

    @Override
    public Publisher getPublisher() {
        return publisher;
    }

    @Override
    public Notes getNotes() {
        return notes;
    }
}