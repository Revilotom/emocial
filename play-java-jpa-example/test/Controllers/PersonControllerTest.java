package Controllers;

import forms.Login;
import models.Person;

import models.Post;
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

import static junit.framework.TestCase.*;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static play.test.Helpers.*;
import static play.test.Helpers.contentAsString;

public class PersonControllerTest extends WithServer {
    private Login l;
    private Http.RequestBuilder post;
    private Http.RequestBuilder get;
    private JPAPersonRepository repo;

    @Before
    public void setUp() {
        repo = app.injector().instanceOf(JPAPersonRepository.class);

        Person person = new Person("hackme", "username", "password");
        Post firstPost = new Post("TEst post");

        person.addPost(firstPost);

        repo.add(person);

        Http.RequestBuilder req = fakeRequest()
                .session("loggedIn", "username");

        post = req.method(POST).uri("/makePost").header("Raw-Request-URI", "/makePost");

        get = req.method(GET).uri("/posts").header("Raw-Request-URI", "/posts");
    }


    @Test
    public void canMakePost() {

        Post firstPost = new Post("blah blah blah...");
        Http.RequestBuilder tokenRequest = CSRFTokenHelper.addCSRFToken( post.bodyJson(Json.toJson(firstPost)));
        Result result = route(app, tokenRequest);
//        final String body = contentAsString(result);
        MatcherAssert.assertThat(result.status(), is(SEE_OTHER));
        MatcherAssert.assertThat(result.header("Location").get(), is("/posts"));
//        System.out.println(body);

    }

    @Test
    public void canViewPosts() {


        Post firstPost = new Post("blah blah blah...");
        Http.RequestBuilder tokenRequest = CSRFTokenHelper.addCSRFToken( post.bodyJson(Json.toJson(firstPost)));
        Result result = route(app, tokenRequest);

        Http.RequestBuilder newTokenRequest = CSRFTokenHelper.addCSRFToken(get);
        Result newResult = route(app, newTokenRequest);
        final String body = contentAsString(newResult);
        System.out.println(body);

    }
}
