package teammates.storage.sql;

import java.util.List;
import java.util.stream.Collectors;

import org.hibernate.Session;
import org.hibernate.query.Query;

import teammates.common.datatransfer.sqlattributes.InstructorAttributes;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.HibernateUtil;
import teammates.storage.sqlentity.Instructor;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

/**
 * Handles CRUD operations for instructors.
 *
 * @see Instructor
 * @see InstructorAttributes
 */
public final class InstructorsDb extends EntitiesDb<Instructor, InstructorAttributes> {

    private static final InstructorsDb instance = new InstructorsDb();

    private InstructorsDb() {
        // prevent initialization
    }

    public static InstructorsDb inst() {
        return instance;
    }

    /**
     * Gets an instructor by unique constraint courseId-email.
     */
    public InstructorAttributes getInstructorForEmail(String courseId, String email) {
        assert email != null;
        assert courseId != null;

        return makeAttributesOrNull(getInstructorEntityForEmail(courseId, email));
    }

    /**
     * Gets an instructor by unique ID.
     */
    public InstructorAttributes getInstructorById(String courseId, String email) {
        assert email != null;
        assert courseId != null;

        return makeAttributesOrNull(getInstructorEntityById(courseId, email));
    }

    /**
     * Gets an instructor by unique constraint courseId-accountId.
     */
    public InstructorAttributes getInstructorForAccountId(String courseId, String accountId) {
        assert accountId != null;
        assert courseId != null;

        return makeAttributesOrNull(getInstructorEntityForAccountId(courseId, accountId));
    }

    /**
     * Gets an instructor by unique constraint registrationKey.
     */
    public InstructorAttributes getInstructorForRegistrationKey(String registrationKey) {
        assert registrationKey != null;

        return makeAttributesOrNull(getInstructorEntityForRegistrationKey(registrationKey.trim()));
    }

    /**
     * Gets all instructors associated with a accountId.
     *
     * @param omitArchived whether archived instructors should be omitted or not
     */
    public List<InstructorAttributes> getInstructorsForAccountId(String accountId, boolean omitArchived) {
        assert accountId != null;

        return makeAttributes(getInstructorEntitiesForAccountId(accountId, omitArchived));
    }

    /**
     * Gets all instructors of a course.
     */
    public List<InstructorAttributes> getInstructorsForCourse(String courseId) {
        assert courseId != null;

        return makeAttributes(getInstructorEntitiesForCourse(courseId));
    }

    /**
     * Gets all instructors that will be displayed to students of a course.
     */
    public List<InstructorAttributes> getInstructorsDisplayedToStudents(String courseId) {
        assert courseId != null;

        return makeAttributes(getInstructorEntitiesThatAreDisplayedInCourse(courseId));
    }

    /**
     * Updates an instructor by {@link InstructorAttributes.UpdateOptionsWithAccountId}.
     *
     * @return updated instructor
     * @throws InvalidParametersException if attributes to update are not valid
     * @throws EntityDoesNotExistException if the instructor cannot be found
     */
    public InstructorAttributes updateInstructorByGoogleId(InstructorAttributes.UpdateOptionsWithAccountId updateOptions)
            throws InvalidParametersException, EntityDoesNotExistException {
        assert updateOptions != null;

        Instructor instructor = getInstructorEntityForAccountId(updateOptions.getCourseId(), updateOptions.getAccountId());
        if (instructor == null) {
            throw new EntityDoesNotExistException(ERROR_UPDATE_NON_EXISTENT + updateOptions);
        }

        InstructorAttributes newAttributes = makeAttributes(instructor);
        newAttributes.update(updateOptions);

        newAttributes.sanitizeForSaving();
        if (!newAttributes.isValid()) {
            throw new InvalidParametersException(newAttributes.getInvalidityInfo());
        }

        // update only if change
        boolean hasSameAttributes =
                this.hasSameValue(instructor.getName(), newAttributes.getName())
                        && this.hasSameValue(instructor.getEmail(), newAttributes.getEmail())
                        && this.hasSameValue(instructor.getIsArchived(), newAttributes.isArchived())
                        && this.hasSameValue(instructor.getRole(), newAttributes.getRole())
                        && this.hasSameValue(instructor.isDisplayedToStudents(), newAttributes.isDisplayedToStudents())
                        && this.hasSameValue(instructor.getDisplayedName(), newAttributes.getDisplayedName())
                        && this.hasSameValue(
                        instructor.getInstructorPrivilegesAsText(), newAttributes.getInstructorPrivilegesAsText());
        if (hasSameAttributes) {
            log.info(String.format(
                    OPTIMIZED_SAVING_POLICY_APPLIED, Instructor.class.getSimpleName(), updateOptions));
            return newAttributes;
        }

        instructor.setName(newAttributes.getName());
        instructor.setEmail(newAttributes.getEmail());
        instructor.setIsArchived(newAttributes.isArchived());
        instructor.setRole(newAttributes.getRole());
        instructor.setIsDisplayedToStudents(newAttributes.isDisplayedToStudents());
        instructor.setDisplayedName(newAttributes.getDisplayedName());
        instructor.setInstructorPrivilegeAsText(newAttributes.getInstructorPrivilegesAsText());

        saveEntity(instructor);

        newAttributes = makeAttributes(instructor);

        return newAttributes;
    }

