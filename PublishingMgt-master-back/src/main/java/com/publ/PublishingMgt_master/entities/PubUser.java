package com.publ.PublishingMgt_master.entities;


import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import com.publ.PublishingMgt_master.entities.enums.Role;

import java.util.Date;

@NoArgsConstructor
@Entity
@Table(name = "users")
@Data //getters/setters/toString/equals/hashCode
@AllArgsConstructor // constructor
@Builder
public class PubUser {

    @Id
    @Column(name = "USER_ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "login",nullable = false)
    private String login;

    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    @CreationTimestamp
    @Column(updatable = false, name = "created_at")
    private Date createdAt;

    @OneToOne
    @JoinColumn(name = "author_id", referencedColumnName = "author_id")
    @JsonManagedReference
    private Author author;

    public PubUser(String login, Role role, String password) {
        this.login = login;
        this.role = role;
        this.password = password;
    }

    public JsonNode asJson() {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.createObjectNode()
                .put("id", id)
                .put("login", login)
                .put("password", password.toString())
                .put("role", role.toString());
    }

}


