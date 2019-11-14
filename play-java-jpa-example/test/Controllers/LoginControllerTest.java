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
import repositories.signUp.JPASignUpRepository;

import java.util.concurrent.ExecutionException;

import static junit.framework.TestCase.*;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static play.test.Helpers.*;
import static play.test.Helpers.contentAsString;

public class LoginControllerTest extends WithServer {
    private Login l;
    private Http.RequestBuilder post;
    private JPASignUpRepository repo;

    @Before
    public void setUp() {
        repo = app.injector().instanceOf(JPASignUpRepository.class);
        repo.add(new Person("hackme", "username", "password"));

        l = new Login("username", "password");
        post = Helpers.fakeRequest()
                .method(POST)
                .uri("/login")
                .header("Raw-Request-URI", "/login");
    }

    @Test
    public void testFieldsAreRendered() {
        Http.RequestBuilder request = Helpers.fakeRequest()
                .method(GET)
                .uri("/");

        Http.RequestBuilder tokenRequest = CSRFTokenHelper.addCSRFToken(request);

        Result result = route(app, tokenRequest);
        final String body = contentAsString(result);

        assertThat(body, containsString("username"));
        assertThat(body, containsString("password"));
    }

    @Test
    public void testWhenUsernameIsMissing() throws ExecutionException, InterruptedException {

        l.setUsername(null);
        Http.RequestBuilder tokenRequest = CSRFTokenHelper.addCSRFToken( post.bodyJson(Json.toJson(l)));

        Result result = route(app, tokenRequest);
        final String body = contentAsString(result);
        MatcherAssert.assertThat(body, containsString("username [error.required]"));
    }

    @Test
    public void testWhenPasswordIsMissing() throws ExecutionException, InterruptedException {

        l.setPassword(null);
        Http.RequestBuilder tokenRequest = CSRFTokenHelper.addCSRFToken( post.bodyJson(Json.toJson(l)));

        Result result = route(app, tokenRequest);
        final String body = contentAsString(result);
        MatcherAssert.assertThat(body, containsString("password [error.required]"));
    }

    @Test
    public void testSuccessfulLogin() {
        Http.RequestBuilder tokenRequest = CSRFTokenHelper.addCSRFToken( post.bodyJson(Json.toJson(l)));
        Result result = route(app, tokenRequest);
        final String body = contentAsString(result);
        MatcherAssert.assertThat(body, is(""));
        MatcherAssert.assertThat(result.status(), is(SEE_OTHER));
        MatcherAssert.assertThat(result.header("Location").get(), is("/home"));
        assertTrue(result.session().getOptional("loggedIn").isPresent());
    }

    @Test
    public void testFailedLogin() {
        l.setPassword("dasdasdasdasdasdas");
        Http.RequestBuilder tokenRequest = CSRFTokenHelper.addCSRFToken( post.bodyJson(Json.toJson(l)));
        Result result = route(app, tokenRequest);
        MatcherAssert.assertThat(result.status(), is(BAD_REQUEST));
        assertNull(result.session());
        final String body = contentAsString(result);
        MatcherAssert.assertThat(body.toLowerCase(), containsString("invalid credentials"));
    }


}