    /**
     * Updates an instructor by {@link InstructorAttributes.UpdateOptionsWithEmail}.
     *
     * @return updated instructor
     * @throws InvalidParametersException if attributes to update are not valid
     * @throws EntityDoesNotExistException if the instructor cannot be found
     */
    public InstructorAttributes updateInstructorByEmail(InstructorAttributes.UpdateOptionsWithEmail updateOptions)
            throws InvalidParametersException, EntityDoesNotExistException {
        assert updateOptions != null;

        Instructor instructor = getInstructorEntityForEmail(updateOptions.getCourseId(), updateOptions.getEmail());
        if (instructor == null) {
            throw new EntityDoesNotExistException(ERROR_UPDATE_NON_EXISTENT + updateOptions);
        }

        InstructorAttributes newAttributes = makeAttributes(instructor);
        newAttributes.update(updateOptions);

        newAttributes.sanitizeForSaving();
        if (!newAttributes.isValid()) {
            throw new InvalidParametersException(newAttributes.getInvalidityInfo());
        }

        // update only if change
        boolean hasSameAttributes =
                this.hasSameValue(instructor.getName(), newAttributes.getName())
                        && this.hasSameValue(instructor.getAccountId(), newAttributes.getAccountId())
                        && this.hasSameValue(instructor.getIsArchived(), newAttributes.isArchived())
                        && this.hasSameValue(instructor.getRole(), newAttributes.getRole())
                        && this.hasSameValue(instructor.isDisplayedToStudents(), newAttributes.isDisplayedToStudents())
                        && this.hasSameValue(instructor.getDisplayedName(), newAttributes.getDisplayedName())
                        && this.hasSameValue(
                        instructor.getInstructorPrivilegesAsText(), newAttributes.getInstructorPrivilegesAsText());
        if (hasSameAttributes) {
            log.info(String.format(OPTIMIZED_SAVING_POLICY_APPLIED, Instructor.class.getSimpleName(), updateOptions));
            return newAttributes;
        }

        instructor.setAccountId(newAttributes.getAccountId());
        instructor.setName(newAttributes.getName());
        instructor.setIsArchived(newAttributes.isArchived());
        instructor.setRole(newAttributes.getRole());
        instructor.setIsDisplayedToStudents(newAttributes.isDisplayedToStudents());
        instructor.setDisplayedName(newAttributes.getDisplayedName());
        instructor.setInstructorPrivilegeAsText(newAttributes.getInstructorPrivilegesAsText());

        saveEntity(instructor);

        newAttributes = makeAttributes(instructor);

        return newAttributes;
    }

    /**
     * Deletes the instructor specified by courseId and email.
     *
     * <p>Fails silently if the instructor does not exist.
     */
    public void deleteInstructor(String courseId, String email) {
        assert email != null;
        assert courseId != null;

        Instructor instructorToDelete = getInstructorEntityForEmail(courseId, email);

        if (instructorToDelete == null) {
            return;
        }

        deleteEntity(instructorToDelete);
    }

    private Instructor getInstructorEntityForAccountId(String courseId, String accountId) {
        Session currentSession = HibernateUtil.getSessionFactory().getCurrentSession();

        CriteriaBuilder criteriaBuilder = currentSession.getCriteriaBuilder();
        CriteriaQuery<Instructor> crq = criteriaBuilder.createQuery(Instructor.class);

        Root<Instructor> root = crq.from(Instructor.class);
        Predicate isSameAccount = criteriaBuilder.equal(root.get("accountId"), accountId);
        Predicate isSameCourse = criteriaBuilder.equal(root.get("courseId"), courseId);

        crq.select(root).where(criteriaBuilder.and(isSameAccount, isSameCourse));

        Query<Instructor> query = currentSession.createQuery(crq);
        List<Instructor> results = query.getResultList();

        if (results.isEmpty()) {
            return null;
        }

        return results.get(0);
    }

