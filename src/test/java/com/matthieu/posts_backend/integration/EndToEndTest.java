package com.matthieu.posts_backend.integration;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import com.matthieu.posts_backend.integration.wiremock.RetrievePostsTransformer;
import com.matthieu.posts_backend.persistence.mongo.MongoDbPostsDAO;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Tag("Integration")
@ExtendWith(SpringExtension.class)
@SpringBootTest
@TestPropertySource(properties = {
        "posts.retrieval.force-at-startup=true",
        "spring.mongodb.embedded.storage.database-dir=.test_db",
        "posts.retrieval.url=http://localhost:18080/posts"})
@AutoConfigureMockMvc
public class EndToEndTest {

    // Here wiremock is used to stub the API at 'jsonplaceholder.typicode.com/posts'
    // This way we can test the retry mechanism, since the first call to '/posts' API will return HTTP 500, while the next
    // ones will return HTTP 202, and a proper JSON body containing the posts (logic implemented in RetreivePostsTransformer class)
    private static WireMockServer wiremock;

    @Autowired
    private MockMvc mvc;

    @Autowired
    private MongoDbPostsDAO mongoDbDao;

    @BeforeAll
    public static void init() throws IOException {
        WireMockConfiguration wireMockConfiguration = new WireMockConfiguration().port(18080).httpsPort(1443).extensions(RetrievePostsTransformer.class);
        wiremock = new WireMockServer(wireMockConfiguration);
        wiremock.start();

        wiremock.stubFor(WireMock.get("/posts").willReturn(WireMock.aResponse().withTransformers(RetrievePostsTransformer.NAME)));
    }

    @AfterAll
    public static void cleanUp() {
        wiremock.stop();
    }

    @Order(1)
    @Test
    public void testGetPostsFirstStart() throws Exception {
        MvcResult resp = mvc.perform(MockMvcRequestBuilders.get("/posts")).andReturn();

        String respJsonBody = Files.readString(Path.of("src/test/resources/api_return.json"));

        assertEquals(202, resp.getResponse().getStatus());
        assertEquals(respJsonBody, resp.getResponse().getContentAsString());
    }

    @Order(2)
    @Test
    public void testGetPostsEmptyDb() throws Exception {
        this.mongoDbDao.deleteAll(); // ensuring the DB is empty
        MvcResult resp = mvc.perform(MockMvcRequestBuilders.get("/posts")).andReturn();
        assertEquals(204, resp.getResponse().getStatus());
    }
}
