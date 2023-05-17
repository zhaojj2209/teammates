package teammates.storage.sqlapi;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.HibernateUtil;
import teammates.storage.sqlentity.Account;
import teammates.storage.sqlentity.Course;
import teammates.storage.sqlentity.Instructor;
import teammates.storage.sqlentity.Section;
import teammates.storage.sqlentity.Student;
import teammates.storage.sqlentity.Team;
import teammates.storage.sqlentity.User;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

/**
 * Handles CRUD operations for users.
 *
 * @see User
 */
public final class UsersDb extends EntitiesDb {

    private static final UsersDb instance = new UsersDb();

    private UsersDb() {
        // prevent initialization
    }

    public static UsersDb inst() {
        return instance;
    }

    /**
     * Creates an instructor.
     */
    public Instructor createInstructor(Instructor instructor)
            throws InvalidParametersException, EntityAlreadyExistsException {
        assert instructor != null;

        if (!instructor.isValid()) {
            throw new InvalidParametersException(instructor.getInvalidityInfo());
        }

        persist(instructor);
        return instructor;
    }

    /**
     * Creates a student.
     */
    public Student createStudent(Student student)
            throws InvalidParametersException, EntityAlreadyExistsException {
        assert student != null;

        if (!student.isValid()) {
            throw new InvalidParametersException(student.getInvalidityInfo());
        }

        persist(student);
        return student;
    }

    /**
     * Gets an instructor by its {@code id}.
     */
    public Instructor getInstructor(UUID id) {
        assert id != null;

        return HibernateUtil.get(Instructor.class, id);
    }

    /**
     * Gets an instructor by {@code regKey}.
     */
    public Instructor getInstructorByRegKey(String regKey) {
        CriteriaBuilder cb = HibernateUtil.getCriteriaBuilder();
        CriteriaQuery<Instructor> cr = cb.createQuery(Instructor.class);
        Root<Instructor> instructorRoot = cr.from(Instructor.class);

        cr.select(instructorRoot).where(cb.equal(instructorRoot.get("regKey"), regKey));

        return HibernateUtil.createQuery(cr).getResultStream().findFirst().orElse(null);
    }

    /**
     * Gets an instructor by {@code googleId}.
     */
    public Instructor getInstructorByGoogleId(String courseId, String googleId) {
        CriteriaBuilder cb = HibernateUtil.getCriteriaBuilder();
        CriteriaQuery<Instructor> cr = cb.createQuery(Instructor.class);
        Root<Instructor> instructorRoot = cr.from(Instructor.class);
        Join<Instructor, Account> accountsJoin = instructorRoot.join("account");

        cr.select(instructorRoot).where(cb.and(
                cb.equal(instructorRoot.get("courseId"), courseId),
                cb.equal(accountsJoin.get("googleId"), googleId)));

        return HibernateUtil.createQuery(cr).getResultStream().findFirst().orElse(null);
    }

    /**
     * Gets all instructors that will be displayed to students of a course.
     */
    public List<Instructor> getInstructorsDisplayedToStudents(String courseId) {
        CriteriaBuilder cb = HibernateUtil.getCriteriaBuilder();
        CriteriaQuery<Instructor> cr = cb.createQuery(Instructor.class);
        Root<Instructor> instructorRoot = cr.from(Instructor.class);

        cr.select(instructorRoot).where(cb.and(
                cb.equal(instructorRoot.get("courseId"), courseId),
                cb.equal(instructorRoot.get("isDisplayedToStudents"), true)));

        return HibernateUtil.createQuery(cr).getResultList();
    }

    /**
     * Gets a student by its {@code id}.
     */
    public Student getStudent(UUID id) {
        assert id != null;

        return HibernateUtil.get(Student.class, id);
    }

    /**
     * Gets a student by {@code regKey}.
     */
    public Student getStudentByRegKey(String regKey) {
        CriteriaBuilder cb = HibernateUtil.getCriteriaBuilder();
        CriteriaQuery<Student> cr = cb.createQuery(Student.class);
        Root<Student> studentRoot = cr.from(Student.class);

        cr.select(studentRoot).where(cb.equal(studentRoot.get("regKey"), regKey));

        return HibernateUtil.createQuery(cr).getResultStream().findFirst().orElse(null);
    }

