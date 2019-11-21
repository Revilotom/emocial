package Controllers;

import Helpers.TestHelper;
import forms.Search;
import models.Person;
import org.hamcrest.MatcherAssert;
import org.junit.Before;
import org.junit.Test;
import play.api.test.CSRFTokenHelper;
import play.libs.Json;
import play.mvc.Http;
import play.mvc.Result;
import play.test.WithServer;
import repositories.person.JPAPersonRepository;

import java.util.concurrent.ExecutionException;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.not;
import static play.test.Helpers.*;

public class SearchControllerTest extends WithServer {
    private Http.RequestBuilder post;
    private Http.RequestBuilder get;
    private JPAPersonRepository repo;

    @Before
    public void before() throws ExecutionException, InterruptedException {
        repo = TestHelper.setup(app);

        Person loggedInUser = new Person( "dasda", "userename", "ads ");
        Person following = new Person("dasda","youFollowMe", "asdasd");
        loggedInUser.addFollowing(following);

//        repo.update(following).toCompletableFuture().get();
        repo.update(loggedInUser).toCompletableFuture().get();

        get = fakeRequest().session("loggedIn", "userename").method(GET).uri("/search").header("Raw-Request-URI", "/search");
        post = fakeRequest().session("loggedIn", "userename").method(POST).uri("/search").header("Raw-Request-URI", "/search");
    }

    @Test
    public void testSubmitSearchReturnsCorrect() {
        Search search = new Search("re");
        Http.RequestBuilder tokenRequest = CSRFTokenHelper.addCSRFToken( post.bodyJson(Json.toJson(search)));
        Result result = route(app, tokenRequest);
        final String body = contentAsString(result);

        MatcherAssert.assertThat(body.toLowerCase(), containsString("revilotom"));
        MatcherAssert.assertThat(body.toLowerCase(), not(containsString("userename")));
    }

    @Test
    public void testSubmitSearchDoesNotReturnPeopleYouAlreadyFollow() {
        Search search = new Search("F");
        Http.RequestBuilder tokenRequest = CSRFTokenHelper.addCSRFToken( post.bodyJson(Json.toJson(search)));
        Result result = route(app, tokenRequest);

        final String body = contentAsString(result);
        System.out.println(body);
        MatcherAssert.assertThat(body.toLowerCase(), not(containsString("youfollowme")));
    }

    @Test
    public void testSubmitSearchSearchDoesNotReturnIncorrect() {
        Search search = new Search("rob");
        Http.RequestBuilder tokenRequest = CSRFTokenHelper.addCSRFToken( post.bodyJson(Json.toJson(search)));
        Result result = route(app, tokenRequest);
        final String body = contentAsString(result);
        MatcherAssert.assertThat(body.toLowerCase(), not(containsString("revilotom")));
    }
}
