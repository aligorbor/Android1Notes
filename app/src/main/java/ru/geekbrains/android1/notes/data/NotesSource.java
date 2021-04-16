package ru.geekbrains.android1.notes.data;

public interface NotesSource {
    NoteData getNoteData(int position);
    int size();
    void deleteNoteData(int position);
    void updateNoteData(int position, NoteData noteData);
    void addNoteData(NoteData noteData);
    void clearNoteData();
}
