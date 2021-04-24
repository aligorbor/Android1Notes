package ru.geekbrains.android1.notes.ui;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.PopupMenu;
import androidx.fragment.app.DialogFragment;

import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import ru.geekbrains.android1.notes.MainActivity;
import ru.geekbrains.android1.notes.R;
import ru.geekbrains.android1.notes.data.NoteData;
import ru.geekbrains.android1.notes.data.NoteTag;
import ru.geekbrains.android1.notes.observe.ObserverScroll;
import ru.geekbrains.android1.notes.observe.Publisher;


public class NoteFragmentDialog extends DialogFragment {
    private static final String ARG_NOTE_DATA = "Param_NoteData";
    private static final String ARG_POSITION = "Param_Position";
    private NoteData noteData;
    private Publisher publisher;

    private EditText editTextTitle;
    private EditText editTextNote;
    private TextView textNoteTag;
    private TextView textNoteDate;
    private CheckBox checkNoteLike;
    private final Calendar today = Calendar.getInstance();
    private int position;
    private NoteTag noteTag;

    private ObserverScroll observerScroll;

    public NoteFragmentDialog() {
        // Required empty public constructor
    }

    public static NoteFragmentDialog newInstance(int position, NoteData noteData) {
        NoteFragmentDialog fragment = new NoteFragmentDialog();
        Bundle args = new Bundle();
        args.putParcelable(ARG_NOTE_DATA, noteData);
        args.putInt(ARG_POSITION, position);
        fragment.setArguments(args);
        return fragment;
    }

    public static NoteFragmentDialog newInstance() {
        NoteFragmentDialog fragment = new NoteFragmentDialog();
        Bundle args = new Bundle();
        args.putInt(ARG_POSITION, -1);
        fragment.setArguments(args);
        return fragment;
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
            position = getArguments().getInt(ARG_POSITION);
        }
        observerScroll = this::saveToNoteData;
        publisher.subscribeScroll(observerScroll);
        noteTag = new NoteTag(getResources()).init();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_note, container, false);
        initView(view);
        //     setHasOptionsMenu(true);
        initPopupMenu(view);
        if (noteData != null) {
            fillViews();
        }
        initDatePickerDialog();

        setCancelable(false); // Запретить выход из диалога, ничего не выбрав
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        //https://stackoverflow.com/questions/7189948/full-screen-dialogfragment-in-android
        Dialog dialog = getDialog();
        if (dialog != null) {
            int width = ViewGroup.LayoutParams.MATCH_PARENT;
            int height = ViewGroup.LayoutParams.MATCH_PARENT;
            dialog.getWindow().setLayout(width, height);
        }
    }

    @Override
    public void onDestroy() {
        publisher.unsubscribeScroll(observerScroll);
        super.onDestroy();
    }

    @Override
    public void onPause() {
        saveToNoteData();
        super.onPause();
    }

    private void saveToNoteData() {
        noteData = collectNoteData();
        if (position >= 0)
            publisher.notifySingle(position, noteData);
        else
            publisher.notifyAdd(noteData);
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
            String strTag = noteTag.getNoteTag(i);
            while (strTag != null) {
                menu.add(strTag);
                strTag = noteTag.getNoteTag(++i);
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
        FloatingActionButton fabSave = view.findViewById(R.id.fabSave);
        fabSave.setOnClickListener(v -> {
            // Закрываем диалог
            dismiss();
        });
    }

    private void fillViews() {
        editTextTitle.setText(noteData.getTitle());
        editTextNote.setText(noteData.getNoteText());
        textNoteTag.setText(noteTag.getNoteTag(noteData.getTag()));
        checkNoteLike.setChecked(noteData.isLike());
        today.setTime(noteData.getDate());
        setInitialDateTime();
    }

    private NoteData collectNoteData() {
        String textTitle = editTextTitle.getText().toString();
        String textNote = editTextNote.getText().toString();
        int tag = noteTag.getIndexNoteTag(textNoteTag.getText().toString());
        Date date = new Date(today.getTimeInMillis());
        boolean like = checkNoteLike.isChecked();

        NoteData answer = new NoteData(textTitle, date, tag, like, textNote);
        if (noteData != null)
            answer.setId(noteData.getId());
        return answer;
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