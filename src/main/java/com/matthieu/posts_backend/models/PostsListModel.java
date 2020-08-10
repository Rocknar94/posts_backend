package com.matthieu.posts_backend.models;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

/**
 *  All the posts will be stored in base as one embedded document, in order to limit the number of operations
 *  to do in DB and ensure the atomicity. It also allows us to directly store the posts sorted by titles, then there is no
 *  need for further processing after reading the posts from the base.
 */
@Document(collection = "posts")
public class PostsListModel {
    @Id
    private String id;

    private List<PostModel> posts;


    public List<PostModel> getPosts() {
        return posts;
    }

    public void setPosts(List<PostModel> posts) {
        this.posts = posts;
    }
}