package teammates.test;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.hibernate.SessionFactory;
import org.testcontainers.containers.JdbcDatabaseContainer;
import teammates.common.datatransfer.sqlattributes.CourseAttributes;
import teammates.logic.api.LogicNewExtension;

import javax.sql.DataSource;

public abstract class BaseTestCaseWithLocalPostgresSqlDatabaseAccess extends BaseTestCaseWithPostgresSqlDatabaseAccess {
    private static SessionFactory sessionFactory;
    private final LogicNewExtension logic = new LogicNewExtension();

    protected DataSource getDataSource(JdbcDatabaseContainer<?> container) {
        HikariConfig hikariConfig = new HikariConfig();
        hikariConfig.setJdbcUrl(container.getJdbcUrl());
        hikariConfig.setUsername(container.getUsername());
        hikariConfig.setPassword(container.getPassword());
        hikariConfig.setDriverClassName(container.getDriverClassName());
        return new HikariDataSource(hikariConfig);
    }

    @Override
    protected CourseAttributes getCourse(CourseAttributes course) {
        return logic.getCourse(course.getId());
    }
}
