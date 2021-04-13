package ru.geekbrains.android1.notes;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.PopupMenu;
import androidx.fragment.app.Fragment;

import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

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
    private TextView textNoteTag;
    private TextView textNoteDate;
    private final Calendar today = Calendar.getInstance();
    private boolean isLandscape;

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
        View view = inflater.inflate(R.layout.fragment_note, container, false);
        setHasOptionsMenu(true);
        initPopupMenu(view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView(view);
        fillViews();
        initDatePickerDialog();
    }

    @Override
    public void onPause() {
        super.onPause();
        fillNote();
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.main_fragment_note, menu);
        menu.findItem(R.id.action_search).setVisible(false);
        menu.findItem(R.id.action_add).setVisible(false);
        menu.findItem(R.id.action_view).setVisible(false);
        menu.findItem(R.id.action_sort).setVisible(false);
        // super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (navigateOptionsMenu(id)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private boolean navigateOptionsMenu(int id) {
        switch (id) {
            case R.id.action_post:
                if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE)
                    fillNote();
                else
                    getActivity().onBackPressed();
                return true;
            case R.id.action_share:
                Toast.makeText(getContext(), "action_share", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.action_link:
                Toast.makeText(getContext(), "action_link", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.action_photo:
                Toast.makeText(getContext(), "action_photo", Toast.LENGTH_SHORT).show();
                return true;
        }
        return false;
    }

    private void initPopupMenu(View view) {
        TextView text = view.findViewById(R.id.textNoteTag);
        text.setOnClickListener(v -> {
            Activity activity = requireActivity();
            PopupMenu popupMenu = new PopupMenu(activity, v);
            activity.getMenuInflater().inflate(R.menu.popup, popupMenu.getMenu());
            Menu menu = popupMenu.getMenu();
            //        menu.add(0, 123456, 12, R.string.new_menu_item_added);
            int i = 0;
            String strTag = getNoteTag(i);
            while (strTag != null) {
                menu.add(strTag);
                strTag = getNoteTag(++i);
            }
            popupMenu.setOnMenuItemClickListener(item -> {
                String title = item.getTitle().toString();
                textNoteTag.setText(title);
                return true;
            });
            popupMenu.show();
        });
    }

    private void initView(View view) {
        editTextTitle = view.findViewById(R.id.editTextTitle);
        editTextNote = view.findViewById(R.id.editTextNote);
        textNoteTag = view.findViewById(R.id.textNoteTag);
        textNoteDate = view.findViewById(R.id.textNoteDate);
    }

    private void fillViews() {
        note = notes.getNote(noteKey);
        if (note != null) {
            editTextTitle.setText(note.getTitle());
            editTextNote.setText(note.getNoteText());
            textNoteTag.setText(getNoteTag(note.getTag()));

            today.setTime(note.getDate());
            setInitialDateTime();
        }
    }

    private void fillNote() {
        note = notes.getNote(noteKey);
        if (note != null) {
            note.setTitle(editTextTitle.getText().toString());
            note.setNoteText(editTextNote.getText().toString());
            note.setTag(setNoteTag(textNoteTag.getText().toString()));

            SimpleDateFormat dt = new SimpleDateFormat(getResources().getString(R.string.date_format));

            Date date = new Date(today.getTimeInMillis());

            note.setDate(date);

            String titleList = note.getTitle() + "\n" + dt.format(note.getDate());
            note.setTitleForList(titleList);
        }
    }

    private String getNoteTag(int tag) {
        switch (tag) {
            case 0:
                return getString(R.string.note_tag_none);
            case 1:
                return getString(R.string.note_tag1);
            case 2:
                return getString(R.string.note_tag2);
            case 3:
                return getString(R.string.note_tag3);
        }
        return null;
    }

    private int setNoteTag(String strTag) {
        if (strTag.equals(getString(R.string.note_tag_none))) return 0;
        if (strTag.equals(getString(R.string.note_tag1))) return 1;
        if (strTag.equals(getString(R.string.note_tag2))) return 2;
        if (strTag.equals(getString(R.string.note_tag3))) return 3;
        return -1;
    }

    private void initDatePickerDialog() {
        textNoteDate.setOnClickListener(v -> new DatePickerDialog(getContext(), d,
                today.get(Calendar.YEAR),
                today.get(Calendar.MONTH),
                today.get(Calendar.DAY_OF_MONTH))
                .show());
    }

    private void setTime() {
        new TimePickerDialog(getContext(), t,
                today.get(Calendar.HOUR_OF_DAY),
                today.get(Calendar.MINUTE), true)
                .show();
    }

    DatePickerDialog.OnDateSetListener d = (view, year, month, dayOfMonth) -> {
        today.set(Calendar.YEAR, year);
        today.set(Calendar.MONTH, month);
        today.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        setTime();
    };

    TimePickerDialog.OnTimeSetListener t = (view, hourOfDay, minute) -> {
        today.set(Calendar.HOUR_OF_DAY, hourOfDay);
        today.set(Calendar.MINUTE, minute);
        setInitialDateTime();
    };

    private void setInitialDateTime() {
        textNoteDate.setText(DateUtils.formatDateTime(getContext(),
                today.getTimeInMillis(),
                DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_SHOW_YEAR
                        | DateUtils.FORMAT_SHOW_TIME));
    }
}