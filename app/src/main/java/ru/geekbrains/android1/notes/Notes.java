package ru.geekbrains.android1.notes;

import java.io.Serializable;
import java.util.TreeMap;

public class Notes implements Serializable {
    private TreeMap<Integer, Note> map;
    private int currentKey;

    public Notes() {
        map = new TreeMap<>();
    }

    public int addNote(String title) {
        int key = 1;
        if (!map.isEmpty()) key = map.lastKey() + 1;
        Note note = new Note();
        note.setTitle(title);
        map.put(key, note);
        currentKey = key;
        return key;
    }

    public Note getNote() {
        if (map.isEmpty()) return null;
        return map.get(currentKey);
    }

    public Note getNote(int key) {
        if (map.isEmpty()) return null;
        return map.get(key);
    }

    public int getLastKey() {
        if (map.isEmpty()) return 0;
        return map.lastKey();
    }

}
