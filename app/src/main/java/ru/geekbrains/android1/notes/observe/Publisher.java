package ru.geekbrains.android1.notes.observe;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import ru.geekbrains.android1.notes.data.NoteData;

public class Publisher {
    private final List<Observer> observers;   // Все обозреватели
    private final List<ObserverScroll> observerScroll;   // Все обозреватели
    private final List<ObserverAdd> observerAdd;

    public Publisher() {
        observers = new ArrayList<>();
        observerScroll = new ArrayList<>();
        observerAdd = new ArrayList<>();
    }

    // Подписать
    public  void subscribe(Observer observer) {
        observers.add(observer);
    }

    // Отписать
    public  void unsubscribe(Observer observer) {
        observers.remove(observer);
    }

    // Разослать событие
    public void notifySingle(int position, NoteData noteData) {
        for (Observer observer : observers) {
            observer.updateNoteData(position, noteData);
    //        unsubscribe(observer);
        }
    }
    // Подписать
    public  void subscribeScroll(ObserverScroll observer) {
        observerScroll.add(observer);
    }

    // Отписать
    public  void unsubscribeScroll(ObserverScroll observer) {
        observerScroll.remove(observer);
    }

    // Разослать событие
    public void notifyScroll() {
        for (ObserverScroll observer : observerScroll) {
            observer.scrollNoteData();
        }
    }

    // Подписать
    public  void subscribeAdd(ObserverAdd observer) {
        observerAdd.add(observer);
    }

    // Отписать
    public  void unsubscribeAdd(ObserverAdd observer) {
        observerAdd.remove(observer);
    }

    // Разослать событие
    public void notifyAdd(NoteData noteData) {
        for (ObserverAdd observer : observerAdd) {
            observer.addNoteData(noteData);
            unsubscribeAdd(observer);
        }
    }
}
