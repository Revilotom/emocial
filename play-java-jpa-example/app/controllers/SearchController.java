package controllers;

import forms.Search;
import models.Person;
import play.data.Form;
import play.data.FormFactory;
import play.libs.concurrent.HttpExecutionContext;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import repositories.person.PersonRepository;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

public class SearchController extends DefaultController {

    @Inject
    public SearchController(FormFactory formFactory, PersonRepository repository, HttpExecutionContext ec) {
        super(formFactory, repository, ec);
    }

    public Result searchPage(){
        return ok(views.html.old.search.render(formFactory.form(Search.class), new ArrayList<>()));
    }

    public CompletionStage<Result> submitSearch(final Http.Request request) throws ExecutionException, InterruptedException {




        Form<Search> searchForm = formFactory.form(Search.class).bindFromRequest(request);

        if (searchForm.hasErrors() || searchForm.hasGlobalErrors()) {
            return CompletableFuture.supplyAsync(() ->
                    badRequest(views.html.old.search.render(searchForm, new ArrayList<>())), ec.current());
        }

        //TODO this is probably bad
        Person logggedInUser = getLoggedInUser(request).toCompletableFuture().get().get();

        return repository.search(searchForm.get().getSearchTerms())
                .thenApplyAsync(stream -> ok(views.html.old.search.render(formFactory.form(Search.class),
                        stream.filter(p -> !p.getUsername().equals(logggedInUser.getUsername())).collect(Collectors.toList()))), ec.current());
    }

}
