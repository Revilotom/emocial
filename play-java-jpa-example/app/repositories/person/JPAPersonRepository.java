package repositories.person;

import models.Person;
import org.hibernate.Hibernate;
import play.db.jpa.JPAApi;
import repositories.JPADefaultRepository;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletionStage;
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
        return supplyAsync(() -> wrap(em -> getbyUsername(em, username)), executionContext);
    }

    @Override
    public CompletionStage<Stream<Person>> stream() {
        return supplyAsync(() -> wrap(em -> stream(em)), executionContext);
    }

    @Override
    public CompletionStage<Person> update(Person p) {
        return supplyAsync(() -> wrap(em -> update(em, p)), executionContext);
    }

    @Override
    public CompletionStage<Boolean> isTaken(String username) {
        return supplyAsync(() -> wrap(em -> getbyUsername(em, username).isPresent()), executionContext);
    }

    @Override
    public CompletionStage<Person> add(Person person) {
        return supplyAsync(() -> wrap(em -> add(em, person)), executionContext);
    }

    private Person add(EntityManager em, Person person) {
        em.persist(person);
        em.flush();
        return person;
    }

    private Person update(EntityManager em, Person person){
        person = em.merge(person);
        em.flush();
//        em.persist(person);
//        System.out.println(person.getPosts());
        return person;
    }

    private boolean credentialsAreValid(EntityManager em, String username, String password){
        return getbyUsername(em, username).map(person -> person.validatePassword(password)).orElse(false);
    }

    private Stream<Person> stream(EntityManager em) {
        List<Person> persons = em.createQuery("select p from Person p", Person.class).getResultList();
        persons.forEach(person -> Hibernate.initialize(person.getPosts()));
        return persons.stream();
    }

    private Optional<Person> getbyUsername(EntityManager em, String username) {
        TypedQuery<Person> query =
                em.createQuery("SELECT p FROM Person p WHERE p.username = :username", Person.class);
        try {
            Person person = query.setParameter("username", username).getSingleResult();
            Hibernate.initialize(person.getPosts());
            return Optional.of(person);
        }
        catch (NoResultException e){
            return Optional.empty();
        }
    }

}
