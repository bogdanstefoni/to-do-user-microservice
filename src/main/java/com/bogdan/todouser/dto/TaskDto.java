package com.bogdan.todouser.dto;


import java.util.List;

public class TaskDto {

    private String title;

    private List<String> taskDescription;


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<String> getTaskDescription() {
        return taskDescription;
    }

    public void setTaskDescription(List<String> taskDescription) {
        this.taskDescription = taskDescription;
    }


}
