package com.publ.PublishingMgt_master.entities;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;


@Entity
@Table(name = "Books")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Book {

    @Id
    @Column(name = "book_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long book_id;

    @Column(nullable = false, length = 100)
    private String title;

    @ManyToOne
    @JoinColumn(name = "publisher_id", referencedColumnName = "publisher_id")
    private Publisher publisher;

    @Builder.Default
    @OneToMany(mappedBy = "book", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference("book-participations")
    private List<AuthorParticipation> participations = new java.util.ArrayList<>();

    public JsonNode asJson() {
        ObjectMapper mapper = new ObjectMapper();
        var node = mapper.createObjectNode()
                .put("id", book_id)
                .put("title", title)
                .put("publisher", publisher.getPublisher_id())
                .put("name", publisher.getName());

        // Add related authors
        var authorsArray = mapper.createArrayNode();
        if (participations != null) {
            participations.forEach(p ->
                    authorsArray.add(
                            mapper.createObjectNode()
                                    .put("id", p.getAuthor().getAuthor_id())
                                    .put("firstname", p.getAuthor().getFirstname())
                                    .put("surname", p.getAuthor().getSurname())
                    )
            );
        }
        node.set("authors", authorsArray);

        return node;
    }

    public void addParticipation(AuthorParticipation participation) {
        this.participations.add(participation);
        participation.setBook(this);
    }

    public void clearParticipations() {
        for (AuthorParticipation p : participations) {
            p.setBook(null);
        }
        participations.clear();
    }

}
