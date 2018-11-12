package com.exmaple.udacity.bakershop.pojo;

public class Procedure {
    private int id;
    private String shortDescription;
    private String description;
    private String videoURL;
    private String thumbnailURL;
    public Procedure() {}
    public Procedure(int id,String shortDescription,String description,String videoURL,String thumbnailURL) {
        this.id=id;
        this.shortDescription=shortDescription;
        this.description=description;
        this.videoURL=videoURL;
        this.thumbnailURL=thumbnailURL;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setShortDescription(String shortDescription) {
        this.shortDescription = shortDescription;
    }

    public String getShortDescription() {
        return shortDescription;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getVideoURL() {
        return videoURL;
    }

    public void setVideoURL(String videoURL) {
        this.videoURL = videoURL;
    }

    public String getThumbnailURL() {
        return thumbnailURL;
    }

    public void setThumbnailURL(String thumbnailURL) {
        this.thumbnailURL = thumbnailURL;
    }
}
