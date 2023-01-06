package teammates.storage.sql;

import org.testng.annotations.Test;
import teammates.common.datatransfer.sqlattributes.CourseAttributes;
import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.FieldValidator;
import teammates.common.util.StringHelperExtension;
import teammates.test.AssertHelper;
import teammates.test.BaseTestCaseWithLocalPostgresSqlDatabaseAccess;

/**
 * SUT: {@link CoursesDb}.
 */
public class CoursePostgresDbTest extends BaseTestCaseWithLocalPostgresSqlDatabaseAccess {
    private final CoursesDb coursesDb = CoursesDb.inst();

    @Test
    public void testCreateCourse() throws Exception {

        ______TS("Success: typical case");

        CourseAttributes c = CourseAttributes
                .builder("CS3281")
                .withName("Thematic Systems Project I")
                .withTimezone("UTC")
                .withInstitute("NUS")
                .build();
        coursesDb.createEntity(c);
        verifyPresentInDatabase(c);

        ______TS("Failure: create duplicate course");

        EntityAlreadyExistsException eaee = assertThrows(EntityAlreadyExistsException.class,
                () -> coursesDb.createEntity(c));
        assertEquals(String.format(coursesDb.ERROR_CREATE_ENTITY_ALREADY_EXISTS, c.toString()), eaee.getMessage());

        ______TS("Failure: create a course with invalid parameter");
        
        CourseAttributes invalidIdCourse = CourseAttributes
                .builder("Invalid id")
                .withName("Basic Computing")
                .withTimezone("UTC")
                .withInstitute("Test institute")
                .build();
        InvalidParametersException ipe = assertThrows(InvalidParametersException.class,
                () -> coursesDb.createEntity(invalidIdCourse));
        AssertHelper.assertContains(
                "not acceptable to TEAMMATES as a/an course ID because it is not in the correct format",
                ipe.getMessage());

        String longCourseName = StringHelperExtension.generateStringOfLength(FieldValidator.COURSE_NAME_MAX_LENGTH + 1);
        CourseAttributes invalidNameCourse = CourseAttributes
                .builder("CDbT.tCC.newCourse")
                .withName(longCourseName)
                .withTimezone("UTC")
                .withInstitute("Test institute")
                .build();
        ipe = assertThrows(InvalidParametersException.class, () -> coursesDb.createEntity(invalidNameCourse));
        AssertHelper.assertContains("not acceptable to TEAMMATES as a/an course name because it is too long",
                ipe.getMessage());

        String longCourseInstitute = StringHelperExtension.generateStringOfLength(
                FieldValidator.INSTITUTE_NAME_MAX_LENGTH + 1);
        CourseAttributes invalidInstituteCourse = CourseAttributes
                .builder("CDbT.tCC.newCourse")
                .withName("Basic computing")
                .withTimezone("UTC")
                .withInstitute(longCourseInstitute)
                .build();
        ipe = assertThrows(InvalidParametersException.class, () -> coursesDb.createEntity(invalidInstituteCourse));
        AssertHelper.assertContains("not acceptable to TEAMMATES as a/an institute name because it is too long",
                ipe.getMessage());

        ______TS("Failure: null parameter");

        assertThrows(AssertionError.class, () -> coursesDb.createEntity(null));
    }
}
