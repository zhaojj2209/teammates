package teammates.common.util;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import teammates.storage.sqlentity.Course;
import teammates.storage.sqlentity.Instructor;

/**
 * Class containing utils for getting the Hibernate session factory.
 */
public final class HibernateUtil {

    private static SessionFactory sessionFactory;

    private HibernateUtil() {
        // Utility class
        // Intentional private constructor to prevent instantiation.
    }

    public static SessionFactory getSessionFactory() {
        if (sessionFactory == null) {
            Configuration cfg = new Configuration()
                    .setProperty("hibernate.dialect", "org.hibernate.dialect.PostgreSQLDialect")
                    .setProperty("hibernate.connection.driver_class", "org.postgresql.Driver")
                    .setProperty("hibernate.connection.username", Config.APP_LOCALPOSTGRES_USERNAME)
                    .setProperty("hibernate.connection.password", Config.APP_LOCALPOSTGRES_PASSWORD)
                    .setProperty("hibernate.connection.url", Config.getDbConnectionUrl())
                    .setProperty("hibernate.hbm2ddl.auto", "validate")
                    .setProperty("show_sql", "true")
                    .addPackage("teammates.storage.sqlentity")
                    .addAnnotatedClass(Course.class)
                    .addAnnotatedClass(Instructor.class);

            sessionFactory = cfg.buildSessionFactory();
        }

        return sessionFactory;
    }

    /**
     * Closes connection to the database.
     */
    public static void shutdown() {
        getSessionFactory().close();
    }

    public static SessionFactory setSessionFactoryForTesting(String username, String password, String url) {
        Configuration cfg = new Configuration()
                .setProperty("hibernate.dialect", "org.hibernate.dialect.PostgreSQLDialect")
                .setProperty("hibernate.connection.driver_class", "org.postgresql.Driver")
                .setProperty("hibernate.connection.username", username)
                .setProperty("hibernate.connection.password", password)
                .setProperty("hibernate.connection.url", url)
                .setProperty("hibernate.hbm2ddl.auto", "validate")
                .setProperty("show_sql", "true")
                .addPackage("teammates.storage.sqlentity")
                .addAnnotatedClass(Course.class)
                .addAnnotatedClass(Instructor.class);
        sessionFactory = cfg.buildSessionFactory();

        return sessionFactory;
    }
}
