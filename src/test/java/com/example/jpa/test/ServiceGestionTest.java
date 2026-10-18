package com.example.jpa.test;

import com.example.jpa.entities.Salle;
import com.example.jpa.entities.Utilisateur;
import com.example.jpa.service.ServiceGestion;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import javax.persistence.PersistenceException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.Assert.*;

public class ServiceGestionTest {
    private ServiceGestion service;

    @Before
    public void setUp() {
        Map<String, String> configuration = new HashMap<>();
        configuration.put("javax.persistence.jdbc.url",
                "jdbc:h2:mem:gestion_test_" + UUID.randomUUID().toString().replace("-", "")
                        + ";DB_CLOSE_DELAY=-1");
        configuration.put("hibernate.hbm2ddl.auto", "create-drop");
        configuration.put("hibernate.show_sql", "false");
        configuration.put("hibernate.format_sql", "false");
        service = new ServiceGestion(configuration);
    }

    @After
    public void tearDown() {
        if (service != null) {
            service.fermer();
        }
    }

    @Test
    public void creationEtRechercheSalle() {
        Salle salle = new Salle("Atlas", 20);
        service.ajouterSalle(salle);

        assertNotNull(salle.getId());
        assertEquals(1, service.listerSalles().size());
        assertEquals("Atlas", service.listerSalles().get(0).getNom());
    }

    @Test
    public void creationUtilisateurAvecSalle() {
        Salle salle = salle("Rif", 15);
        Utilisateur utilisateur = utilisateur("Alice", "alice@example.com", salle);

        Utilisateur retrouve = service.trouverUtilisateur(utilisateur.getId());
        assertEquals("Alice", retrouve.getNom());
        assertEquals("alice@example.com", retrouve.getEmail());
        assertEquals(salle.getId(), retrouve.getSalle().getId());
    }

    @Test
    public void plusieursUtilisateursDansLaMemeSalle() {
        Salle salle = salle("Moyenne", 30);
        Salle autre = salle("Petite", 10);
        utilisateur("Bob", "bob@example.com", salle);
        utilisateur("Carole", "carole@example.com", salle);
        utilisateur("Dan", "dan@example.com", autre);

        List<Utilisateur> resultats = service.utilisateursParSalle("Moyenne");
        assertEquals(2, resultats.size());
        assertTrue(resultats.stream().anyMatch(u -> u.getNom().equals("Bob")));
        assertTrue(resultats.stream().anyMatch(u -> u.getNom().equals("Carole")));
        assertTrue(service.utilisateursParSalle("Inconnue").isEmpty());
    }

    @Test
    public void capaciteMinimumInclusif() {
        salle("Petite", 10);
        salle("Moyenne", 30);
        salle("Grande", 60);

        List<Salle> resultats = service.sallesParCapaciteMin(30);
        assertEquals(2, resultats.size());
        assertTrue(resultats.stream().anyMatch(s -> s.getNom().equals("Moyenne")));
        assertTrue(resultats.stream().anyMatch(s -> s.getNom().equals("Grande")));
    }

    @Test
    public void lectureParIdentifiant() {
        Salle salle = salle("Cèdre", 25);

        assertEquals("Cèdre", service.trouverSalle(salle.getId()).getNom());
        assertNull(service.trouverSalle(Long.MAX_VALUE));
    }

    @Test
    public void modificationSalleEtUtilisateur() {
        Salle salle = salle("Ancienne", 12);
        Utilisateur utilisateur = utilisateur("Avant", "avant@example.com", salle);
        salle.setCapacite(18);
        utilisateur.setNom("Après");

        service.modifierSalle(salle);
        service.modifierUtilisateur(utilisateur);

        assertEquals(18, service.trouverSalle(salle.getId()).getCapacite());
        assertEquals(1, service.sallesParCapaciteMin(18).size());
        assertEquals("Après", service.trouverUtilisateur(utilisateur.getId()).getNom());
    }

    @Test
    public void suppressionUtilisateurEtSalle() {
        Salle salle = salle("Temporaire", 8);
        Utilisateur utilisateur = utilisateur("Éphémère", "ephemere@example.com", salle);

        service.supprimerUtilisateur(utilisateur);
        assertNull(service.trouverUtilisateur(utilisateur.getId()));
        service.supprimerSalle(salle);
        assertNull(service.trouverSalle(salle.getId()));
    }

    @Test
    public void capaciteStrictementSuperieure() {
        salle("Petite", 10);
        salle("Égale", 30);
        salle("Grande", 60);

        List<Salle> resultats = service.sallesParCapaciteSuperieureA(30);
        assertEquals(1, resultats.size());
        assertEquals("Grande", resultats.get(0).getNom());
    }

    @Test
    public void rechercheNomContenantEtCaracteresSpeciaux() {
        Salle salle = salle("Test", 20);
        utilisateur("Carole", "carole@example.com", salle);
        utilisateur("Marc", "marc@example.com", salle);
        utilisateur("A%line", "aline@example.com", salle);
        utilisateur("B_line", "bline@example.com", salle);
        utilisateur("Bang!name", "bang@example.com", salle);

        assertEquals(2, service.utilisateursParNomContenant("ar").size());
        List<Utilisateur> litteral = service.utilisateursParNomContenant("%");
        assertEquals(1, litteral.size());
        assertEquals("A%line", litteral.get(0).getNom());
        assertEquals("B_line", service.utilisateursParNomContenant("_").get(0).getNom());
        assertEquals("Bang!name", service.utilisateursParNomContenant("!").get(0).getNom());
    }

    @Test
    public void rollbackApresDoublonEmailEtNouvelleTransaction() {
        Salle salle = salle("Test", 20);
        utilisateur("Alice", "unique@example.com", salle);

        assertThrows(PersistenceException.class,
                () -> utilisateur("Bob", "unique@example.com", salle));
        assertEquals(1, service.listerUtilisateurs().size());

        utilisateur("Carole", "autre@example.com", salle);
        assertEquals(2, service.listerUtilisateurs().size());
    }

    @Test
    public void contrainteUniciteNomSalle() {
        salle("Unique", 20);

        assertThrows(PersistenceException.class, () -> salle("Unique", 40));
        assertEquals(1, service.listerSalles().size());
        assertEquals(20, service.listerSalles().get(0).getCapacite());
    }

    private Salle salle(String nom, int capacite) {
        Salle salle = new Salle(nom, capacite);
        service.ajouterSalle(salle);
        return salle;
    }

    private Utilisateur utilisateur(String nom, String email, Salle salle) {
        Utilisateur utilisateur = new Utilisateur(nom, email, salle);
        service.ajouterUtilisateur(utilisateur);
        return utilisateur;
    }
}
