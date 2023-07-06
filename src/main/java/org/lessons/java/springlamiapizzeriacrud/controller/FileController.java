package org.lessons.java.springlamiapizzeriacrud.controller;

import org.lessons.java.springlamiapizzeriacrud.exceptions.PizzaNotFoundException;
import org.lessons.java.springlamiapizzeriacrud.model.Pizza;
import org.lessons.java.springlamiapizzeriacrud.service.PizzaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/files")
public class FileController {
    @Autowired
    PizzaService pizzaService;

    // metodo che cerca la pizza per id e ne restituisce la cover come image
    @GetMapping("/cover/{pizzaId}")
    public ResponseEntity<byte[]> getPizzaCover(@PathVariable Integer pizzaId) {
        try {
            Pizza pizza = pizzaService.getById(pizzaId);
            MediaType mediaType = MediaType.IMAGE_JPEG;
            return ResponseEntity.ok().contentType(mediaType).body(pizza.getCover());
        } catch (PizzaNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}