    /**
     * Gets a student by {@code googleId}.
     */
    public Student getStudentByGoogleId(String courseId, String googleId) {
        CriteriaBuilder cb = HibernateUtil.getCriteriaBuilder();
        CriteriaQuery<Student> cr = cb.createQuery(Student.class);
        Root<Student> studentRoot = cr.from(Student.class);
        Join<Student, Account> accountsJoin = studentRoot.join("account");

        cr.select(studentRoot).where(cb.and(
                cb.equal(studentRoot.get("courseId"), courseId),
                cb.equal(accountsJoin.get("googleId"), googleId)));

        return HibernateUtil.createQuery(cr).getResultStream().findFirst().orElse(null);
    }

    /**
     * Gets all students by {@code googleId}.
     */
    public List<Student> getStudentsByGoogleId(String googleId) {
        CriteriaBuilder cb = HibernateUtil.getCriteriaBuilder();
        CriteriaQuery<Student> cr = cb.createQuery(Student.class);
        Root<Student> studentRoot = cr.from(Student.class);
        Join<Student, Account> accountsJoin = studentRoot.join("account");

        cr.select(studentRoot).where(cb.equal(accountsJoin.get("googleId"), googleId));

        return HibernateUtil.createQuery(cr).getResultList();
    }

    /**
     * Gets a list of students by {@code teamName} and {@code courseId}.
     */
    public List<Student> getStudentsByTeamName(String teamName, String courseId) {
        CriteriaBuilder cb = HibernateUtil.getCriteriaBuilder();
        CriteriaQuery<Student> cr = cb.createQuery(Student.class);
        Root<Student> studentRoot = cr.from(Student.class);

        studentRoot.alias("student");

        Join<Student, Team> teamsJoin = studentRoot.join("team");

        cr.select(studentRoot).where(cb.and(
                cb.equal(studentRoot.get("courseId"), courseId),
                cb.equal(teamsJoin.get("name"), teamName)));

        return HibernateUtil.createQuery(cr).getResultList();
    }

    /**
     * Gets all instructors and students by {@code googleId}.
     */
    public List<User> getAllUsersByGoogleId(String googleId) {
        CriteriaBuilder cb = HibernateUtil.getCriteriaBuilder();
        CriteriaQuery<User> usersCr = cb.createQuery(User.class);
        Root<User> usersRoot = usersCr.from(User.class);
        Join<User, Account> accountsJoin = usersRoot.join("account");

        usersCr.select(usersRoot).where(cb.equal(accountsJoin.get("googleId"), googleId));

        return HibernateUtil.createQuery(usersCr).getResultList();
    }

    /**
     * Gets all instructors by {@code googleId}.
     */
    public List<Instructor> getAllInstructorsByGoogleId(String googleId) {
        CriteriaBuilder cb = HibernateUtil.getCriteriaBuilder();
        CriteriaQuery<Instructor> instructorsCr = cb.createQuery(Instructor.class);
        Root<Instructor> instructorsRoot = instructorsCr.from(Instructor.class);
        Join<Instructor, Account> accountsJoin = instructorsRoot.join("account");

        instructorsCr.select(instructorsRoot).where(cb.equal(accountsJoin.get("googleId"), googleId));

        return HibernateUtil.createQuery(instructorsCr).getResultList();
    }

    /**
     * Gets all students by {@code googleId}.
     */
    public List<Student> getAllStudentsByGoogleId(String googleId) {
        CriteriaBuilder cb = HibernateUtil.getCriteriaBuilder();
        CriteriaQuery<Student> studentsCr = cb.createQuery(Student.class);
        Root<Student> studentsRoot = studentsCr.from(Student.class);
        Join<Student, Account> accountsJoin = studentsRoot.join("account");

        studentsCr.select(studentsRoot).where(cb.equal(accountsJoin.get("googleId"), googleId));

        return HibernateUtil.createQuery(studentsCr).getResultList();
    }

    /**
     * Deletes a user.
     */
    public <T extends User> void deleteUser(T user) {
        if (user != null) {
            delete(user);
        }
    }

    /**
     * Gets the number of instructors created within a specified time range.
     */
    public long getNumInstructorsByTimeRange(Instant startTime, Instant endTime) {
        CriteriaBuilder cb = HibernateUtil.getCriteriaBuilder();
        CriteriaQuery<Long> cr = cb.createQuery(Long.class);
        Root<Instructor> root = cr.from(Instructor.class);

        cr.select(cb.count(root.get("id"))).where(cb.and(
                cb.greaterThanOrEqualTo(root.get("createdAt"), startTime),
                cb.lessThan(root.get("createdAt"), endTime)));

        return HibernateUtil.createQuery(cr).getSingleResult();
    }

