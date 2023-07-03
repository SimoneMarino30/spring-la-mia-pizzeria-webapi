package org.lessons.java.springlamiapizzeriacrud.security;

import org.lessons.java.springlamiapizzeriacrud.model.User;
import org.lessons.java.springlamiapizzeriacrud.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

//Devo aggiungere una classe annotata come @Service che implementa l'interfaccia UserDetailsService
@Service
public class DatabaseUserDetailsService implements UserDetailsService {
    // ci serve uno userRepository per fare query sulla tabella DB users
    @Autowired
    UserRepository userRepository;

    /*
    L'unico metodo da implementare è loadUserByUsername(String username) Questo metodo deve ritornare una istanza
    di una classe di tipo UserDetails (DatabaseUserDetails) a partire dalla stringa con lo username,
    che tipicamente viene passata dalla form di login
    */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // devo recuperare un user da DB a partire dalla stringa username
        Optional<User> result = userRepository.findByEmail(username); // User è il mio model
        if (result.isPresent()) {
            // devo costruire uno userDetails a partire da quello user
            return new DatabaseUserDetails(result.get());
            // se non trovo l' utente con quella mail tiro un eccezione UsernameNotFoundException
        } else throw new UsernameNotFoundException("User with email " + username + " not found");
    }
    /*
    Per sapere se lo username è presente su database dobbiamo eseguire una query attraverso lo UserRepository:
    se trova uno User con quello username lo passa al costruttore di DatabaseUserDetails,
    altrimenti solleva un'eccezione di utente non trovato
    */
}

