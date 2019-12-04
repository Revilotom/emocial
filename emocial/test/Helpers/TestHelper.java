package Helpers;

import models.FollowRelation;
import models.Person;
import models.Post;
import play.Application;
import repositories.person.JPAPersonRepository;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class TestHelper {
    public static JPAPersonRepository setup(Application app) throws ExecutionException, InterruptedException {
        JPAPersonRepository repo = app.injector().instanceOf(JPAPersonRepository.class);

        Person person = new Person("tom oliver", "revilotom", "123456789");
        Post post = new Post("Hello");
        post.setOwner(person);
        person.addPost(post);

        Person person2 = new Person("kunal", "usekk", "123456789");
        Post post2 = new Post("Goodbye");
        post2.setOwner(person2);
        person2.addPost(post2);

        repo.update(person2).toCompletableFuture().get();
        repo.update(person).toCompletableFuture().get();

        Person p1 = new Person("dasdasd", "robertrick", "1231231231");
        repo.update(p1).toCompletableFuture().get();

        Person p2 = new Person("dasdasd", "richard231312", "1231231231");
        repo.update(p2).toCompletableFuture().get();


        return repo;
    }
}
