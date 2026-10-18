
package com.example.jpa.entities;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Column;
import javax.persistence.ManyToOne;
import javax.persistence.JoinColumn;

@Entity
public class Utilisateur {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nom;

    @Column(nullable = false, unique = true)
    private String email;

    // Association entre utilisateur et salle
    @ManyToOne
    @JoinColumn(name = "salle_id")
    private Salle salle;

    // Constructeur obligatoire pour JPA
    public Utilisateur() {
    }

    // Initialisation d'un utilisateur
    public Utilisateur(String nom, String email, Salle salle) {
        this.nom = nom;
        this.email = email;
        this.salle = salle;
    }

    public Long getId() {
        return id;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Salle getSalle() {
        return salle;
    }

    public void setSalle(Salle salle) {
        this.salle = salle;
    }

    @Override
    public String toString() {
        String nomSalle = (salle == null)
                ? "Non attribuee"
                : salle.getNom();

        return String.format(
                "Utilisateur{id=%d, nom='%s', email='%s', salle='%s'}",
                id, nom, email, nomSalle
        );
    }
}
