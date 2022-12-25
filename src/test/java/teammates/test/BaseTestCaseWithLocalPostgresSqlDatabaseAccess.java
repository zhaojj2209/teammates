package teammates.test;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.testng.annotations.BeforeSuite;
import teammates.common.datatransfer.sqlattributes.CourseAttributes;
import teammates.logic.api.LogicNewExtension;
import teammates.storage.sqlentity.Course;
import teammates.storage.sqlentity.Instructor;

public abstract class BaseTestCaseWithLocalPostgresSqlDatabaseAccess extends BaseTestCaseWithPostgresSqlDatabaseAccess {
    private static SessionFactory sessionFactory;
    private final LogicNewExtension logic = new LogicNewExtension();

    @BeforeSuite
    public void setupSessionFactory() {
        Configuration cfg = new Configuration()
                .setProperty("hibernate.dialect", "org.hibernate.dialect.PostgreSQLDialect")
                .setProperty("hibernate.connection.driver_class", "org.postgresql.Driver")
                .setProperty("hibernate.connection.username", TestProperties.TEST_LOCALPOSTGRES_USERNAME)
                .setProperty("hibernate.connection.password", TestProperties.TEST_LOCALPOSTGRES_PASSWORD)
                .setProperty("hibernate.connection.url", TestProperties.getTestPostgresConnectionUrl())
                .setProperty("hibernate.hbm2ddl.auto", "validate")
                .setProperty("show_sql", "true")
                .addPackage("teammates.storage.sqlentity")
                .addAnnotatedClass(Course.class)
                .addAnnotatedClass(Instructor.class);

        sessionFactory = cfg.buildSessionFactory();
    }

    @Override
    protected CourseAttributes getCourse(CourseAttributes course) {
        return logic.getCourse(course.getId());
    }
}
