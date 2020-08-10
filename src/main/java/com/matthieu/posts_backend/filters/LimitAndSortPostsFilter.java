package com.matthieu.posts_backend.filters;

import com.matthieu.posts_backend.models.PostModel;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Implements PostsFilter. This filter is used to keep only the first 50 posts of the list, and sort them by title
 */
@Component
public class LimitAndSortPostsFilter implements PostsFilter {

    @Override
    public List<PostModel> apply(List<PostModel> posts) {
        return posts.stream()
                .limit(50)
                .sorted((p1, p2) -> {
                    int comp = StringUtils.compare(p1.getTitle(), p2.getTitle());
                    return comp == 0 ? p2.getId() - p1.getId() : comp;
                })
                .collect(Collectors.toList());
    }
}
