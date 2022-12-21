package teammates.logic.sql;

import java.util.List;

import teammates.common.datatransfer.sqlattributes.InstructorAttributes;
import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InstructorUpdateException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Const;
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

    /**
     * Gets an instructor by unique constraint courseId-email.
     */
    public InstructorAttributes getInstructorForEmail(String courseId, String email) {
        return instructorsDb.getInstructorForEmail(courseId, email);
    }

    /**
     * Gets an instructor by unique ID.
     */
    public InstructorAttributes getInstructorById(String courseId, String email) {
        return instructorsDb.getInstructorById(courseId, email);
    }

    /**
     * Gets an instructor by unique constraint courseId-accountId.
     */
    public InstructorAttributes getInstructorForAccountId(String courseId, String accountId) {
        return instructorsDb.getInstructorForAccountId(courseId, accountId);
    }

    /**
     * Gets an instructor by unique constraint registrationKey.
     */
    public InstructorAttributes getInstructorForRegistrationKey(String registrationKey) {
        return instructorsDb.getInstructorForRegistrationKey(registrationKey);
    }

    /**
     * Gets all instructors of a course.
     */
    public List<InstructorAttributes> getInstructorsForCourse(String courseId) {
        List<InstructorAttributes> instructorReturnList = instructorsDb.getInstructorsForCourse(courseId);
        InstructorAttributes.sortByName(instructorReturnList);

        return instructorReturnList;
    }

    /**
     * Gets all non-archived instructors associated with an accountId.
     */
    public List<InstructorAttributes> getInstructorsForAccountId(String accountId) {
        return getInstructorsForAccountId(accountId, false);
    }

    /**
     * Gets all instructors associated with an accountId.
     *
     * @param omitArchived whether archived instructors should be omitted or not
     */
    public List<InstructorAttributes> getInstructorsForAccountId(String accountId, boolean omitArchived) {
        return instructorsDb.getInstructorsForAccountId(accountId, omitArchived);
    }

    /**
     * Verifies that at least one instructor is displayed to students.
     *
     * @throws InstructorUpdateException if there is no instructor displayed to students.
     */
    void verifyAtLeastOneInstructorIsDisplayed(String courseId, boolean isOriginalInstructorDisplayed,
                                               boolean isEditedInstructorDisplayed)
            throws InstructorUpdateException {
        List<InstructorAttributes> instructorsDisplayed = instructorsDb.getInstructorsDisplayedToStudents(courseId);
        boolean isEditedInstructorChangedToNonVisible = isOriginalInstructorDisplayed && !isEditedInstructorDisplayed;
        boolean isNoInstructorMadeVisible = instructorsDisplayed.isEmpty() && !isEditedInstructorDisplayed;

        if (isNoInstructorMadeVisible || instructorsDisplayed.size() == 1 && isEditedInstructorChangedToNonVisible) {
            throw new InstructorUpdateException("At least one instructor must be displayed to students");
        }
    }

    /**
     * Updates an instructor by {@link InstructorAttributes.UpdateOptionsWithAccountId}.
     *
     * <p>Cascade update the comments, responses and deadline extensions associated with the instructor.
     *
     * @return updated instructor
     * @throws InvalidParametersException if attributes to update are not valid
     * @throws EntityDoesNotExistException if the instructor cannot be found
     */
    public InstructorAttributes updateInstructorByAccountIdCascade(
            InstructorAttributes.UpdateOptionsWithAccountId updateOptions)
            throws InstructorUpdateException, InvalidParametersException, EntityDoesNotExistException {

        InstructorAttributes originalInstructor =
                instructorsDb.getInstructorForAccountId(updateOptions.getCourseId(), updateOptions.getAccountId());

        if (originalInstructor == null) {
            throw new EntityDoesNotExistException("Trying to update non-existent Entity: " + updateOptions);
        }

        InstructorAttributes newInstructor = originalInstructor.getCopy();
        newInstructor.update(updateOptions);

        boolean isOriginalInstructorDisplayed = originalInstructor.isDisplayedToStudents();
        verifyAtLeastOneInstructorIsDisplayed(originalInstructor.getCourseId(), isOriginalInstructorDisplayed,
                newInstructor.isDisplayedToStudents());

        InstructorAttributes updatedInstructor = instructorsDb.updateInstructorByGoogleId(updateOptions);

        if (!originalInstructor.getEmail().equals(updatedInstructor.getEmail())) {
            // TODO: cascade responses, comments, deadline extensions
        }

        return updatedInstructor;
    }

    /**
     * Updates an instructor by {@link InstructorAttributes.UpdateOptionsWithEmail}.
     *
     * @return updated instructor
     * @throws InvalidParametersException if attributes to update are not valid
     * @throws EntityDoesNotExistException if the instructor cannot be found
     */
    public InstructorAttributes updateInstructorByEmail(InstructorAttributes.UpdateOptionsWithEmail updateOptions)
            throws InstructorUpdateException, InvalidParametersException, EntityDoesNotExistException {
        assert updateOptions != null;

        InstructorAttributes originalInstructor =
                instructorsDb.getInstructorForEmail(updateOptions.getCourseId(), updateOptions.getEmail());

        if (originalInstructor == null) {
            throw new EntityDoesNotExistException("Trying to update non-existent Entity: " + updateOptions);
        }

        InstructorAttributes newInstructor = originalInstructor.getCopy();
        newInstructor.update(updateOptions);

        boolean isOriginalInstructorDisplayed = originalInstructor.isDisplayedToStudents();
        verifyAtLeastOneInstructorIsDisplayed(originalInstructor.getCourseId(), isOriginalInstructorDisplayed,
                newInstructor.isDisplayedToStudents());

        return instructorsDb.updateInstructorByEmail(updateOptions);
    }

    /**
     * Deletes an instructor cascade its associated feedback responses, deadline extensions and comments.
     *
     * <p>Fails silently if the student does not exist.
     */
    public void deleteInstructorCascade(String courseId, String email) {
        InstructorAttributes instructorAttributes = getInstructorForEmail(courseId, email);
        if (instructorAttributes == null) {
            return;
        }

        instructorsDb.deleteInstructor(courseId, email);
        // TODO: Cascade feedback responses, deadline extensions, comments
    }

    /**
     * Checks if there are any other registered instructors that can modify instructors.
     * If there are none, the instructor currently being edited will be granted the privilege
     * of modifying instructors automatically.
     *
     * @param courseId         Id of the course.
     * @param instructorToEdit Instructor that will be edited.
     *                         This may be modified within the method.
     */
    public void updateToEnsureValidityOfInstructorsForTheCourse(String courseId, InstructorAttributes instructorToEdit) {
        List<InstructorAttributes> instructors = getInstructorsForCourse(courseId);
        int numOfInstrCanModifyInstructor = 0;
        InstructorAttributes instrWithModifyInstructorPrivilege = null;
        for (InstructorAttributes instructor : instructors) {
            if (instructor.isAllowedForPrivilege(Const.InstructorPermissions.CAN_MODIFY_INSTRUCTOR)) {
                numOfInstrCanModifyInstructor++;
                instrWithModifyInstructorPrivilege = instructor;
            }
        }
        boolean isLastRegInstructorWithPrivilege = numOfInstrCanModifyInstructor <= 1
                && instrWithModifyInstructorPrivilege != null
                && (!instrWithModifyInstructorPrivilege.isRegistered()
                || instrWithModifyInstructorPrivilege.getAccountId()
                .equals(instructorToEdit.getAccountId()));
        if (isLastRegInstructorWithPrivilege) {
            instructorToEdit.getPrivileges().updatePrivilege(Const.InstructorPermissions.CAN_MODIFY_INSTRUCTOR, true);
        }
    }
}
