package teammates.logic.api;

import java.time.Instant;
import java.util.List;

import teammates.common.datatransfer.sqlattributes.CourseAttributes;
import teammates.common.datatransfer.sqlattributes.InstructorAttributes;
import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InstructorUpdateException;
import teammates.common.exception.InvalidParametersException;
import teammates.logic.sql.CoursesLogic;
import teammates.logic.sql.InstructorsLogic;

/**
 * Provides the business logic for production usage of the system.
 *
 * <p>This is a Facade class which simply forwards the method to internal classes.
 */
public class LogicNew {

    private static final LogicNew instance = new LogicNew();

    final InstructorsLogic instructorsLogic = InstructorsLogic.inst();
    final CoursesLogic coursesLogic = CoursesLogic.inst();

    LogicNew() {
        // prevent initialization
    }

    public static LogicNew inst() {
        return instance;
    }

    /**
     * Creates an instructor.
     *
     * <p>Preconditions:</p>
     * * All parameters are non-null.
     *
     * @return the created instructor
     * @throws InvalidParametersException if the instructor is not valid
     * @throws EntityAlreadyExistsException if the instructor already exists in the database
     */
    public InstructorAttributes createInstructor(InstructorAttributes instructor)
            throws InvalidParametersException, EntityAlreadyExistsException {
        assert instructor != null;

        return instructorsLogic.createInstructor(instructor);
    }

    /**
     * Update instructor being edited to ensure validity of instructors for the course.
     *
     * @see InstructorsLogic#updateToEnsureValidityOfInstructorsForTheCourse(String, InstructorAttributes)
     */
    public void updateToEnsureValidityOfInstructorsForTheCourse(String courseId, InstructorAttributes instructorToEdit) {

        assert courseId != null;
        assert instructorToEdit != null;

        instructorsLogic.updateToEnsureValidityOfInstructorsForTheCourse(courseId, instructorToEdit);
    }

    /**
     * Preconditions: <br>
     * * All parameters are non-null.
     * @return null if not found.
     */
    public InstructorAttributes getInstructorForEmail(String courseId, String email) {

        assert courseId != null;
        assert email != null;

        return instructorsLogic.getInstructorForEmail(courseId, email);
    }

    /**
     * Preconditions: <br>
     * * All parameters are non-null.
     * @return null if not found.
     */
    public InstructorAttributes getInstructorForAccountId(String courseId, String accountId) {

        assert accountId != null;
        assert courseId != null;

        return instructorsLogic.getInstructorForAccountId(courseId, accountId);
    }

    /**
     * Preconditions: <br>
     * * All parameters are non-null.
     * @return null if not found.
     */
    public InstructorAttributes getInstructorForRegistrationKey(String registrationKey) {

        assert registrationKey != null;

        return instructorsLogic.getInstructorForRegistrationKey(registrationKey);
    }

    /**
     * Preconditions: <br>
     * * All parameters are non-null.
     * @return Empty list if none found.
     */
    public List<InstructorAttributes> getInstructorsForAccountId(String accountId) {

        assert accountId != null;

        return instructorsLogic.getInstructorsForAccountId(accountId);
    }

    /**
     * Preconditions: <br>
     * * All parameters are non-null.
     * @return Empty list if none found.
     */
    public List<InstructorAttributes> getInstructorsForAccountId(String accountId, boolean omitArchived) {

        assert accountId != null;

        return instructorsLogic.getInstructorsForAccountId(accountId, omitArchived);
    }

    /**
     * Preconditions: <br>
     * * All parameters are non-null.
     * @return Empty list if none found.
     */
    public List<InstructorAttributes> getInstructorsForCourse(String courseId) {

        assert courseId != null;

        return instructorsLogic.getInstructorsForCourse(courseId);
    }

    /**
     * Updates an instructor by {@link InstructorAttributes.UpdateOptionsWithAccountId}.
     *
     * <p>Cascade update the comments, responses and deadline extensions associated with the instructor.
     *
     * <br/>Preconditions: <br/>
     * * All parameters are non-null.
     *
     * @return updated instructor
     * @throws InvalidParametersException if attributes to update are not valid
     * @throws EntityDoesNotExistException if the instructor cannot be found
     */
    public InstructorAttributes updateInstructorCascade(InstructorAttributes.UpdateOptionsWithAccountId updateOptions)
            throws InstructorUpdateException, InvalidParametersException, EntityDoesNotExistException {
        assert updateOptions != null;

        return instructorsLogic.updateInstructorByAccountIdCascade(updateOptions);
    }

    /**
     * Updates an instructor by {@link InstructorAttributes.UpdateOptionsWithEmail}.
     *
     * <br/>Preconditions: <br/>
     * * All parameters are non-null.
     *
     * @return updated instructor
     * @throws InvalidParametersException if attributes to update are not valid
     * @throws EntityDoesNotExistException if the instructor cannot be found
     */
    public InstructorAttributes updateInstructor(InstructorAttributes.UpdateOptionsWithEmail updateOptions)
            throws InstructorUpdateException, InvalidParametersException, EntityDoesNotExistException {
        assert updateOptions != null;

        return instructorsLogic.updateInstructorByEmail(updateOptions);
    }

    /**
     * Deletes an instructor cascade its associated feedback responses, deadline extensions and comments.
     *
     * <p>Fails silently if the student does not exist.
     *
     * <br/>Preconditions: <br/>
     * * All parameters are non-null.
     */
    public void deleteInstructorCascade(String courseId, String email) {

        assert courseId != null;
        assert email != null;

        instructorsLogic.deleteInstructorCascade(courseId, email);
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
     * Gets the institute associated with the course.
     */
    public String getCourseInstitute(String courseId) {
        return coursesLogic.getCourseInstitute(courseId);
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
