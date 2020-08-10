package com.matthieu.posts_backend.integration.wiremock;

import com.github.tomakehurst.wiremock.common.FileSource;
import com.github.tomakehurst.wiremock.extension.Parameters;
import com.github.tomakehurst.wiremock.extension.ResponseTransformer;
import com.github.tomakehurst.wiremock.http.Request;
import com.github.tomakehurst.wiremock.http.Response;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * This transformer is used to test the retry mechanism. First call to '/posts' API will return HTTP status 500,
 * while all the next calls will return HTTP 202, and a proper JSON body containing the posts list
 */
public class RetrievePostsTransformer extends ResponseTransformer {

    public static final String NAME = "RETRIEVE_POSTS_TRANSFORMER";
    private boolean error;

    private String respBodySuccess;

    public RetrievePostsTransformer() throws IOException {
        this.error = true;
        this.respBodySuccess = Files.readString(Path.of("src/test/resources/raw.json"));
    }

    @Override
    public boolean applyGlobally() {
        return false;
    }

    @Override
    public Response transform(Request request, Response response, FileSource files, Parameters parameters) {
        Response.Builder respBuilder = Response.Builder.like(response);
        if (this.error) {
            respBuilder = respBuilder.but().status(500);
        } else {
            respBuilder = respBuilder.but().status(202).body(this.respBodySuccess);
        }

        this.error = false;
        return respBuilder.build();
    }

    @Override
    public String getName() {
        return NAME;
    }
}
