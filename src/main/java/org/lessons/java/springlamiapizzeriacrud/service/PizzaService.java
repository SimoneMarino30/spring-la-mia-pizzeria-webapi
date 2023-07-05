package org.lessons.java.springlamiapizzeriacrud.service;

import org.lessons.java.springlamiapizzeriacrud.dto.PizzaForm;
import org.lessons.java.springlamiapizzeriacrud.exceptions.NotUniqueNameException;
import org.lessons.java.springlamiapizzeriacrud.exceptions.PizzaNotFoundException;
import org.lessons.java.springlamiapizzeriacrud.model.Pizza;
import org.lessons.java.springlamiapizzeriacrud.repository.PizzaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class PizzaService {
    @Autowired
    PizzaRepository pizzaRepository;

    // metodo che restituisce la lista di tutti le pizze filtrata o no a seconda del parametro
    public List<Pizza> getAll(Optional<String> keywordOpt) {
        if (keywordOpt.isEmpty()) {
            return pizzaRepository.findAll();
        } else {
            Optional<Pizza> result = pizzaRepository.findByName(keywordOpt.get());
            List<Pizza> pizzaList = new ArrayList<>();
            if (result.isPresent()) {
                pizzaList.add(result.get());
            }
            return pizzaList;
        }
    }

    // metodo che restituisce il Pizza preso per id o un'eccezione se non lo trova
    public Pizza getById(Integer id) throws PizzaNotFoundException {
        Optional<Pizza> pizzaOpt = pizzaRepository.findById(id);
        if (pizzaOpt.isPresent()) {
            return pizzaOpt.get();
        } else {
            throw new PizzaNotFoundException("Pizza with id " + id);
        }
    }

    // metodo che salva un nuovo pizza a partire da quello passato come parametro
    public Pizza create(Pizza pizza) throws NotUniqueNameException {
        // valido il name del book
        if (!isUniqueName(pizza)) {
            throw new NotUniqueNameException(pizza.getName());
        }
        // creo il book da salvare
        Pizza pizzaToPersist = new Pizza();
        // genero il timestamp di createdAt
        pizzaToPersist.setCreatedAt(LocalDateTime.now());
        // copio tutti i campi di book che mi interessano
        pizzaToPersist.setName(pizza.getName());
        pizzaToPersist.setDescription(pizza.getDescription());
        pizzaToPersist.setPrice(pizza.getPrice());
        pizzaToPersist.setUrlPic(pizza.getUrlPic());
        pizzaToPersist.setIngredients(pizza.getIngredients());
        pizzaToPersist.setCoverImage(pizza.getCoverImage());
        // persisto il book
        return pizzaRepository.save(pizzaToPersist);
    }

    // metodo che crea un nuovo Pizza a partire da un PizzaForm
    public Pizza create(PizzaForm pizzaFormFile) throws NotUniqueNameException {
        // converto il BookForm in un Book
        Pizza pizza = mapPizzaFormToPizza(pizzaFormFile);
        // salvo il pizza to database
        return create(pizza);
    }

    // metodo per creare un PizzaForm a partire dall'id di un pizza salvato su db
    public PizzaForm getPizzaFormById(Integer id) throws PizzaNotFoundException {
        Pizza pizza = getById(id);
        return mapPizzaToPizzaForm(pizza);
    }

    public Pizza update(PizzaForm pizzaFormFile) throws PizzaNotFoundException, NotUniqueNameException {
        // converto il pizzaForm in pizza
        Pizza pizza = mapPizzaFormToPizza(pizzaFormFile);
        // cerco il book su database
        Pizza pizzaDb = getById(pizza.getId());
        // valido name
        if (!pizza.getName().equals(pizzaDb.getName()) && !isUniqueName(pizza)) {
            throw new NotUniqueNameException(pizzaDb.getName());
        }
        pizzaDb.setName(pizza.getName());
        pizzaDb.setDescription(pizza.getDescription());
        pizzaDb.setPrice(pizza.getPrice());
        pizzaDb.setUrlPic(pizza.getUrlPic());
        pizzaDb.setIngredients(pizza.getIngredients());
        pizzaDb.setCoverImage(pizza.getCoverImage());
        // salvo il pizza in update
        return pizzaRepository.save(pizzaDb);
    }

    // metodo per convertire un PizzaForm in una Pizza
    private Pizza mapPizzaFormToPizza(PizzaForm pizzaFormFile) {
        // creo un nuovo Pizza vuoto
        Pizza pizza = new Pizza();
        // copio i campi con corrispondenza esatta chiamando i setter e matchandoli con i getter
        pizza.setId(pizzaFormFile.getId());
        pizza.setName(pizzaFormFile.getName());
        pizza.setDescription(pizzaFormFile.getDescription());
        pizza.setPrice(pizzaFormFile.getPrice());
        pizza.setUrlPic(pizzaFormFile.getUrlPic());
        pizza.setIngredients(pizzaFormFile.getIngredients());
        // converto il campo cover
        byte[] coverBytes = multipartFileToByteArray(pizzaFormFile.getCoverFile());
        pizza.setCoverImage(coverBytes); //2:16:00 5-7-2023

        return pizza;
    }

    private PizzaForm mapPizzaToPizzaForm(Pizza pizza) {
        // creo un nuovo BookForm vuoto
        PizzaForm pizzaFormFile = new PizzaForm();
        // copio i campi con corrispondenza esatta
        pizzaFormFile.setId(pizza.getId());
        pizzaFormFile.setName(pizza.getName());
        pizzaFormFile.setDescription(pizza.getDescription());
        pizzaFormFile.setPrice(pizza.getPrice());
        pizzaFormFile.setUrlPic(pizza.getUrlPic());
        pizzaFormFile.setIngredients(pizza.getIngredients());

        return pizzaFormFile;
    }

    private byte[] multipartFileToByteArray(MultipartFile mpf) {
        byte[] bytes = null;
        if (mpf != null && !mpf.isEmpty()) {
            try {
                bytes = mpf.getBytes();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return bytes;
    }

    // UTILITY METHODS
    // metodo per verificare se su database c'è già una pizza con lo stesso name (unique)
    private boolean isUniqueName(Pizza pizzaForm) {
        Optional<Pizza> result = pizzaRepository.findByName(pizzaForm.getName());
        return result.isEmpty();
    }
}
