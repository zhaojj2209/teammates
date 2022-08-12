package teammates.storage.sql;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;

import teammates.common.datatransfer.sqlattributes.EntityAttributes;
import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.HibernateUtil;
import teammates.common.util.JsonUtils;
import teammates.common.util.Logger;
import teammates.storage.sqlentity.BaseEntity;

/**
 * Base class for all classes performing CRUD operations against the database.
 *
 * @param <E> Specific entity class
 * @param <A> Specific attributes class
 */
abstract class EntitiesDb<E extends BaseEntity, A extends EntityAttributes<E>> {

    /**
     * Error message when trying to create entity that already exist.
     */
    static final String ERROR_CREATE_ENTITY_ALREADY_EXISTS = "Trying to create an entity that exists: %s";

    /**
     * Error message when trying to update entity that does not exist.
     */
    static final String ERROR_UPDATE_NON_EXISTENT = "Trying to update non-existent Entity: ";

    /**
     * Info message when entity is not saved because it does not change.
     */
    static final String OPTIMIZED_SAVING_POLICY_APPLIED =
            "Saving request is not issued because entity %s does not change by the update (%s)";

    static final Logger log = Logger.getLogger();

    /**
     * Creates the entity in the database.
     *
     * @return created entity
     * @throws InvalidParametersException if the entity to create is invalid
     * @throws EntityAlreadyExistsException if the entity to create already exists
     */
    public A createEntity(A entityToCreate) throws InvalidParametersException, EntityAlreadyExistsException {
        return createEntity(entityToCreate, true);
    }

    private A createEntity(A entityToAdd, boolean shouldCheckExistence)
            throws InvalidParametersException, EntityAlreadyExistsException {
        assert entityToAdd != null;

        entityToAdd.sanitizeForSaving();

        if (!entityToAdd.isValid()) {
            throw new InvalidParametersException(entityToAdd.getInvalidityInfo());
        }

        if (shouldCheckExistence && hasExistingEntities(entityToAdd)) {
            String error = String.format(ERROR_CREATE_ENTITY_ALREADY_EXISTS, entityToAdd.toString());
            throw new EntityAlreadyExistsException(error);
        }

        E entity = convertToEntityForSaving(entityToAdd);

        Transaction tx = null;

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            session.persist(entity);
            tx.commit();
            log.info("Entity created: " + JsonUtils.toJson(entityToAdd));
        } catch (HibernateException e) {
            // TODO: Handle errors
            tx.rollback();
        }

        return makeAttributes(entity);
    }

    E convertToEntityForSaving(A entityAttributes) {
        return entityAttributes.toEntity();
    }

    /**
     * Checks whether there are existing entities in the database.
     */
    abstract boolean hasExistingEntities(A entityToCreate);

    /**
     * Checks whether two values are the same.
     */
    <T> boolean hasSameValue(T oldValue, T newValue) {
        return oldValue.equals(newValue);
    }

    /**
     * Saves an entity.
     */
    void saveEntity(E entityToSave) {
        assert entityToSave != null;

        Transaction tx = null;

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            session.merge(entityToSave);
            tx.commit();
            log.info("Entity saved: " + JsonUtils.toJson(entityToSave));
        } catch (HibernateException e) {
            // TODO: Handle errors
            tx.rollback();
        }
    }

    /**
     * Converts from entity to attributes.
     */
    abstract A makeAttributes(E entity);

    /**
     * Converts a collection of entities to a list of attributes.
     */
    List<A> makeAttributes(Collection<E> entities) {
        List<A> attributes = new LinkedList<>();
        for (E entity : entities) {
            attributes.add(makeAttributes(entity));
        }
        return attributes;
    }

    /**
     * Converts from entity to attributes.
     *
     * @return null if the original entity is null
     */
    A makeAttributesOrNull(E entity) {
        if (entity != null) {
            return makeAttributes(entity);
        }
        return null;
    }

}
