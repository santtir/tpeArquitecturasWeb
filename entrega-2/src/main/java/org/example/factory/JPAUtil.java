package org.example.factory;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

public final class JPAUtil {
    private static final EntityManagerFactory EMF =
            Persistence.createEntityManagerFactory("tpePU"); // mismo nombre que en persistence.xml

    private JPAUtil() {}

    public static EntityManager em() { return EMF.createEntityManager(); }

    public static void close() { EMF.close(); }
}
    