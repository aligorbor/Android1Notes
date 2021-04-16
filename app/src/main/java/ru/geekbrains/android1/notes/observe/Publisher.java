package ru.geekbrains.android1.notes.observe;

import java.util.ArrayList;
import java.util.List;

import ru.geekbrains.android1.notes.data.NoteData;

public class Publisher {
    private final List<Observer> observers;   // Все обозреватели

    public Publisher() {
        observers = new ArrayList<>();
    }

    // Подписать
    public void subscribe(Observer observer) {
        observers.add(observer);
    }

    // Отписать
    public void unsubscribe(Observer observer) {
        observers.remove(observer);
    }

    // Разослать событие
    public void notifySingle(NoteData noteData) {
        for (Observer observer : observers) {
            observer.updateNoteData(noteData);
            unsubscribe(observer);
        }
    }
}
