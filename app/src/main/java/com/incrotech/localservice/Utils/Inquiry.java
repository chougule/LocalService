package com.incrotech.localservice.Utils;

/**
 * Created by jeet on 18/7/17.
 */

public class Inquiry {

    private String id="";
    private String title = "";
    private String priority = "";
    private String description = "";
    private String budget = "";
    private String budget_icon="";
    private String date;
    private String file = "";


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public String getBudget() {
        return budget;
    }

    public void setBudget(String budget) {
        this.budget = budget;
    }

    public String getBudget_icon() {
        return budget_icon;
    }

    public void setBudget_icon(String budget_icon) {
        this.budget_icon = budget_icon;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getFile() {
        return file;
    }

    public void setFile(String file) {
        this.file = file;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
