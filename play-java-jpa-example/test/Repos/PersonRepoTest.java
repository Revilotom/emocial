package Repos;

import repositories.person.JPAPersonRepository;
import models.Person;
import org.junit.Before;
import org.junit.Test;
import play.test.WithApplication;
import repositories.signUp.JPASignUpRepository;

import java.util.concurrent.ExecutionException;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertFalse;

public class PersonRepoTest extends WithApplication {
    JPAPersonRepository repo;

    @Before
    public void before() {
        repo = app.injector().instanceOf(JPAPersonRepository.class);
        JPASignUpRepository signUpRepository = app.injector().instanceOf(JPASignUpRepository.class);
        Person person = new Person("tom oliver", "revilotom", "123456789");
        signUpRepository.add(person);
        Person person2 = new Person("kunal", "userk", "123456789");
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



}
