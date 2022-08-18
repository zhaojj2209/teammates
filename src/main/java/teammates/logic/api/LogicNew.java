package teammates.logic.api;

import java.time.Instant;

import teammates.common.datatransfer.sqlattributes.CourseAttributes;
import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.EntityDoesNotExistException;
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
     * Creates a course and an associated instructor for the course.
     *
     * <br/>Preconditions: <br/>
     * * All parameters are non-null. <br/>
     * * {@code instructorAccountId} already has an account and instructor privileges.
     */
    public void createCourseAndInstructor(String instructorAccountId, CourseAttributes courseAttributes)
            throws EntityAlreadyExistsException, InvalidParametersException {
        assert instructorAccountId != null;
        assert courseAttributes != null;

        coursesLogic.createCourseAndInstructor(instructorAccountId, courseAttributes);
    }

    /**
     * Preconditions: <br>
     * * All parameters are non-null.
     * @return null if not found.
     */
    public CourseAttributes getCourse(String courseId) {

        assert courseId != null;

        return coursesLogic.getCourse(courseId);
    }

    /**
     * Updates a course by {@link CourseAttributes.UpdateOptions}.
     *
     * <p>If the {@code timezone} of the course is changed, cascade the change to its corresponding feedback sessions.
     *
     * <br/>Preconditions: <br/>
     * * All parameters are non-null.
     *
     * @return updated course
     * @throws InvalidParametersException if attributes to update are not valid
     * @throws EntityDoesNotExistException if the course cannot be found
     */
    public CourseAttributes updateCourseCascade(CourseAttributes.UpdateOptions updateOptions)
            throws InvalidParametersException, EntityDoesNotExistException {
        assert updateOptions != null;

        return coursesLogic.updateCourseCascade(updateOptions);
    }

    /**
     * Deletes a course cascade its students, instructors, sessions, responses, deadline extensions and comments.
     *
     * <p>Fails silently if no such course.
     *
     * <br/>Preconditions: <br/>
     * * All parameters are non-null.
     */
    public void deleteCourseCascade(String courseId) {
        assert courseId != null;
        coursesLogic.deleteCourseCascade(courseId);
    }

    /**
     * Moves a course to Recycle Bin by its given corresponding ID.
     *
     * <br/>Preconditions: <br/>
     * * All parameters are non-null.
     *
     * @return the deletion timestamp assigned to the course.
     */
    public Instant moveCourseToRecycleBin(String courseId) throws EntityDoesNotExistException {
        assert courseId != null;
        return coursesLogic.moveCourseToRecycleBin(courseId);
    }

    /**
     * Restores a course and all data related to the course from Recycle Bin by
     * its given corresponding ID.
     *
     * <br/>Preconditions: <br/>
     * * All parameters are non-null.
     */
    public void restoreCourseFromRecycleBin(String courseId) throws EntityDoesNotExistException {
        assert courseId != null;

        coursesLogic.restoreCourseFromRecycleBin(courseId);
    }

}
