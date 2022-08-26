package teammates.ui.webapi;

import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.util.Const;
import teammates.ui.output.InstructorData;
import teammates.ui.request.Intent;

/**
 * Get the information of an instructor inside a course.
 */
class GetInstructorAction extends BasicFeedbackSubmissionAction {

    private static final String UNAUTHORIZED_ACCESS = "You are not allowed to view this resource!";

    @Override
    AuthType getMinAuthLevel() {
        return AuthType.PUBLIC;
    }

    @Override
    void checkSpecificAccessControl() throws UnauthorizedAccessException {
        Intent intent = Intent.valueOf(getNonNullRequestParamValue(Const.ParamsNames.INTENT));
        switch (intent) {
        case INSTRUCTOR_SUBMISSION:
        case INSTRUCTOR_RESULT:
            String courseId = getNonNullRequestParamValue(Const.ParamsNames.COURSE_ID);
            InstructorAttributes instructorAttributes = getInstructorOfCourseFromRequest(courseId);
            teammates.common.datatransfer.sqlattributes.InstructorAttributes sqlInstructorAttributes =
                    getSqlInstructorOfCourseFromRequest(courseId);
            if (instructorAttributes == null && sqlInstructorAttributes == null) {
                throw new UnauthorizedAccessException(UNAUTHORIZED_ACCESS);
            }
            break;
        case FULL_DETAIL:
            gateKeeper.verifyLoggedInUserPrivileges(userInfo);
            break;
        default:
            throw new InvalidHttpParameterException("Unknown intent " + intent);
        }
    }

    @Override
    public JsonResult execute() {
        String intentString = getNonNullRequestParamValue(Const.ParamsNames.INTENT);
        Intent intent;
        try {
            intent = Intent.valueOf(intentString);
        } catch (IllegalArgumentException e) {
            throw new InvalidHttpParameterException("Invalid intent: " + intentString, e);
        }

        InstructorAttributes instructorAttributes;
        teammates.common.datatransfer.sqlattributes.InstructorAttributes sqlInstructorAttributes;
        String courseId = getNonNullRequestParamValue(Const.ParamsNames.COURSE_ID);

        switch (intent) {
        case INSTRUCTOR_SUBMISSION:
        case INSTRUCTOR_RESULT:
            instructorAttributes = getInstructorOfCourseFromRequest(courseId);
            sqlInstructorAttributes = getSqlInstructorOfCourseFromRequest(courseId);
            break;
        case FULL_DETAIL:
            instructorAttributes = logic.getInstructorForGoogleId(courseId, userInfo.getId());
            sqlInstructorAttributes = logicNew.getInstructorForAccountId(courseId, userInfo.getId());
            break;
        default:
            throw new InvalidHttpParameterException("Unknown intent " + intent);
        }

        if (instructorAttributes == null && sqlInstructorAttributes == null) {
            throw new EntityNotFoundException("Instructor could not be found for this course");
        }

        InstructorData instructorData = sqlInstructorAttributes == null
                ? new InstructorData(instructorAttributes)
                : new InstructorData(sqlInstructorAttributes);
        String courseInstitute = logicNew.getCourseInstitute(courseId) == null
                ? logic.getCourseInstitute(courseId)
                : logicNew.getCourseInstitute(courseId);
        instructorData.setInstitute(courseInstitute);
        if (intent == Intent.FULL_DETAIL) {
            String accountId = sqlInstructorAttributes == null
                    ? sqlInstructorAttributes.getAccountId()
                    : instructorAttributes.getGoogleId();
            instructorData.setGoogleId(accountId);
        }

        return new JsonResult(instructorData);
    }

}
