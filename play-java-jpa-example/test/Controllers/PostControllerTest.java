package Controllers;

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
import play.test.WithServer;
import repositories.person.JPAPersonRepository;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static play.test.Helpers.*;
import static play.test.Helpers.contentAsString;

public class PostControllerTest extends WithServer {
    private Http.RequestBuilder postCreate;
    private Http.RequestBuilder postDelete;
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

        get = fakeRequest().session("loggedIn", "username").method(GET).uri("/myPosts").header("Raw-Request-URI", "/myPosts");
        postCreate = fakeRequest().session("loggedIn", "username").method(POST).uri("/makePost").header("Raw-Request-URI", "/makePost");
        postDelete= fakeRequest().session("loggedIn", "username").method(POST).uri("/deletePost/" + firstPost.id).header("Raw-Request-URI", "/deletePost");
    }

    @After
    public void tearDown(){
        repo = null;
    }

    @Test
    public void canDeletePost() throws ExecutionException, InterruptedException {
        Http.RequestBuilder tokenRequest = CSRFTokenHelper.addCSRFToken( postDelete);
        Result result = route(app, tokenRequest);
        Thread.sleep(500L); // wait for the second post to be written to the DB.
        MatcherAssert.assertThat(result.status(), is(SEE_OTHER));

        MatcherAssert.assertThat(result.header("Location").get(), is("/myPosts"));
        List<Post> posts = repo.findByUsername("username").toCompletableFuture().get().get().getPosts();
        MatcherAssert.assertThat(posts.size(), is(0));
    }

    //TODO sometimes test randomly fail

    @Test
    public void canMakePost() throws ExecutionException, InterruptedException {

        Post secondPost = new Post("second post");
        Http.RequestBuilder tokenRequest = CSRFTokenHelper.addCSRFToken( postCreate.bodyJson(Json.toJson(secondPost)));
        Result result = route(app, tokenRequest);

        Thread.sleep(500L); // wait for the second post to be written to the DB.

        MatcherAssert.assertThat(result.status(), is(SEE_OTHER));
//        MatcherAssert.assertThat(result.header("Location").get(), is("/myPosts"));
        List<Post> posts = repo.findByUsername("username").toCompletableFuture().get().get().getPosts();
        MatcherAssert.assertThat(
                posts.get(1).content,
                is("second post"));
    }

    @Test
    public void cannotMakePostIfMaxLengthIsExceeded() throws ExecutionException, InterruptedException {

        char[] chars = new char[300];
        Arrays.fill(chars, '@');

        Post firstPost = new Post(new String(chars));

        Http.RequestBuilder tokenRequest = CSRFTokenHelper.addCSRFToken( postCreate.bodyJson(Json.toJson(firstPost)));
        Result result = route(app, tokenRequest);

        MatcherAssert.assertThat(result.status(), is(BAD_REQUEST));
        MatcherAssert.assertThat(contentAsString(result), containsString("max"));
    }

    @Test
    public void canViewPosts() {
        Http.RequestBuilder newTokenRequest = CSRFTokenHelper.addCSRFToken(get);
        Result newResult = route(app, newTokenRequest);
        final String body = contentAsString(newResult);
        MatcherAssert.assertThat(body, containsString("thisIsATest"));
    }
}
