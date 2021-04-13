package ru.geekbrains.android1.notes;

import java.io.Serializable;
import java.util.Date;

public class Note implements Serializable {
    private String title;
    private String description;
    private Date date;
    private int tag;
    private boolean like;
    private String noteText;
    private String titleForList;

    public Note() {
        title = "";
        description = "";
        tag = 0;
        like=false;
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

    public int getTag() {
        return tag;
    }

    public void setTag(int tag) {
        this.tag = tag;
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

    public boolean isLike() {
        return like;
    }

    public void setLike(boolean like) {
        this.like = like;
    }

}
