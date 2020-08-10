package com.matthieu.posts_backend.persistence;

import com.matthieu.posts_backend.models.PostModel;
import com.matthieu.posts_backend.models.PostsListModel;

import java.util.List;

/**
 * Interface exposing all the needed method to interact with the database.
 * Each implementation of this interface might be used to interact with a different kind of database.
 * Current version only supports MongoDB through MongoDbAccessor implementation
 */
public interface DbAccessor {
    /**
     * Inserts or updates the posts in base
     * @param posts List of posts to insert / update
     */
    void upsertPostList(List<PostModel> posts);

    /**
     * Checks if base is empty. Used to check DB state at startup
     * @return True if empty, else false
     */
    boolean isEmpty();

    /**
     * Retrieves the posts from DB
     * @return List of the posts stored in database
     */
    PostsListModel getPosts();
}
