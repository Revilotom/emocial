package controllers;

import models.Person;
import models.Post;
import play.data.Form;
import play.data.FormFactory;
import play.libs.concurrent.HttpExecutionContext;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import play.twirl.api.Html;
import repositories.person.PersonRepository;

import javax.inject.Inject;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

public abstract class DefaultController extends Controller {
    final FormFactory formFactory;
    final PersonRepository repository;
    final HttpExecutionContext ec;

    @Inject
    public DefaultController(FormFactory formFactory, PersonRepository repository, HttpExecutionContext ec) {
        this.formFactory = formFactory;
        this.repository = repository;
        this.ec = ec;
    }

    Person getLoggedInUser(final Http.Request request) throws ExecutionException, InterruptedException {
        Optional<Person> maybeUSer = request.session().getOptional("loggedIn")
                .map(repository::findByUsername).get().toCompletableFuture().get();

        assert maybeUSer.isPresent();
        return maybeUSer.get();
    }

    boolean hasFormBadRequestError(Form<?> form) { // part
        return form.hasErrors() || form.hasGlobalErrors();
    }

    CompletableFuture<Result> supplyAsyncBadRequest(Html html) { // part
        return CompletableFuture.supplyAsync(() -> badRequest(html));
    }


    List<Long> postIdsThatYouLiked(List<Post> input, Person loggedInUser) {
        return input.stream().map(Post::getId).filter(pId ->
                loggedInUser.getLikedPosts().stream().map(Post::getId)
                        .collect(Collectors.toSet()).contains(pId)).collect(Collectors.toList());
    }

    List<Long> postIdsThatYouDisliked(List<Post> input, Person loggedInUser) {
        return input.stream().map(Post::getId).filter(pId ->
                loggedInUser.getDislikedPosts().stream().map(Post::getId)
                        .collect(Collectors.toSet()).contains(pId)).collect(Collectors.toList());
    }


    // TODO make sure sort by rating also takes into account time
    // TODO Pagination
    // TODO Allow sort by rating
    // TODO Show the cuernt view as highlhited on the nav bar
    // TODO HTTPS?
    // TODO Make landing page
    // TODO Allow comemting
    // TODO test for verylong names/usernames
    // TODO search includes names not just usernames
    // TODO get rid of all fotn awesome
    // TODO Move everything from the head into a static assets directory.


}
