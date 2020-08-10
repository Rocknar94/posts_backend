package com.matthieu.posts_backend.persistence.mongo;

import com.matthieu.posts_backend.models.PostModel;
import com.matthieu.posts_backend.models.PostsListModel;
import com.matthieu.posts_backend.persistence.DbAccessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Implementation of DbAccessor used to interact with a MongoDB base
 */
@Component
public class MongoDbAccessor implements DbAccessor {

    private final MongoDbPostsDAO dao;


    @Autowired
    public MongoDbAccessor(MongoDbPostsDAO dao) {
        this.dao = dao;
    }

    @Override
    public void upsertPostList(List<PostModel> posts) {
        PostsListModel postsList = this.dao.findFirstByIdIsNotNull();

        if (postsList == null) {
            postsList = new PostsListModel();
        }

        postsList.setPosts(posts);
        this.dao.save(postsList);
    }

    @Override
    public PostsListModel getPosts() {
        return this.dao.findFirstByIdIsNotNull();
    }

    @Override
    public boolean isEmpty() {
        return this.dao.count() == 0;
    }
}