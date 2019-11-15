package Controllers;

import Helpers.TestHelper;
import forms.Search;
import org.hamcrest.MatcherAssert;
import org.junit.Before;
import org.junit.Test;
import play.api.test.CSRFTokenHelper;
import play.libs.Json;
import play.mvc.Http;
import play.mvc.Result;
import play.test.WithServer;
import repositories.person.JPAPersonRepository;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.not;
import static play.test.Helpers.*;

public class SearchControllerTest extends WithServer {
    private Http.RequestBuilder post;
    private Http.RequestBuilder get;
    private JPAPersonRepository repo;

    @Before
    public void before() {
        repo = TestHelper.setup(app);
        get = fakeRequest().session("loggedIn", "username").method(GET).uri("/search").header("Raw-Request-URI", "/search");
        post = fakeRequest().session("loggedIn", "username").method(POST).uri("/search").header("Raw-Request-URI", "/search");
    }

    @Test
    public void testSubmitSearchReturnsCorrect() {
        Search search = new Search("re");
        Http.RequestBuilder tokenRequest = CSRFTokenHelper.addCSRFToken( post.bodyJson(Json.toJson(search)));
        Result result = route(app, tokenRequest);
        final String body = contentAsString(result);
        MatcherAssert.assertThat(body.toLowerCase(), containsString("revilotom"));
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
