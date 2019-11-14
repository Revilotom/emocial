package repositories.person;

import com.google.inject.ImplementedBy;
import models.Person;

import java.util.concurrent.CompletionStage;
import java.util.stream.Stream;

/**
 * This interface provides a non-blocking API for possibly blocking operations.
 */
@ImplementedBy(JPAPersonRepository.class)
public interface PersonRepository {

    CompletionStage<Boolean> credentialsAreValid(String username, String password);

    CompletionStage<Stream<Person>>  findByUsername(String username);

    CompletionStage<Stream<Person>> list();

//    CompletionStage<Person> save(Person p);
}
