package teammates.logic.sql;

import teammates.common.datatransfer.sqlattributes.CourseAttributes;
import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.InvalidParametersException;
import teammates.storage.sql.CoursesDb;

/**
 * Handles operations related to courses.
 *
 * @see CourseAttributes
 * @see CoursesDb
 */
public final class CoursesLogic {

    private static final CoursesLogic instance = new CoursesLogic();

    private final CoursesDb coursesDb = CoursesDb.inst();

    private CoursesLogic() {
        // prevent initialization
    }

    public static CoursesLogic inst() {
        return instance;
    }

    /**
     * Creates a course.
     *
     * @return the created course
     * @throws InvalidParametersException if the course is not valid
     * @throws EntityAlreadyExistsException if the course already exists in the database.
     */
    public CourseAttributes createCourse(CourseAttributes courseToCreate)
            throws InvalidParametersException, EntityAlreadyExistsException {
        return coursesDb.createEntity(courseToCreate);
    }

    /**
     * Gets the course with the specified ID.
     */
    public CourseAttributes getCourse(String courseId) {
        return coursesDb.getCourse(courseId);
    }

}

