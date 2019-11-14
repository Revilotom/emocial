package repositories.person;

import models.Person;
import play.db.jpa.DefaultJPAApi;
import play.db.jpa.JPAApi;
import repositories.JPADefaultRepository;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletionStage;
import java.util.function.Function;
import java.util.stream.Stream;

import static java.util.concurrent.CompletableFuture.supplyAsync;

/**
 * Provide JPA operations running inside of a thread pool sized to the connection pool
 */
public class JPAPersonRepository extends JPADefaultRepository implements PersonRepository {

    @Inject
    public JPAPersonRepository(JPAApi jpaApi, DatabaseExecutionContext executionContext) {
        super(jpaApi, executionContext);
    }

    @Override
    public CompletionStage<Boolean> credentialsAreValid(String username, String password) {
        return supplyAsync(() -> wrap(em -> credentialsAreValid(em, username, password)), executionContext);
    }

    @Override
    public CompletionStage<Optional<Person>> findByUsername(String username) {
        return supplyAsync(() -> wrap(em -> getbyUsername(em, username).findAny()), executionContext);
    }

    @Override
    public CompletionStage<Stream<Person>> list() {
        return supplyAsync(() -> wrap(em -> list(em)), executionContext);
    }

    @Override
    public CompletionStage<Person> save(Person p) {
        return supplyAsync(() -> wrap(em -> save(em, p)), executionContext);
    }

    private Person save (EntityManager em, Person person){
        person = em.merge(person);
        em.flush();
//        em.persist(person);
        System.out.println(person.getPosts());
        return person;
    }

    private boolean credentialsAreValid(EntityManager em, String username, String password){
       return getbyUsername(em, username).anyMatch(person -> person.validatePassword(password));
    }

    private Stream<Person> list(EntityManager em) {
        List<Person> persons = em.createQuery("select p from Person p", Person.class).getResultList();
        return persons.stream();
    }

}
