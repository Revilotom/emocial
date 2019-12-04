package Repos;

import Helpers.TestHelper;
import models.Person;
import models.Post;
import org.hamcrest.MatcherAssert;
import org.junit.Before;
import org.junit.Test;
import play.test.WithApplication;
import repositories.person.JPAPersonRepository;
import repositories.post.JPAPostRepository;

import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;
import static org.hamcrest.Matchers.*;


public class PostRepoTest extends WithApplication {
    private JPAPersonRepository personRepo;
    private JPAPostRepository postRepository;


    @Before
    public void before() throws ExecutionException, InterruptedException {
        personRepo = TestHelper.setup(app);
        postRepository = app.injector().instanceOf(JPAPostRepository.class);
    }

    @Test
    public void testCanLikePost() throws ExecutionException, InterruptedException {
        Post post = postRepository.findById(2).toCompletableFuture().get().get();
        Person person = personRepo.findByUsername("revilotom").toCompletableFuture().get().get();

        person.likePost(post);

        person = personRepo.update(person).toCompletableFuture().get();

        person = personRepo.findByUsername("revilotom").toCompletableFuture().get().get();
        post = postRepository.findById(2).toCompletableFuture().get().get();

        MatcherAssert.assertThat(person.getLikedPosts().size(), is(1));
        MatcherAssert.assertThat(post.getLikers().size(), is(1));
    }

    @Test
    public void testCannotLikePostTwice() throws ExecutionException, InterruptedException {
        Post post = postRepository.findById(2).toCompletableFuture().get().get();
        Person person = personRepo.findByUsername("revilotom").toCompletableFuture().get().get();

        person.likePost(post);

        person = personRepo.update(person).toCompletableFuture().get();

        person = personRepo.findByUsername("revilotom").toCompletableFuture().get().get();
        post = postRepository.findById(2).toCompletableFuture().get().get();

        person.likePost(post);

        person = personRepo.update(person).toCompletableFuture().get();

        person = personRepo.findByUsername("revilotom").toCompletableFuture().get().get();
        post = postRepository.findById(2).toCompletableFuture().get().get();

        MatcherAssert.assertThat(person.getLikedPosts().size(), is(1));
        MatcherAssert.assertThat(post.getLikers().size(), is(1));
    }

    @Test
    public void testCanDislikePost() throws ExecutionException, InterruptedException {
        Post post = postRepository.findById(2).toCompletableFuture().get().get();
        Person person = personRepo.findByUsername("revilotom").toCompletableFuture().get().get();

        person.dislikePost(post);

        person = personRepo.update(person).toCompletableFuture().get();

        person = personRepo.findByUsername("revilotom").toCompletableFuture().get().get();
        post = postRepository.findById(2).toCompletableFuture().get().get();

        MatcherAssert.assertThat(person.getDislikedPosts().size(), is(1));
        MatcherAssert.assertThat(post.getLikers().size(), is(1));
    }

    // TODO fix the time

}
