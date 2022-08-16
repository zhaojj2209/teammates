package teammates.storage.sql;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.query.Query;

import teammates.common.datatransfer.sqlattributes.InstructorAttributes;
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

    @Override
    boolean hasExistingEntities(InstructorAttributes entityToCreate) {
        Session session = HibernateUtil.getSessionFactory().openSession();

        CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
        CriteriaQuery<Instructor> cr = criteriaBuilder.createQuery(Instructor.class);
        Root<Instructor> root = cr.from(Instructor.class);
        Predicate sameAccount = criteriaBuilder.equal(root.get("accountId"), entityToCreate.getAccountId());
        Predicate sameCourse = criteriaBuilder.equal(root.get("courseId"), entityToCreate.getCourseId());
        cr.select(root).where(criteriaBuilder.and(sameAccount, sameCourse));

        Query<Instructor> query = session.createQuery(cr);
        List<Instructor> results = query.getResultList();
        session.close();

        return !results.isEmpty();
    }

    @Override
    InstructorAttributes makeAttributes(Instructor entity) {
        assert entity != null;

        return InstructorAttributes.valueOf(entity);
    }

}
