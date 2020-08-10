package com.matthieu.posts_backend.services;


import com.matthieu.posts_backend.client.PostsRetrievalClient;
import com.matthieu.posts_backend.exceptions.PostsRetrievalException;
import com.matthieu.posts_backend.filters.PostsFilter;
import com.matthieu.posts_backend.models.PostModel;
import com.matthieu.posts_backend.persistence.DbAccessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class PostsService {

    private static final Logger log = LoggerFactory.getLogger(PostsService.class);

    private final DbAccessor dbAccessor;
    private final PostsRetrievalClient httpClient;
    private final List<PostsFilter> postsFilters;

    @Value("${posts.retrieval.force-at-startup}")
    private boolean forceRefresh;


    @Autowired
    public PostsService(DbAccessor dbAccessor, PostsRetrievalClient httpClient, List<PostsFilter> postsFilters) {
        this.dbAccessor = dbAccessor;
        this.httpClient = httpClient;
        this.postsFilters = postsFilters;
    }

    /**
     * Handles the context refreshed event, ensuring the posts are retrieved and stored in database at startup.
     * In case of PostsRetrievalException, a retry mechanism relaunches the method. The number of retries is configurable
     * through the property 'posts.retrieval.max-retry'.
     * @param cre Spring event published while context has been refreshed
     * @throws PostsRetrievalException Exception thrown while an issue occurs during posts retrieval from the distant API
     */
    @EventListener
    @Retryable(value = {PostsRetrievalException.class},
            maxAttemptsExpression = "#{${posts.retrieval.max-retry}}",
            backoff = @Backoff(delayExpression = "#{${posts.retrieval.backoff-delay}}"))
    public void handleAppStartup(ContextRefreshedEvent cre) throws PostsRetrievalException {

        // If the posts refresh is forced, or the DB is empty, we retrieve the posts from distant API then store them in DB
        // else there is nothing to do, the data is already existing in base
        if (this.forceRefresh || this.dbAccessor.isEmpty()) {
            log.info("Retrieving new posts");
            List<PostModel> posts = this.httpClient.retrievePosts();

            if (posts == null || posts.isEmpty()) {
                log.info("No post to store in DB.");
            } else {
                // Filters the posts then stores them in DB. Current version of the App possesses only one filter
                // keeping the 50 first posts, and sorting them by title.
                for (PostsFilter filter : this.postsFilters) {
                    posts = filter.apply(posts);
                }
                this.dbAccessor.upsertPostList(posts);
                log.info("Posts stored in DB");
            }
        }
    }

    /**
     * Called once the max retry attempts has been reached for handleAppStartup method.
     * Used to log the latest thrown exception
     * @param e Exception thrown by the latest handleAppStartup retry
     */
    @Recover
    public void recoverStartup(PostsRetrievalException e) {
        log.warn("Retry limit reached, the app was unable to retrieve the posts.", e);
    }
}
