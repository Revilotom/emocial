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
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
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


    public Result getPostsByPerson(final Http.Request request, String username) throws ExecutionException, InterruptedException {

        Person loggedInUser = getLoggedInUser(request);

        Optional<Person> personToFind = repository.findByUsername(username).toCompletableFuture().get();

        System.out.println(personToFind);

        if (personToFind.isEmpty()) {
            return badRequest("Could not find user with username: " + username);
        }

        Person person = personToFind.get();

        List<Post> posts = new ArrayList<>(person.getPosts());
        posts.sort(Post.ComparePostsTime);


        return ok(views.html.old.personsPosts.render(
                username,
                posts,
                postIdsThatYouLiked(posts, loggedInUser),
                postIdsThatYouDisliked(posts, loggedInUser),
                Post.isByRating(request)
                ));
    }


    public Result getPosts(final Http.Request request) throws ExecutionException, InterruptedException {
        Person loggedInUser = getLoggedInUser(request);
        List<Post> postList = new ArrayList<>(loggedInUser.getPosts());
        postList.sort(Post.ComparePostsTime);
        return ok(views.html.old.myPosts.render(postList));
    }

    public Result deletePost(final Http.Request request, long postId) throws ExecutionException, InterruptedException {
        Person loggedInUser = getLoggedInUser(request);
        loggedInUser.deletePost(postId);
        repository.update(loggedInUser);
        return redirect(routes.PostController.getPosts());
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
        Person loggedInUser = getLoggedInUser(request);
        loggedInUser.addPost(post);

        repository.update(loggedInUser).toCompletableFuture().get();

        return redirect(routes.HomeController.home());
    }


    private void handleOpinion(final Http.Request request, Post post, Opinion opinion) throws ExecutionException, InterruptedException {
        Person user = getLoggedInUser(request);

        if (opinion == Opinion.LIKE) {
            user.likePost(post);
        } else if (opinion == Opinion.DISLIKE) {
            user.dislikePost(post);
        } else {
            user.getLikedPosts().removeIf(p -> p.getId().equals(post.getId()));
            user.getDislikedPosts().removeIf(p -> p.getId().equals(post.getId()));
        }
        repository.update(user).toCompletableFuture().get();
    }


    public Result submitLike(final Http.Request request, long postId) throws ExecutionException, InterruptedException {
        return getCorrectRedirect(request, postId, Opinion.LIKE);
    }

    public Result submitDislike(final Http.Request request, long postId) throws ExecutionException, InterruptedException {
        return getCorrectRedirect(request, postId, Opinion.DISLIKE);
    }

    public Result removeOpinion(final Http.Request request, long postId) throws ExecutionException, InterruptedException {
        return getCorrectRedirect(request, postId, Opinion.NONE);
    }

    private Result getCorrectRedirect(final Http.Request request, long postId, Opinion opinion) throws ExecutionException, InterruptedException {

        Optional<Post> maybePost = postRepository.findById(postId).toCompletableFuture().get();

        if (maybePost.isEmpty()) {
            return badRequest("could not find post: " + postId);
        }

        Post post = maybePost.get();

        handleOpinion(request, post, opinion);

        if (request.session().getOptional("oldURI").orElse("home").contains("home")) {
            return redirect(routes.HomeController.home());
        }

        return redirect(routes.PostController.getPostsByPerson(post.getOwner().getUsername()));
    }

    public Result submitOrder(final Http.Request request, boolean byRating){
        return redirect(routes.HomeController.home()).addingToSession(request, "order", byRating ? "rating" : "time");
    }
}
