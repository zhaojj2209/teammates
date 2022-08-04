package teammates.storage.sql;

import org.hibernate.Session;

import teammates.common.datatransfer.sqlattributes.CourseAttributes;
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

    @Override
    boolean hasExistingEntities(CourseAttributes entityToCreate) {
        Course course = getCourseEntity(entityToCreate.getId());
        return course != null;
    }

    /**
     * Gets a course.
     */
    public CourseAttributes getCourse(String courseId) {
        assert courseId != null;

        return makeAttributesOrNull(getCourseEntity(courseId));
    }

    @Override
    CourseAttributes makeAttributes(Course entity) {
        assert entity != null;

        return CourseAttributes.valueOf(entity);
    }

    private Course getCourseEntity(String courseId) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Course course = session.get(Course.class, courseId);
        session.close();
        return course;
    }

}
