package org.example.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;

public abstract class BaseRepository {
    protected final EntityManager em;

    protected BaseRepository(EntityManager em) { this.em = em; }

    protected void inTx(Runnable work) {
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            work.run();
            tx.commit();
        } catch (RuntimeException e) {
            if (tx.isActive()) tx.rollback();
            throw e;
        }
    }
}
