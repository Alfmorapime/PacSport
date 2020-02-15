package com.example.projectpacsport;

import java.io.Serializable;

class Team implements Serializable {
    private int id;
    private String name;
    private String logo;
    private String abbreviation;
    private int score;

    int getId() {
        return id;
    }

    void setId(int id) {
        this.id = id;
    }

    String getName() {
        return name;
    }

    void setName(String name) {
        this.name = name;
    }

    String getLogo() {
        return logo;
    }

    void setLogo(String logo) {
        this.logo = logo;
    }

    public String getAbbreviation() {
        return abbreviation;
    }

    public void setAbbreviation(String abbreviation) {
        this.abbreviation = abbreviation;
    }

    int getScore() {
        return score;
    }

    void setScore(int score) {
        this.score = score;
    }
}
