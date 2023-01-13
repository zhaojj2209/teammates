package teammates.storage.sql;

import java.time.Instant;

import teammates.common.datatransfer.sqlattributes.CourseAttributes;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.HibernateUtil;
import teammates.storage.sqlentity.Course;

/**
 * Handles CRUD operations for courses.
 *
 * @see Course
 * @see CourseAttributes
 */
public final class CoursesDb extends EntitiesDb<Course, CourseAttributes> {

    private static final CoursesDb instance = new CoursesDb();

    private CoursesDb() {
        // prevent initialization
    }

    public static CoursesDb inst() {
        return instance;
    }

    /**
     * Gets a course.
     */
    public CourseAttributes getCourse(String courseId) {
        assert courseId != null;

        return makeAttributesOrNull(getCourseEntity(courseId));
    }

    /**
     * Updates a course by {@link CourseAttributes.UpdateOptions}.
     *
     * @return updated course
     * @throws InvalidParametersException if attributes to update are not valid
     * @throws EntityDoesNotExistException if the course cannot be found
     */
    public CourseAttributes updateCourse(CourseAttributes.UpdateOptions updateOptions)
            throws InvalidParametersException, EntityDoesNotExistException {
        assert updateOptions != null;

        Course course = getCourseEntity(updateOptions.getCourseId());

        if (course == null) {
            throw new EntityDoesNotExistException(ERROR_UPDATE_NON_EXISTENT);
        }

        CourseAttributes newAttributes = makeAttributes(course);
        newAttributes.update(updateOptions);

        newAttributes.sanitizeForSaving();
        if (!newAttributes.isValid()) {
            throw new InvalidParametersException(newAttributes.getInvalidityInfo());
        }

        // update only if change
        boolean hasSameAttributes =
                this.hasSameValue(course.getName(), newAttributes.getName())
                        && this.hasSameValue(course.getInstitute(), newAttributes.getInstitute())
                        && this.hasSameValue(course.getTimeZone(), newAttributes.getTimeZone());
        if (hasSameAttributes) {
            log.info(String.format(OPTIMIZED_SAVING_POLICY_APPLIED, Course.class.getSimpleName(), updateOptions));
            return newAttributes;
        }

        course.setName(newAttributes.getName());
        course.setTimeZone(newAttributes.getTimeZone());
        course.setInstitute(newAttributes.getInstitute());

        saveEntity(course);

        return makeAttributes(course);
    }

    /**
     * Deletes a course.
     */
    public void deleteCourse(String courseId) {
        assert courseId != null;

        deleteEntity(getCourseEntity(courseId));
    }

    /**
     * Soft-deletes a course by its given corresponding ID.
     * @return Soft-deletion time of the course.
     */
    public Instant softDeleteCourse(String courseId) throws EntityDoesNotExistException {
        assert courseId != null;
        Course courseEntity = getCourseEntity(courseId);

        if (courseEntity == null) {
            throw new EntityDoesNotExistException(ERROR_UPDATE_NON_EXISTENT);
        }

        courseEntity.setDeletedAt(Instant.now());
        saveEntity(courseEntity);

        return courseEntity.getDeletedAt();
    }

    /**
     * Restores a soft-deleted course by its given corresponding ID.
     */
    public void restoreDeletedCourse(String courseId) throws EntityDoesNotExistException {
        assert courseId != null;
        Course courseEntity = getCourseEntity(courseId);

        if (courseEntity == null) {
            throw new EntityDoesNotExistException(ERROR_UPDATE_NON_EXISTENT);
        }

        courseEntity.setDeletedAt(null);
        saveEntity(courseEntity);
    }

    @Override
    boolean hasExistingEntities(CourseAttributes entityToCreate) {
        Course course = getCourseEntity(entityToCreate.getId());
        return course != null;
    }

    @Override
    CourseAttributes makeAttributes(Course entity) {
        assert entity != null;

        return CourseAttributes.valueOf(entity);
    }

    private Course getCourseEntity(String courseId) {
        return HibernateUtil.getSessionFactory().getCurrentSession()
                .get(Course.class, courseId);
    }

}
