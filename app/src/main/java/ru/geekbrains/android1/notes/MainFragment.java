package ru.geekbrains.android1.notes;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

public class MainFragment extends Fragment {
    private Publisher publisher;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        publisher = ((PublisherGetter) context).getPublisher(); // получим обработчика подписок
    }

    public MainFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_main, container, false);

        final EditText textView = view.findViewById(R.id.editText);
        Button button = view.findViewById(R.id.buttonNewNote);       // По этой кнопке будем отправлять события
        button.setOnClickListener(v -> {
            String text = textView.getText().toString();
            if (!text.isEmpty())
                publisher.notifyNote(text);                       // Отправить изменившуюся строку
            textView.setText("");
        });
        return view;
    }
}