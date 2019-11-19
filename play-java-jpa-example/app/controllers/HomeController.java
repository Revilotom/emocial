package controllers;

import models.Person;
import play.mvc.Http;
import repositories.person.PersonRepository;

import play.data.FormFactory;
import play.libs.concurrent.HttpExecutionContext;
import play.mvc.Controller;
import play.mvc.Result;

import javax.inject.Inject;
import java.util.Optional;
import java.util.concurrent.CompletionStage;

public class HomeController extends DefaultController {


    @Inject
    public HomeController(FormFactory formFactory, PersonRepository repository, HttpExecutionContext ec) {
        super(formFactory, repository, ec);
    }

    public CompletionStage<Result> home(final Http.Request request) {
        return getLoggedInUser(request)
                .thenApplyAsync(user -> user.map(Person::getUsername))
                .thenApplyAsync(username -> ok(views.html.old.home.render(username.orElse(""))));
    }
}
