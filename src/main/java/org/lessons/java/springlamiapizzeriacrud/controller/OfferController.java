package org.lessons.java.springlamiapizzeriacrud.controller;

import jakarta.validation.Valid;
import org.lessons.java.springlamiapizzeriacrud.model.Offer;
import org.lessons.java.springlamiapizzeriacrud.model.Pizza;
import org.lessons.java.springlamiapizzeriacrud.repository.OfferRepository;
import org.lessons.java.springlamiapizzeriacrud.repository.PizzaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.Optional;

@Controller
@RequestMapping("/offers")
public class OfferController {

    @Autowired
    OfferRepository offerRepository;
    @Autowired
    PizzaRepository pizzaRepository;

    @GetMapping("/create")
    public String create(@RequestParam("pizzaId") Integer pizzaId, Model model) {
        Optional<Pizza> foundPizza = pizzaRepository.findById(pizzaId);
        if (foundPizza.isEmpty()) throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        Offer offer = new Offer();
        offer.setStartDate(LocalDate.now());
        offer.setEndDate(LocalDate.now().plusDays(1));
        offer.setPizza(foundPizza.get());
        model.addAttribute("offer", offer);
        return "offers/form";

    }

    @PostMapping("/create")
    public String doCreate(@Valid @ModelAttribute("offer") Offer formOffer,
                           BindingResult bindingResult) {
        if (formOffer.getEndDate().isBefore(formOffer.getStartDate()))
            bindingResult.addError(new FieldError("offer", "endDate", formOffer.getEndDate(), false, null, null, "The end date of the offer must be later than the start date"));
        if (bindingResult.hasErrors()) return "/offers/form";
        offerRepository.save(formOffer);
        // redirect
        return "redirect:/pizzas";
    }

    @GetMapping("/edit/{id}")
    public String edit(@PathVariable Integer id, Model model) {
        Optional<Offer> offer = offerRepository.findById(id);
        if (offer.isEmpty()) throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        // invio offer al model
        model.addAttribute("offer", offer.get());
        return "/offers/form";
    }

    @PostMapping("/edit/{id}")
    public String update(
            @PathVariable Integer id,
            @Valid @ModelAttribute("offer") Offer offer,
            BindingResult bindingResult) {
        // verifico che esiste
        Optional<Offer> foundOffer = offerRepository.findById(id);
        if (foundOffer.isEmpty()) throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        if (offer.getEndDate().isBefore(offer.getStartDate()))
            bindingResult.addError(new FieldError("offer", "endDate", offer.getEndDate(), false, null, null, "The end date of the offer must be later than the start date"));
        if (bindingResult.hasErrors()) return "/offers/form";
        // setto l'id
        offer.setId(id);
        offerRepository.save(offer);
        return "redirect:/pizzas";
    }

    @PostMapping("/delete/{id}")
    public String delete(@PathVariable Integer id) {
        Optional<Offer> foundOffer = offerRepository.findById(id);
        if (foundOffer.isEmpty()) throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        offerRepository.delete(foundOffer.get());
        return "redirect:/pizzas";
    }


}
