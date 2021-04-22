package ru.geekbrains.android1.notes.data;

import android.content.res.Resources;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.concurrent.CopyOnWriteArrayList;

import ru.geekbrains.android1.notes.R;

public class NotesSourceImpl implements NotesSource {
    private final ArrayList<NoteData> dataSource;
    private final Resources resources;

    public NotesSourceImpl(Resources resources) {
        dataSource = new ArrayList<>(7);
        this.resources = resources;
    }

    public NotesSourceImpl init(NotesSourceResponse notesSourceResponse) {
        String[] titles = resources.getStringArray(R.array.titles);
        for (String title : titles) {
            dataSource.add(new NoteData(title, Calendar.getInstance().getTime(), 0, false, title + " text of the note"));
        }
        if (notesSourceResponse != null) {
            notesSourceResponse.initialized(this);
        }
        return this;
    }

    @Override
    public NoteData getNoteData(int position) {
        return dataSource.get(position);
    }

    @Override
    public int size() {
        return dataSource.size();
    }

    @Override
    public void deleteNoteData(int position) {
        dataSource.remove(position);
    }

    @Override
    public void updateNoteData(int position, NoteData noteData) {
        dataSource.set(position, noteData);
    }

    @Override
    public void addNoteData(NoteData noteData) {
        dataSource.add(noteData);
    }

    @Override
    public void clearNoteData() {
        dataSource.clear();
    }
}
