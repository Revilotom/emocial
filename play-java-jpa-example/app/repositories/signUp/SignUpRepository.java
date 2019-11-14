package repositories.signUp;

import com.google.inject.ImplementedBy;
import models.Person;

import java.util.concurrent.CompletionStage;

@ImplementedBy(JPASignUpRepository.class)
public interface SignUpRepository {
    CompletionStage<Boolean> isTaken(String username);
    CompletionStage<Person> add(Person person);
}
