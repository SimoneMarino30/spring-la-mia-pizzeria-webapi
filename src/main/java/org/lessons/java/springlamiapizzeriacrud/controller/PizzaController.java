package org.lessons.java.springlamiapizzeriacrud.controller;


import jakarta.validation.Valid;
import org.lessons.java.springlamiapizzeriacrud.dto.PizzaForm;
import org.lessons.java.springlamiapizzeriacrud.exceptions.NotUniqueNameException;
import org.lessons.java.springlamiapizzeriacrud.exceptions.PizzaNotFoundException;
import org.lessons.java.springlamiapizzeriacrud.messages.AlertMessage;
import org.lessons.java.springlamiapizzeriacrud.messages.AlertMessageType;
import org.lessons.java.springlamiapizzeriacrud.model.Pizza;
import org.lessons.java.springlamiapizzeriacrud.repository.IngredientRepository;
import org.lessons.java.springlamiapizzeriacrud.repository.PizzaRepository;
import org.lessons.java.springlamiapizzeriacrud.service.PizzaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;

@Controller
//@Service
@RequestMapping("/pizzas")
public class PizzaController {
    @Autowired
    // PizzaController dipende da pizzaRepository
    private PizzaRepository pizzaRepository;

    @Autowired
    private IngredientRepository ingredientRepository;

    @Autowired
    PizzaService pizzaService;

    /*@Autowired
    private EntityManager entityManager;*/

    /* METODI PER LA READ
     * */

//**************************************************************************************************************************\\
    // Controller che ritorna tutti le pizze (SELECT *)
    /*@GetMapping
    public String index(Model model) {
        // Recupera la lista di pizze dal DB
        List<Pizza> pizzas = pizzaRepository.findAll();
        //Passo la lista delle pizze alla view attraverso il model
        model.addAttribute("pizzaList", pizzas);
        return "/pizzas/index"; // ritorno la vista index
    }*/
//**************************************************************************************************************************\\

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
        model.addAttribute("searchedInput", searchString == null ? "" : searchString);
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

    // 2° METODO: Il metodo findById prevede che io possa non trovare l' ID, di conseguenza mi restituisce un Optional
    // L'oggetto Optional è una contenitore che può contenere un valore !nullo oppure sarà un oggetto Optional vuoto.
    // La principale funzionalità degli oggetti Optional è quella di evitare errori di "NullPointerException"
    @GetMapping("/{id}")
    public String show(@PathVariable("id") Integer pizzaId, Model model) {
        /*// cerco su DB i dettagli della pizza con quell'id
        Optional<Pizza> result = pizzaRepository.findById(pizzaId);
        if (result.isPresent()) {
            // passo la pizza alla view
            model.addAttribute("pizza", result.get());
            // ritorno il nome della view
            return "/pizzas/show";
        } else {
            // ritorno un HTTP Status 404 Not Found
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }*/
        Pizza pizza = getPizzaById(pizzaId);
        // passa il libro alla view
        model.addAttribute("pizza", pizza);
        // ritorna il nome del template della view
        return "/pizzas/show";
    }

    /*
    METODI PER LA CREATE
    * */

    // controller che restituisce la pagina col form di crezione della nuova Pizza (gestisce la GET)
    /*@GetMapping("/create")
    public String create(Model model) {
        model.addAttribute("pizza", new Pizza()); // obj new Pizza è vuoto in questo momento
        // ** INGREDIENT LIST X CHECKBOX ** aggiungo al model la lista delgli ingredienti per popolare le checkbox
        model.addAttribute("ingredientList", ingredientRepository.findAll());
        // ** return "/pizzas/create"; // ritorno la vista create
        return "/pizzas/edit"; // ** REFACTORING: ritorno la vista del form di edit in base alla variabile isEdit
    }*/

    @GetMapping("/create")
    public String create(Model model) {
        // aggiungo al model l'attributo pizza con una PizzaForm vuoto
        model.addAttribute("pizza", new PizzaForm());

        model.addAttribute("ingredientList", ingredientRepository.findAll());

        return "/pizzas/edit";
    }

