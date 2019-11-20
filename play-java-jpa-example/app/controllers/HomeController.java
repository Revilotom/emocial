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
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

public class HomeController extends DefaultController {


    @Inject
    public HomeController(FormFactory formFactory, PersonRepository repository, HttpExecutionContext ec) {
        super(formFactory, repository, ec);
    }

    public Result home(final Http.Request request) throws ExecutionException, InterruptedException {
        Optional<Person> maybe = getLoggedInUser(request).toCompletableFuture().get();
        Person user = maybe.get();
        String username = user.getUsername();
        List<Post> nF = user.getNewsFeed();
        return ok(views.html.old.home.render(username,nF));
    }
}
