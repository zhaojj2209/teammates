package teammates.logic.sql;

import java.time.Instant;

import teammates.common.datatransfer.InstructorPrivileges;
import teammates.common.datatransfer.attributes.AccountAttributes;
import teammates.common.datatransfer.sqlattributes.CourseAttributes;
import teammates.common.datatransfer.sqlattributes.InstructorAttributes;
import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Const;
import teammates.common.util.HibernateUtil;
import teammates.logic.core.AccountsLogic;
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

    private AccountsLogic accountsLogic;

    private InstructorsLogic instructorsLogic;

    private CoursesLogic() {
        // prevent initialization
    }

    public static CoursesLogic inst() {
        return instance;
    }

    void initLogicDependencies() {
        accountsLogic = AccountsLogic.inst();
        instructorsLogic = InstructorsLogic.inst();
    }

    /**
     * Gets the institute associated with the course.
     */
    public String getCourseInstitute(String courseId) {
        CourseAttributes cd = getCourse(courseId);
        assert cd != null : "Trying to getCourseInstitute for inexistent course with id " + courseId;
        return cd.getInstitute();
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
     * Creates a course and an associated instructor for the course.
     *
     * <br/>Preconditions: <br/>
     * * {@code instructorAccountId} already has an account and instructor privileges.
     */
    public void createCourseAndInstructor(String instructorAccountId, CourseAttributes courseToCreate)
            throws InvalidParametersException, EntityAlreadyExistsException {

        AccountAttributes courseCreator = accountsLogic.getAccount(instructorAccountId);
        assert courseCreator != null : "Trying to create a course for a non-existent instructor :" + instructorAccountId;

        CourseAttributes createdCourse = createCourse(courseToCreate);

        // Create the initial instructor for the course
        InstructorPrivileges privileges = new InstructorPrivileges(
                Const.InstructorPermissionRoleNames.INSTRUCTOR_PERMISSION_ROLE_COOWNER);
        InstructorAttributes instructor = InstructorAttributes
                .builder(createdCourse.getId(), courseCreator.getEmail())
                .withName(courseCreator.getName())
                .withAccountId(instructorAccountId)
                .withPrivileges(privileges)
                .build();

        try {
            instructorsLogic.createInstructor(instructor);
        } catch (EntityAlreadyExistsException | InvalidParametersException e) {
            // roll back the transaction
            HibernateUtil.getSessionFactory().getCurrentSession().getTransaction().rollback();

            String errorMessage = "Unexpected exception while trying to create instructor for a new course "
                    + System.lineSeparator() + instructor.toString();
            assert false : errorMessage;
        }
    }

    /**
     * Gets the course with the specified ID.
     */
    public CourseAttributes getCourse(String courseId) {
        return coursesDb.getCourse(courseId);
    }

    /**
     * Updates a course by {@link CourseAttributes.UpdateOptions}.
     *
     * <p>If the {@code timezone} of the course is changed, cascade the change to its corresponding feedback sessions.
     *
     * @return updated course
     * @throws InvalidParametersException if attributes to update are not valid
     * @throws EntityDoesNotExistException if the course cannot be found
     */
    public CourseAttributes updateCourseCascade(CourseAttributes.UpdateOptions updateOptions)
            throws InvalidParametersException, EntityDoesNotExistException {
        CourseAttributes oldCourse = coursesDb.getCourse(updateOptions.getCourseId());
        CourseAttributes updatedCourse = coursesDb.updateCourse(updateOptions);

        if (!updatedCourse.getTimeZone().equals(oldCourse.getTimeZone())) {
            // TODO: Cascade update to feedback sessions once feedback sessions are migrated
        }

        return updatedCourse;
    }

    /**
     * Deletes a course cascade its students, instructors, sessions, responses, deadline extensions and comments.
     *
     * <p>Fails silently if no such course.
     */
    public void deleteCourseCascade(String courseId) {
        if (getCourse(courseId) == null) {
            return;
        }

        // TODO: Handle cascading
        coursesDb.deleteCourse(courseId);
    }

    /**
     * Moves a course to Recycle Bin by its given corresponding ID.
     * @return the time when the course is moved to the recycle bin
     */
    public Instant moveCourseToRecycleBin(String courseId) throws EntityDoesNotExistException {
        return coursesDb.softDeleteCourse(courseId);
    }

    /**
     * Restores a course from Recycle Bin by its given corresponding ID.
     */
    public void restoreCourseFromRecycleBin(String courseId) throws EntityDoesNotExistException {
        coursesDb.restoreDeletedCourse(courseId);
    }

}