    // controller che gestisce la POST del form coi dati della pizza
    /*@PostMapping("/create")
    public String store(@Valid @ModelAttribute("pizza") Pizza pizzaForm, BindingResult bindingResult, RedirectAttributes redirectattributes, Model model) { //pizzaForm e' un altro modo di passare il model
        // i dati della pizza sono dentro l'obj pizzaForm
        // verifico se il name è univoco
        if (!isUniqueName(pizzaForm)) {
            // aggiungo a mano un errore nella mappa BindingResult(oggetto utilizzato per gestire la validazione dei dati, utilizzato insieme all'annotazione @Valid per validare i dati provenienti da una richiesta HTTP)
            bindingResult.addError(new FieldError("pizza", "name", pizzaForm.getName(), false, null, null,
                    "You cannot add an already existing name, choose another name please!"));
        }
        // ModelAttribute aggiunge i dati del book contenuti nel model.attribute()
        // verifico se in validazione ci sono stati errori
        if (bindingResult.hasErrors()) {
            //ci sono stati errori
            // ** return "/pizzas/create"; // ritorno template form ma con obj Pizza precaricato

            // ** INGREDIENT LIST X CHECKBOX ** aggiungo al model la lista delgli ingredienti per popolare le checkbox
            model.addAttribute("ingredientList", ingredientRepository.findAll());
            return "/pizzas/edit"; // ** REFACTORING: ritorno la vista del form di edit
        } // se non ci sono stati errori
        // setto il timestamp di creazione
        pizzaForm.setCreatedAt(LocalDateTime.now());
        // persisto l' obj pizza su DB
        // il metodo save fa una CREATE Sql se l'oggetto con quella Primary Key non esiste, altrimenti fa l' UPDATE
        pizzaRepository.save(pizzaForm);
        // messaggio custo per avvenuta creazione
        redirectattributes.addFlashAttribute("message", new AlertMessage(AlertMessageType.SUCCESS, "New Pizza created"));
        // se tutto va bene rimando alla lista di pizzas
        return "redirect:/pizzas";
    }*/

    @PostMapping("/create")
    public String store(@Valid @ModelAttribute("pizza") PizzaForm pizzaFormFile, BindingResult bindingResult, RedirectAttributes redirectattributes, Model model) {
        if (bindingResult.hasErrors()) {
            // chiedo al BookService di salvare su db un Book a partire dal BookForm
            try {
                pizzaService.create(pizzaFormFile);
            } catch (NotUniqueNameException e) {
                bindingResult.addError(new FieldError("pizza", "name", pizzaFormFile.getName(), false, null, null,
                        "Name must be unique"));
            }
        }
        if (bindingResult.hasErrors()) {
            // ci sono stati errori
            // aggiungo al model la lista delle categorie per popolare le checkbox
            model.addAttribute("ingredientList", ingredientRepository.findAll());
            return "/pizzas/edit";
        }

        redirectattributes.addFlashAttribute("message", new AlertMessage(AlertMessageType.SUCCESS, "New Pizza created"));
        return "redirect:/pizzas";
    }


    /*
    METODI PER UPDATE
    * */
    /*@GetMapping("/edit/{id}")
    public String edit(@PathVariable Integer id, Model model) {
        // verificare se esiste una pizza con quell'id
        Pizza pizza = getPizzaById(id);
        // recupero i dati di quella pizza da DB
        // aggiungio la pizza al model
        model.addAttribute("pizza", pizza);// get mi restituisce il book dentro Optinal
        // ** INGREDIENT LIST X CHECKBOX ** aggiungo al model la lista delgli ingredienti per popolare le checkbox
        model.addAttribute("ingredientList", ingredientRepository.findAll());
        return "/pizzas/edit"; // ritorno la vista con form di edit in base alla variabile isEdit(form unico)
    }*/

    @GetMapping("/edit/{id}")
    public String edit(@PathVariable Integer id, Model model) {
        try {
            // recupero i dati di quel book da database
            PizzaForm pizzaFormFile = pizzaService.getPizzaFormById(id);
            // aggiungo il book al model
            model.addAttribute("pizza", pizzaFormFile);
            // aggiungo al model la lista delle categorie per popolare le checkbox
            model.addAttribute("ingredientList", ingredientRepository.findAll());
            // restituisco il template con il form di edit
            return "/pizzas/edit";
        } catch (PizzaNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
    }

    /*@PostMapping("/edit/{id}")
    public String doEdit(@PathVariable Integer id,
                         @Valid @ModelAttribute("pizza") Pizza pizzaForm,
                         BindingResult bindingResult,
                         RedirectAttributes redirectAttributes,
                         Model model
    ) { //pizzaForm e' un altro modo di passare il model
        Pizza pizzaToEdit = getPizzaById(id); // fotografia della pizza prima di essere modificata (vecchia versione della pizza)
        // nuova versione della pizza è pizzaForm

        // valido pizzaForm

        // se il vecchio name e quello nuovo sono diversi e quello nuovo è gia presente su DB allora errore +  verifico se name è univoco in pizzaservice  [&& !isUniqueName(pizzaForm)]
        if (!pizzaToEdit.getName().equals(pizzaForm.getName())) {
            // aggiungo a mano un errore alla mappa BindingResult
            bindingResult.addError(new FieldError("pizza", "name", pizzaForm.getName(), false, null, null, "Name must be unique"));
        }
        if (bindingResult.hasErrors()) {
            // ** INGREDIENT LIST X CHECKBOX ** aggiungo al model la lista delgli ingredienti per popolare le checkbox
            model.addAttribute("ingredientList", ingredientRepository.findAll());
            // se ci sono errori ritorno il template con il form
            return "/pizzas/edit";
        }
        // trasferisco su formBook tutti i valori dei campi che non sono presenti nel form (altrimenti li perdo)
        pizzaForm.setId(pizzaToEdit.getId());
        pizzaForm.setCreatedAt(pizzaToEdit.getCreatedAt());
        // salvo i dati
        pizzaRepository.save(pizzaForm);
        redirectAttributes.addFlashAttribute("message",
                new AlertMessage(AlertMessageType.SUCCESS, "Pizza " + pizzaForm.getName() + " updated successfully!"));
        return "redirect:/pizzas";
    }*/

    @PostMapping("/edit/{id}")
    public String doEdit(@PathVariable Integer id,
                         @Valid @ModelAttribute("pizza") PizzaForm pizzaFormFile,
                         BindingResult bindingResult,
                         RedirectAttributes redirectAttributes,
                         Model model
    ) {
        if (!bindingResult.hasErrors()) {
            try {
                pizzaService.update(pizzaFormFile);
            } catch (PizzaNotFoundException e) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND);
            } catch (NotUniqueNameException e) {
                bindingResult.addError(new FieldError("pizza", "name", pizzaFormFile.getName(), false, null, null,
                        "name must be unique"));
            }
        }
        if (bindingResult.hasErrors()) {
            // aggiungo al model la lista degli ingredienti per popolare le checkbox
            model.addAttribute("ingredientList", ingredientRepository.findAll());
            // se ci sono errori ritorno il template col form
            return "/pizzas/edit";
        }

