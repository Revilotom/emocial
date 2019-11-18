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
import java.util.ArrayList;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

public class PostController extends DefaultController {


    @Inject
    public PostController(FormFactory formFactory, PersonRepository repository, HttpExecutionContext ec) {
        super(formFactory, repository, ec);
    }

    public CompletionStage<Result> getPersons() {
        return repository.stream().thenApplyAsync(stream ->
                stream.collect(Collectors.toList())).thenApplyAsync(people ->
                ok(views.html.old.persons.render(people)));
    }

    public CompletionStage<Result> getPosts(final Http.Request request) {
        return request.session().getOptional("loggedIn")
                .map(repository::findByUsername).get()
                .thenApply(person -> person.map(Person::getPosts))
                .thenApplyAsync(posts ->
                        ok(views.html.old.posts.render(posts.orElseGet(ArrayList::new))));
    }


    public Result makePostPage() {
        return ok(views.html.old.makePost.render(formFactory.form(Post.class)));
    }

    public CompletionStage<Result> submitMakePost(final Http.Request request) {

        Form<Post> postForm = formFactory.form(Post.class).bindFromRequest(request);

        if (postForm.hasErrors() || postForm.hasGlobalErrors()) {
            return CompletableFuture.supplyAsync(() -> badRequest(views.html.old.makePost.render(postForm)), ec.current());
        }

        Post post = postForm.get();

        return request.session().getOptional("loggedIn")
                .map(repository::findByUsername).get()
                .thenApplyAsync(Optional::get)
                .thenApply(person -> {
                            post.setOwner(person);
                            person.addPost(post);
                            return person;
                }).thenApplyAsync(repository::update)
                .thenApplyAsync(personCompletionStage -> redirect(routes.PostController.getPosts()));
    }
}
