package repositories.signUp;

import com.google.inject.ImplementedBy;

import java.util.concurrent.CompletionStage;

@ImplementedBy(JPASignUpRepository.class)
public interface SignUpRepository {
    CompletionStage<Boolean> isTaken(String username);
}
