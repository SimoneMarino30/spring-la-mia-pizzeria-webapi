package org.lessons.java.springlamiapizzeriacrud.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Entity
@Table(name = "pizzas")
// annotations for soft delete

//@SQLDelete(sql = "UPDATE pizzas SET deleted = true WHERE id=?") // fa l'override del comando delete di SQL
//@FilterDef(name = "deletedProductFilter", parameters = @ParamDef(name = "isDeleted", type = "boolean"))
//TYPE BOOLEAN DA ERRORE, VUOLE UNA CLASSE
//@Filter(name = "deletedProductFilter", condition = "deleted = :isDeleted")

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
    @DecimalMin(value = "0.0", inclusive = false)
    @NotNull(message = "price must not be null or blank")
    @Column(nullable = false)
    private BigDecimal price;
    private LocalDateTime createdAt;

    // soft delete
    /*@Column(nullable = false)
    private boolean deleted = Boolean.FALSE;*/


    // CONSTRUCTOR
    public Pizza(String name, String description, String urlPic, BigDecimal price, boolean deleted) {
        this.name = name;
        this.description = description;
        this.urlPic = urlPic;
        this.price = price;
        //this.deleted = deleted;
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

    // GETTERS & SETTERS SOFT DELETE
    /*public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }*/

    // CUSTOM GETTER PER FORMATTARE LA DATA

    public String getFormattedCreatedAt() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy MMMM dd 'at' HH:mm");
        return createdAt.format(formatter);
    }
}
