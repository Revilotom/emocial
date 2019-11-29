package controllers;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.stream.ActorMaterializer;
import akka.stream.Materializer;
import akka.stream.OverflowStrategy;
import akka.stream.javadsl.Sink;
import akka.stream.javadsl.Source;
import forms.Login;
import play.data.Form;
import play.data.FormFactory;
import play.libs.EventSource;
import play.libs.concurrent.HttpExecutionContext;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import play.twirl.api.Html;
import repositories.person.PersonRepository;
import views.html.old.login;

import javax.inject.Inject;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;


public class LoginController extends DefaultController {

    final ActorSystem system = ActorSystem.create("QuickStart");
    final Materializer materializer = ActorMaterializer.create(system);

    final Source<String, ActorRef> source = Source.actorRef(1000, OverflowStrategy.dropHead());
    final ActorRef ref = source
            .map(elem -> {
                System.out.println(elem);
                return elem;
            })
            .to(Sink.foreach(elem -> System.out.println("sinking")))
            .run(materializer);


    @Inject
    public LoginController(FormFactory formFactory, PersonRepository repository, HttpExecutionContext ec) {
        super(formFactory, repository, ec);
    }

    public Result index() {
//        ref.tell("MESSAGE", ActorRef.noSender());
        return ok(views.html.old.login.render(formFactory.form(Login.class)));
    }

    public Result sse() {
        final Source<EventSource.Event, ?> eventSource = source.map(EventSource.Event::event);
        return ok().chunked(eventSource.via(EventSource.flow())).as(Http.MimeTypes.EVENT_STREAM);
    }

    public CompletionStage<Result> submitLogin(final Http.Request request) {
        Form<Login> loginForm = formFactory.form(Login.class).bindFromRequest(request);

        if (hasFormBadRequestError(loginForm)) {
            return supplyAsyncBadRequest(login.render(loginForm));
        }

        Login login = loginForm.get();

        return repository.credentialsAreValid(login.getUsername(), login.getPassword()).thenApplyAsync(isValid -> {
            if (isValid){
                return redirect(routes.HomeController.home())
                        .addingToSession(request, "loggedIn", login.getUsername());
            }

            String invalidCredentials = "INVALID CREDENTIALS!";

            return badRequest(
                    views.html.old.login.render(loginForm
                        .withError("username", invalidCredentials)
                        .withError("password", invalidCredentials)
                    )
            );
        }, ec.current());
    }
}
