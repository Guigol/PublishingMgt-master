package com.publ.PublishingMgt_master.entities;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "Publisher")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Publisher {
    public Publisher(Long publisher_id) {
        this.publisher_id = publisher_id;
    }

    @Id
    @Column(name = "publisher_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long publisher_id;

    @Column(nullable = false, length = 100)
    private String name;
}
