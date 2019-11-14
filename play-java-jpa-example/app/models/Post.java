package models;

import play.data.validation.Constraints;

import javax.persistence.*;

@Entity
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public Long id;

    @ManyToOne
    public Person person;

    @Constraints.Required
    public String content;

    public Post() {
    }

    public Post(@Constraints.Required String content) {
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
                ", person=" + person +
                ", content='" + content + '\'' +
                '}';
    }
}
