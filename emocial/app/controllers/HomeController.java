package controllers;

import models.Person;
import models.Post;
import play.mvc.Http;
import repositories.person.PersonRepository;

import play.data.FormFactory;
import play.libs.concurrent.HttpExecutionContext;
import play.mvc.Result;

import javax.inject.Inject;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

public class HomeController extends DefaultController {


    @Inject
    public HomeController(FormFactory formFactory, PersonRepository repository, HttpExecutionContext ec) {
        super(formFactory, repository, ec);
    }

    public Result home(final Http.Request request) throws ExecutionException, InterruptedException {

        Person user = getLoggedInUser(request);
        String username = user.getUsername();
        List<Post> nF = user.getNewsFeed(request.session().getOptional("order"));

        List<Long> likes = nF.stream().map(Post::getId).filter(pId ->
                user.getLikedPosts().stream().map(Post::getId)
                        .collect(Collectors.toSet()).contains(pId)).collect(Collectors.toList());

        List<Long> dislikes = nF.stream().map(Post::getId).filter(pId ->
                user.getDislikedPosts().stream().map(Post::getId)
                        .collect(Collectors.toSet()).contains(pId)).collect(Collectors.toList());

        return ok(views.html.old.home.render(username, nF, likes, dislikes, Post.isByRating(request)));
    }
}
