package com.project.ecommerce.dto;

public class ReviewRequest {

    private String headline;
    private String comment;
    private int rating;

    public String getHeadline() { return headline; }
    public String getComment() { return comment; }
    public int getRating() { return rating; }

    public void setHeadline(String headline) { this.headline = headline; }
    public void setComment(String comment) { this.comment = comment; }
    public void setRating(int rating) { this.rating = rating; }
}
