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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Set;
import java.util.concurrent.ExecutionException;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.Matchers.is;
import static play.test.Helpers.*;

public class PostControllerTest extends WithServer {
    private Http.RequestBuilder postRemoveOpinion;
    private Http.RequestBuilder postCreate;
    private Http.RequestBuilder postDelete;
    private Http.RequestBuilder postLike;
    private Http.RequestBuilder postDislike;
    private Http.RequestBuilder get;
    private JPAPersonRepository repo;
    private Http.RequestBuilder getPersonsPosts;
    private Http.RequestBuilder getPersonsPostsUserDoesNotExist;

    @Before
    public void setUp() throws ExecutionException, InterruptedException {
        repo = app.injector().instanceOf(JPAPersonRepository.class);

        Person otherUser = new Person("hello", "revilotom", "123456789");
        otherUser.addPost(new Post("My name is tom!"));
        repo.update(otherUser).toCompletableFuture().get();
        System.out.println(repo.findByUsername("revilotom").toCompletableFuture().get().get());


        Person loggedInUser = new Person("hackme", "username", "password");
        Post firstPost = new Post("thisIsATest");
        loggedInUser.addPost(firstPost);
        repo.update(loggedInUser).toCompletableFuture().get();

        loggedInUser = repo.findByUsername("username").toCompletableFuture().get().get();

        long firstPostId =  new ArrayList<>(loggedInUser.getPosts()).get(0).getId();

        get =        fakeRequest().session("loggedIn", "username")
                .method(GET).uri("/myPosts").header("Raw-Request-URI", "/myPosts");
        postCreate = fakeRequest().session("loggedIn", "username")
                .method(POST).uri("/makePost").header("Raw-Request-URI", "/makePost");
        postDelete = fakeRequest().session("loggedIn", "username")
                .method(POST).uri("/deletePost/" + firstPostId).header("Raw-Request-URI", "/deletePost");
        postLike =   fakeRequest().session("loggedIn", "username")
                .method(POST).uri("/like/" + firstPostId).header("Raw-Request-URI", "/like");
        postDislike =fakeRequest().session("loggedIn", "username")
                .method(POST).uri("/dislike/" + firstPostId).header("Raw-Request-URI", "/dislike");
        postRemoveOpinion = fakeRequest().session("loggedIn", "username")
                .method(POST).uri("/removeOpinion/" + firstPostId).header("Raw-Request-URI", "/removeOpinion");
        getPersonsPosts = fakeRequest().session("loggedIn", "username")
                .method(GET).uri("/posts/revilotom").header("Raw-Request-URI", "/posts/revilotom");
        getPersonsPostsUserDoesNotExist = fakeRequest().session("loggedIn", "username")
                .method(GET).uri("/posts/asda").header("Raw-Request-URI", "/posts/asda");
    }

    @After
    public void tearDown() {
        repo = null;
    }

    @Test
    public void canDeletePost() throws ExecutionException, InterruptedException {
        Http.RequestBuilder tokenRequest = CSRFTokenHelper.addCSRFToken(postDelete);
        Result result = route(app, tokenRequest);
        Thread.sleep(500L); // wait for the second post to be written to the DB.
        MatcherAssert.assertThat(result.status(), is(SEE_OTHER));

        MatcherAssert.assertThat(result.header("Location").get(), is("/myPosts"));
        Set<Post> posts = repo.findByUsername("username").toCompletableFuture().get().get().getPosts();
        MatcherAssert.assertThat(posts.size(), is(0));
    }


    @Test
    public void canMakePost() throws ExecutionException, InterruptedException {

        Post secondPost = new Post("\uD83D\uDC69\uD83C\uDFFF\u200D\uD83C\uDF3E");
        Http.RequestBuilder tokenRequest = CSRFTokenHelper.addCSRFToken(postCreate.bodyJson(Json.toJson(secondPost)));
        Result result = route(app, tokenRequest);

        Thread.sleep(1000L); // wait for the second post to be written to the DB.

        MatcherAssert.assertThat(result.status(), is(SEE_OTHER));
        MatcherAssert.assertThat(result.header("Location").get(), is("/home"));
        Set<Post> posts = repo.findByUsername("username").toCompletableFuture().get().get().getPosts();
        MatcherAssert.assertThat(posts.size(), is(2));

//        MatcherAssert.assertThat(contentAsString(result), containsString("second post"));

    }


