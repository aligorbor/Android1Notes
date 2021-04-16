package ru.geekbrains.android1.notes.ui;

import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.Locale;

import ru.geekbrains.android1.notes.R;
import ru.geekbrains.android1.notes.data.NoteData;
import ru.geekbrains.android1.notes.data.NotesSource;

public class NotesAdapter extends RecyclerView.Adapter<NotesAdapter.ViewHolder> {
    private final static String TAG = "NotesAdapter";
    private final NotesSource dataSource;
    private final Fragment fragment;
    private OnItemClickListener itemClickListener;
    private int menuPosition;

    public NotesAdapter(NotesSource dataSource, Fragment fragment) {
        this.dataSource = dataSource;
        this.fragment = fragment;
    }

    @NonNull
    @Override
    public NotesAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item, parent, false);
        Log.d(TAG, "onCreateViewHolder");
        // Здесь можно установить всякие параметры
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull NotesAdapter.ViewHolder holder, int position) {
        holder.setData(dataSource.getNoteData(position));
        Log.d(TAG, "onBindViewHolder");
    }

    @Override
    public int getItemCount() {
        return dataSource.size();
    }

    public int getMenuPosition() {
        return menuPosition;
    }

    public void setItemClickListener(OnItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView title;
        private final TextView description;
        private final TextView tag;
        private final TextView date;
        private final CheckBox like;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.cardTitle);
            description = itemView.findViewById(R.id.cardDescription);
            tag = itemView.findViewById(R.id.cardTag);
            date = itemView.findViewById(R.id.cardDate);
            like = itemView.findViewById(R.id.cardLike);

            registerContextMenu(itemView);

            title.setOnClickListener(v -> itemClickListener.onItemClick(v, getAdapterPosition()));
            title.setOnLongClickListener(new View.OnLongClickListener() {
                @RequiresApi(api = Build.VERSION_CODES.N)
                @Override
                public boolean onLongClick(View v) {
                    menuPosition = ViewHolder.this.getLayoutPosition();
                    itemView.showContextMenu(10, 10);
                    return true;
                }
            });

            like.setOnClickListener(v -> {
                dataSource.getNoteData(getAdapterPosition()).setLike(((CheckBox) v).isChecked());
                v.clearFocus();
            });
        }

        private void registerContextMenu(View itemView) {
            if (fragment != null) {
                itemView.setOnLongClickListener(v -> {
                    menuPosition = getLayoutPosition();
                    return false;
                });
                fragment.registerForContextMenu(itemView);
            }
        }

        public void setData(NoteData noteData) {
            title.setText(noteData.getTitle());
            description.setText(noteData.getDescription());
            tag.setText(String.format(Locale.getDefault(), "%d", noteData.getTag()));
            date.setText(new SimpleDateFormat("dd-MM-yy hh:mm", Locale.getDefault()).format(noteData.getDate()));
            like.setChecked(noteData.isLike());
        }
    }
}
