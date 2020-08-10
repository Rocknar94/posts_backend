package com.matthieu.posts_backend.client;


import com.matthieu.posts_backend.exceptions.PostsRetrievalException;
import com.matthieu.posts_backend.models.PostModel;

import java.util.List;

/**
 * Interface exposing all the methods used to retrieve the posts from the distant API
 */
public interface PostsRetrievalClient {
    List<PostModel> retrievePosts() throws PostsRetrievalException;
}