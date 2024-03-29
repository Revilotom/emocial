package Controllers;

import forms.Login;
import models.Person;

import org.hamcrest.MatcherAssert;
import org.junit.Before;
import org.junit.Test;
import play.api.test.CSRFTokenHelper;
import play.libs.Json;
import play.mvc.Http;
import play.mvc.Result;
import play.test.Helpers;
import play.test.WithServer;
import repositories.person.JPAPersonRepository;

import java.util.Optional;
import java.util.concurrent.ExecutionException;

import static junit.framework.TestCase.*;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static play.test.Helpers.*;
import static play.test.Helpers.contentAsString;

public class LoginControllerTest extends WithServer {
    private Login login;
    private Http.RequestBuilder post;
    private JPAPersonRepository repo;

    @Before
    public void setUp() throws ExecutionException, InterruptedException {
        repo = app.injector().instanceOf(JPAPersonRepository.class);
        repo.update(new Person("hackme", "username", "password")).toCompletableFuture().get();

        login = new Login("username", "password");
        post = Helpers.fakeRequest()
                .method(POST)
                .uri("/login")
                .header("Raw-Request-URI", "/login");
    }

    @Test
    public void testFieldsAreRendered() {
        Http.RequestBuilder request = Helpers.fakeRequest()
                .method(GET)
                .uri("/")
                .header("Raw-Request-URI", "/login");

        Http.RequestBuilder tokenRequest = CSRFTokenHelper.addCSRFToken(request);

        Result result = route(app, tokenRequest);
        final String body = contentAsString(result);

        assertThat(body, containsString("username"));
        assertThat(body, containsString("password"));
    }

    @Test
    public void testWhenUsernameIsMissing() {

        login.setUsername(null);
        Http.RequestBuilder tokenRequest = CSRFTokenHelper.addCSRFToken( post.bodyJson(Json.toJson(login)));

        Result result = route(app, tokenRequest);
        final String body = contentAsString(result);
        MatcherAssert.assertThat(body, containsString("Error[username]: This field is required"));
    }

    @Test
    public void testWhenPasswordIsMissing() {

        login.setPassword(null);
        Http.RequestBuilder tokenRequest = CSRFTokenHelper.addCSRFToken( post.bodyJson(Json.toJson(login)));

        Result result = route(app, tokenRequest);
        final String body = contentAsString(result);
        MatcherAssert.assertThat(body, containsString("Error[password]: This field is required"));
    }

    @Test
    public void testSuccessfulLogin() {
        Http.RequestBuilder tokenRequest = CSRFTokenHelper.addCSRFToken( post.bodyJson(Json.toJson(login)));
        Result result = route(app, tokenRequest);
        final String body = contentAsString(result);
        MatcherAssert.assertThat(body, is(""));
        MatcherAssert.assertThat(result.status(), is(SEE_OTHER));
        MatcherAssert.assertThat(result.header("Location").get(), is("/home"));
        assertTrue(result.session().getOptional("loggedIn").isPresent());
    }

    @Test
    public void testFailedLogin() {
        login.setPassword("dasdasdasdasdasdas");
        Http.RequestBuilder tokenRequest = CSRFTokenHelper.addCSRFToken( post.bodyJson(Json.toJson(login)));
        Result result = route(app, tokenRequest);
        MatcherAssert.assertThat(result.status(), is(BAD_REQUEST));
        MatcherAssert.assertThat(result.session().getOptional("loggedIn"), is(Optional.empty()));
        final String body = contentAsString(result);
        MatcherAssert.assertThat(body.toLowerCase(), containsString("invalid credentials"));
    }


}