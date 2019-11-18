package Controllers;

import models.Person;
import forms.SignUp;
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

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static play.test.Helpers.*;

public class SignUpControllerTest extends WithServer {

    private SignUp signUp;
    private Http.RequestBuilder post;
    private JPAPersonRepository repo;

    @Before
    public void setUp() {
        repo = app.injector().instanceOf(JPAPersonRepository.class);
        repo.update(new Person("mattori", "mimichu", "1233123"));

        signUp = new SignUp("tom oliver", "revilotom", "123456789", "123456789");

        post = Helpers.fakeRequest()
                .method(POST)
                .uri("/signUp")
                .header("Raw-Request-URI", "/signUp");
    }

    @Test
    public void testWhenUsernameTaken() {
        signUp.setUsername("mimichu");
        Http.RequestBuilder tokenRequest = CSRFTokenHelper.addCSRFToken( post.bodyJson(Json.toJson(signUp)));
        Result result = route(app, tokenRequest);
        final String body = contentAsString(result);
        MatcherAssert.assertThat(body.toLowerCase(), containsString("username taken"));
    }


    @Test
    public void testFieldsAreRendered() {
        Http.RequestBuilder request = Helpers.fakeRequest()
                .method(GET)
                .uri("/signUp")
                .header("Raw-Request-URI", "/signUp");

        // XXX This should be play.test.CSRFTokenHelper
        Http.RequestBuilder tokenRequest = CSRFTokenHelper.addCSRFToken(request);

        Result result = route(app, tokenRequest);
        final String body = contentAsString(result);

        assertThat(body, containsString("name"));
        assertThat(body, containsString("username"));
        assertThat(body, containsString("password1"));
        assertThat(body, containsString("password2"));
    }


    @Test
    public void testWhenPasswordIsNotAlphanumeric() {
        signUp.setPassword1("***********");
        signUp.setPassword2("***********");
        Http.RequestBuilder tokenRequest = CSRFTokenHelper.addCSRFToken( post.bodyJson(Json.toJson(signUp)));

        Result result = route(app, tokenRequest);
        final String body = contentAsString(result);
        MatcherAssert.assertThat(body, containsString("Error[password2]: Must satisfy ^[a-zA-Z0-9 ]*$"));
    }

    @Test
    public void testWhenUsernameIsNotAlphanumeric() {
        signUp.setUsername("***********");
        Http.RequestBuilder tokenRequest = CSRFTokenHelper.addCSRFToken( post.bodyJson(Json.toJson(signUp)));

        Result result = route(app, tokenRequest);
        final String body = contentAsString(result);
        MatcherAssert.assertThat(body, containsString("Error[username]: Must satisfy ^[a-zA-Z0-9 ]*$"));
    }

    @Test
    public void testWhenNameToShort() {
        signUp.setName("1");

        Http.RequestBuilder tokenRequest = CSRFTokenHelper.addCSRFToken( post.bodyJson(Json.toJson(signUp)));

        Result result = route(app, tokenRequest);
        final String body = contentAsString(result);
        MatcherAssert.assertThat(body, containsString("Error[name]: Minimum length is 5"));
    }

    @Test
    public void testWhenUsernameToShort() {

        signUp.setUsername("1");

        Http.RequestBuilder tokenRequest = CSRFTokenHelper.addCSRFToken( post.bodyJson(Json.toJson(signUp)));

        Result result = route(app, tokenRequest);
        final String body = contentAsString(result);
        MatcherAssert.assertThat(body, containsString("Error[username]: Minimum length is 5"));
    }

    @Test
    public void testWhenPasswordToShort() {
        signUp.setPassword1("1");
        signUp.setPassword2("1");

        Http.RequestBuilder tokenRequest = CSRFTokenHelper.addCSRFToken( post.bodyJson(Json.toJson(signUp)));

        Result result = route(app, tokenRequest);
        final String body = contentAsString(result);
        MatcherAssert.assertThat(body, containsString("Error[password2]: Minimum length is 8"));
    }


    @Test
    public void testWhenFieldIsMissing() {

        signUp.setUsername(null);
        Http.RequestBuilder tokenRequest = CSRFTokenHelper.addCSRFToken( post.bodyJson(Json.toJson(signUp)));

        Result result = route(app, tokenRequest);
        final String body = contentAsString(result);
        MatcherAssert.assertThat(body, containsString("Error[username]: This field is required"));
    }

    @Test
    public void testWhenValidNoError() {
        Http.RequestBuilder tokenRequest = CSRFTokenHelper.addCSRFToken( post.bodyJson(Json.toJson(signUp)));
        Result result = route(app, tokenRequest);
        final String body = contentAsString(result);
        MatcherAssert.assertThat(body, is(""));
    }

    @Test
    public void testWhenPasswordsAreDifferentErrorsAreShown()  {

        signUp.setPassword1("SDASSDADSA");

        Http.RequestBuilder tokenRequest = CSRFTokenHelper.addCSRFToken( post.bodyJson(Json.toJson(signUp)));

        Result result = route(app, tokenRequest);
        final String body = contentAsString(result);
        MatcherAssert.assertThat(body, containsString("Passwords dont match!"));

    }
}
