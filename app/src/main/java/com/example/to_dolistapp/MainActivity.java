package com.example.to_dolistapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;

import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;

public class MainActivity extends AppCompatActivity {
    EditText task;
    AppCompatButton addBtn;
    RecyclerView tasksRecycler;
    TasksAdapter adapter;
    ArrayList<String> tasksList;
    String deletedTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        task = findViewById(R.id.task);
        addBtn = findViewById(R.id.addBtn);
        tasksRecycler = findViewById(R.id.tasksRecycler);

        tasksList = new ArrayList<>();

        tasksRecycler.setLayoutManager(new LinearLayoutManager(this));
        adapter = new TasksAdapter(this, tasksList, tasksRecycler);
        tasksRecycler.setAdapter(adapter);

        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String taskVal = task.getText().toString();
                if(!taskVal.isEmpty())
                {
                    task.setText("");
                    tasksList.add(0, taskVal);
                    adapter.notifyDataSetChanged();
                }
                else
                {
                    Toast.makeText(MainActivity.this, "Please Enter Task", Toast.LENGTH_LONG).show();
                }
            }
        });

        ItemTouchHelper.SimpleCallback callback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();

                switch (direction){
                    case ItemTouchHelper.LEFT:
                        deletedTask = tasksList.get(position);
                        tasksList.remove(position);
                        adapter.notifyItemRemoved(position);
                        Snackbar.make(tasksRecycler, deletedTask, Snackbar.LENGTH_LONG)
                                .setAction("Undo", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        tasksList.add(position, deletedTask);
                                        adapter.notifyItemInserted(position);
                                    }
                                }).show();
                        break;
                    case ItemTouchHelper.RIGHT:
                        tasksList.set(position, tasksList.get(position));
                        adapter.notifyDataSetChanged();

                        Dialog editDialog = new Dialog(MainActivity.this);
                        editDialog.setContentView(R.layout.edit_dialog);
                        editDialog.setCancelable(false);
                        EditText editTask = editDialog.findViewById(R.id.editTask);
                        editTask.setText(tasksList.get(position));
                        Button editBtn = editDialog.findViewById(R.id.editBtn);
                        editBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                String newTask = editTask.getText().toString();
                                editDialog.dismiss();
                                tasksList.set(position, newTask);
                                adapter.notifyDataSetChanged();
                            }
                        });
                        Button cancelBtn = editDialog.findViewById(R.id.cancelBtn);
                        cancelBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                editDialog.dismiss();
                            }
                        });
                        editDialog.show();
                        break;
                }
            }

            @Override
            public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                new RecyclerViewSwipeDecorator.Builder(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                        .addSwipeLeftBackgroundColor(Color.RED)
                        .addSwipeLeftActionIcon(R.drawable.baseline_delete_24)
                        .addSwipeRightBackgroundColor(Color.GREEN)
                        .addSwipeRightActionIcon(R.drawable.baseline_edit_24)
                        .addPadding(3, 3, 4, 3)
                        .create()
                        .decorate();

                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }
        };

        ItemTouchHelper helper = new ItemTouchHelper(callback);
        helper.attachToRecyclerView(tasksRecycler);
    }
}