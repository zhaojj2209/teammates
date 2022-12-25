package teammates.storage.sql;

import org.testng.annotations.Test;
import teammates.common.datatransfer.sqlattributes.CourseAttributes;
import teammates.storage.sql.CoursesDb;
import teammates.test.BaseTestCaseWithLocalPostgresSqlDatabaseAccess;

public class CourseDbTest extends BaseTestCaseWithLocalPostgresSqlDatabaseAccess {
    private final CoursesDb coursesDb = CoursesDb.inst();

    @Test
    public void testCreateCourse() throws Exception {
        CourseAttributes c = CourseAttributes
                .builder("CDbT.tCC.newCourse")
                .withName("Basic Computing")
                .withTimezone("UTC")
                .withInstitute("Test institute")
                .build();
        coursesDb.createEntity(c);
    }
}
