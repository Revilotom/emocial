package repositories.post;

import com.google.inject.ImplementedBy;
import models.Person;
import models.Post;

import java.util.concurrent.CompletionStage;
import java.util.stream.Stream;

/**
 * This interface provides a non-blocking API for possibly blocking operations.
 */
@ImplementedBy(JPAPostRepository.class)
public interface PostRepository {

    CompletionStage<Stream<Post>> findByUsername(String username);

    CompletionStage<Stream<Post>> list();
}
