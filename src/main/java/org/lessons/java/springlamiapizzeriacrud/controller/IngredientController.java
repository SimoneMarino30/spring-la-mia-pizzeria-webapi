package org.lessons.java.springlamiapizzeriacrud.controller;

import org.lessons.java.springlamiapizzeriacrud.model.Ingredient;
import org.lessons.java.springlamiapizzeriacrud.repository.IngredientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Optional;


@Controller
@RequestMapping("/ingredients")
public class IngredientController {
    @Autowired
    private IngredientRepository ingredientRepository;


    // controller index gestisce sia la lista delle categorie presenti sul db, sia il form
    // per creare o editare una categoria
    @GetMapping
    public String index(Model model, @RequestParam("edit") Optional<Integer> ingredientId) {
        // recupero da db tutti gli ingredients
        List<Ingredient> ingredientList = ingredientRepository.findAll();
        // passo al model un attributo ingredients con tutte gli ingredients in una lista
        model.addAttribute("ingredients", ingredientList);
        // inizializzo l' oggetto Ingredient
        Ingredient ingredientObj;
        // se ho il parametro ingredientId allora cerco l' ingredient su DB
        if (ingredientId.isPresent()) {
            Optional<Ingredient> ingredientOnDb = ingredientRepository.findById(ingredientId.get());
            // se è presente valorizzo categoryObj con la categoria da db
            if (ingredientOnDb.isPresent()) {
                ingredientObj = ingredientOnDb.get();
            } else {
                // se non è presente valorizzo ingredientObj con una categoria vuota
                ingredientObj = new Ingredient();
            }
        } else {
            // se non ho il parametro categoryObj con una categoria vuota
            ingredientObj = new Ingredient();
        }
        // passo al model un attributo categoryObj per mappare il form su un oggetto di tipo Category
        model.addAttribute("ingredientObj", ingredientObj);
        return "/ingredients/index";
    }
}
