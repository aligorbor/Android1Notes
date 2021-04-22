package ru.geekbrains.android1.notes.data;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;

public class NoteData implements Parcelable {
    final static int lengthDescription = 50;
    private String id; // идентификатор
    private final String title;       // заголовок
    private final String description; // описание
    private final Date date;
    private final int tag;       // Тег
    private boolean like;       // флажок
    private final String noteText; //текст заметки

    public NoteData(String title, Date date, int tag, boolean like, String noteText) {
        this.title = title;
        this.date = date;
        this.tag = tag;
        this.like = like;
        this.noteText = noteText;
        this.description = noteText.substring(0, Math.min(noteText.length(), lengthDescription));
    }

    protected NoteData(Parcel in) {
        title = in.readString();
        description = in.readString();
        tag = in.readInt();
        like = in.readByte() != 0;
        noteText = in.readString();
        date = new Date(in.readLong());
    }

    public static final Creator<NoteData> CREATOR = new Creator<NoteData>() {
        @Override
        public NoteData createFromParcel(Parcel in) {
            return new NoteData(in);
        }

        @Override
        public NoteData[] newArray(int size) {
            return new NoteData[size];
        }
    };

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public Date getDate() {
        return date;
    }

    public int getTag() {
        return tag;
    }

    public boolean isLike() {
        return like;
    }

    public void setLike(boolean like) {
        this.like = like;
    }

    public String getNoteText() {
        return noteText;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeString(description);
        dest.writeInt(tag);
        dest.writeByte((byte) (like ? 1 : 0));
        dest.writeString(noteText);
        dest.writeLong(date.getTime());
    }
}
