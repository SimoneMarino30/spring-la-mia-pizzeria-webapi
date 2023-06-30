package org.lessons.java.springlamiapizzeriacrud.controller;

import org.lessons.java.springlamiapizzeriacrud.repository.IngredientRepository;
import org.springframework.beans.factory.annotation.Autowired;

public class IngredientController {
    @Autowired
    private IngredientRepository ingredientRepository;

}
