package com.publ.PublishingMgt_master.repositories;

import com.publ.PublishingMgt_master.entities.PubUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PubUserRepository extends JpaRepository<PubUser, Long> {
    Optional<PubUser> findByLogin(String login);



}


