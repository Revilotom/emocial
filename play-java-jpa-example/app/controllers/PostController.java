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
import scala.concurrent.impl.FutureConvertersImpl;
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

//    public CompletionStage<Result> getOtherPersonsPosts(Long personId){
//
//    }
//
//    public CompletionStage<Set<Post>> getPostsByUsername(String username){
//        return repository.findByUsername(username)
//                .thenApplyAsync(maybePerson -> {
//                    if (maybePerson.isPresent()){
//                        return maybePerson.get().getPosts();
//                    }
//                    return new HashSet<>();
//                });
//    }


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
                .thenApplyAsync(voidVar -> redirect(routes.PostController.getPosts()));
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
                    return person;
                })
                .thenApply(repository::update).toCompletableFuture().get().toCompletableFuture().get();

        return redirect(routes.HomeController.home());
    }

    private Result handleOpinion(final Http.Request request, long postId, Opinion opinion) throws ExecutionException, InterruptedException {
        Person user = getLoggedInUser(request).toCompletableFuture().get().get();
        Post post = postRepository.findById(postId).toCompletableFuture().get().get();

        if (opinion == Opinion.LIKE) {
            user.likePost(post);
        }

        else if (opinion == Opinion.DISLIKE) {
            user.dislikePost(post);
        }

        else {
            user.getLikedPosts().removeIf(p -> p.getId().equals(postId));
            user.getDislikedPosts().removeIf(p -> p.getId().equals(postId));
        }

        repository.update(user).toCompletableFuture().get();
        return redirect(routes.HomeController.home());

    }

    public Result submitLike(final Http.Request request, long postId) throws ExecutionException, InterruptedException {
        return handleOpinion(request, postId, Opinion.LIKE);
    }

    public Result submitDislike(final Http.Request request, long postId) throws ExecutionException, InterruptedException {
        return handleOpinion(request, postId, Opinion.DISLIKE);
    }

    public Result removeOpinion(final Http.Request request, long postId) throws ExecutionException, InterruptedException {
        return handleOpinion(request, postId, Opinion.NONE);
    }
}
