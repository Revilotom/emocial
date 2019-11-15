import models.Person;
import repositories.person.PersonRepository;
import org.junit.Test;
import play.api.test.CSRFTokenHelper;
import play.mvc.Http;
import play.test.Helpers;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import static java.util.concurrent.CompletableFuture.supplyAsync;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static play.test.Helpers.contentAsString;

/**
 * Simple (JUnit) tests that can call all parts of a play app.
 * <p>
 * https://www.playframework.com/documentation/latest/JavaTest
 */
public class UnitTest extends EndpointTest {

    @Test
    public void checkIndex() {
//
//        Http.RequestBuilder request = CSRFTokenHelper.addCSRFToken(Helpers.fakeRequest("GET", "/"));
//
//        PersonRepository repository = mock(PersonRepository.class);
//        FormFactory formFactory = mock(FormFactory.class);
//        HttpExecutionContext ec = new HttpExecutionContext(ForkJoinPool.commonPool());
//        final PersonController controller = new PersonController(formFactory, repository, ec);
//        final Result result = controller.index(request.build());
//
//        assertThat(result.status()).isEqualTo(OK);
    }

    @Test
    public void checkTemplate() {
        Http.RequestBuilder request = CSRFTokenHelper.addCSRFToken(Helpers.fakeRequest("GET", "/"));
//        Content html = views.html.index.render(request.build());
//        assertThat(html.contentType()).isEqualTo("text/html");
//        assertThat(contentAsString(html)).contains("Add Person");
    }

    @Test
    public void getPerson() throws ExecutionException, InterruptedException {
        PersonRepository repository = mock(PersonRepository.class);
        Person person = new Person();
        person.id = 1L;
        person.name = "Steve";
        List<Person> ps = new ArrayList<>();
        ps.add(person);
        when(repository.stream()).thenReturn(supplyAsync(() -> ps.stream()));

        System.out.println(repository.stream().toCompletableFuture().get().collect(Collectors.toList()));
    }

//    @Test
//    public void checkAddPersonSuccess() throws ExecutionException, InterruptedException {
//        // Don't need to be this involved in setting up the mock, but for demo it works:
//        PersonRepository repository = mock(PersonRepository.class);
//        Person person = new Person("steve", "stevewonder101", "password1234");
//        when(repository.add(any())).thenReturn(supplyAsync(() -> person));
//
//        System.out.println(Json.toJson(person).toString());
//
//        testEndpoint(
//                "{\"username\": \"tester\", \"name\": \"tester\", \"password\": \"tester\"}",
//                "/person",
//                POST,
//                OK,
//                ""
//        );
//    }

//    @Test
//    public void checkAddPersonNameTaken() throws ExecutionException, InterruptedException {
//        // Don't need to be this involved in setting up the mock, but for demo it works:
//        PersonRepository repository = mock(PersonRepository.class);
//        when(repository.isTaken(any())).thenReturn(supplyAsync(() -> true));
//
//        testEndpoint(
//                "{\"username\": \"tester\", \"name\": \"tester\", \"password\": \"tester\"}",
//                "/person",
//                POST,
//                OK,
//                ""
//        );
//
//        testEndpoint(
//                "{\"username\": \"tester\", \"name\": \"tester\", \"password\": \"tester\"}",
//                "/person",
//                POST,
//                BAD_REQUEST,
//                ""
//        );
//    }

}
