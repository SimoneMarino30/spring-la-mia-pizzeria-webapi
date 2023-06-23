package org.lessons.java.springlamiapizzeriacrud.repository;

import org.lessons.java.springlamiapizzeriacrud.model.Pizza;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PizzaRepository extends JpaRepository<Pizza, Integer> {
    List<Pizza> findByNameContainingIgnoreCase(String myName);
}
