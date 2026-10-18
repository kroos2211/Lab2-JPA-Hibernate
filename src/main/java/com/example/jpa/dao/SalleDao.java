
package com.example.jpa.dao;

import com.example.jpa.entities.Salle;
import javax.persistence.EntityManager;
import java.util.List;

public class SalleDao extends GenericDao<Salle> {

    public SalleDao(EntityManager entityManager) {
        super(Salle.class, entityManager);
    }

    // Rechercher les salles ayant une capacite suffisante
    public List<Salle> findByCapaciteMin(int capaciteMin) {

        String requete = "SELECT s FROM Salle s " +
                "WHERE s.capacite >= :capaciteMin";

        return em.createQuery(requete, Salle.class)
                .setParameter("capaciteMin", capaciteMin)
                .getResultList();
    }

    public List<Salle> findByCapaciteSuperieureA(int capacite) {
        return em.createQuery("SELECT s FROM Salle s WHERE s.capacite > :capacite", Salle.class)
                .setParameter("capacite", capacite)
                .getResultList();
    }
}
