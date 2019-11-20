package Controllers;

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

public class PostControllerTest extends WithServer {
    private Http.RequestBuilder post;
    private Http.RequestBuilder get;
    private JPAPersonRepository repo;

    @Before
    public void setUp() throws ExecutionException, InterruptedException {
        repo = app.injector().instanceOf(JPAPersonRepository.class);

        Person person = new Person("hackme", "username", "password");
        Post firstPost = new Post("thisIsATest");
        firstPost.setOwner(person);
        person.addPost(firstPost);
        repo.update(person).toCompletableFuture().get();

        get = fakeRequest().session("loggedIn", "username").method(GET).uri("/posts").header("Raw-Request-URI", "/posts");
        post = fakeRequest().session("loggedIn", "username").method(POST).uri("/makePost").header("Raw-Request-URI", "/makePost");
    }

    @After
    public void tearDown(){
        repo = null;
    }

    //TODO sometimes test randomly fail

    @Test
    public void canMakePost() throws ExecutionException, InterruptedException {

        Post firstPost = new Post("second post");
        Http.RequestBuilder tokenRequest = CSRFTokenHelper.addCSRFToken( post.bodyJson(Json.toJson(firstPost)));
        Result result = route(app, tokenRequest);

        MatcherAssert.assertThat(result.status(), is(SEE_OTHER));
        MatcherAssert.assertThat(result.header("Location").get(), is("/posts"));
        MatcherAssert.assertThat(
                repo.findByUsername("username").toCompletableFuture().get().get().getPosts().get(1).content,
                is("second post"));
    }

    @Test
    public void canViewPosts() {
        Http.RequestBuilder newTokenRequest = CSRFTokenHelper.addCSRFToken(get);
        Result newResult = route(app, newTokenRequest);
        final String body = contentAsString(newResult);
        MatcherAssert.assertThat(body, containsString("thisIsATest"));
    }
}
