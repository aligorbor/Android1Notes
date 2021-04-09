package ru.geekbrains.android1.notes;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


public class NoteFragment extends Fragment {
    private static final String ARG_NOTE_KEY = "noteKey";
    private int noteKey;
    private Notes notes;
    private Note note;
    private EditText editTextTitle;
    private EditText editTextNote;
    private DatePicker mDatePicker;

    public NoteFragment() {
        // Required empty public constructor
    }

    public static NoteFragment newInstance(int noteKey1) {
        NoteFragment fragment = new NoteFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_NOTE_KEY, noteKey1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        notes = ((NotesGetter) context).getNotes(); // получим класс заметок
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            noteKey = getArguments().getInt(ARG_NOTE_KEY);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_note, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        editTextTitle = view.findViewById(R.id.editTextTitle);
        editTextNote = view.findViewById(R.id.editTextNote);
        mDatePicker = view.findViewById(R.id.datePicker);

        fillViews();
    }

    private void fillViews() {
        note = notes.getNote(noteKey);
        if (note != null) {
            editTextTitle.setText(note.getTitle());
            editTextNote.setText(note.getNoteText());

            Calendar today = Calendar.getInstance();
            today.setTime(note.getDate());
            mDatePicker.init(today.get(Calendar.YEAR), today.get(Calendar.MONTH),
                    today.get(Calendar.DAY_OF_MONTH), null);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        fillNote();
    }

    private void fillNote() {
        note = notes.getNote(noteKey);
        if (note != null) {
            note.setTitle(editTextTitle.getText().toString());
            SimpleDateFormat dt = new SimpleDateFormat(getResources().getString(R.string.date_format));
            Calendar today = Calendar.getInstance();
            today.set(mDatePicker.getYear(), mDatePicker.getMonth(), mDatePicker.getDayOfMonth());
            Date date = new Date(today.getTimeInMillis());

            note.setDate(date);
            note.setNoteText(editTextNote.getText().toString());
            String titleList = note.getTitle() + "\n" + dt.format(note.getDate());
            note.setTitleForList(titleList);
        }
    }
}