package com.matthieu.posts_backend.filters;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.matthieu.posts_backend.models.PostModel;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Tag("Unit")
class LimitAndSortPostsFilterTest {
    private LimitAndSortPostsFilter filter = new LimitAndSortPostsFilter();

    @Test
    public void testFilter() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        List<PostModel> rawPostList = mapper.readValue(new File("src/test/resources/raw.json"), new TypeReference<>(){});
        List<PostModel> orderedPostList = mapper.readValue(new File("src/test/resources/ordered.json"), new TypeReference<>(){});

        List<PostModel> filteredPostList = this.filter.apply(rawPostList);

        assertEquals(filteredPostList.size(), orderedPostList.size());
        PostModel filteredPost, orderedPost;
        for (int i = 0; i < filteredPostList.size(); ++i) {
            filteredPost = filteredPostList.get(i);
            orderedPost = orderedPostList.get(i);

            assertEquals(filteredPost.getBody(), orderedPost.getBody());
            assertEquals(filteredPost.getId(), orderedPost.getId());
            assertEquals(filteredPost.getTitle(), orderedPost.getTitle());
            assertEquals(filteredPost.getUserId(), orderedPost.getUserId());
        }
    }
}