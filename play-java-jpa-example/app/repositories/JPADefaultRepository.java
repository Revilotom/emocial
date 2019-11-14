package repositories;

import models.Person;
import org.hibernate.Hibernate;
import play.db.jpa.JPAApi;
import repositories.person.DatabaseExecutionContext;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;

public abstract class JPADefaultRepository {
    public final JPAApi jpaApi;
    public final DatabaseExecutionContext executionContext;

    @Inject
    public JPADefaultRepository(JPAApi jpaApi, DatabaseExecutionContext executionContext) {
        this.jpaApi = jpaApi;
        this.executionContext = executionContext;
    }

    protected <T> T wrap(Function<EntityManager, T> function) {

        return jpaApi.withTransaction(function);
    }

    protected Stream<Person> getbyUsername(EntityManager em, String username) {
        TypedQuery<Person> query = em.createQuery("SELECT p FROM Person p WHERE p.username = :username", Person.class);
        List<Person> persons = query.setParameter("username", username).getResultList();
        persons.forEach(person -> Hibernate.initialize(person.getPosts()));
        return persons.stream();
    }
}
