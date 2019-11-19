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

    // add following form

    public Result makeFollowPage() {
        return ok(views.html.old.followPerson.render(formFactory.form(Follow.class)));
    }

    public CompletionStage<Result> getFollowing(final Http.Request request) {
        return request.session().getOptional("loggedIn")
                .map(repository::findByUsername).get()
                .thenApply(person -> person.map(Person::getFollowing))
                .thenApplyAsync(list -> ok(views.html.old.persons.render(list.orElseGet(ArrayList::new))));
    }

    public CompletionStage<Result> getFollowers(final Http.Request request) {
        return request.session().getOptional("loggedIn")
                .map(repository::findByUsername).get()
                .thenApply(person -> person.map(Person::getFollowers))
                .thenApplyAsync(list -> ok(views.html.old.persons.render(list.orElseGet(ArrayList::new))));
    }

    public CompletionStage<Result> submitFollow(final Http.Request request) {

        Form<Follow> followForm = formFactory.form(Follow.class).bindFromRequest(request);

        if (followForm.hasErrors() || followForm.hasGlobalErrors()) {
            return CompletableFuture.supplyAsync(() -> badRequest(views.html.old.followPerson.render(followForm)), ec.current());
        }

        Follow follow = followForm.get();

        return request.session().getOptional("loggedIn")
                .map(repository::findByUsername).get()
                .thenApplyAsync(Optional::get)
                .thenAcceptAsync(loggedInUser -> repository.findByUsername(follow.getNameOfPersonToFollow())
                        .thenApply(Optional::get)
                        .thenApply(loggedInUser::addFollowing)
                        .thenApply(repository::update))
                .thenApplyAsync(personCompletionStage -> redirect(routes.FollowController.getFollowing()));
    }
}

