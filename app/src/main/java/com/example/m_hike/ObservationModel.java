package com.example.m_hike;

public class ObservationModel {
    String id, name, time, comment, image, createdAt, lastUpdated, hikeID;

    public ObservationModel(String id, String name, String time, String comment, String image, String createdAt, String lastUpdated, String hikeID) {
        this.id = id;
        this.name = name;
        this.time = time;
        this.comment = comment;
        this.image = image;
        this.createdAt = createdAt;
        this.lastUpdated = lastUpdated;
        this.hikeID = hikeID;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(String lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public String getHikeID() {
        return hikeID;
    }

    public void setHikeID(String hikeID) {
        this.hikeID = hikeID;
    }
}
