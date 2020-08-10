package com.matthieu.posts_backend.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

/**
 * Class modeling a post received from the distant API
 */
@JsonPropertyOrder({"id", "title", "body", "userId"})
public class PostModel {

    @JsonProperty
    private int id;
    @JsonProperty
    private int userId;
    @JsonProperty
    private String title;
    @JsonProperty
    private String body;


    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }
}
