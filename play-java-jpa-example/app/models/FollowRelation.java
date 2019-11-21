package models;

import com.fasterxml.jackson.annotation.JsonBackReference;

import javax.persistence.*;

@Entity
public class FollowRelation {

    @Override
    public String toString() {
        return "FollowRelation{" +
                "id=" + id +
                ", from=" + from.getUsername() +
                ", to=" + to.getUsername() +
                '}';
    }

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private long id;

    @ManyToOne(cascade = CascadeType.MERGE)
    @JoinColumn
    @JsonBackReference
    private Person from;

    @ManyToOne( cascade = CascadeType.MERGE)
    @JoinColumn
    @JsonBackReference
    private Person to;

    public FollowRelation() {};

    public FollowRelation(Person from, Person to) {
        this.from = from;
        this.to = to;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Person getFrom() {
        return from;
    }

    public void setFrom(Person from) {
        this.from = from;
    }

    public Person getTo() {
        return to;
    }

    public void setTo(Person to) {
        this.to = to;
    }
}