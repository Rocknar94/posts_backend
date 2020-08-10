package com.matthieu.posts_backend.client;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.matthieu.posts_backend.exceptions.PostsRetrievalException;
import com.matthieu.posts_backend.models.PostModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;


/**
 * Implementation of PostsRetrievalClient using Java8 HttpClient
 */
@Component
public class PostsRetrievalHttpClient implements PostsRetrievalClient {

    private static final Logger log = LoggerFactory.getLogger(PostsRetrievalHttpClient.class);

    private final HttpRequest retrievalRequest;
    private final HttpClient httpClient;


    @Autowired
    public PostsRetrievalHttpClient(HttpRequest retrievalRequest, HttpClient httpClient) {
        this.retrievalRequest = retrievalRequest;
        this.httpClient = httpClient;
    }

    /**
     * Retrieving all posts from distant API
     * @return List of all the posts returned by the distant API
     * @throws PostsRetrievalException thrown in case of issue during the retrieval
     */
    @Override
    public List<PostModel> retrievePosts() throws PostsRetrievalException {
        HttpResponse<String> resp;

        try {
            resp = httpClient.send(retrievalRequest, HttpResponse.BodyHandlers.ofString());
        } catch (InterruptedException | IOException e) {
            log.warn("An issue occurred while trying to retrieve the posts.", e);
            throw new PostsRetrievalException("An issue occurred while trying to retrieve the posts.", e);
        }

        List<PostModel> posts = null;

        if (HttpStatus.valueOf(resp.statusCode()).is2xxSuccessful()) {
            try {
                ObjectMapper jsonMapper = new ObjectMapper();
                posts = jsonMapper.readValue(resp.body(), new TypeReference<>(){});
            } catch (JsonProcessingException e) {
                log.warn("Unable to convert JSON body to PostModel objects. Body : {}", resp.body(), e);
            }
        } else {
            log.warn("Posts retrieval failed with HTTP code {}", resp.statusCode());
            throw new PostsRetrievalException("Received HTTP status " + resp.statusCode() + " while trying to retrieve the posts.");
        }

        return posts;
    }
}
