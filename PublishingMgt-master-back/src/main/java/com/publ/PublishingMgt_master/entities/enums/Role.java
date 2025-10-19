package com.publ.PublishingMgt_master.entities.enums;

import org.springframework.security.core.GrantedAuthority;

public enum Role implements GrantedAuthority {
    ADMIN,
    MANAGER,
    USER,
    AUTHOR;

    @Override
    public String getAuthority() {
        return name();
    }
}

