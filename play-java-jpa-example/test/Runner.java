
import Controllers.LoginControllerTest;
import Controllers.SignUpControllerTest;
import Models.PersonModelTest;
import Models.SignUpModelTest;
import Repos.SignUpRepoTest;
import Repos.PersonRepoTest;
import controllers.LoginController;
import controllers.SignUpController;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
        SignUpModelTest.class,
        SignUpRepoTest.class,
        SignUpControllerTest.class,

        LoginControllerTest.class,

        PersonRepoTest.class,
        PersonModelTest.class,

        AcceptanceTest.class,
        IntegrationTest.class,
        UnitTest.class,
})

public class Runner {
}
