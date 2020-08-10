package com.matthieu.posts_backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.matthieu.posts_backend.controllers.PostsController;
import com.matthieu.posts_backend.models.PostsListModel;
import com.matthieu.posts_backend.services.PostsService;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.server.ResponseStatusException;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Tag("Unit")
@ExtendWith(SpringExtension.class)
@WebMvcTest(PostsController.class)
class PostsControllerTest {
    @Autowired
    private MockMvc mvc;

    @MockBean
    private PostsService service;


    @Test
    public void testGetPosts() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        String respJsonBody = Files.readString(Path.of("src/test/resources/api_return.json"));
        PostsListModel posts = mapper.readValue(respJsonBody, PostsListModel.class);

        Mockito.when(service.getPosts()).thenReturn(posts);

        MvcResult resp = mvc.perform(MockMvcRequestBuilders.get("/posts")).andReturn();

        assertEquals(202, resp.getResponse().getStatus());
        assertEquals(respJsonBody, resp.getResponse().getContentAsString());
    }

    @Test
    public void testGetPostsNoContent() throws Exception {
        Mockito.when(service.getPosts()).thenThrow(new ResponseStatusException(HttpStatus.NO_CONTENT, "No post found in DB."));

        MvcResult resp = mvc.perform(MockMvcRequestBuilders.get("/posts")).andReturn();

        assertEquals(204, resp.getResponse().getStatus());
        assertEquals(0, resp.getResponse().getContentLength());
    }

    @Test
    public void testGetPostsInternalError() throws Exception {
        Mockito.when(service.getPosts()).thenThrow(new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Unexpected error"));

        MvcResult resp = mvc.perform(MockMvcRequestBuilders.get("/posts")).andReturn();

        assertEquals(500, resp.getResponse().getStatus());
        assertEquals(0, resp.getResponse().getContentLength());
    }
}