    /**
     * Gets the number of students created within a specified time range.
     */
    public long getNumStudentsByTimeRange(Instant startTime, Instant endTime) {
        CriteriaBuilder cb = HibernateUtil.getCriteriaBuilder();
        CriteriaQuery<Long> cr = cb.createQuery(Long.class);
        Root<Student> root = cr.from(Student.class);

        cr.select(cb.count(root.get("id"))).where(cb.and(
                cb.greaterThanOrEqualTo(root.get("createdAt"), startTime),
                cb.lessThan(root.get("createdAt"), endTime)));

        return HibernateUtil.createQuery(cr).getSingleResult();
    }

    /**
     * Gets the list of instructors for the specified {@code courseId}.
     */
    public List<Instructor> getInstructorsForCourse(String courseId) {
        assert courseId != null;

        CriteriaBuilder cb = HibernateUtil.getCriteriaBuilder();
        CriteriaQuery<Instructor> cr = cb.createQuery(Instructor.class);
        Root<Instructor> root = cr.from(Instructor.class);

        cr.select(root).where(cb.equal(root.get("courseId"), courseId));

        return HibernateUtil.createQuery(cr).getResultList();
    }

    /**
     * Gets the list of students for the specified {@code courseId}.
     */
    public List<Student> getStudentsForCourse(String courseId) {
        assert courseId != null;

        CriteriaBuilder cb = HibernateUtil.getCriteriaBuilder();
        CriteriaQuery<Student> cr = cb.createQuery(Student.class);
        Root<Student> root = cr.from(Student.class);

        cr.select(root).where(cb.equal(root.get("courseId"), courseId));

        return HibernateUtil.createQuery(cr).getResultList();
    }

    /**
     * Gets the list of students for the specified {@code courseId} in batches with {@code batchSize}.
     */
    public List<Student> getStudentsForCourse(String courseId, int batchSize) {
        assert courseId != null;

        CriteriaBuilder cb = HibernateUtil.getCriteriaBuilder();
        CriteriaQuery<Student> cr = cb.createQuery(Student.class);
        Root<Student> root = cr.from(Student.class);

        cr.select(root).where(cb.equal(root.get("courseId"), courseId));

        return HibernateUtil.createQuery(cr).setMaxResults(batchSize).getResultList();
    }

    /**
     * Gets the instructor with the specified {@code userEmail}.
     */
    public Instructor getInstructorForEmail(String courseId, String userEmail) {
        assert courseId != null;
        assert userEmail != null;

        CriteriaBuilder cb = HibernateUtil.getCriteriaBuilder();
        CriteriaQuery<Instructor> cr = cb.createQuery(Instructor.class);
        Root<Instructor> instructorRoot = cr.from(Instructor.class);

        cr.select(instructorRoot)
                .where(cb.and(
                    cb.equal(instructorRoot.get("courseId"), courseId),
                    cb.equal(instructorRoot.get("email"), userEmail)));

        return HibernateUtil.createQuery(cr).getResultStream().findFirst().orElse(null);
    }

    /**
     * Gets instructors with the specified {@code userEmail}.
     */
    public List<Instructor> getInstructorsForEmails(String courseId, List<String> userEmails) {
        assert courseId != null;
        assert userEmails != null;

        CriteriaBuilder cb = HibernateUtil.getCriteriaBuilder();
        CriteriaQuery<Instructor> cr = cb.createQuery(Instructor.class);
        Root<Instructor> instructorRoot = cr.from(Instructor.class);

        List<Predicate> predicates = new ArrayList<>();
        for (String userEmail : userEmails) {
            predicates.add(cb.equal(instructorRoot.get("email"), userEmail));
        }

        cr.select(instructorRoot)
                .where(cb.and(
                    cb.equal(instructorRoot.get("courseId"), courseId),
                    cb.or(predicates.toArray(new Predicate[0]))));

        return HibernateUtil.createQuery(cr).getResultList();
    }

    /**
     * Gets the student with the specified {@code userEmail}.
     */
    public Student getStudentForEmail(String courseId, String userEmail) {
        assert courseId != null;
        assert userEmail != null;

        CriteriaBuilder cb = HibernateUtil.getCriteriaBuilder();
        CriteriaQuery<Student> cr = cb.createQuery(Student.class);
        Root<Student> studentRoot = cr.from(Student.class);

        cr.select(studentRoot)
                .where(cb.and(
                    cb.equal(studentRoot.get("courseId"), courseId),
                    cb.equal(studentRoot.get("email"), userEmail)));

        return HibernateUtil.createQuery(cr).getResultStream().findFirst().orElse(null);
    }

