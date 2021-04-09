package ru.geekbrains.android1.notes;

import java.io.Serializable;
import java.util.Date;

public class Note implements Serializable {
    private String title;
    private String description;
    private Date date;
    private int teg;
    private String noteText;
    private String titleForList;

    public Note() {
        title = "";
        description = "";
        teg = 0;
        noteText = "";
        date = new Date();
        titleForList = "";
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public int getTeg() {
        return teg;
    }

    public void setTeg(int teg) {
        this.teg = teg;
    }

    public String getNoteText() {
        return noteText;
    }

    public void setNoteText(String noteText) {
        this.noteText = noteText;
    }

    public String getTitleForList() {
        return titleForList;
    }

    public void setTitleForList(String titleForList) {
        this.titleForList = titleForList;
    }

}
