package teammates.storage.sql;

import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import teammates.test.BaseTestCaseWithLocalPostgresSqlDatabaseAccess;
import org.testcontainers.containers.PostgreSQLContainer;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.Statement;

/**
 * SUT: {@link CoursesDb}.
 */
public class CoursePostgresDbTest extends BaseTestCaseWithLocalPostgresSqlDatabaseAccess {
    private final CoursesDb coursesDb = CoursesDb.inst();

    @Test
    public void testContainer() throws Exception {
        DataSource ds = getDataSource(pgsql);
        Statement stmt = ds.getConnection().createStatement();
        stmt.execute("CREATE TABLE test (number INTEGER)");
        stmt.execute("INSERT INTO test VALUES (1)");
        stmt.execute("SELECT * FROM test;");
        ResultSet resultSet = stmt.getResultSet();
        resultSet.next();
        assertEquals(1, resultSet.getInt(1));
    }
}
