
package com.example.jpa.dao;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.PersistenceException;
import java.util.List;

public class GenericDao<T> {

    private final Class<T> entityClass;
    protected final EntityManager em;

    // Initialisation du DAO
    public GenericDao(Class<T> entityClass, EntityManager em) {
        this.entityClass = entityClass;
        this.em = em;
    }

    // CREATE : enregistrer une nouvelle entite
    public void create(T entity) {
        EntityTransaction transaction = em.getTransaction();

        try {
            transaction.begin();
            em.persist(entity);
            transaction.commit();
        } catch (RuntimeException ex) {
            rollbackAndClear(transaction, ex);
            throw new PersistenceException("Impossible de créer " + entityClass.getSimpleName(), ex);
        }
    }

    // READ : rechercher par identifiant
    public T find(Object id) {
        return em.find(entityClass, id);
    }

    // UPDATE : modifier une entite existante
    public T update(T entity) {
        EntityTransaction transaction = em.getTransaction();

        try {
            transaction.begin();

            T updatedEntity = em.merge(entity);

            transaction.commit();
            return updatedEntity;

        } catch (RuntimeException ex) {
            rollbackAndClear(transaction, ex);
            throw new PersistenceException("Impossible de modifier " + entityClass.getSimpleName(), ex);
        }
    }

    // DELETE : supprimer une entite
    public void delete(T entity) {
        EntityTransaction transaction = em.getTransaction();

        try {
            transaction.begin();

            T managedEntity = em.contains(entity)
                    ? entity
                    : em.merge(entity);

            em.remove(managedEntity);
            transaction.commit();

        } catch (RuntimeException ex) {
            rollbackAndClear(transaction, ex);
            throw new PersistenceException("Impossible de supprimer " + entityClass.getSimpleName(), ex);
        }
    }

    // READ ALL : recuperer toutes les entites
    public List<T> findAll() {
        String jpql = "SELECT e FROM "
                + entityClass.getSimpleName() + " e";

        return em.createQuery(jpql, entityClass)
                .getResultList();
    }

    private void rollbackAndClear(EntityTransaction transaction, RuntimeException cause) {
        try {
            if (transaction.isActive()) {
                transaction.rollback();
            }
        } catch (RuntimeException rollbackFailure) {
            cause.addSuppressed(rollbackFailure);
        } finally {
            // Après un échec SQL, les entités en mémoire ne reflètent plus la base.
            try {
                em.clear();
            } catch (RuntimeException clearFailure) {
                cause.addSuppressed(clearFailure);
            }
        }
    }
}
