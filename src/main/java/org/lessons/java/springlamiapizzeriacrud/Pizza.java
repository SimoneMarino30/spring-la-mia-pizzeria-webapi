package org.lessons.java.springlamiapizzeriacrud;

import java.math.BigDecimal;

public class Pizza {
    private String name;
    private String description;
    private String urlPic;
    private BigDecimal price;

    // CONSTRUCTOR
    public Pizza(String name, String description, String urlPic, BigDecimal price) {
        this.name = name;
        this.description = description;
        this.urlPic = urlPic;
        this.price = price;
    }

    // GETTERS & SETTERS
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
}
