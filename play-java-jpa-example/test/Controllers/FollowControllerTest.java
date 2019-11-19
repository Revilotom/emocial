package Controllers;

import forms.Follow;
import forms.Login;
import models.Person;

import models.Post;
import org.hamcrest.MatcherAssert;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import play.api.test.CSRFTokenHelper;
import play.libs.Json;
import play.mvc.Http;
import play.mvc.Result;
import play.test.Helpers;
import play.test.WithServer;
import repositories.person.JPAPersonRepository;

import java.util.concurrent.ExecutionException;

import static junit.framework.TestCase.*;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static play.test.Helpers.*;
import static play.test.Helpers.contentAsString;

public class FollowControllerTest extends WithServer {

    private Http.RequestBuilder post;
    private Http.RequestBuilder getFollowing;
    private Http.RequestBuilder getFollowers;
    private JPAPersonRepository repo;

    @Before
    public void setUp() throws ExecutionException, InterruptedException {
        repo = app.injector().instanceOf(JPAPersonRepository.class);

        Person person1 = new Person("hackme", "username", "password");
        Person person2 = new Person("ok", "anonymous", "cr4cked");

        person1.addFollowing(person2);

        repo.update(person1).toCompletableFuture().get();
        Person person3 = new Person("random", "followMe", "password");
        repo.update(person3).toCompletableFuture().get();


        getFollowing = fakeRequest().session("loggedIn", "username").method(GET).uri("/following").header("Raw-Request-URI", "/following");
        getFollowers = fakeRequest().session("loggedIn", "anonymous").method(GET).uri("/followers").header("Raw-Request-URI", "/followers");

        post = fakeRequest().session("loggedIn", "username").method(POST).uri("/following").header("Raw-Request-URI", "/follow");
    }

    @After
    public void tearDown(){
        repo = null;
    }

    //TODO sometimes test randomly fail

    @Test
    public void canFollowPerson() throws ExecutionException, InterruptedException {

        Follow follow = new Follow();
        follow.setNameOfPersonToFollow("followMe");
        Http.RequestBuilder tokenRequest = CSRFTokenHelper.addCSRFToken( post.bodyJson(Json.toJson(follow)));
        Result result = route(app, tokenRequest);

        MatcherAssert.assertThat(result.status(), is(SEE_OTHER));
        MatcherAssert.assertThat(result.header("Location").get(), is("/following"));
        MatcherAssert.assertThat(
                repo.findByUsername("username").toCompletableFuture().get().get().getFollowing().size(),
                is(2));
    }

    @Test
    public void canViewFollowing() {
        Http.RequestBuilder newTokenRequest = CSRFTokenHelper.addCSRFToken(getFollowing);
        Result newResult = route(app, newTokenRequest);
        final String body = contentAsString(newResult);
        MatcherAssert.assertThat(body, containsString("anonymous"));
    }

    @Test
    public void canViewFollowers() {
        Http.RequestBuilder newTokenRequest = CSRFTokenHelper.addCSRFToken(getFollowers);
        Result newResult = route(app, newTokenRequest);
        final String body = contentAsString(newResult);
        MatcherAssert.assertThat(body, containsString("username"));
    }

    
}
