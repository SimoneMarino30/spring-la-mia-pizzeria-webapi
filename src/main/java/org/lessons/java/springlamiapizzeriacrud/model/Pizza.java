package org.lessons.java.springlamiapizzeriacrud.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Entity
@Table(name = "pizzas")
public class Pizza {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotBlank(message = "Name must not be null or blank")
    @Column(nullable = false, unique = true)
    private String name;
    private String description;
    @NotBlank(message = "URL must not be null or blank")
    private String urlPic;
    @Min(0)
    @Max(999)
    @Column(nullable = false)
    private BigDecimal price;
    private LocalDateTime createdAt;


    // CONSTRUCTOR
    public Pizza(String name, String description, String urlPic, BigDecimal price) {
        this.name = name;
        this.description = description;
        this.urlPic = urlPic;
        this.price = price;
    }

    // DEFAULT CONSTRUCTOR
    public Pizza() {
    }

    // GETTERS & SETTERS

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUrlPic() {
        return urlPic;
    }

    public void setUrlPic(String urlPic) {
        this.urlPic = urlPic;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    // CUSTOM GETTER PER FORMATTARE LA DATA

    public String getFormattedCreatedAt() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy MMMM dd 'at' HH:mm");
        return createdAt.format(formatter);
    }
}
