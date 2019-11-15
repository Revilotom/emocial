package controllers;

import models.Person;
import models.Post;
import play.data.Form;
import play.data.FormFactory;
import play.libs.concurrent.HttpExecutionContext;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import repositories.person.PersonRepository;

import javax.inject.Inject;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

public class PostController extends Controller {

    private final FormFactory formFactory;
    private final PersonRepository personRepository;
    private final HttpExecutionContext ec;

    @Inject
    public PostController(FormFactory formFactory, PersonRepository personRepository, HttpExecutionContext ec) {
        this.formFactory = formFactory;
        this.personRepository = personRepository;
        this.ec = ec;
    }

    public CompletionStage<Result> getPersons() {
        return personRepository.stream().thenApplyAsync(stream ->
                stream.collect(Collectors.toList())).thenApplyAsync(people ->
                ok(views.html.persons.render(people)));
    }

    public CompletionStage<Result> getPosts(final Http.Request request) {
        return request.session().getOptional("loggedIn")
                .map(personRepository::findByUsername).get()
                .thenApplyAsync(maybePerson ->
                        ok(views.html.posts.render(maybePerson.get().getPosts())));
    }


    public Result makePostPage() {
        return ok(views.html.makePost.render(formFactory.form(Post.class)));
    }

    public CompletionStage<Result> submitMakePost(final Http.Request request) {

        Form<Post> postForm = formFactory.form(Post.class).bindFromRequest(request);

        if (postForm.hasErrors() || postForm.hasGlobalErrors()) {
            return CompletableFuture.supplyAsync(() -> badRequest(views.html.makePost.render(postForm)), ec.current());
        }

        Post post = postForm.get();

        return request.session().getOptional("loggedIn")
                .map(personRepository::findByUsername).get()
                .thenApplyAsync(Optional::get)
                .thenApplyAsync(person -> {
                            post.setOwner(person);
                            person.addPost(post);
                            return person;
                }).thenApplyAsync(personRepository::update)
                .thenApplyAsync(personCompletionStage -> redirect(routes.PostController.getPosts()));
    }
}
