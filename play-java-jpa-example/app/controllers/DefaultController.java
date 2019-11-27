package controllers;

import akka.NotUsed;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.stream.ActorMaterializer;
import akka.stream.Materializer;
import akka.stream.OverflowStrategy;
import akka.stream.javadsl.Sink;
import akka.stream.javadsl.Source;
import models.Person;
import play.data.Form;
import play.data.FormFactory;
import play.libs.concurrent.HttpExecutionContext;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import play.twirl.api.Html;
import repositories.person.PersonRepository;

import javax.inject.Inject;
import java.util.Arrays;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

public abstract class DefaultController extends Controller {
    final FormFactory formFactory;
    final PersonRepository repository;
    final HttpExecutionContext ec;

    @Inject
    public DefaultController(FormFactory formFactory, PersonRepository repository, HttpExecutionContext ec) {
        this.formFactory = formFactory;
        this.repository = repository;
        this.ec = ec;
    }

    public CompletionStage<Optional<Person>> getLoggedInUser(final Http.Request request){
        return request.session().getOptional("loggedIn")
                .map(repository::findByUsername).get();
    }

    protected boolean hasFormBadRequestError(Form<?> form) { // part
        return form.hasErrors() || form.hasGlobalErrors();
    }
    protected CompletableFuture<Result> supplyAsyncBadRequest(Html html) { // part
        return CompletableFuture.supplyAsync(() -> badRequest(html));
    }
}
