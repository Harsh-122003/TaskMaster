package com.example.to_dolistapp;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;

public class TasksAdapter extends RecyclerView.Adapter<TasksAdapter.ViewHolder> {
    Context context;
    ArrayList<String> tasksList;
    RecyclerView tasksRecycler;

    public TasksAdapter(Context context, ArrayList<String> tasksList, RecyclerView tasksRecycler){
        this.context = context;
        this.tasksList = tasksList;
        this.tasksRecycler = tasksRecycler;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        int pos = position;
        holder.item.setText(tasksList.get(pos));

        holder.edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Dialog editDialog = new Dialog(context);
                editDialog.setContentView(R.layout.edit_dialog);
                editDialog.setCancelable(false);
                EditText editTask = editDialog.findViewById(R.id.editTask);
                editTask.setText(tasksList.get(pos));
                Button editBtn = editDialog.findViewById(R.id.editBtn);
                editBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String newTask = editTask.getText().toString();
                        editDialog.dismiss();
                        tasksList.set(pos, newTask);
                        notifyDataSetChanged();
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
            }
        });

        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String deletedTask = tasksList.get(pos);
                tasksList.remove(pos);
                notifyItemRemoved(pos);
                Snackbar.make(tasksRecycler, deletedTask, Snackbar.LENGTH_LONG)
                        .setAction("Undo", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                tasksList.add(pos, deletedTask);
                                notifyItemInserted(pos);
                            }
                        }).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return tasksList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView item;
        ImageView edit, delete;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            item = itemView.findViewById(R.id.item);
            edit = itemView.findViewById(R.id.edit);
            delete = itemView.findViewById(R.id.delete);
        }
    }
}