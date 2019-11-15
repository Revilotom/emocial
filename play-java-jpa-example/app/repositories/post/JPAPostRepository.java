package repositories.post;

import models.Person;
import models.Post;
import org.hibernate.Hibernate;
import play.db.jpa.JPAApi;
import repositories.JPADefaultRepository;
import repositories.person.DatabaseExecutionContext;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.util.List;
import java.util.concurrent.CompletionStage;
import java.util.function.Function;
import java.util.stream.Stream;

import static java.util.concurrent.CompletableFuture.supplyAsync;

/**
 * Provide JPA operations running inside of a thread pool sized to the connection pool
 */
public class JPAPostRepository extends JPADefaultRepository implements PostRepository {

    @Inject
    public JPAPostRepository(JPAApi jpaApi, DatabaseExecutionContext executionContext) {
        super(jpaApi, executionContext);
    }

    @Override
    public CompletionStage<Stream<Post>> stream() {
        return supplyAsync(() -> wrap(em -> stream(em)), executionContext);
    }

    private Stream<Post> stream(EntityManager em) {
        List<Post> posts = em.createQuery("select p from Post p", Post.class).getResultList();
        return posts.stream();
    }
}
