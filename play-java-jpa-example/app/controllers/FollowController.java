package controllers;

import forms.Follow;
import models.FollowRelation;
import models.Person;
import models.Post;
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
import java.util.stream.Collectors;


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
                .thenApply(person ->
                        person.map(Person::getFollowing)
                                .map(following -> following.stream().map(FollowRelation::getTo)))
                .thenApplyAsync(following -> following.map(f -> f.collect(Collectors.toList())))
                .thenApplyAsync(list -> ok(views.html.old.persons.render(list.orElseGet(ArrayList::new))));
    }

    public CompletionStage<Result> getFollowers(final Http.Request request) {
        return request.session().getOptional("loggedIn")
                .map(repository::findByUsername).get()
                .thenApply(person ->
                        person.map(Person::getFollowers)
                                .map(followers -> followers.stream().map(FollowRelation::getFrom)))
                .thenApplyAsync(followers -> followers.map(f -> f.collect(Collectors.toList())))
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
                .thenApplyAsync(loggedInUser -> {
                    repository.findByUsername(follow.getNameOfPersonToFollow())
                            .thenApplyAsync(Optional::get)
                            .thenApplyAsync(personToFollow -> new FollowRelation(loggedInUser, personToFollow))
                            .thenAcceptAsync(followRelation -> {
                                followRelation.getFrom().addFollowing(followRelation);
//                                followRelation.getTo().addFollower(followRelation);
                                repository.update(followRelation.getFrom()).toCompletableFuture();
                                repository.update(followRelation.getTo());
                            });
                    return redirect(routes.FollowController.getFollowing());
                });
    }
}

