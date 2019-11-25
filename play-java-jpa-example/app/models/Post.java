package models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.vdurmont.emoji.EmojiParser;
import play.data.validation.Constraints;
import play.data.validation.ValidationError;

import javax.persistence.*;
import java.time.Instant;


@Entity
@Constraints.Validate
public class Post implements Constraints.Validatable<ValidationError>{

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public Long id;

    @ManyToOne( cascade = CascadeType.MERGE)
    @JoinColumn
    @JsonBackReference
    public Person owner;

    public long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }

    public long timeStamp;

    public Person getOwner() {
        return owner;
    }

    public void setOwner(Person owner) {
        this.owner = owner;
    }

    @Constraints.Required
    @Constraints.MaxLength(200)
    public String content;

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
        return EmojiParser.removeAllEmojis(content).equals("") ? null :
                new ValidationError("content", "The content of all posts " +
                        "must consist exclusively of emojis!!");
    }
}
