package controllers;

import forms.Search;
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
import java.util.stream.Collectors;

public class SearchController extends Controller {

    private final FormFactory formFactory;
    private final PersonRepository personRepository;
    private final HttpExecutionContext ec;

    @Inject
    public SearchController(FormFactory formFactory, PersonRepository personRepository, HttpExecutionContext ec) {
        this.formFactory = formFactory;
        this.personRepository = personRepository;
        this.ec = ec;
    }

    public Result searchPage(){
        return ok(views.html.old.search.render(formFactory.form(Search.class), new ArrayList<>()));
    }

    public CompletionStage<Result> submitSearch(final Http.Request request) {

        Form<Search> searchForm = formFactory.form(Search.class).bindFromRequest(request);

        if (searchForm.hasErrors() || searchForm.hasGlobalErrors()) {
            return CompletableFuture.supplyAsync(() -> badRequest(views.html.old.search.render(searchForm, new ArrayList<>())), ec.current());
        }

        return personRepository.search(searchForm.get().getSearchTerms())
                .thenApplyAsync(stream -> ok(views.html.old.search.render(formFactory.form(Search.class),
                        stream.collect(Collectors.toList()))), ec.current());
    }

}
