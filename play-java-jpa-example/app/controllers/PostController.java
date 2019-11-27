package controllers;

import models.Person;
import models.Post;
import play.data.Form;
import play.data.FormFactory;
import play.libs.concurrent.HttpExecutionContext;
import play.mvc.Http;
import play.mvc.Result;
import repositories.person.PersonRepository;
import repositories.post.PostRepository;
import views.html.old.makePost;

import javax.inject.Inject;
import java.util.*;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

public class PostController extends DefaultController {

    private PostRepository postRepository;

    @Inject
    public PostController(FormFactory formFactory, PersonRepository repository, PostRepository postRepository, HttpExecutionContext ec) {
        super(formFactory, repository, ec);
        this.postRepository = postRepository;
    }

    public CompletionStage<Result> getPersons() {
        return repository.stream().thenApplyAsync(stream ->
                stream.collect(Collectors.toList())).thenApplyAsync(people ->
                ok(views.html.old.persons.render(people, false)));
    }

    public CompletionStage<Result> getPosts(final Http.Request request) {
        return getLoggedInUser(request)
                .thenApply(person -> {
                   List<Post> posts = new ArrayList<>(person.map(Person::getPosts).get());
                    posts.sort(Person.ComparePosts);
                    return posts;
                })
                .thenApplyAsync(posts ->
                        ok(views.html.old.myPosts.render(posts)), ec.current());
    }

    public CompletionStage<Result> deletePost(final Http.Request request, long postId) {
        return getLoggedInUser(request)
                .thenApply(Optional::get)
                .thenApply(person -> {
                    person.deletePost(postId);
                    return person;
                })
                .thenAccept(repository::update)
                .thenApplyAsync(posts -> redirect(routes.PostController.getPosts()));
    }

    public Result makePostPage() {
        return ok(views.html.old.makePost.render(formFactory.form(Post.class)));
    }

    public Result submitPost(final Http.Request request) throws ExecutionException, InterruptedException {

        Form<Post> postForm = formFactory.form(Post.class).bindFromRequest(request);

        if (hasFormBadRequestError(postForm)) {
            return badRequest(makePost.render(postForm));
        }

        Post post = postForm.get();


        getLoggedInUser(request)
                .thenApply(Optional::get)
                .thenApply(person -> {
                    post.setOwner(person);
                    person.addPost(post);

                    System.out.println(post);
                    System.out.println(post.content);
                    return person;
                })
                .thenApply(repository::update).toCompletableFuture().get().toCompletableFuture().get();

        return redirect(routes.HomeController.home());
    }

    public Result submitLike(final Http.Request request, long postId) throws ExecutionException, InterruptedException {

        Person user = getLoggedInUser(request).toCompletableFuture().get().get();
        Post post = postRepository.findById(postId).toCompletableFuture().get().get();

        user.likePost(post);

        repository.update(user).toCompletableFuture().get();

        return redirect(routes.HomeController.home());
    }

    public Result submitDislike(final Http.Request request, long postId) throws ExecutionException, InterruptedException {

        Person user = getLoggedInUser(request).toCompletableFuture().get().get();
        Post post = postRepository.findById(postId).toCompletableFuture().get().get();

        user.dislikePost(post);

        repository.update(user).toCompletableFuture().get();

        return redirect(routes.HomeController.home());
    }

    public Result removeOpinion(final Http.Request request, long postId) throws ExecutionException, InterruptedException {

        Person user = getLoggedInUser(request).toCompletableFuture().get().get();

        user.getLikedPosts().removeIf(p -> p.getId().equals(postId));
        user.getDislikedPosts().removeIf(p -> p.getId().equals(postId));

        repository.update(user).toCompletableFuture().get();

        return redirect(routes.HomeController.home());
    }


}
