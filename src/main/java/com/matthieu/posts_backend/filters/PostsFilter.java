package com.matthieu.posts_backend.filters;


import com.matthieu.posts_backend.models.PostModel;

import java.util.List;


/**
 * Interface used to easily extend or modify the posts filtering in the future versions
 */
public interface PostsFilter {
    List<PostModel> apply(List<PostModel> posts);
}
