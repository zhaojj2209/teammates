package teammates.common.util;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import teammates.storage.persistence.Course;

/**
 * Class containing utils for getting the Hibernate session factory.
 */
public final class HibernateUtil {

    private static SessionFactory sessionFactory = buildSessionFactory();

    private static SessionFactory buildSessionFactory() {
        try {
            if (sessionFactory == null) {
                Configuration cfg = new Configuration()
                        .setProperty("hibernate.dialect", "org.hibernate.dialect.PostgreSQLDialect")
                        .setProperty("hibernate.connection.driver_class", "org.postgresql.Driver")
                        .setProperty("hibernate.connection.username", "jingjing")
                        .setProperty("hibernate.connection.password", "password")
                        .setProperty("hibernate.connection.url", "jdbc:postgresql://localhost:5432/teammates")
                        .setProperty("show_sql", "true")
                        .setProperty("hbm2ddl.auto", "update")
                        .addPackage("teammates.storage.persistence")
                        .addAnnotatedClass(Course.class);

                sessionFactory = cfg.buildSessionFactory();
            }
            return sessionFactory;
        } catch (Throwable ex) {
            throw new ExceptionInInitializerError(ex);
        }
    }

    public static SessionFactory getSessionFactory() {
        return sessionFactory;
    }

    /**
     * Closes connection to the database.
     */
    public static void shutdown() {
        getSessionFactory().close();
    }
}
