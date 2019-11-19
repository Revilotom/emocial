
import Controllers.FollowControllerTest;
import Controllers.LoginControllerTest;
import Controllers.PostControllerTest;
import Controllers.SignUpControllerTest;
import Models.PersonModelTest;
import Models.SignUpModelTest;

import Repos.PersonRepoTest;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
        SignUpModelTest.class,
        SignUpControllerTest.class,
        LoginControllerTest.class,
        PostControllerTest.class,
        FollowControllerTest.class,

        PersonRepoTest.class,
        PersonModelTest.class,

//        AcceptanceTest.class,
//        IntegrationTest.class,
//        UnitTest.class,
})

public class Runner {
}
