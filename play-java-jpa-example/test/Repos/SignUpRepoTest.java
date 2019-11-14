package Repos;

import repositories.person.JPAPersonRepository;
import models.Person;
import repositories.signUp.JPASignUpRepository;
import org.junit.Before;
import org.junit.Test;
import play.test.WithApplication;

import java.util.concurrent.ExecutionException;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertFalse;

public class SignUpRepoTest extends WithApplication {
    private JPAPersonRepository personRepository;
    private JPASignUpRepository signUpRepository;

    @Before
    public void before() {
        personRepository = app.injector().instanceOf(JPAPersonRepository.class);
        signUpRepository = app.injector().instanceOf(JPASignUpRepository.class);

        Person person = new Person("tom", "revilotom", "blah");
        personRepository.add(person);
        Person person2 = new Person("kunal", "userk", " blah");
        personRepository.add(person2);
    }

    @Test
    public void testUsernameIsTaken() throws ExecutionException, InterruptedException {
        Boolean taken = signUpRepository.isTaken("revilotom").toCompletableFuture().get();
        assertTrue(taken);
    }

    @Test
    public void testUsernameIsNotTaken() throws ExecutionException, InterruptedException {
        Boolean taken = signUpRepository.isTaken("jojo").toCompletableFuture().get();
        assertFalse(taken);
    }
}
