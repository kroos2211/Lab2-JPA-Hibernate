package com.example.jpa;

import com.example.jpa.entities.Salle;
import com.example.jpa.entities.Utilisateur;
import com.example.jpa.service.ServiceGestion;

public class App {
    public static void main(String[] args) {
        try (ServiceGestion service = new ServiceGestion()) {
            Salle petite = new Salle("Petite salle", 12);
            Salle moyenne = new Salle("Salle moyenne", 30);
            Salle grande = new Salle("Grande salle", 60);

            System.out.println("=== Création des salles ===");
            service.ajouterSalle(petite);
            service.ajouterSalle(moyenne);
            service.ajouterSalle(grande);

            System.out.println("=== Création des utilisateurs et affectations ===");
            Utilisateur alice = new Utilisateur("Alice", "alice@example.com", petite);
            Utilisateur bob = new Utilisateur("Bob", "bob@example.com", moyenne);
            Utilisateur carole = new Utilisateur("Carole", "carole@example.com", moyenne);
            service.ajouterUtilisateur(alice);
            service.ajouterUtilisateur(bob);
            service.ajouterUtilisateur(carole);

            System.out.println("=== Salles enregistrées ===");
            service.listerSalles().forEach(System.out::println);
            System.out.println("=== Utilisateurs enregistrés ===");
            service.listerUtilisateurs().forEach(System.out::println);
            System.out.println("=== Salle recherchée par identifiant ===");
            System.out.println(service.trouverSalle(moyenne.getId()));
            System.out.println("=== Salles de capacité au moins 30 ===");
            service.sallesParCapaciteMin(30).forEach(System.out::println);
            System.out.println("=== Salles de capacité supérieure à 30 ===");
            service.sallesParCapaciteSuperieureA(30).forEach(System.out::println);
            System.out.println("=== Utilisateurs de la salle moyenne ===");
            service.utilisateursParSalle(moyenne.getNom()).forEach(System.out::println);
            System.out.println("=== Utilisateurs dont le nom contient 'ar' ===");
            service.utilisateursParNomContenant("ar").forEach(System.out::println);

            System.out.println("=== Modification de la petite salle ===");
            petite.setCapacite(15);
            service.modifierSalle(petite);
            System.out.println(service.trouverSalle(petite.getId()));

            System.out.println("=== Suppression d'Alice ===");
            service.supprimerUtilisateur(alice);
            System.out.println("Utilisateur supprimé : " + (service.trouverUtilisateur(alice.getId()) == null));
        }
    }
}
