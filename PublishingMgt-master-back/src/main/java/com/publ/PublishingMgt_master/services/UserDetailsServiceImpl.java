package com.publ.PublishingMgt_master.services;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import com.publ.PublishingMgt_master.entities.PubUser;
import com.publ.PublishingMgt_master.repositories.PubUserRepository;

import java.util.Collections;
import java.util.List;
import java.util.Optional;


@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    private final PubUserRepository repository;

    public UserDetailsServiceImpl(PubUserRepository repository) {
        this.repository = repository;
    }

    @Override
    public UserDetails loadUserByUsername(String login) throws UsernameNotFoundException {

      //  System.out.println(" [UserDetailsServiceImpl] Recherche utilisateur: " + login);

        PubUser pubUser = repository.findByLogin(login)
                .orElseThrow(() -> new UsernameNotFoundException("No user found with login name: " + login));

       // System.out.println("[UserDetailsServiceImpl] Trouvé: " + pubUser.getLogin()
        //        + " / pass hash = " + pubUser.getPassword()
           //     + " / role = " + pubUser.getRole());


        return this.mapToUser(pubUser);
    }


    public PubUser findByLogin(String login) throws UsernameNotFoundException {
        Optional<PubUser> pubUser = repository.findByLogin(login);

        if (pubUser.isEmpty()) {
            throw new UsernameNotFoundException("User with username " + login + " not found!");
        }
        return pubUser.get();
    }


    private User mapToUser(PubUser pubUser) {
        String roleStr = pubUser.getRole().name(); // AUTHOR, MANAGER, ADMIN
        //System.out.println("[DEBUG] mapToUser role = " + roleStr);

        return new User(
                pubUser.getLogin(),
                pubUser.getPassword(),
                true, true, true, true,
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + roleStr))
        );
    }
    public PubUser save(PubUser pubUser) {
        return repository.save(pubUser);
    }

    public List<PubUser> getPubUsers() {
        return repository.findAll();
    }

}

