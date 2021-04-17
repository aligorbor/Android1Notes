package ru.geekbrains.android1.notes.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import ru.geekbrains.android1.notes.MainActivity;
import ru.geekbrains.android1.notes.Navigation;
import ru.geekbrains.android1.notes.R;
import ru.geekbrains.android1.notes.data.NoteData;
import ru.geekbrains.android1.notes.data.NoteTag;
import ru.geekbrains.android1.notes.data.NotesSource;
import ru.geekbrains.android1.notes.data.NotesSourceImpl;
import ru.geekbrains.android1.notes.observe.Observer;
import ru.geekbrains.android1.notes.observe.ObserverAdd;
import ru.geekbrains.android1.notes.observe.Publisher;

public class ListNotesFragment extends Fragment {
    private static final int MY_DEFAULT_DURATION = 1000;
    private NotesSource data;
    private NotesAdapter adapter;
    private RecyclerView recyclerView;
    private Navigation navigation;
    private Publisher publisher;
    private boolean tile = false;
    private int currentPosition;
    private Observer observer;
    private Fragment currentNoteFragment;
    private NoteTag noteTag;

    private boolean moveToLastPosition;

    public ListNotesFragment() {
        // Required empty public constructor
    }

    public static ListNotesFragment newInstance() {
        return new ListNotesFragment();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        MainActivity activity = (MainActivity) context;
        navigation = activity.getNavigation();
        publisher = activity.getPublisher();
    }

    @Override
    public void onDetach() {
        navigation = null;
        publisher = null;
        super.onDetach();
    }

    @Override
    public void onDestroy() {
        publisher.unsubscribe(observer);
        super.onDestroy();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Получим источник данных для списка
        // Поскольку onCreateView запускается каждый раз
        // при возврате в фрагмент, данные надо создавать один раз
        noteTag = new NoteTag(getResources()).init();
        data = new NotesSourceImpl(getResources()).init();
        observer = (position, noteData) -> {
            data.updateNoteData(position, noteData);
            adapter.notifyItemChanged(position);
        };
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_list_notes, container, false);
        currentPosition = -1;
        initView(view);
        setHasOptionsMenu(true);
        return view;
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.main_fragment_notes, menu);
        MenuItem search = menu.findItem(R.id.action_search);
        SearchView searchText = (SearchView) search.getActionView();
        searchText.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Toast.makeText(getContext(), query, Toast.LENGTH_SHORT).show();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return true;
            }
        });
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_add:
//                data.addNoteData(new NoteData("Заголовок " + data.size(), new Date(), 0,true,"Описание "+data.size()));
//                adapter.notifyItemInserted(data.size()-1);
//            //    recyclerView.scrollToPosition(data.size()-1);
//                recyclerView.smoothScrollToPosition(data.size()-1);
                showNoteForAdd();
                return true;
            case R.id.action_clear:
                if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
                    if (currentNoteFragment != null)
                        navigation.removeFragment(currentNoteFragment, false);
                }
                data.clearNoteData();
                adapter.notifyDataSetChanged();
                return true;
            case R.id.action_view:
                tile = !tile;
                setLayoutManager(tile);
                return true;
            case R.id.action_sort:
                Toast.makeText(getContext(), "action_sort", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.action_search:
                Toast.makeText(getContext(), "action_search", Toast.LENGTH_SHORT).show();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showNoteForAdd() {
        currentNoteFragment = NoteFragment.newInstance();
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            publisher.notifyScroll();
            navigation.addFragment(currentNoteFragment, false, R.id.fragment_note);
        } else
            navigation.addFragment(currentNoteFragment, true);
        publisher.subscribeAdd(noteData -> {
            data.addNoteData(noteData);
            adapter.notifyItemInserted(data.size() - 1);
            // это сигнал, чтобы вызванный метод onCreateView
            // перепрыгнул на конец списка
            moveToLastPosition = true;
        });
    }

    private void initView(View view) {
        FloatingActionButton fabAdd = view.findViewById(R.id.fabAdd);
        fabAdd.setOnClickListener(v -> showNoteForAdd());
        recyclerView = view.findViewById(R.id.recycler_view_lines);
        initRecyclerView();
    }

    private void setLayoutManager(boolean tile) {
        LinearLayoutManager layoutManager;
        if (tile)
            layoutManager = new GridLayoutManager(getContext(), 2);
        else
            layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
    }

    private void initRecyclerView() {
        //  recyclerView.setHasFixedSize(true);
        setLayoutManager(tile);
        adapter = new NotesAdapter(data, this, noteTag);
        recyclerView.setAdapter(adapter);
        DividerItemDecoration itemDecoration = new DividerItemDecoration(getContext(), LinearLayoutManager.VERTICAL);
        itemDecoration.setDrawable(getResources().getDrawable(R.drawable.separator, null));
        recyclerView.addItemDecoration(itemDecoration);

        DefaultItemAnimator animator = new DefaultItemAnimator();
        animator.setAddDuration(MY_DEFAULT_DURATION);
        animator.setRemoveDuration(MY_DEFAULT_DURATION);
        recyclerView.setItemAnimator(animator);

        if (moveToLastPosition) {
            recyclerView.smoothScrollToPosition(data.size() - 1);
            moveToLastPosition = false;
        }
        adapter.setItemClickListener((view, position) -> {
            //          Toast.makeText(getContext(),String.format("Позиция - %d",position),Toast.LENGTH_SHORT).show();
            showNoteForUpdate(position);
        });
    }

    @Override
    public void onCreateContextMenu(@NonNull ContextMenu menu, @NonNull View v, @Nullable ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = requireActivity().getMenuInflater();
        inflater.inflate(R.menu.context, menu);
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        int position = adapter.getMenuPosition();
        switch (item.getItemId()) {
            case R.id.action_update:
//                data.updateNoteData(position,new NoteData("Кадр " + position, data.getNoteData(position).getDate(), data.getNoteData(position).getTag(),false,data.getNoteData(position).getNoteText()));
                showNoteForUpdate(position);
                return true;
            case R.id.action_delete:
                if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
                    if (currentNoteFragment != null)
                        navigation.removeFragment(currentNoteFragment, false);
                }
                data.deleteNoteData(position);
                adapter.notifyItemRemoved(position);
                return true;
        }
        return super.onContextItemSelected(item);
    }

    private void showNoteForUpdate(int position) {
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            publisher.notifyScroll();
            if (position != currentPosition) {
                currentNoteFragment = NoteFragment.newInstance(position, data.getNoteData(position));
                navigation.addFragment(currentNoteFragment, false, R.id.fragment_note);
            }
        } else {
            currentNoteFragment = NoteFragment.newInstance(position, data.getNoteData(position));
            navigation.addFragment(currentNoteFragment, true);
        }
        currentPosition = position;
        publisher.subscribe(observer);
    }
}