package Repos;

import models.Post;
import repositories.person.JPAPersonRepository;
import models.Person;
import org.junit.Before;
import org.junit.Test;
import play.test.WithApplication;

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

        Person person = new Person("tom oliver", "revilotom", "123456789");
        Post post = new Post("Hello");
        person.addPost(post);
//        repo.update(person);
        repo.add(person);



        Post post2 = new Post("Goddbye");
        Person person2 = new Person("kunal", "userk", "123456789");
        person2.addPost(post2);

        repo.add(person2);

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
        assertTrue(repo.findByUsername("revilotom").toCompletableFuture().get().isPresent());
    }

    @Test
    public void testUsernameIsTaken() throws ExecutionException, InterruptedException {
        Boolean taken = repo.isTaken("revilotom").toCompletableFuture().get();
        assertTrue(taken);
    }

    @Test
    public void testUsernameIsNotTaken() throws ExecutionException, InterruptedException {
        Boolean taken = repo.isTaken("jojo").toCompletableFuture().get();
        assertFalse(taken);
    }
//
//    @Test
//    public void testPostsAreAdded() throws ExecutionException, InterruptedException {
//        Person person = repo.findByUsername("revilotom").toCompletableFuture().get().get();
//        MatcherAssert.assertThat(person.getPosts().size(), is(1));
//        Post post = person.getPosts().get(0);
//        MatcherAssert.assertThat(post.getContent(), is("Hello"));
//    }


}
