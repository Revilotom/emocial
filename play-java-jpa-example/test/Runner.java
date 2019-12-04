import Controllers.*;
import Models.PersonModelTest;
import Models.PostModelTest;
import Repos.PersonRepoTest;
import Repos.PostRepoTest;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
        PersonModelTest.class,
        PostModelTest.class,

        PersonRepoTest.class,
        PostRepoTest.class,

        PostControllerTest.class,
        FollowControllerTest.class,
        LoginControllerTest.class,
        LogoutControllerTest.class,
        PostControllerTest.class,
        SearchControllerTest.class,
        SignUpControllerTest.class,

        TestEmojiHelpers.class,
        AcceptanceTest.class
})

public class Runner {
}