    /**
     * Gets students with the specified {@code userEmail}.
     */
    public List<Student> getStudentsForEmails(String courseId, List<String> userEmails) {
        assert courseId != null;
        assert userEmails != null;

        CriteriaBuilder cb = HibernateUtil.getCriteriaBuilder();
        CriteriaQuery<Student> cr = cb.createQuery(Student.class);
        Root<Student> studentRoot = cr.from(Student.class);

        List<Predicate> predicates = new ArrayList<>();
        for (String userEmail : userEmails) {
            predicates.add(cb.equal(studentRoot.get("email"), userEmail));
        }

        cr.select(studentRoot)
                .where(cb.and(
                    cb.equal(studentRoot.get("courseId"), courseId),
                    cb.or(predicates.toArray(new Predicate[0]))));

        return HibernateUtil.createQuery(cr).getResultList();
    }

    /**
     * Gets list of students by email.
     */
    public List<Student> getAllStudentsForEmail(String email) {
        assert email != null;

        CriteriaBuilder cb = HibernateUtil.getCriteriaBuilder();
        CriteriaQuery<Student> cr = cb.createQuery(Student.class);
        Root<Student> studentRoot = cr.from(Student.class);

        cr.select(studentRoot)
                .where(cb.equal(studentRoot.get("email"), email));

        return HibernateUtil.createQuery(cr).getResultList();
    }

    /**
     * Gets all instructors associated with a googleId.
     */
    public List<Instructor> getInstructorsForGoogleId(String googleId) {
        assert googleId != null;

        CriteriaBuilder cb = HibernateUtil.getCriteriaBuilder();
        CriteriaQuery<Instructor> cr = cb.createQuery(Instructor.class);
        Root<Instructor> instructorRoot = cr.from(Instructor.class);
        Join<Instructor, Account> accountsJoin = instructorRoot.join("account");

        cr.select(instructorRoot).where(cb.equal(accountsJoin.get("googleId"), googleId));

        return HibernateUtil.createQuery(cr).getResultList();
    }

    /**
     * Gets all students of a section of a course.
     */
    public List<Student> getStudentsForSection(String sectionName, String courseId) {
        assert sectionName != null;
        assert courseId != null;

        CriteriaBuilder cb = HibernateUtil.getCriteriaBuilder();
        CriteriaQuery<Student> cr = cb.createQuery(Student.class);
        Root<Student> studentRoot = cr.from(Student.class);
        Join<Student, Course> courseJoin = studentRoot.join("course");
        Join<Student, Team> teamsJoin = studentRoot.join("team");
        Join<Team, Section> sectionJoin = teamsJoin.join("section");

        cr.select(studentRoot)
                .where(cb.and(
                    cb.equal(courseJoin.get("id"), courseId),
                    cb.equal(sectionJoin.get("name"), sectionName)));

        return HibernateUtil.createQuery(cr).getResultList();
    }

    /**
     * Gets all students of a team of a course.
     */
    public List<Student> getStudentsForTeam(String teamName, String courseId) {
        assert teamName != null;
        assert courseId != null;

        CriteriaBuilder cb = HibernateUtil.getCriteriaBuilder();
        CriteriaQuery<Student> cr = cb.createQuery(Student.class);
        Root<Student> studentRoot = cr.from(Student.class);
        Join<Student, Course> courseJoin = studentRoot.join("course");
        Join<Student, Team> teamsJoin = studentRoot.join("team");

        cr.select(studentRoot)
                .where(cb.and(
                    cb.equal(courseJoin.get("id"), courseId),
                    cb.equal(teamsJoin.get("name"), teamName)));

        return HibernateUtil.createQuery(cr).getResultList();
    }

    /**
     * Gets count of students of a team of a course.
     */
    public long getStudentCountForTeam(String teamName, String courseId) {
        assert teamName != null;
        assert courseId != null;

        CriteriaBuilder cb = HibernateUtil.getCriteriaBuilder();
        CriteriaQuery<Long> cr = cb.createQuery(Long.class);
        Root<Student> studentRoot = cr.from(Student.class);
        Join<Student, Course> courseJoin = studentRoot.join("course");
        Join<Student, Team> teamsJoin = studentRoot.join("team");

        cr.select(cb.count(studentRoot.get("id")))
                .where(cb.and(
                    cb.equal(courseJoin.get("id"), courseId),
                    cb.equal(teamsJoin.get("name"), teamName)));

        return HibernateUtil.createQuery(cr).getSingleResult();
    }

}
