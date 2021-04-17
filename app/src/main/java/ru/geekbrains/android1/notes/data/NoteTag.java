package ru.geekbrains.android1.notes.data;

import android.content.res.Resources;

import java.util.ArrayList;
import java.util.Arrays;

import ru.geekbrains.android1.notes.R;

public class NoteTag {
    private final ArrayList<String> tags;
    private final Resources resources;

    public NoteTag(Resources resources) {
        tags = new ArrayList<>(7);
        this.resources = resources;
    }

    public NoteTag init() {
        String[] strings = resources.getStringArray(R.array.tags);
        tags.addAll(Arrays.asList(strings));
        return this;
    }

    public int getIndexNoteTag(String strTag) {
        if (strTag.isEmpty())
            return 0;
        else
            return tags.indexOf(strTag);
    }

    public String getNoteTag(int index) {
        if (index >= tags.size() || index < 0)
            return null;
        else
            return tags.get(index);
    }

}
