package com.example.jpa.service;

import com.example.jpa.dao.SalleDao;
import com.example.jpa.dao.UtilisateurDao;
import com.example.jpa.entities.Salle;
import com.example.jpa.entities.Utilisateur;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class ServiceGestion implements AutoCloseable {
    private final EntityManagerFactory emf;
    private final EntityManager em;
    private final SalleDao salleDao;
    private final UtilisateurDao utilisateurDao;

    public ServiceGestion() {
        this(Collections.emptyMap());
    }

    // Les propriétés passées ici remplacent celles de persistence.xml, notamment pour les tests.
    public ServiceGestion(Map<String, ?> proprietes) {
        emf = Persistence.createEntityManagerFactory("gestionPU", proprietes);
        try {
            em = emf.createEntityManager();
        } catch (RuntimeException ex) {
            emf.close();
            throw ex;
        }
        salleDao = new SalleDao(em);
        utilisateurDao = new UtilisateurDao(em);
    }

    public void ajouterSalle(Salle salle) {
        salleDao.create(salle);
    }

    public void ajouterUtilisateur(Utilisateur utilisateur) {
        utilisateurDao.create(utilisateur);
    }

    public Salle trouverSalle(Object id) {
        return salleDao.find(id);
    }

    public Utilisateur trouverUtilisateur(Object id) {
        return utilisateurDao.find(id);
    }

    public List<Salle> listerSalles() {
        return salleDao.findAll();
    }

    public List<Utilisateur> listerUtilisateurs() {
        return utilisateurDao.findAll();
    }

    public List<Salle> sallesParCapaciteMin(int capaciteMin) {
        return salleDao.findByCapaciteMin(capaciteMin);
    }

    public List<Salle> sallesParCapaciteSuperieureA(int capacite) {
        return salleDao.findByCapaciteSuperieureA(capacite);
    }

    public List<Utilisateur> utilisateursParSalle(String nomSalle) {
        return utilisateurDao.findBySalleNom(nomSalle);
    }

    public List<Utilisateur> utilisateursParNomContenant(String fragment) {
        return utilisateurDao.findByNomContenant(fragment);
    }

    public Salle modifierSalle(Salle salle) {
        return salleDao.update(salle);
    }

    public Utilisateur modifierUtilisateur(Utilisateur utilisateur) {
        return utilisateurDao.update(utilisateur);
    }

    public void supprimerSalle(Salle salle) {
        salleDao.delete(salle);
    }

    public void supprimerUtilisateur(Utilisateur utilisateur) {
        utilisateurDao.delete(utilisateur);
    }

    public void fermer() {
        try {
            if (em.isOpen()) {
                em.close();
            }
        } finally {
            if (emf.isOpen()) {
                emf.close();
            }
        }
    }

    @Override
    public void close() {
        fermer();
    }
}
