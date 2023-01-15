package teammates.test;

import org.testcontainers.containers.PostgreSQLContainer;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import teammates.common.datatransfer.sqlattributes.CourseAttributes;
import teammates.common.util.FlywayUtil;
import teammates.common.util.HibernateUtil;
import teammates.logic.api.LogicNewExtension;

public abstract class BaseTestCaseWithLocalSqlDatabaseAccess extends BaseTestCaseWithSqlDatabaseAccess {
    private final LogicNewExtension logic = new LogicNewExtension();
    protected static PostgreSQLContainer pgsql;

    @BeforeClass
    public static void startContainer() {
        pgsql = new PostgreSQLContainer<>("postgres:14.4")
                .withDatabaseName("teammates")
                .withUsername("teammates")
                .withPassword("teammates");
        pgsql.start();

        FlywayUtil.getFlywayInst(pgsql.getJdbcUrl(), pgsql.getUsername(), pgsql.getPassword()).migrate();
        HibernateUtil.setSessionFactoryForTesting(
                pgsql.getUsername(), pgsql.getPassword(), pgsql.getJdbcUrl());
    }

    @AfterClass
    public static void closeContainer() {
        pgsql.close();
    }

    @Override
    protected CourseAttributes getCourse(CourseAttributes course) {
        return logic.getCourse(course.getId());
    }
}
