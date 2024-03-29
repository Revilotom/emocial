package Models;

import models.Person;
import models.Post;
import org.hamcrest.MatcherAssert;
import org.junit.Test;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static junit.framework.TestCase.assertTrue;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertFalse;

public class PersonModelTest {

    @Test
    public void testP() {
        String hash = Person.hashPassword("jon");

    }

    @Test
    public void testHashPassword() {
        String hash = Person.hashPassword("myPassword");
    }

    @Test
    public void testVerifyPasswordCorrect() {
        Person person = new Person();
        person.setHash("$2a$10$J7lxDySdqDOJCPjhOIoq3eYhSAqJyucV1/jnWb5DcBzRHvNn8K0wq");
        assertTrue(person.validatePassword("myPassword"));
    }

    @Test
    public void testVerifyPasswordWrong() {
        Person person = new Person();
        person.setHash("$2a$10$J7lxDySdqDOJCPjhOIoq3eYhSAqJyucV1/jnWb5DcBzRHvNn8K0wq");
        assertFalse(person.validatePassword("wrong"));
    }

    @Test
    public void testNewsFeed()  {
        Person p1 = new Person("adssa", "person1", "dasd");
        Post post1 = new Post("person1's post");
        p1.addPost(post1);

        Person p2 = new Person("adssa", "person2", "dasd");
        Post post2 = new Post("person2's post");
        post2.setTimeStamp(post2.getTimeStamp() + 23123214);
        p2.addPost(post2);

        Person user = new Person("Assa", "User", "dasdas");
        Post userPost = new Post("user post");
        user.addPost(userPost);

        user.addFollowing(p1);
        user.addFollowing(p2);

        List<Post> newsFeed = user.getNewsFeed(Optional.empty());

        MatcherAssert.assertThat(newsFeed.size(), is(3));
        MatcherAssert.assertThat(newsFeed.get(0).getOwner().getUsername(), is("person2"));
        MatcherAssert.assertThat(newsFeed.get(1).getOwner().getUsername(), is("User"));
        MatcherAssert.assertThat(newsFeed.get(2).getOwner().getUsername(), is("person1"));
    }

    @Test
    public void testLikeThenDisLikePost() {
        Person p = new Person();
        p.setId(1L);
        Post post = new Post("dasad");
        post.setOwner(p);
        post.setId(256L);

        p.likePost(post);
        p.dislikePost(post);

        MatcherAssert.assertThat(p.getLikedPosts().size(), is(0));
        MatcherAssert.assertThat(p.getDislikedPosts().size(), is(1));
        MatcherAssert.assertThat(post.getLikers().size(), is(0));
        MatcherAssert.assertThat(post.getDislikers().size(), is(1));
    }

    @Test
    public void testDislikeThenLikePost() {
        Person p = new Person();
        p.setId(1L);
        Post post = new Post("dasad");
        post.setOwner(p);
        post.setId(256L);

        p.dislikePost(post);
        p.likePost(post);

        MatcherAssert.assertThat(p.getLikedPosts().size(), is(1));
        MatcherAssert.assertThat(p.getDislikedPosts().size(), is(0));
        MatcherAssert.assertThat(post.getLikers().size(), is(1));
        MatcherAssert.assertThat(post.getDislikers().size(), is(0));
    }
}
