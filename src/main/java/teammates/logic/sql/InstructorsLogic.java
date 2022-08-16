package teammates.logic.sql;

import teammates.common.datatransfer.sqlattributes.InstructorAttributes;
import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.InvalidParametersException;
import teammates.storage.sql.InstructorsDb;

/**
 * Handles operations related to instructors.
 *
 * @see InstructorAttributes
 * @see InstructorsDb
 */
public final class InstructorsLogic {

    private static final InstructorsLogic instance = new InstructorsLogic();

    private final InstructorsDb instructorsDb = InstructorsDb.inst();

    private InstructorsLogic() {
        // prevent initialization
    }

    public static InstructorsLogic inst() {
        return instance;
    }

    /**
     * Creates an instructor.
     *
     * @return the created instructor
     * @throws InvalidParametersException if the instructor is not valid
     * @throws EntityAlreadyExistsException if the instructor already exists in the database
     */
    public InstructorAttributes createInstructor(InstructorAttributes instructorToAdd)
            throws InvalidParametersException, EntityAlreadyExistsException {
        return instructorsDb.createEntity(instructorToAdd);
    }
}
