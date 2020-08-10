package com.matthieu.posts_backend.config;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;


@Configuration
public class HttpClientConfig {

    @Bean
    public HttpClient httpClient() {
        return HttpClient.newHttpClient();
    }

    @Bean
    public HttpRequest retrievalRequest(@Value("${posts.retrieval.url}") String url) {
        // If url parameter is incorrect, the App won't start
        return HttpRequest.newBuilder().uri(URI.create(url)).GET().build();
    }
}
