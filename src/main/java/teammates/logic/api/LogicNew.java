package teammates.logic.api;

import teammates.common.datatransfer.sqlattributes.CourseAttributes;
import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.InvalidParametersException;
import teammates.logic.sql.CoursesLogic;

/**
 * Provides the business logic for production usage of the system.
 *
 * <p>This is a Facade class which simply forwards the method to internal classes.
 */
public class LogicNew {

    private static final LogicNew instance = new LogicNew();

    final CoursesLogic coursesLogic = CoursesLogic.inst();

    LogicNew() {
        // prevent initialization
    }

    public static LogicNew inst() {
        return instance;
    }

    /**
     * Creates a course.
     *
     * <br/>Preconditions: <br/>
     * * All parameters are non-null. <br/>
     */
    public void createCourse(CourseAttributes courseAttributes)
            throws EntityAlreadyExistsException, InvalidParametersException {
        assert courseAttributes != null;

        coursesLogic.createCourse(courseAttributes);
    }

}
