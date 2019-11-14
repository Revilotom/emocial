package Models;

import models.Person;
import org.junit.Test;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertFalse;

public class PersonModelTest {

    @Test
    public void testP() {
        String hash = Person.hashPassword("jon");
        System.out.println(hash);
    }

    @Test
    public void testHashPassword() {
        String hash = Person.hashPassword("myPassword");
        System.out.println(hash);
    }

    @Test
    public void testVerifyPasswordCorrect() {
        Person person = new Person();
        person.setHash("$2a$10$J7lxDySdqDOJCPjhOIoq3eYhSAqJyucV1/jnWb5DcBzRHvNn8K0wq");
        assertTrue(person.validatePassword("myPassword"));
    }

    @Test
    public void testVerifyPasswordWrong() {
        Person person = new Person();
        person.setHash("$2a$10$J7lxDySdqDOJCPjhOIoq3eYhSAqJyucV1/jnWb5DcBzRHvNn8K0wq");
        assertFalse(person.validatePassword("wrong"));
    }


}
