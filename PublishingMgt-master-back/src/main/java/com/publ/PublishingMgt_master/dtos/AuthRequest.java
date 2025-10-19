package com.publ.PublishingMgt_master.dtos;

import com.publ.PublishingMgt_master.entities.enums.Role;
import lombok.Data;

@Data
public class AuthRequest {
    private String login;
    private String password;
    private Role role;
    private AuthorRequest author;

    public AuthRequest(String login, String password, Role role) {
        this.login = login;
        this.password = password;
        this.role = role;
    }




}
