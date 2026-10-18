
package com.example.jpa.dao;

import com.example.jpa.entities.Utilisateur;
import javax.persistence.EntityManager;
import java.util.List;

public class UtilisateurDao extends GenericDao<Utilisateur> {

    public UtilisateurDao(EntityManager entityManager) {
        super(Utilisateur.class, entityManager);
    }

    // Rechercher les utilisateurs associes a une salle
    public List<Utilisateur> findBySalleNom(String nomSalle) {

        String requete = "SELECT u FROM Utilisateur u " +
                "WHERE u.salle.nom = :nomSalle";

        return em.createQuery(requete, Utilisateur.class)
                .setParameter("nomSalle", nomSalle)
                .getResultList();
    }

    public List<Utilisateur> findByNomContenant(String fragment) {
        String literal = fragment.replace("!", "!!").replace("%", "!%").replace("_", "!_");
        return em.createQuery(
                        "SELECT u FROM Utilisateur u WHERE u.nom LIKE :motif ESCAPE '!'",
                        Utilisateur.class)
                .setParameter("motif", "%" + literal + "%")
                .getResultList();
    }
}
