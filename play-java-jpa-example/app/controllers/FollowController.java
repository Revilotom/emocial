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
import java.util.Set;
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
        Person user = getLoggedInUser(request);
        Set<Person> people = (wantFollowing ? user.getFollowing() : user.getFollowers());
        return ok(persons.render(new ArrayList<>(people), wantFollowing));

    }

    public Result getFollowing(final Http.Request request) throws ExecutionException, InterruptedException {
        return getRelevantPeople(request, true);
    }

    public Result getFollowers(final Http.Request request) throws ExecutionException, InterruptedException {
        return getRelevantPeople(request, false);
    }

    public Result unFollow(final Http.Request request, String username) throws ExecutionException, InterruptedException {
//        getLoggedInUser(request)
//                .thenApply(Optional::get)
//                .thenApply(loggedInUser -> {loggedInUser.unFollow(username); return loggedInUser;})
//                .thenAccept(repository::update).toCompletableFuture().get();

        Person loggedInUser = getLoggedInUser(request);
        loggedInUser.unFollow(username);
        repository.update(loggedInUser).toCompletableFuture().get();

//        return CompletableFuture.supplyAsync(() -> redirect(routes.FollowController.getFollowing()));
        return redirect(routes.FollowController.getFollowing());
    }

    public Result writeFollowToDB(final Http.Request request, String username) throws ExecutionException, InterruptedException {

        Person user = getLoggedInUser(request);
        Optional<Person> maybePerson = repository.findByUsername(username).toCompletableFuture().get();

        if(maybePerson.isEmpty()){
            return badRequest("could not find user to follow");
        }

        Person userToFollow = maybePerson.get();
        user.addFollowing(userToFollow);

        repository.update(user).toCompletableFuture().get();
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

