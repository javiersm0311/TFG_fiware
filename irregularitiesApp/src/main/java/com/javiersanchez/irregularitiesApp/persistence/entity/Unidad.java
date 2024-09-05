package com.javiersanchez.irregularitiesApp.persistence.entity;

import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import java.util.List;

@Entity
public class Unidad {

    @Id
    private String id;

    private String type;

    @OneToMany(mappedBy = "unidad", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<Zonas> zonas;

    // Getters and setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<Zonas> getZonas() {
        return zonas;
    }

    public void setZonas(List<Zonas> zonas) {
        this.zonas = zonas;
    }
}