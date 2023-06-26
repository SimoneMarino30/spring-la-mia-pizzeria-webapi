package org.lessons.java.springlamiapizzeriacrud.controller;


import jakarta.validation.Valid;
import org.lessons.java.springlamiapizzeriacrud.model.Pizza;
import org.lessons.java.springlamiapizzeriacrud.repository.PizzaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/pizzas")
public class PizzaController {
    @Autowired
    // PizzaController dipende da pizzaRepository
    private PizzaRepository pizzaRepository;

    // Controller che ritorna tutti le pizze (SELECT *)
    /*@GetMapping
    public String index(Model model) {
        // Recupera la lista di pizze dal DB
        List<Pizza> pizzas = pizzaRepository.findAll();
        //Passo la lista delle pizze alla view attraverso il model
        model.addAttribute("pizzaList", pizzas);
        return "/pizzas/index"; // ritorno la vista index
    }*/

    // Metodo che può ricevere opzionalmente un parametro da query string:
    // se cè il param, filtriamo la lista per il param,
    // se non cè restituiamo la lista di tutti le pizze
    @GetMapping
    public String index(@RequestParam(name = "keyword", required = false) String searchString,
                        Model model) {
        List<Pizza> pizzas;
        if (searchString == null || searchString.isBlank()) {
            //recupero la lista di pizzas dal DB
            pizzas = pizzaRepository.findAll();
        } else {
            // se ho inserito il param searchString faccio la query con filtro
            pizzas = pizzaRepository.findByNameContainingIgnoreCase(searchString);
        }

        //Passo la lista delle pizze alla view attraverso il model
        model.addAttribute("pizzaList", pizzas);
        //searchedInput è la mia keyword che rimane nella barra di ricerca
        model.addAttribute("searchedInput", searchString);
        return "/pizzas/index"; // ritorno la vista index
    }

//**************************************************************************************************************************\\

    // 1° METODO: Il metodo getReferenceById potrebbe non ritornare nulla e va gestito con un try/catch

    /*@GetMapping("/{id}")
    public String show(@PathVariable Integer id, Model model) {
        try {
            // Recupero l' ID
            Pizza pizza = pizzaRepository.getReferenceById(id);
            // Aggiungo l'oggetto "pizza" all'oggetto "model" per renderlo disponibile nella vista
            model.addAttribute("pizza", pizza);
        } catch (EntityNotFoundException e) {
            return "redirect:/pizzas";
        }
        return "pizzas/show"; // ritorno la vista show
    }*/
//**************************************************************************************************************************\\

    // 2° METODO: Il metodo findById prevede che io non possa trovare l' ID, di conseguenza mi restituisce un Optional
    // L'oggetto Optional è una contenitore che può contenere un valore !nullo oppure sarà un oggetto Optional vuoto.
    // La principale funzionalità degli oggetti Optional è quella di evitare errori di "NullPointerException"
    @GetMapping("/{id}")
    public String show(@PathVariable("id") Integer pizzaId, Model model) {
        // cerco su DB i dettagli della pizza con quell'id
        Optional<Pizza> result = pizzaRepository.findById(pizzaId);
        if (result.isPresent()) {
            // passo la pizza alla view
            model.addAttribute("pizza", result.get());
            // ritorno il nome della view
            return "/pizzas/show";
        } else {
            // ritorno un HTTP Status 404 Not Found
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
    }

    // controller che restituisce la pagina col form di crezione del nuovo book (gestisce la GET)
    @GetMapping("/create")
    public String create(Model model) {
        model.addAttribute("pizza", new Pizza()); // obj new Pizza è vuoto in questo momento
        return "/pizzas/create"; // ritorno la vista create
    }

    // controller che gestisce la POST del form coi dati del book (gestisce le POST)
    @PostMapping("/create")
    public String store(@Valid @ModelAttribute("pizza") Pizza pizzaForm, BindingResult bindingResult) { //pizzaForm e' un altro modo di passare il model
        // i dati del book sono dentro l'oggetto formBook
        // verifico se l'isbn è univoco
        if (!isUniqueName(pizzaForm)) {
            // aggiungo a mano un errore nella mappa BindingResult
            bindingResult.addError(new FieldError("book", "isbn", pizzaForm.getName(), false, null, null,
                    "You cannot add an alredy existing name, choose another name please!"));
        }
        // ModelAttribute aggiunge i dati del book contenuti nel model.attribute()
        // verifico se in validazione ci sono stati errori
        if (bindingResult.hasErrors()) {
            //ci sono stati errori
            return "/pizzas/create"; // ritorno template form ma con obj Pizza precaricato
        } // se non ci sono stati errori
        // setto il timestamp di creazione
        pizzaForm.setCreatedAt(LocalDateTime.now());
        // persisto l' obj pizza su DB
        // il metodo save fa una CREATE Sql se l'oggetto con quella Primary Key non esiste, altrimenti fa l' UPDATE
        pizzaRepository.save(pizzaForm);
        // se tutto va bene rimando alla lista di books
        return "redirect:/pizzas";
    }

    // metodo per verificare se su database c'è già una pizza con lo stesso name (unique)
    private boolean isUniqueName(Pizza pizzaForm) {
        Optional<Pizza> result = pizzaRepository.findByName(pizzaForm.getName());
        return result.isEmpty();
    }
}
