package teammates.common.util;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import teammates.storage.persistence.Course;

/**
 * Class containing utils for getting the Hibernate session factory.
 */
public final class HibernateUtil {

    private static SessionFactory sessionFactory;

    static {
        Configuration cfg = new Configuration()
                .setProperty("hibernate.dialect", "org.hibernate.dialect.PostgreSQLDialect")
                .setProperty("hibernate.connection.driver_class", "org.postgresql.Driver")
                .setProperty("hibernate.connection.username", Config.APP_LOCALPOSTGRES_USERNAME)
                .setProperty("hibernate.connection.password", Config.APP_LOCALPOSTGRES_PASSWORD)
                .setProperty("hibernate.connection.url", "jdbc:postgresql://localhost:"
                        + Config.APP_LOCALPOSTGRES_PORT + "/" + Config.APP_LOCALPOSTGRES_DB)
                .setProperty("show_sql", "true")
                .setProperty("hibernate.hbm2ddl.auto", "create")
                .addPackage("teammates.storage.persistence")
                .addAnnotatedClass(Course.class);

        sessionFactory = cfg.buildSessionFactory();
    }

    private HibernateUtil() {
        // Utility class
        // Intentional private constructor to prevent instantiation.
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
