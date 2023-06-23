package org.lessons.java.springlamiapizzeriacrud.controller;

import org.lessons.java.springlamiapizzeriacrud.model.Pizza;
import org.lessons.java.springlamiapizzeriacrud.repository.PizzaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;

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
            // se ho il param searchString faccio la query con filtro
            pizzas = pizzaRepository.findByNameContainingIgnoreCase(searchString);
        }
        //Passo la lista dei libri alla view attraverso il model
        model.addAttribute("pizzaList", pizzas);
        //searchedInput è la mia keyword che rimane nella barra di ricerca
        model.addAttribute("searchedInput", searchString);
        return "/pizzas/index"; // ritorno la vista index
    }

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

    // 2° METODO: Il metodo findById prevede che io non possa trovare l' ID, di conseguenza mi restituisce un Optional
    @GetMapping("/{id}")
    public String show(@PathVariable("id") Integer pizzaId, Model model) {
        // cerca su database i dettagli della pizza con quell'id
        Optional<Pizza> result = pizzaRepository.findById(pizzaId);
        if (result.isPresent()) {
            // passa la pizza alla view
            model.addAttribute("pizza", result.get());
            // ritorna il nome del template della view
            return "/pizzas/show";
        } else {
            // ritorno un HTTP Status 404 Not Found
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "Book with id " + pizzaId + " not found");
        }
    }
}
