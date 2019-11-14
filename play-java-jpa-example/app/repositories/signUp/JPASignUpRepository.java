package repositories.signUp;

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

public class JPASignUpRepository implements SignUpRepository {
    private final JPAApi jpaApi;
    private final DatabaseExecutionContext executionContext;

    @Inject
    public JPASignUpRepository(JPAApi jpaApi, DatabaseExecutionContext executionContext) {
        this.jpaApi = jpaApi;
        this.executionContext = executionContext;
    }

    @Override
    public CompletionStage<Boolean> isTaken(String username) {
        return supplyAsync(() -> wrap(em -> getbyUsername(em, username).count() > 0), executionContext);
    }

    private <T> T wrap(Function<EntityManager, T> function) {
        return jpaApi.withTransaction(function);
    }

    private Stream<Person> getbyUsername(EntityManager em, String username) {
        TypedQuery<Person> query = em.createQuery("SELECT p FROM Person p WHERE p.username = :username", Person.class);
        List<Person> persons = query.setParameter("username", username).getResultList();
        return persons.stream();
    }
}
