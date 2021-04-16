package ru.geekbrains.android1.notes.observe;

import ru.geekbrains.android1.notes.data.NoteData;

public interface Observer {
    void updateNoteData(NoteData noteData);
}
