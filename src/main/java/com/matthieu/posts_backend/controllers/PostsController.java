package com.matthieu.posts_backend.controllers;


import com.matthieu.posts_backend.models.PostsListModel;
import com.matthieu.posts_backend.services.PostsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;


/**
 * Controller exposing the /posts API, used to retrieve the 50 posts stored in DB, sorted by title.
 */
@RestController
public class PostsController {

    private final PostsService postsService;

    @Autowired
    public PostsController(PostsService postsService) {
        this.postsService = postsService;
    }

    /**
     * REST API returning a list of the 50 posts stored in DB, and sorted by title.
     * Returns HTTP 202 and a JSON body containing the posts if everything went well,
     * HTTP 204 and an empty body if the DB is empty,
     * and HTTP 500 in case of unexpected error during the process.
     * @return List of posts stored in DB, sorted by title.
     */
    @CrossOrigin
    @GetMapping(value = "/posts", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    @ResponseStatus(HttpStatus.ACCEPTED)
    public PostsListModel getPosts() {
        return this.postsService.getPosts();
    }
}