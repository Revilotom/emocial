package repositories.person;

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

/**
 * Provide JPA operations running inside of a thread pool sized to the connection pool
 */
public class JPAPersonRepository implements PersonRepository {

    private final JPAApi jpaApi;
    private final DatabaseExecutionContext executionContext;

    @Inject
    public JPAPersonRepository(JPAApi jpaApi, DatabaseExecutionContext executionContext) {
        this.jpaApi = jpaApi;
        this.executionContext = executionContext;
    }

    @Override
    public CompletionStage<Boolean> credentialsAreValid(String username, String password) {
        return supplyAsync(() -> wrap(em -> credentialsAreValid(em, username, password)), executionContext);
    }

    @Override
    public CompletionStage<Person> add(Person person) {
        return supplyAsync(() -> wrap(em -> insert(em, person)), executionContext);
    }

    @Override
    public CompletionStage<Stream<Person>> findByUsername(String username) {
        return supplyAsync(() -> wrap(em -> getbyUsername(em, username)), executionContext);
    }

    @Override
    public CompletionStage<Stream<Person>> list() {
        return supplyAsync(() -> wrap(em -> list(em)), executionContext);
    }


    private <T> T wrap(Function<EntityManager, T> function) {
        return jpaApi.withTransaction(function);
    }

    private boolean credentialsAreValid(EntityManager em, String username, String password){
       return getbyUsername(em, username).anyMatch(person -> person.validatePassword(password));
    }

    private Person insert(EntityManager em, Person person) {
        em.persist(person);
        return person;
    }

    private Stream<Person> list(EntityManager em) {
        List<Person> persons = em.createQuery("select p from Person p", Person.class).getResultList();
        return persons.stream();
    }

    private Stream<Person> getbyUsername(EntityManager em, String username) {
        TypedQuery<Person> query = em.createQuery("SELECT p FROM Person p WHERE p.username = :username", Person.class);
        List<Person> persons = query.setParameter("username", username).getResultList();
        return persons.stream();
    }
}
