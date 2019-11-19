package models;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.hibernate.Hibernate;
import org.mindrot.jbcrypt.BCrypt;
import play.data.validation.Constraints;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Entity
public class Person {

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

    @JsonSerialize
    @OneToMany( cascade = CascadeType.ALL, mappedBy = "owner")
    private
    List<Post> posts = new ArrayList<>();

    @JsonSerialize
    @OneToMany(mappedBy="to", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private
    List<FollowRelation> followers = new ArrayList<>();

    @JsonSerialize
    @OneToMany(mappedBy="from", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private
    List<FollowRelation> following = new ArrayList<>();

    public void addFollower(FollowRelation f){
        f.setTo(this);
        this.followers.add(f);
    }

    public void addFollowing(FollowRelation f){
        f.setFrom(this);
        this.following.add(f);
    }

//    public void follow(Person follower){
//        following.add(new FollowRelation(this, follower));
//    }

//    public void addFollower(Person followee){
//        followers.add(new FollowRelation(followee, this));
//    }

//    public List<Person> getFollowers() {
//        return followers.stream().map(FollowRelation::getFrom).collect(Collectors.toList());
//    }

    public void setFollowers(List<FollowRelation> followers) {
        this.followers = followers;
    }

    public List<FollowRelation> getFollowers() {
        return followers;
    }

    public List<FollowRelation> getFollowing() {
        return following;
    }
//    public List<Person> getFollowing() {
//        return following.stream().map(FollowRelation::getTo).collect(Collectors.toList());
//    }

    public void setFollowing(List<FollowRelation> following) {
        this.following = following;
    }

    public List<Post> getPosts() {
        return posts;
    }

    public void setPosts(List<Post> posts) {
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

    public boolean validatePassword(String plainTextPassword) {
        return BCrypt.checkpw(plainTextPassword, this.hash);
    }

    public void addPost(Post post){
        this.posts.add(post);
    }

    @Override
    public String toString() {
        return "Person{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", username='" + username + '\'' +
                ", hash='" + hash + '\'' +
                ", posts=" + posts +
                ", followers=" + followers +
                ", following=" + following +
                '}';
    }
}
