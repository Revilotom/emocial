package controllers;

import models.Person;
import models.Post;
import play.mvc.Http;
import repositories.person.PersonRepository;

import play.data.FormFactory;
import play.libs.concurrent.HttpExecutionContext;
import play.mvc.Controller;
import play.mvc.Result;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletionStage;
import java.util.stream.Collectors;

public class HomeController extends DefaultController {


    @Inject
    public HomeController(FormFactory formFactory, PersonRepository repository, HttpExecutionContext ec) {
        super(formFactory, repository, ec);
    }

    public CompletionStage<Result> home(final Http.Request request) {
        return getLoggedInUser(request)
                .thenApplyAsync(maybeUser ->{
                    Person user = maybeUser.orElse(null);
                    assert user != null;
                    return ok(views.html.old.home.render(user.getUsername(), user.getNewsFeed()));
                });
    }
}