        redirectAttributes.addFlashAttribute("message",
                new AlertMessage(AlertMessageType.SUCCESS, "Pizza updated!"));
        return "redirect:/pizzas";
    }

    /*
     * MEDODI PER DELETE
     * */
    @PostMapping("/delete/{id}")
    public String delete(@PathVariable Integer id, RedirectAttributes redirectAttributes) { // reindirizzo una mappa di attributi( usato per il toast dell' avvenuta canncellazione)
        // verifichiamo che esiste book con quell' id( altrimenti deleteById lo ignora e non cancella)
        Pizza pizzaToDelete = getPizzaById(id);
        // lo cancelliamo
        pizzaRepository.delete(pizzaToDelete);
        // aggiungo un messaggio di successo con flashAttributes
        redirectAttributes.addFlashAttribute("message", new AlertMessage(AlertMessageType.SUCCESS, "Pizza " + pizzaToDelete.getName() + " deleted"));
        return "redirect:/pizzas";

    }

    // METODO SOFT DELETE
    /*public Iterable<Pizza> findAll(@RequestParam(value = "isDeleted", required = false, defaultValue = "false") boolean isDeleted) {
        Session session = entityManager.unwrap(Session.class); // L'oggetto entityManager è responsabile della gestione delle operazioni di persistenza delle entità nel database.
        Filter filter = session.enableFilter("deletedProductFilter");// I filtri di Hibernate consentono di applicare condizioni aggiuntive alle query eseguite sul database. In questo caso, il filtro verrà utilizzato per filtrare i record eliminati in base al valore del parametro "isDeleted".
        filter.setParameter("isDeleted", isDeleted);// Viene impostato il valore del parametro "isDeleted" nel filtro. Questo valore determina se verranno restituiti solo i record eliminati o quelli non eliminati.
        Iterable<Pizza> pizzasDeleted = pizzaRepository.findAll();// Viene eseguita una query per recuperare tutti gli oggetti di tipo Pizza dal repository pizzaRepository. La query viene influenzata dal filtro abilitato precedentemente, quindi restituirà solo le pizze che soddisfano la condizione di eliminazione (o non eliminazione) specificata dal parametro "isDeleted".
        session.disableFilter("deletedProductFilter");// Il filtro "deletedProductFilter" viene disabilitato per assicurarsi che non influenzi altre query successive eseguite nella stessa sessione.
        //model.addAttribute("deletedPizzas", pizzasDeleted);
        return pizzasDeleted;// l' iterable contenente le pizze che soddisfano la condizione di eliminazione specificata viene restituito come risultato del metodo
    }*/

    // UTILITY METHODS
    // metodo per verificare se su database c'è già una pizza con lo stesso name (unique)
    private boolean isUniqueName(Pizza pizzaForm) {
        Optional<Pizza> result = pizzaRepository.findByName(pizzaForm.getName());
        return result.isEmpty();
    }

    // metodo per selezionare pizza da Db o tirare exception
    private Pizza getPizzaById(Integer id) {
        // verificare se esiste una pizza con quell' id
        Optional<Pizza> result = pizzaRepository.findById(id);
        // se non esiste, ritorno un http 404
        if (result.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Pizza with id " + id + " not found");
            // eccezione 'custom' per richieste http
        }
        return result.get();
    }
}
