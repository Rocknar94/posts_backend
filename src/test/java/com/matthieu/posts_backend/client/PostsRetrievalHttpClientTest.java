package com.matthieu.posts_backend.client;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.matthieu.posts_backend.exceptions.PostsRetrievalException;
import com.matthieu.posts_backend.models.PostModel;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Tag("Unit")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ExtendWith(SpringExtension.class)
class PostsRetrievalHttpClientTest {
    @Mock
    private HttpRequest request;

    @Mock
    private HttpClient httpClient;

    @Mock
    private HttpResponse<Object> resp;

    private String respJsonBody;
    private List<PostModel> retrievedPosts;

    private PostsRetrievalHttpClient postsRetrievalHttpClient;


    @BeforeAll
    public void init() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        this. respJsonBody = Files.readString(Path.of("src/test/resources/raw.json"));
        this.retrievedPosts = mapper.readValue(respJsonBody, new TypeReference<List<PostModel>>() {});
    }

    @BeforeEach
    public void setup() {
        this.postsRetrievalHttpClient = new PostsRetrievalHttpClient(request, httpClient);
    }


    @Test
    public void testRetrievePosts() throws IOException, InterruptedException {
        Mockito.when(httpClient.send(Mockito.any(), Mockito.any())).thenReturn(resp);
        Mockito.when(resp.statusCode()).thenReturn(202);
        Mockito.when(resp.body()).thenReturn(respJsonBody);

        List<PostModel> posts = postsRetrievalHttpClient.retrievePosts();

        assertEquals(retrievedPosts.size(), posts.size());
        PostModel retrievedPost, post;
        for (int i = 0; i < retrievedPosts.size(); ++i) {
            retrievedPost = retrievedPosts.get(i);
            post = posts.get(i);

            assertEquals(retrievedPost.getBody(), post.getBody());
            assertEquals(retrievedPost.getId(), post.getId());
            assertEquals(retrievedPost.getTitle(), post.getTitle());
            assertEquals(retrievedPost.getUserId(), post.getUserId());
        }
    }

    @Test
    public void testRetrievePostsInterruptedException() throws IOException, InterruptedException {
        Mockito.when(httpClient.send(Mockito.any(), Mockito.any())).thenThrow(new IOException());

        PostsRetrievalException exception = assertThrows(PostsRetrievalException.class, () -> postsRetrievalHttpClient.retrievePosts());
        assertEquals("An issue occurred while trying to retrieve the posts.", exception.getMessage());
    }

    @Test
    public void testRetrievePostsBadResponse() throws IOException, InterruptedException {
        Mockito.when(httpClient.send(Mockito.any(), Mockito.any())).thenReturn(resp);
        Mockito.when(resp.statusCode()).thenReturn(500);

        PostsRetrievalException exception = assertThrows(PostsRetrievalException.class, () -> postsRetrievalHttpClient.retrievePosts());
        assertEquals("Received HTTP status 500 while trying to retrieve the posts.", exception.getMessage());
    }

    @Test
    @Order(1)
    public void testRetrievePostsMalformedJsonBody() throws IOException, InterruptedException {
        Mockito.when(httpClient.send(Mockito.any(), Mockito.any())).thenReturn(resp);
        Mockito.when(resp.statusCode()).thenReturn(200);
        Mockito.when(resp.body()).thenReturn("{:{}");

        assertNull(postsRetrievalHttpClient.retrievePosts());
    }
}
