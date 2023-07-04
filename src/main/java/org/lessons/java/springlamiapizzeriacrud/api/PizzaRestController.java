package org.lessons.java.springlamiapizzeriacrud.api;

import jakarta.validation.Valid;
import org.lessons.java.springlamiapizzeriacrud.model.Pizza;
import org.lessons.java.springlamiapizzeriacrud.repository.PizzaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

@RestController
@CrossOrigin
@RequestMapping("api/pizzeria/pizzas")
public class PizzaRestController {
    @Autowired
    private PizzaRepository pizzaRepository;

    /*@GetMapping
    public List<Pizza> index() {
        // restituisco la lista di tutte le pizze prese da DB
        return pizzaRepository.findAll();
    }*/

    // servizio per vedere il dettaglio del singolo libro
    @GetMapping("/{id}")
    public Pizza get(@PathVariable Integer id) {
        // cerco il libro per id su DB
        Optional<Pizza> pizza = pizzaRepository.findById(id);
        if (pizza.isPresent()) {
            return pizza.get();
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
    }

    // servizio per creare un nuovo book, che arriva come JSON nella request nel body
    @PostMapping
    public Pizza create(@Valid @RequestBody Pizza pizza) {
        return pizzaRepository.save(pizza);
    }

    // servizio per cancellare un book
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Integer id) {
        pizzaRepository.deleteById(id);
    }

    // servizio per l' update del Book
    @PutMapping("/{id}")
    // bodyrequest passa i dati da modificare, ma devo passare tutti i campi(PATCH fa modifica di un singolo campo)
    public Pizza update(@PathVariable Integer id, @RequestBody Pizza pizza) {
        pizza.setId(id);// setto l'id passato come param
        return pizzaRepository.save(pizza);
    }

    // servizio paginazione
    @GetMapping
    public Page<Pizza> page(
            @RequestParam(defaultValue = "4") Integer size,
            @RequestParam(defaultValue = "0") Integer page,
            Pageable pageable) {
        // creo un Pageable a partire da size e page
        pageable = PageRequest.of(page, size);
        // restituisco una Page estratta da database dal metodo findAll
        return pizzaRepository.findAll(pageable);
    }
}

