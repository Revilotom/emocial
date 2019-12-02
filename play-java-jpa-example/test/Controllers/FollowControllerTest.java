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

import javax.management.BadAttributeValueExpException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import static junit.framework.TestCase.*;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static play.test.Helpers.*;
import static play.test.Helpers.contentAsString;

public class FollowControllerTest extends WithServer {

    private Http.RequestBuilder postFollow;
    private Http.RequestBuilder postUnfollow;
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

        postFollow = fakeRequest().session("loggedIn", "username").method(POST).uri("/follow/followMe").header("Raw-Request-URI", "/follow");
        postUnfollow = fakeRequest().session("loggedIn", "username").method(POST).uri("/unFollow/anonymous").header("Raw-Request-URI", "/follow");

    }

    @After
    public void tearDown(){
        repo = null;
    }

    @Test
    public void canUnfollowPerson() throws ExecutionException, InterruptedException {
        Http.RequestBuilder tokenRequest = CSRFTokenHelper.addCSRFToken( postUnfollow);
        Result result = route(app, tokenRequest);

        Thread.sleep(500L);
        Thread.sleep(500L);

        List<Person> following = new ArrayList<>(repo.findByUsername("username").toCompletableFuture().get().get().getFollowing());

        MatcherAssert.assertThat(result.status(), is(SEE_OTHER));
        MatcherAssert.assertThat(result.header("Location").get(), is("/following"));
        MatcherAssert.assertThat(
                following.size(),
                is(0)
        );
    }


    @Test
    public void testFollowingNonExistantUserIsBadRequest() throws ExecutionException, InterruptedException {
        Http.RequestBuilder tokenRequest = CSRFTokenHelper.addCSRFToken( postFollow.uri("/follow/8577658").bodyJson(Json.toJson(new Follow())));
        Result result = route(app, tokenRequest);

        Thread.sleep(500L);
        final String body = contentAsString(result);

        MatcherAssert.assertThat(body.toLowerCase(), containsString("could not find user to follow"));

        MatcherAssert.assertThat(result.status(), is(BAD_REQUEST));
    }

    @Test
    public void canFollowPerson() throws ExecutionException, InterruptedException {
        Http.RequestBuilder tokenRequest = CSRFTokenHelper.addCSRFToken( postFollow.bodyJson(Json.toJson(new Follow())));
        Result result = route(app, tokenRequest);

        Thread.sleep(500L);

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
