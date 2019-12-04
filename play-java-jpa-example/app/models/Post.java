package models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import play.data.validation.Constraints;
import play.data.validation.ValidationError;
import play.mvc.Http;

import javax.persistence.*;
import java.time.Instant;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


@Entity
@Constraints.Validate
public class Post implements Constraints.Validatable<ValidationError> {

    private static final Pattern r = Pattern.compile(
            "^(?:(?:[\u00a9\u00ae\u203c\u2049\u2122\u2139\u2194" +
                    "-\u2199\u21a9-\u21aa\u231a-\u231b\u2328\u23cf\u23e9-\u23f3\u23f8-\u23fa\u24c2\u25aa-" +
                    "\u25ab\u25b6\u25c0\u25fb-\u25fe\u2600-\u2604\u260e\u2611\u2614-\u2615\u2618\u261d\u2620\u2622-" +
                    "\u2623\u2626\u262a\u262e-\u262f\u2638-\u263a\u2648-\u2653\u2660\u2663\u2665-" +
                    "\u2666\u2668\u267b\u267f\u2692-\u2694\u2696-\u2697\u2699\u269b-\u269c\u26a0-" +
                    "\u26a1\u26aa-\u26ab\u26b0-\u26b1\u26bd-\u26be\u26c4-\u26c5\u26c8\u26ce-" +
                    "\u26cf\u26d1\u26d3-\u26d4\u26e9-\u26ea\u26f0-\u26f5\u26f7-\u26fa\u26fd\u2702\u2705\u2708-" +
                    "\u270d\u270f\u2712\u2714\u2716\u271d\u2721\u2728\u2733-\u2734\u2744\u2747\u274c\u274e\u2753-" +
                    "\u2755\u2757\u2763-\u2764\u2795-\u2797\u27a1\u27b0\u27bf\u2934-\u2935\u2b05-\u2b07\u2b1b-" +
                    "\u2b1c\u2b50\u2b55\u3030\u303d\u3297\u3299\ud83c\udc04\ud83c\udccf\ud83c\udd70-" +
                    "\ud83c\udd71\ud83c\udd7e-\ud83c\udd7f\ud83c\udd8e\ud83c\udd91-\ud83c\udd9a\ud83c\ude01-" +
                    "\ud83c\ude02\ud83c\ude1a\ud83c\ude2f\ud83c\ude32-\ud83c\ude3a\ud83c\ude50-" +
                    "\ud83c\ude51\u200d\ud83c\udf00-\ud83d\uddff\ud83d\ude00-\ud83d\ude4f\ud83d\ude80-" +
                    "\ud83d\udeff\ud83e\udd00-\ud83e\uddff\udb40\udc20-\udb40\udc7f]" +
                    "|\u200d[\u2640\u2642]|[\ud83c\udde6-\ud83c\uddff]{2}|.[\u20e0\u20e3\ufe0f]+)+[\n\r]*)+$"
    );

    private static final Pattern invisible = Pattern.compile("\u200D");


    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne(cascade = CascadeType.MERGE)
    @JoinColumn
    @JsonBackReference
    private Person owner;

    @ManyToMany(mappedBy = "likedPosts", cascade = CascadeType.MERGE)
    @JsonBackReference
    private Set<Person> likers = new HashSet<>();

    @ManyToMany(mappedBy = "dislikedPosts", cascade = CascadeType.MERGE)
    @JsonBackReference
    private Set<Person> dislikers = new HashSet<>();

    private long timeStamp;

    public long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }

    public static final Comparator<Post> ComparePostsTime = (o1, o2) -> (int) (o2.timeStamp - o1.timeStamp);

    static final Comparator<Post> ComparePostsRating = (o1, o2) -> {

        int difference = (o2.getRating() - o1.getRating());
        if (difference == 0) {
            return (int) (o2.getTimeStamp() - o1.getTimeStamp());
        }
        return difference;
    };

    public Person getOwner() {
        return owner;
    }

    public void setOwner(Person owner) {
        this.owner = owner;
    }

    @Constraints.Required
    @Constraints.MaxLength(200)
    private String content;

    public Post() {
        this.timeStamp = Instant.now().toEpochMilli();
    }

    public Post(@Constraints.Required String content) {
        this();
        this.content = content;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public String toString() {
        return "Post{" +
                "id=" + id +
                ", person=" + owner.getUsername() +
                ", content='" + content + '\'' +
                '}';
    }

    @Override
    public ValidationError validate() {

        ValidationError error = new ValidationError("content", "The content of all posts " +
                "must consist exclusively of emojis!!");

        int matches = 0;
        Matcher matcher = invisible.matcher(content);
        while (matcher.find())
            matches++;

        if (matches == content.length()) {
            return error;
        }

        String res = r.matcher(content).replaceAll("");

        return res.length() == 0 ? null : error;

    }

    public void addLiker(Person person) {
        this.likers.add(person);
    }

    public void removeLiker(Person person) {
        this.likers.removeIf(person1 -> person1.getId().equals(person.getId()));
    }

    public void addDisLiker(Person person) {
        this.dislikers.add(person);
    }

    public void removeDisliker(Person person) {
        this.dislikers.removeIf(person1 -> person1.getId().equals(person.getId()));
    }

    public int getRating() {
        return likers.size() - dislikers.size();
    }


    public Set<Person> getLikers() {
        return likers;
    }

    public void setLikers(Set<Person> likers) {
        this.likers = likers;
    }

    public Set<Person> getDislikers() {
        return dislikers;
    }

    public void setDislikers(Set<Person> dislikers) {
        this.dislikers = dislikers;
    }

    public static boolean isByRating(Http.Request request) {
        return request.session().getOptional("order").orElse("").contains("rating");

    }

}
