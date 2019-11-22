package controllers;

import models.Person;
import models.Post;
import play.data.Form;
import play.data.FormFactory;
import play.libs.concurrent.HttpExecutionContext;
import play.mvc.Http;
import play.mvc.Result;
import repositories.person.PersonRepository;
import views.html.old.makePost;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.stream.Collectors;

public class PostController extends DefaultController {


    @Inject
    public PostController(FormFactory formFactory, PersonRepository repository, HttpExecutionContext ec) {
        super(formFactory, repository, ec);
    }

    public CompletionStage<Result> getPersons() {
        return repository.stream().thenApplyAsync(stream ->
                stream.collect(Collectors.toList())).thenApplyAsync(people ->
                ok(views.html.old.persons.render(people, false)));
    }

    public CompletionStage<Result> getPosts(final Http.Request request) {
        return getLoggedInUser(request)
                .thenApply(person -> person.map(Person::getPosts))
                .thenApplyAsync(posts ->
                        ok(views.html.old.myPosts.render(posts.orElseGet(ArrayList::new))));
    }

    public CompletionStage<Result> deletePost(final Http.Request request, long postId) {
        return getLoggedInUser(request)
                .thenApply(Optional::get)
                .thenApply(person -> {person.deletePost(postId); return person;})
                .thenAccept(repository::update)
                .thenApplyAsync(posts -> redirect(routes.PostController.getPosts()));
    }

    public Result makePostPage() {
        return ok(views.html.old.makePost.render(formFactory.form(Post.class)));
    }

    public CompletionStage<Result> submitPost(final Http.Request request) {

        Form<Post> postForm = formFactory.form(Post.class).bindFromRequest(request);

        if (hasFormBadRequestError(postForm)){
            return supplyAsyncBadRequest(makePost.render(postForm));
        }

        Post post = postForm.get();

        return getLoggedInUser(request)
                .thenApply(Optional::get)
                .thenApply(person -> {
                            post.setOwner(person);
                            person.addPost(post);
                            return person;
                })
                .thenApply(repository::update)
                .thenApply(personCompletionStage -> redirect(routes.HomeController.home()));
    }
}
