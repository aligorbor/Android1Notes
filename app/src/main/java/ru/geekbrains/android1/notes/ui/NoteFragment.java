package ru.geekbrains.android1.notes.ui;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
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
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import ru.geekbrains.android1.notes.MainActivity;
import ru.geekbrains.android1.notes.R;
import ru.geekbrains.android1.notes.data.NoteData;
import ru.geekbrains.android1.notes.observe.Publisher;


public class NoteFragment extends Fragment {
    private static final String ARG_NOTE_DATA = "Param_NoteData";
    private NoteData noteData;
    private Publisher publisher;

    private EditText editTextTitle;
    private EditText editTextNote;
    private TextView textNoteTag;
    private TextView textNoteDate;
    private CheckBox checkNoteLike;
    private final Calendar today = Calendar.getInstance();

    public NoteFragment() {
        // Required empty public constructor
    }

    public static NoteFragment newInstance(NoteData noteData) {
        NoteFragment fragment = new NoteFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_NOTE_DATA, noteData);
        fragment.setArguments(args);
        return fragment;
    }

    public static NoteFragment newInstance() {
        return new NoteFragment();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        MainActivity activity = (MainActivity) context;
        publisher = activity.getPublisher();
    }

    @Override
    public void onDetach() {
        publisher = null;
        super.onDetach();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            noteData = getArguments().getParcelable(ARG_NOTE_DATA);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_note, container, false);
        initView(view);
        setHasOptionsMenu(true);
        initPopupMenu(view);
        if (noteData != null) {
            fillViews();
        }
        initDatePickerDialog();
        return view;
    }

    @Override
    public void onStop() {
        super.onStop();
        noteData = collectNoteData();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        publisher.notifySingle(noteData);
    }

    @Override
    public void onPause() {
        super.onPause();
        //    fillNote();
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.main_fragment_note, menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (navigateOptionsMenu(id)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressLint("NonConstantResourceId")
    private boolean navigateOptionsMenu(int id) {
        switch (id) {
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
        checkNoteLike = view.findViewById(R.id.checkNoteLike);
    }

    private void fillViews() {
        editTextTitle.setText(noteData.getTitle());
        editTextNote.setText(noteData.getNoteText());
        textNoteTag.setText(getNoteTag(noteData.getTag()));
        checkNoteLike.setChecked(noteData.isLike());
        today.setTime(noteData.getDate());
        setInitialDateTime();
    }

    private NoteData collectNoteData() {
        String textTitle = editTextTitle.getText().toString();
        String textNote = editTextNote.getText().toString();
        int tag = setNoteTag(textNoteTag.getText().toString());
        Date date = new Date(today.getTimeInMillis());
        boolean like = checkNoteLike.isChecked();
        return new NoteData(textTitle, date, tag, like, textNote);
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
        return 0;
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
        //  SimpleDateFormat dt = new SimpleDateFormat(getResources().getString(R.string.date_format), Locale.getDefault());
        textNoteDate.setText(DateUtils.formatDateTime(getContext(),
                today.getTimeInMillis(),
                DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_SHOW_YEAR
                        | DateUtils.FORMAT_SHOW_TIME));
    }

}