    private Instructor getInstructorEntityForEmail(String courseId, String email) {
        return getInstructorEntityById(courseId, email);
    }

    private Instructor getInstructorEntityById(String courseId, String email) {
        Session currentSession = HibernateUtil.getSessionFactory().getCurrentSession();

        return currentSession.get(Instructor.class, Instructor.generateId(email, courseId));
    }

    private List<Instructor> getInstructorEntitiesThatAreDisplayedInCourse(String courseId) {
        Session currentSession = HibernateUtil.getSessionFactory().getCurrentSession();

        CriteriaBuilder criteriaBuilder = currentSession.getCriteriaBuilder();
        CriteriaQuery<Instructor> crq = criteriaBuilder.createQuery(Instructor.class);

        Root<Instructor> root = crq.from(Instructor.class);
        Predicate isSameCourse = criteriaBuilder.equal(root.get("courseId"), courseId);
        Predicate isDisplayedToStudents = criteriaBuilder.equal(root.get("isDisplayedToStudents"), true);

        crq.select(root).where(criteriaBuilder.and(isSameCourse, isDisplayedToStudents));

        Query<Instructor> query = currentSession.createQuery(crq);
        List<Instructor> results = query.getResultList();

        return results;
    }

    private Instructor getInstructorEntityForRegistrationKey(String key) {
        Session currentSession = HibernateUtil.getSessionFactory().getCurrentSession();

        CriteriaBuilder criteriaBuilder = currentSession.getCriteriaBuilder();
        CriteriaQuery<Instructor> crq = criteriaBuilder.createQuery(Instructor.class);

        Root<Instructor> root = crq.from(Instructor.class);
        Predicate isSameRegistrationKey = criteriaBuilder.equal(root.get("registrationKey"), key);

        crq.select(root).where(isSameRegistrationKey);

        Query<Instructor> query = currentSession.createQuery(crq);
        List<Instructor> instructorList = query.getResultList();

        // If registration key detected is not unique, something is wrong
        if (instructorList.size() > 1) {
            log.severe("Duplicate registration keys detected for: "
                    + instructorList.stream().map(i -> i.getUniqueId()).collect(Collectors.joining(", ")));
        }

        if (instructorList.isEmpty()) {
            return null;
        }

        return instructorList.get(0);
    }

    /**
     * Omits instructors with isArchived == omitArchived.
     * This means that the corresponding course is archived by the instructor.
     */
    private List<Instructor> getInstructorEntitiesForAccountId(String accountId, boolean omitArchived) {
        Session currentSession = HibernateUtil.getSessionFactory().getCurrentSession();

        CriteriaBuilder criteriaBuilder = currentSession.getCriteriaBuilder();
        CriteriaQuery<Instructor> crq = criteriaBuilder.createQuery(Instructor.class);

        Root<Instructor> root = crq.from(Instructor.class);
        Predicate isSameAccount = criteriaBuilder.equal(root.get("accountId"), accountId);

        if (omitArchived) {
            Predicate isNotArchived = criteriaBuilder.equal(root.get("isArchived"), false);
            crq.select(root).where(criteriaBuilder.and(isSameAccount, isNotArchived));
        } else {
            crq.select(root).where(isSameAccount);
        }

        Query<Instructor> query = currentSession.createQuery(crq);
        List<Instructor> results = query.getResultList();

        return results;
    }

    private List<Instructor> getInstructorEntitiesForCourse(String courseId) {
        Session currentSession = HibernateUtil.getSessionFactory().getCurrentSession();

        CriteriaBuilder criteriaBuilder = currentSession.getCriteriaBuilder();
        CriteriaQuery<Instructor> crq = criteriaBuilder.createQuery(Instructor.class);

        Root<Instructor> root = crq.from(Instructor.class);
        Predicate isSameCourse = criteriaBuilder.equal(root.get("courseId"), courseId);
        crq.select(root).where(isSameCourse);

        Query<Instructor> query = currentSession.createQuery(crq);
        List<Instructor> results = query.getResultList();

        return results;
    }

    @Override
    boolean hasExistingEntities(InstructorAttributes entityToCreate) {
        Instructor instructor = getInstructorEntityById(
                entityToCreate.getCourseId(), entityToCreate.getEmail());

        return instructor != null;
    }

    @Override
    InstructorAttributes makeAttributes(Instructor entity) {
        assert entity != null;

        return InstructorAttributes.valueOf(entity);
    }

}