    @Test
    public void cannotMakePostIfMaxLengthIsExceeded() throws ExecutionException, InterruptedException {

        char[] chars = new char[300];
        Arrays.fill(chars, '@');

        Post firstPost = new Post(new String(chars));

        Http.RequestBuilder tokenRequest = CSRFTokenHelper.addCSRFToken(postCreate.bodyJson(Json.toJson(firstPost)));
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


    @Test
    public void testBadRequestIfPostToBeLikedDoesNotExist() throws InterruptedException, ExecutionException {

        Http.RequestBuilder tokenRequest = CSRFTokenHelper.addCSRFToken(postLike.uri("/like/3123123"));

        Result result = route(app, tokenRequest);

        Thread.sleep(1000L); // wait for the second post to be written to the DB.

        final String body = contentAsString(result);

        MatcherAssert.assertThat(body.toLowerCase(), containsString("could not find post"));
        MatcherAssert.assertThat(result.status(), is(BAD_REQUEST));
    }



    @Test
    public void testCanLikePost() throws InterruptedException, ExecutionException {

        Http.RequestBuilder tokenRequest = CSRFTokenHelper.addCSRFToken(postLike);

        Result result = route(app, tokenRequest);

        Thread.sleep(1000L); // wait for the second post to be written to the DB.

        MatcherAssert.assertThat(result.status(), is(SEE_OTHER));
        MatcherAssert.assertThat(result.header("Location").get(), is("/home"));

        Set<Post> posts = repo.findByUsername("username").toCompletableFuture().get().get().getPosts();
        MatcherAssert.assertThat(new ArrayList<>(posts).get(0).likers.size(), is(1));
    }

    @Test
    public void testCanDislikePost() throws InterruptedException, ExecutionException {

        Http.RequestBuilder tokenRequest = CSRFTokenHelper.addCSRFToken(postDislike);

        Result result = route(app, tokenRequest);

        Thread.sleep(1000L); // wait for the second post to be written to the DB.

        MatcherAssert.assertThat(result.status(), is(SEE_OTHER));
        MatcherAssert.assertThat(result.header("Location").get(), is("/home"));

        Set<Post> posts = repo.findByUsername("username").toCompletableFuture().get().get().getPosts();
        MatcherAssert.assertThat(new ArrayList<>(posts).get(0).dislikers.size(), is(1));
    }

    @Test
    public void testCanRemoveOpinionLike() throws InterruptedException, ExecutionException {

        Http.RequestBuilder tokenRequest = CSRFTokenHelper.addCSRFToken(postLike);

        Result result = route(app, tokenRequest);

        tokenRequest = CSRFTokenHelper.addCSRFToken(postRemoveOpinion);

        result = route(app, tokenRequest);

        Thread.sleep(1000L); // wait for the second post to be written to the DB.

        MatcherAssert.assertThat(result.status(), is(SEE_OTHER));
        MatcherAssert.assertThat(result.header("Location").get(), is("/home"));

        Set<Post> posts = repo.findByUsername("username").toCompletableFuture().get().get().getPosts();
        MatcherAssert.assertThat(new ArrayList<>(posts).get(0).likers.size(), is(0));
    }

    @Test
    public void testCanRemoveOpinionDislike() throws InterruptedException, ExecutionException {

        Http.RequestBuilder tokenRequest = CSRFTokenHelper.addCSRFToken(postDislike);

        Result result = route(app, tokenRequest);

        tokenRequest = CSRFTokenHelper.addCSRFToken(postRemoveOpinion);

        result = route(app, tokenRequest);

        Thread.sleep(1000L); // wait for the second post to be written to the DB.

        MatcherAssert.assertThat(result.status(), is(SEE_OTHER));
        MatcherAssert.assertThat(result.header("Location").get(), is("/home"));

        Set<Post> posts = repo.findByUsername("username").toCompletableFuture().get().get().getPosts();
        MatcherAssert.assertThat(new ArrayList<>(posts).get(0).dislikers.size(), is(0));
    }


    @Test
    public void testCanShowAPersonsPostsFailsIfUserDoesntExist() throws InterruptedException, ExecutionException {

        Http.RequestBuilder tokenRequest = CSRFTokenHelper.addCSRFToken(getPersonsPostsUserDoesNotExist);
        Result result = route(app, tokenRequest);

        final String body = contentAsString(result);
        MatcherAssert.assertThat(body.toLowerCase(), containsString("could not find user with username"));
        MatcherAssert.assertThat(result.status(), is(BAD_REQUEST));
    }

    @Test
    public void testCanShowAPersonsPosts() throws InterruptedException, ExecutionException {

        Http.RequestBuilder tokenRequest = CSRFTokenHelper.addCSRFToken(getPersonsPosts);

        Result result = route(app, tokenRequest);

        Thread.sleep(1000L); // wait for the second post to be written to the DB.

        final String body = contentAsString(result);

        MatcherAssert.assertThat(body, containsString("name is tom"));

        MatcherAssert.assertThat(result.status(), is(OK));
    }
}

//TODO make everything private
