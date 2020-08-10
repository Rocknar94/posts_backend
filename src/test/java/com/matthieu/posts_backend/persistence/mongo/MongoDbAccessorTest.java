package com.matthieu.posts_backend.persistence.mongo;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.matthieu.posts_backend.models.PostModel;
import com.matthieu.posts_backend.models.PostsListModel;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertFalse;

@Tag("Unit")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ExtendWith(SpringExtension.class)
class MongoDbAccessorTest {
    @Mock
    private MongoDbPostsDAO dao;

    private MongoDbAccessor dbAccessor;

    private List<PostModel> posts;

    @BeforeAll
    private void init() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        this.posts = mapper.readValue(new File("src/test/resources/ordered.json"), new TypeReference<>(){});

        this.dbAccessor = new MongoDbAccessor(dao);
    }

    @Test
    public void testUpsertPostListBaseNotEmpty() {
        ArgumentCaptor<PostsListModel> argumentCaptor = ArgumentCaptor.forClass(PostsListModel.class);
        PostsListModel postsListModel = new PostsListModel();
        Mockito.when(dao.findFirstByIdIsNotNull()).thenReturn(postsListModel);
        Mockito.when(dao.save(argumentCaptor.capture())).thenReturn(null);

        dbAccessor.upsertPostList(posts);

        assertSame(posts, argumentCaptor.getValue().getPosts());
    }

    @Test
    public void testUpsertPostListBaseEmpty() {
        ArgumentCaptor<PostsListModel> argumentCaptor = ArgumentCaptor.forClass(PostsListModel.class);
        Mockito.when(dao.findFirstByIdIsNotNull()).thenReturn(null);
        Mockito.when(dao.save(argumentCaptor.capture())).thenReturn(null);

        dbAccessor.upsertPostList(posts);

        assertNotNull(argumentCaptor.getValue());
        assertSame(posts, argumentCaptor.getValue().getPosts());
    }

    @Test
    public void testGetPosts() {
        Mockito.when(dao.findFirstByIdIsNotNull()).thenReturn(new PostsListModel());
        dbAccessor.getPosts();
    }

    @Test
    public void testIsEmptyTrue() {
        Mockito.when(dao.count()).thenReturn(0L);
        assertTrue(dbAccessor.isEmpty());
    }

    @Test
    public void testIsEmptyFalse() {
        Mockito.when(dao.count()).thenReturn(2L);
        assertFalse(dbAccessor.isEmpty());
    }
}