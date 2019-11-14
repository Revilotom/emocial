package Repos;

import models.Post;
import org.hamcrest.MatcherAssert;
import repositories.person.JPAPersonRepository;
import models.Person;
import org.junit.Before;
import org.junit.Test;
import play.test.WithApplication;
import repositories.signUp.JPASignUpRepository;

import java.util.concurrent.ExecutionException;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertFalse;

public class PersonRepoTest extends WithApplication {
    JPAPersonRepository repo;

    @Before
    public void before() {
        repo = app.injector().instanceOf(JPAPersonRepository.class);
        JPASignUpRepository signUpRepository = app.injector().instanceOf(JPASignUpRepository.class);

        Person person = new Person("tom oliver", "revilotom", "123456789");
        Post post = new Post("Hello");
        person.addPost(post);

        signUpRepository.add(person);

        Post post2 = new Post("Goddbye");
        Person person2 = new Person("kunal", "userk", "123456789");
        person2.addPost(post2);

        signUpRepository.add(person2);

    }

    @Test
    public void testCredentialsInvalid() throws ExecutionException, InterruptedException {
        boolean isValid = repo.credentialsAreValid("revilotom", "dasdasdasads").toCompletableFuture().get();
        assertFalse(isValid);
    }

    @Test
    public void testCredentialsValid() throws ExecutionException, InterruptedException {
        boolean isValid = repo.credentialsAreValid("revilotom", "123456789").toCompletableFuture().get();
        assertTrue(isValid);
    }

    @Test
    public void testListUsers() throws ExecutionException, InterruptedException {
        long count = repo.list().toCompletableFuture().get().count();
        assertEquals(2, count);
    }

    @Test
    public void testCanFindAUser() throws ExecutionException, InterruptedException {
        long count = repo.findByUsername("revilotom").toCompletableFuture().get().count();
        assertEquals(1, count);
    }

    @Test
    public void testPostsAreAdded() throws ExecutionException, InterruptedException {
        Person person = repo.findByUsername("revilotom").toCompletableFuture().get().findAny().get();
        MatcherAssert.assertThat(person.getPosts().size(), is(1));
        Post post = person.getPosts().get(0);
        MatcherAssert.assertThat(post.getContent(), is("Hello"));
    }


}
