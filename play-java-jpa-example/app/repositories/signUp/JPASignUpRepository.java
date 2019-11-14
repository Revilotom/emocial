package repositories.signUp;

import repositories.JPADefaultRepository;
import repositories.person.DatabaseExecutionContext;
import models.Person;
import play.db.jpa.JPAApi;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.util.List;
import java.util.concurrent.CompletionStage;
import java.util.function.Function;
import java.util.stream.Stream;

import static java.util.concurrent.CompletableFuture.supplyAsync;

public class JPASignUpRepository extends JPADefaultRepository implements SignUpRepository  {

    @Inject
    public JPASignUpRepository(JPAApi jpaApi, DatabaseExecutionContext executionContext) {
        super(jpaApi, executionContext);
    }

    @Override
    public CompletionStage<Boolean> isTaken(String username) {
        return supplyAsync(() -> wrap(em -> getbyUsername(em, username).count() > 0), executionContext);
    }

    @Override
    public CompletionStage<Person> add(Person person) {
        return supplyAsync(() -> wrap(em -> insert(em, person)), executionContext);
    }

    private Person insert(EntityManager em, Person person) {
        em.persist(person);
        return person;
    }
}
