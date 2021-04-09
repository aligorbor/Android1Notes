package ru.geekbrains.android1.notes;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Paint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import java.util.List;

public class ListNotesFragment extends Fragment implements Observer {
    private Notes notes;
    private LinearLayout layoutView;
    private boolean isLandscape;

    public ListNotesFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        notes = ((NotesGetter) context).getNotes(); // получим класс заметок
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_list_notes, container, false);
        layoutView = view.findViewById(R.id.linearLayout);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //   initList();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        isLandscape = getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE;
        if (isLandscape) {
            showNoteLand(notes.getLastKey());
        }
        initList();
    }

    private void initList() {
        int keyNote = notes.getLastKey();

        while (keyNote > 0) {
            Note note = notes.getNote(keyNote);
            initListElement(note, keyNote);
            keyNote--;
        }
    }

    private void initListElement(Note note, int keyNotef) {
        if (note != null) {
            TextView tv = new TextView(getContext());
            tv.setText(note.getTitleForList());
            tv.setTextSize(getResources().getDimension(R.dimen.text_sizeL));
            tv.setTag(keyNotef);
            tv.setOnClickListener(v -> showNote(keyNotef));
            layoutView.addView(tv);
        }
    }

    @Override
    public void updateNotes(String text) {
        int keyNote = notes.addNote(text);
        //  Note note = notes.getNote();
        showNote(keyNote);
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

    private void showNote(int keyNote) {
        if (isLandscape) {
            showNoteLand(keyNote);
        } else {
            showNotePort(keyNote);
        }
    }

    private void showNotePort(int keyNote) {
        // Создаем новый фрагмент с текущей позицией для вывода note
        NoteFragment noteFragment = NoteFragment.newInstance(keyNote);

        // Выполняем транзакцию по замене фрагмента
        FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        Fragment fragmentToRemove = getVisibleFragment(fragmentManager);
        if (fragmentToRemove != null)
            fragmentTransaction.remove(fragmentToRemove);

        fragmentTransaction.add(R.id.fragment_list, noteFragment);  // замена фрагмента
        fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    private void showNoteLand(int keyNote) {
        // Создаем новый фрагмент с текущей позицией для вывода note
        NoteFragment noteFragment = NoteFragment.newInstance(keyNote);

        // Выполняем транзакцию по замене фрагмента
        FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_note, noteFragment);  // замена фрагмента
        fragmentTransaction.commit();
    }

}