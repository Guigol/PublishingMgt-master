package com.publ.PublishingMgt_master.entities;


import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "author_participation")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthorParticipation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "author_id", nullable = false)
    @JsonBackReference("author-participations")
    private Author author;

    @ManyToOne
    @JoinColumn(name = "book_id", nullable = false)
    @JsonBackReference("book-participations")
    private Book book;

    @Column(name = "pct_rate_royalties", nullable = false)
    private Double pctRateRoyalties;
}

