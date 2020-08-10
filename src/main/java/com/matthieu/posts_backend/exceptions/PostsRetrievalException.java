package com.matthieu.posts_backend.exceptions;


import java.io.IOException;

/**
 * Exception used to trigger a retry in PostService.handleAppStartup() method
 */
public class PostsRetrievalException extends IOException {

    public  PostsRetrievalException(String message) {
        super(message);
    }
    public PostsRetrievalException(String message, Throwable cause) {
        super(message, cause);
    }
}
