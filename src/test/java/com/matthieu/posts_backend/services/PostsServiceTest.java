package com.matthieu.posts_backend.services;

import com.matthieu.posts_backend.client.PostsRetrievalClient;
import com.matthieu.posts_backend.exceptions.PostsRetrievalException;
import com.matthieu.posts_backend.filters.PostsFilter;
import com.matthieu.posts_backend.models.PostModel;
import com.matthieu.posts_backend.models.PostsListModel;
import com.matthieu.posts_backend.persistence.DbAccessor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Tag("Unit")
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
@ExtendWith(SpringExtension.class)
class PostsServiceTest {

    @Mock
    private PostsRetrievalClient client;

    @Mock
    private DbAccessor dbAccessor;

    @Mock
    private PostsFilter postsFilter;

    @Mock
    private List<PostModel> posts;

    @Mock
    private PostsListModel postsListModel;

    private PostsService service;


    @BeforeEach
    public void initService() {
        this.service = new PostsService(dbAccessor, client, List.of(postsFilter));
    }

    @Test
    public void testDbIsEmptyPostsRetrieved() throws PostsRetrievalException {
        Mockito.when(dbAccessor.isEmpty()).thenReturn(true);
        Mockito.when(client.retrievePosts()).thenReturn(posts);
        Mockito.when(posts.isEmpty()).thenReturn(false);
        Mockito.when(postsFilter.apply(Mockito.any())).thenReturn(posts);
        Mockito.doNothing().when(dbAccessor).upsertPostList(posts);

        service.handleAppStartup(null);

        InOrder inOrder = Mockito.inOrder(dbAccessor, client, postsFilter);
        inOrder.verify(dbAccessor).isEmpty();
        inOrder.verify(client).retrievePosts();
        inOrder.verify(postsFilter).apply(Mockito.any());
        inOrder.verify(dbAccessor).upsertPostList(Mockito.any());
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void testDbNotEmptyForceRefreshFalse() throws PostsRetrievalException {
        Mockito.when(dbAccessor.isEmpty()).thenReturn(false);

        service.handleAppStartup(null);

        InOrder inOrder = Mockito.inOrder(dbAccessor, client, postsFilter, posts);
        inOrder.verify(dbAccessor).isEmpty();
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void testNoPostToStore() throws PostsRetrievalException {
        Mockito.when(dbAccessor.isEmpty()).thenReturn(true);
        Mockito.when(client.retrievePosts()).thenReturn(null);

        service.handleAppStartup(null);

        InOrder inOrder = Mockito.inOrder(dbAccessor, client, postsFilter);
        inOrder.verify(dbAccessor).isEmpty();
        inOrder.verify(client).retrievePosts();
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void testException() throws PostsRetrievalException {
        Mockito.when(dbAccessor.isEmpty()).thenReturn(true);
        Mockito.when(client.retrievePosts()).thenThrow(new PostsRetrievalException(""));

        assertThrows(PostsRetrievalException.class, () -> service.handleAppStartup(null));

        InOrder inOrder = Mockito.inOrder(dbAccessor, client, postsFilter);
        inOrder.verify(dbAccessor).isEmpty();
        inOrder.verify(client).retrievePosts();
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void testGetPosts() {
        Mockito.when(dbAccessor.getPosts()).thenReturn(postsListModel);
        Mockito.when(postsListModel.getPosts()).thenReturn(posts);
        Mockito.when(posts.isEmpty()).thenReturn(false);

        service.getPosts();

        InOrder inOrder = Mockito.inOrder(dbAccessor, postsListModel, posts);
        inOrder.verify(dbAccessor).getPosts();
        inOrder.verify(postsListModel, Mockito.times(2)).getPosts();
        inOrder.verify(posts).isEmpty();
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void testGetPostsRuntimeException() {
        Mockito.when(dbAccessor.getPosts()).thenThrow(new RuntimeException(""));

        this.commonExceptionTesting(HttpStatus.INTERNAL_SERVER_ERROR, "Unexpected error");
    }

    @Test
    public void testGetPostsReturnsNull() {
        Mockito.when(dbAccessor.getPosts()).thenReturn(null);

        this.commonExceptionTesting(HttpStatus.NO_CONTENT, "No post found in DB.");
    }

    @Test
    public void testGetPostsNullList() {
        Mockito.when(dbAccessor.getPosts()).thenReturn(postsListModel);
        Mockito.when(postsListModel.getPosts()).thenReturn(null);

        this.commonExceptionTesting(HttpStatus.NO_CONTENT, "No post found in DB.");
    }

    @Test
    public void testGetPostsEmptyBase() {
        Mockito.when(dbAccessor.getPosts()).thenReturn(postsListModel);
        Mockito.when(postsListModel.getPosts()).thenReturn(posts);
        Mockito.when(posts.isEmpty()).thenReturn(true);

        this.commonExceptionTesting(HttpStatus.NO_CONTENT, "No post found in DB.");
    }

    private void commonExceptionTesting(HttpStatus status, String reason) {
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> service.getPosts());

        assertEquals(status, exception.getStatus());
        assertEquals(reason, exception.getReason());
    }
}