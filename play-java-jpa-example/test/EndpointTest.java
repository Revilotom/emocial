
import play.Application;
import play.libs.Json;
import play.mvc.Http;
import play.mvc.Result;
import play.test.WithApplication;

import static org.junit.Assert.assertEquals;
import static play.test.Helpers.*;

abstract class EndpointTest extends WithApplication {

    void testEndpoint(String body, String path, String method, int expectedStatus, String content) {
        Application app = fakeApplication();
        running(app, () -> {
            Http.RequestBuilder req = new Http.RequestBuilder().method(method).uri(path);
            req = req.bodyJson(Json.parse(body));

            Result res = route(app, req);
            assertEquals(expectedStatus, res.status());
            assertEquals(content, contentAsString(res));
        });
    }
}