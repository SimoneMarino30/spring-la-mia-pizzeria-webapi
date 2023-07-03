package org.lessons.java.springlamiapizzeriacrud.security;

import org.lessons.java.springlamiapizzeriacrud.model.Role;
import org.lessons.java.springlamiapizzeriacrud.model.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/*
La classe DatabaseUserDetails implementa l'interfaccia UserDetails: questo significa che deve
implementarne tutti i metodi: questi metodi sono quelli con cui si può risalire alle informazioni
dell'utente loggato
*/
public class DatabaseUserDetails implements UserDetails {
    //private final Integer id;
    private final String username;
    private final String password;
    private final Set<GrantedAuthority> authorities;
    // COSTRUTTORE CHE COPIA I DATI DI UNO USER PER COSTRUIRE UN DatabaseUserDetails

    /*
    Al costruttore di DatabaseUserDetails viene passato l'oggetto User valorizzato con i dati trovati su
    database per quell'utente
    */
    public DatabaseUserDetails(User user) { // User from my model
        // copio i campi corrispondenti (id potrebbe non servire e puo' essere cancellato)
        //this.id = user.getId();
        this.username = user.getEmail(); // Email è il mio campo univoco per il riconoscimento
        this.password = user.getPassword();
        //*******************************\\
        // inizializzo un nuovo Hashset(uso HashSet perchè i campi non si devono ripetere)
        this.authorities = new HashSet<>();
        // itero su tutti i ruoli e li trasformo in authorities
        for (Role role : user.getRoles()) {
            authorities.add(new SimpleGrantedAuthority(role.getName()));
        }
    }

    /*
     L'oggetto DatabaseUserDetails viene configurato a partire da quei valori

     Tutti i metodi dell'interfaccia UserDetails devono essere implementati
    */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
