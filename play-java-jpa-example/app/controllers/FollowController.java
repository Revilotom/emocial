package controllers;

import forms.Follow;
import models.Person;
import models.Post;
import play.data.Form;
import play.data.FormFactory;
import play.libs.concurrent.HttpExecutionContext;
import play.mvc.Http;
import play.mvc.Result;
import repositories.person.PersonRepository;
import views.html.old.followPerson;
import views.html.old.persons;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ExecutionException;


public class FollowController extends DefaultController {

    @Inject
    public FollowController(FormFactory formFactory, PersonRepository repository, HttpExecutionContext ec) {
        super(formFactory, repository, ec);
    }

    public Result makeFollowPage() {
        return ok(views.html.old.followPerson.render(formFactory.form(Follow.class)));
    }

    private Result getRelevantPeople(final Http.Request request, boolean wantFollowing) throws ExecutionException, InterruptedException {
//        return getLoggedInUser(request)
//                .thenApply(person -> person.map(wantFollowing ? Person::getFollowing : Person::getFollowers))
//                .thenApplyAsync(list ->
//                        ok(views.html.old.persons.render(list.orElseGet(ArrayList::new), wantFollowing)), ec.current());
        Optional<Person> maybe = getLoggedInUser(request).toCompletableFuture().get();
        Person user = maybe.get();
        List<Person> people = wantFollowing ? user.getFollowing() : user.getFollowers();

        return ok(persons.render(people, wantFollowing));

    }

    public Result getFollowing(final Http.Request request) throws ExecutionException, InterruptedException {
        return getRelevantPeople(request, true);
    }

    public Result getFollowers(final Http.Request request) throws ExecutionException, InterruptedException {
        return getRelevantPeople(request, false);
    }

    public CompletionStage<Result> unFollow(final Http.Request request, String username) {
        getLoggedInUser(request)
                .thenApply(Optional::get)
                .thenApply(loggedInUser -> {loggedInUser.unFollow(username); return loggedInUser;})
                .thenAccept(repository::update);

        return CompletableFuture.supplyAsync(() -> redirect(routes.FollowController.getFollowing()));
    }

    public Result writeFollowToDB(final Http.Request request, String username) throws ExecutionException, InterruptedException {

        getLoggedInUser(request)
                .thenApply(Optional::get)
                .thenApply(loggedInUser -> repository.findByUsername(username)
                        .thenApply(Optional::get)
                        .thenApply(loggedInUser::addFollowing)
                        .thenApply(repository::update))
                .toCompletableFuture().get().toCompletableFuture().get().toCompletableFuture().get();

        return redirect(routes.FollowController.getFollowing());
    }


    public Result submitFollowWithUsername(final Http.Request request, String username) throws ExecutionException, InterruptedException {
        return writeFollowToDB(request, username);
    }


    public Result submitFollow(final Http.Request request) throws ExecutionException, InterruptedException {

        Form<Follow> followForm = formFactory.form(Follow.class).bindFromRequest(request);

        if (hasFormBadRequestError(followForm)){
            return badRequest(followPerson.render(followForm));
        }

        Follow follow = followForm.get();

        return writeFollowToDB(request, follow.getUsernameOfPersonToFollow());
    }
}

