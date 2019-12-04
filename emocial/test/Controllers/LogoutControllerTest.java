package Controllers;

import org.hamcrest.MatcherAssert;
import org.junit.Test;
import play.api.test.CSRFTokenHelper;
import play.libs.Json;
import play.mvc.Http;
import play.mvc.Result;
import play.test.Helpers;
import play.test.WithServer;

import static junit.framework.TestCase.assertNull;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.Matchers.is;
import static play.mvc.Http.Status.BAD_REQUEST;
import static play.test.Helpers.*;

public class LogoutControllerTest extends WithServer {

    @Test
    public void testLogoutDeletesSession() {

        Http.RequestBuilder request = Helpers.fakeRequest()
                .method(POST)
                .uri("/logout")
                .header("Raw-Request-URI", "/logout")
                .session("loggedIn", "me");

        Http.RequestBuilder tokenRequest = CSRFTokenHelper.addCSRFToken(request);
        Result result = route(app, tokenRequest);
        MatcherAssert.assertThat(result.status(), is(SEE_OTHER));
        MatcherAssert.assertThat(result.session().getOptional("loggedIn").isPresent(), is(false));
        MatcherAssert.assertThat(result.header("Location").get(), is("/"));
    }
}
