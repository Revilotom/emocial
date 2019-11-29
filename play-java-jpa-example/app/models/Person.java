package models;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.hibernate.Hibernate;
import org.mindrot.jbcrypt.BCrypt;
import play.data.validation.Constraints;

import javax.persistence.*;
import java.util.*;
import java.util.stream.Collectors;

@Entity
public class Person {

    public static final Comparator<Post> ComparePosts = new Comparator<>() {
        @Override
        public int compare(Post o1, Post o2) {
            return (int) (o2.timeStamp - o1.timeStamp);
        }
    };

    public static String hashPassword(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt());
    }

    public Person(){}

    public Person(@Constraints.Required String name, @Constraints.Required String username, @Constraints.Required String password) {
        this.name = name;
        this.username = username;
        this.hash = hashPassword(password);
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public Long id;

    @Constraints.Required
    public String name;

    @Constraints.Required
    @Column (nullable = false, unique = true)
    public String username;

    @Constraints.Required
    private String hash;

//    {CascadeType.DETACH, CascadeType.PERSIST, CascadeType.REFRESH, CascadeType.REMOVE}

    @JsonSerialize
    @OneToMany(mappedBy = "owner", cascade = CascadeType.ALL, orphanRemoval = true)
    private
    Set<Post> posts = new HashSet<>();

    @JsonSerialize
    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(
            name = "post_like",
            joinColumns = @JoinColumn(name = "peerson_id"),
            inverseJoinColumns = @JoinColumn(name = "post_id"))
    private
    Set<Post> likedPosts = new HashSet<>();

    @JsonSerialize
    @JoinTable(
            name = "post_dislike",
            joinColumns = @JoinColumn(name = "person_id"),
            inverseJoinColumns = @JoinColumn(name = "post_id"))
    @ManyToMany(cascade = CascadeType.ALL)
    private
    Set<Post> dislikedPosts = new HashSet<>();

    @JsonSerialize
    @OneToMany(mappedBy="to", cascade = CascadeType.ALL, orphanRemoval = true)
    private
    Set<FollowRelation> followers = new HashSet<>();

    @JsonSerialize
    @OneToMany(mappedBy="from", cascade = CascadeType.ALL, orphanRemoval = true)
    private
    Set<FollowRelation> following = new HashSet<>();

    public void likePost(Post p){
        p.removeDisliker(this);
        p.addLiker(this);
        this.dislikedPosts.removeIf(post -> post.getId().equals(p.getId()));
        this.likedPosts.add(p);
    }

    public void dislikePost(Post p){
        p.removeLiker(this);
        p.addDisLiker(this);
        this.likedPosts.removeIf(post -> {


            return post.getId().equals(p.getId());
        });
        this.dislikedPosts.add(p);
    }

    public Set<Post> getLikedPosts() {
        return likedPosts;
    }

    public void setLikedPosts(Set<Post> likedPosts) {
        this.likedPosts = likedPosts;
    }

    public Set<Post> getDislikedPosts() {
        return dislikedPosts;
    }

    public void setDislikedPosts(Set<Post> dislikedPosts) {
        this.dislikedPosts = dislikedPosts;
    }

    public void addFollowing(Person personToFollow){
        this.following.add(new FollowRelation(this, personToFollow));
    }

    public void setFollowers(Set<FollowRelation> followers) {
        this.followers = followers;
    }

    public void unFollow(String username){
        this.following.removeIf(p -> p.getTo().getUsername().equals(username));
    }

    public void deletePost(long id){
        this.posts.removeIf((post -> post.getId() == id));
    }

    public Set<Person> getFollowers() {
        return followers.stream().map(FollowRelation::getFrom).collect(Collectors.toSet());
    }

    public Set<Person> getFollowing() {
        return following.stream().map(FollowRelation::getTo).collect(Collectors.toSet());
    }

    public void setFollowing(Set<FollowRelation> following) {
        this.following = following;
    }

    public Set<Post> getPosts() {
        return posts;
    }

    public void setPosts(Set<Post> posts) {
        this.posts = posts;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUsername() {
        return username;
    }

    public void setPassword(@Constraints.Required String password) {
        if (password == null) {
            throw new NullPointerException("password must not be null");
        }
        this.hash = hashPassword(password);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public String getHash() {
        return this.hash;
    }

    public List<Post> getNewsFeed(){
        List<Post> postList = new ArrayList<>(getPosts());
        postList.addAll(getFollowing().stream()
                .map(Person::getPosts)
                .flatMap(Set::stream)
                .collect(Collectors.toList()));

        Collections.sort(postList, ComparePosts);

        return postList;
    }

    public boolean validatePassword(String plainTextPassword) {
        return BCrypt.checkpw(plainTextPassword, this.hash);
    }

    public void addPost(Post post){
        post.setOwner(this);
        this.posts.add(post);
    }

//    @Override
//    public String toString() {
//        return hashCode() + "Person{" +
//                " username='" + username + '\'' +
//                ", myPosts=" + posts.stream().map(Post::getContent).collect(Collectors.toList()) +
//                ", followers=" + followers +
//                ", following=" + following +
//                '}';
//    }
}

// TODO myPosts are not displayed in chronological order
// TODO fix unfollowing