package ru.geekbrains.android1.notes.data;

import com.google.firebase.Timestamp;

import java.util.HashMap;
import java.util.Map;

public class NoteDataMapping {
    public static class Fields {
        public final static String TITLE = "title";
        public final static String DATE = "date";
        public final static String TAG = "tag";
        public final static String LIKE = "like";
        public final static String NOTETEXT = "notetext";
    }

    public static NoteData toNoteData(String id, Map<String, Object> doc) {
        Timestamp timestamp = (Timestamp) doc.get(Fields.DATE);
        Long tagL = (Long) doc.get(Fields.TAG);

        NoteData answer = new NoteData((String) doc.get(Fields.TITLE), timestamp.toDate(),
                tagL.intValue(), (boolean) doc.get(Fields.LIKE),
                (String) doc.get(Fields.NOTETEXT));
        answer.setId(id);
        return answer;
    }

    public static Map<String, Object> toDocument(NoteData noteData) {
        Map<String, Object> answer = new HashMap<>();
        answer.put(Fields.TITLE, noteData.getTitle());
        answer.put(Fields.DATE, noteData.getDate());
        answer.put(Fields.TAG, noteData.getTag());
        answer.put(Fields.LIKE, noteData.isLike());
        answer.put(Fields.NOTETEXT, noteData.getNoteText());
        return answer;
    }

}
