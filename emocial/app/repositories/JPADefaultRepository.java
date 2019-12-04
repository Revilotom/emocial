package repositories;

import play.db.jpa.JPAApi;
import repositories.person.DatabaseExecutionContext;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import java.util.function.Function;

public abstract class JPADefaultRepository {
    private final JPAApi jpaApi;
    protected final DatabaseExecutionContext executionContext;

    @Inject
    public JPADefaultRepository(JPAApi jpaApi, DatabaseExecutionContext executionContext) {
        this.jpaApi = jpaApi;
        this.executionContext = executionContext;
    }

    protected <T> T wrap(Function<EntityManager, T> function) {
        return jpaApi.withTransaction(function);
    }


}
