package models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import org.joda.time.DateTime;
import play.data.validation.Constraints;

import javax.persistence.*;
import java.time.Instant;

@Entity
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public Long id;

    @ManyToOne( cascade = CascadeType.ALL)
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
}
