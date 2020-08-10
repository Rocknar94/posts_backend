package com.matthieu.posts_backend.persistence.mongo;

import com.matthieu.posts_backend.models.PostsListModel;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Component;

/**
 * Interface extending Spring Repository, it exposes CRUD operations for a mongoDB base
 */
@Component
public interface MongoDbPostsDAO extends MongoRepository<PostsListModel, String> {
    /**
     * Find the first document stored in DB. Here the document returned embed the list of posts, sorted by title.
     * @return Embedded document containing the posts stored in db. If the db is empty, this method returns null
     */
    PostsListModel findFirstByIdIsNotNull();
}
