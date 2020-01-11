package com.example.alexh.ajenda;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

public class ToDoList implements Serializable {
    private String name;
    private ArrayList<module> todolist;

    public ToDoList(String name, ArrayList<module> todolist) {
        this.name = name;
        this.todolist = todolist;
    }

    public ToDoList(String name) {
        this.name = name;
        this.todolist = new ArrayList<>();
    }

    public HashMap<String, ArrayList<String>> convertToEvents() {
        HashMap<String, ArrayList<String>> result = new HashMap<>();

        return result;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<module> getTodolist() {
        return todolist;
    }

    public void setTodolist(ArrayList<module> todolist) {
        this.todolist = todolist;
    }
}
