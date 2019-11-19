package Repos;

import Helpers.TestHelper;
import models.FollowRelation;
import models.Post;
import org.hamcrest.MatcherAssert;
import org.junit.After;
import repositories.person.JPAPersonRepository;
import models.Person;
import org.junit.Before;
import org.junit.Test;
import play.test.WithApplication;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertFalse;

public class PersonRepoTest extends WithApplication {
    private JPAPersonRepository repo;

    @Before
    public void before() throws ExecutionException, InterruptedException {
        repo = TestHelper.setup(app);
    }

    @After
    public void after(){
        repo = null;
    }


    @Test
    public void testFollowersAreAdded() throws ExecutionException, InterruptedException {

        Person person = repo.findByUsername("revilotom").toCompletableFuture().get().get();
        Person person2 = repo.findByUsername("usekk").toCompletableFuture().get().get();

//        MatcherAssert.assertThat(person.getFollowers().size(), is(1));
//        Post post = person.getPosts().get(0);
//        MatcherAssert.assertThat(post.getContent(), is("Hello"));

        FollowRelation f = new FollowRelation();
        f.setFrom(person2);
        f.setTo(person);
//
        person.addFollower(f);
        person2.addFollowing(f);

        repo.update(person);
        repo.update(person2);

        System.out.println(repo.stream().toCompletableFuture().get().map(x -> x.getFollowers()).collect(Collectors.toList()));
        System.out.println(repo.stream().toCompletableFuture().get().map(x -> x.getFollowers()).collect(Collectors.toList()));

        System.out.println(repo.stream().toCompletableFuture().get().map(x -> x.getFollowers()).collect(Collectors.toList()));

        System.out.println(repo.stream().toCompletableFuture().get().map(x -> x.getFollowers()).collect(Collectors.toList()));

        System.out.println(repo.stream().toCompletableFuture().get().map(x -> x.getFollowers()).collect(Collectors.toList()));


    }


    @Test
    public void testPostsAreAdded() throws ExecutionException, InterruptedException {
        Person person = repo.findByUsername("revilotom").toCompletableFuture().get().get();
        MatcherAssert.assertThat(person.getPosts().size(), is(1));
        Post post = person.getPosts().get(0);
        MatcherAssert.assertThat(post.getContent(), is("Hello"));
    }

    @Test
    public void testCredentialsInvalidPassword() throws ExecutionException, InterruptedException {
        boolean isValid = repo.credentialsAreValid("revilotom", "dasdasdasads").toCompletableFuture().get();
        assertFalse(isValid);
    }

    @Test
    public void testCredentialsInvalidUsername() throws ExecutionException, InterruptedException {
        boolean isValid = repo.credentialsAreValid("blah", "dasdasdasads").toCompletableFuture().get();
        assertFalse(isValid);
    }

    @Test
    public void testCredentialsValid() throws ExecutionException, InterruptedException {
        boolean isValid = repo.credentialsAreValid("revilotom", "123456789").toCompletableFuture().get();
        assertTrue(isValid);
    }

    @Test
    public void testListUsers() throws ExecutionException, InterruptedException {
        long count = repo.stream().toCompletableFuture().get().count();
        assertEquals(4, count);
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


    @Test
    public void testSearchNoResults() throws ExecutionException, InterruptedException {
        List<Person> list = repo.search("nothing").toCompletableFuture().get().collect(Collectors.toList());
        MatcherAssert.assertThat(list.isEmpty(), is(true));
    }

    @Test
    public void testPartialSearch1Result() throws ExecutionException, InterruptedException {
        List<Person> list = repo.search("rev").toCompletableFuture().get().collect(Collectors.toList());
        MatcherAssert.assertThat(list.get(0).getUsername(), is("revilotom"));
    }

    @Test
    public void testPartialSearch3Results() throws ExecutionException, InterruptedException {
        List<Person> list = repo.search("r").toCompletableFuture().get().collect(Collectors.toList());
        MatcherAssert.assertThat(list.size(), is(3));
    }
}
