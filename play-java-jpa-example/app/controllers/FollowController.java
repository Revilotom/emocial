package controllers;

import forms.Follow;
import models.Person;
import play.data.Form;
import play.data.FormFactory;
import play.libs.concurrent.HttpExecutionContext;
import play.mvc.Http;
import play.mvc.Result;
import repositories.person.PersonRepository;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;


public class FollowController extends DefaultController {

    @Inject
    public FollowController(FormFactory formFactory, PersonRepository repository, HttpExecutionContext ec) {
        super(formFactory, repository, ec);
    }

    public Result makeFollowPage() {
        return ok(views.html.old.followPerson.render(formFactory.form(Follow.class)));
    }

    private CompletionStage<Result> getRelevantPeople(final Http.Request request, boolean following){
        return getLoggedInUser(request)
                .thenApply(person -> person.map(following ? Person::getFollowing : Person::getFollowers))
                .thenApplyAsync(list ->
                        ok(views.html.old.persons.render(list.orElseGet(ArrayList::new), following)), ec.current());
    }

    public CompletionStage<Result> getFollowing(final Http.Request request) {
        return getRelevantPeople(request, true);
    }

    public CompletionStage<Result> getFollowers(final Http.Request request) {
        return getRelevantPeople(request, false);
    }
    public CompletionStage<Result> submitFollowWithUsername(final Http.Request request, String username) {

        return getLoggedInUser(request)
                .thenApply(Optional::get)
                .thenApply(loggedInUser -> repository.findByUsername(username)
                        .thenApply(Optional::get)
                        .thenApply(loggedInUser::addFollowing)
                        .thenApply(repository::update))
                .thenApply(personCompletionStage -> redirect(routes.FollowController.getFollowing()));
    }

    public CompletionStage<Result> submitFollow(final Http.Request request) {

        Form<Follow> followForm = formFactory.form(Follow.class).bindFromRequest(request);

        if (followForm.hasErrors() || followForm.hasGlobalErrors()) {
            return CompletableFuture.supplyAsync(() -> badRequest(views.html.old.followPerson.render(followForm)), ec.current());
        }

        Follow follow = followForm.get();

        return getLoggedInUser(request)
                .thenApply(Optional::get)
                .thenApply(loggedInUser -> repository.findByUsername(follow.getNameOfPersonToFollow())
                .thenApply(Optional::get)
                .thenApply(loggedInUser::addFollowing)
                .thenApply(repository::update))
                .thenApply(personCompletionStage -> redirect(routes.FollowController.getFollowing()));
    }
}

