package com.trulden;

import java.util.Date;
import java.util.HashSet;

public class Interaction {
    private int id;
    private Date date;
    private String type;
    private HashSet<String> personNames;
    private String comment;

    Interaction(){
        personNames = new HashSet<>();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public HashSet<String> getPersonNames() {
        return personNames;
    }

    public void setPersonNames(HashSet<String> personNames) {
        this.personNames = personNames;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
