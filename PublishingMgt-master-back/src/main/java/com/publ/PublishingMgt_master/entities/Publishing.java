package com.publ.PublishingMgt_master.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Objects;

@Entity
@Table(name = "publishing")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Publishing {

    @Id
    @Column(name = "publishing_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long publishing_id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false, length = 100)
    private String isbn;

    @Column(nullable = false)
    private Double noTprice;

    @Column(name = "pct_rate_author_royalties")
    private Double royalties;

    @ManyToOne(optional = false)
    @JoinColumn(name = "book_id", referencedColumnName = "book_id", nullable = false)
    private Book book;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Publishing)) return false;
        Publishing that = (Publishing) o;
        return isbn != null && isbn.equals(that.isbn);
    }

    @Override
    public int hashCode() {
        return Objects.hash(isbn);
    }
}
