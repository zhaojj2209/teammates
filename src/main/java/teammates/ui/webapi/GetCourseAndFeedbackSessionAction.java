package teammates.ui.webapi;

import teammates.common.datatransfer.attributes.CourseAttributes;
import teammates.common.util.Const;
import teammates.ui.output.CourseAndFeedbackSessionData;

class GetCourseAndFeedbackSessionsAction extends Action {

    @Override
    AuthType getMinAuthLevel() {
        return AuthType.PUBLIC;
    }

    @Override
    void checkSpecificAccessControl() throws UnauthorizedAccessException {
        if (userInfo != null && userInfo.isAdmin) {
            return;
        }

        String courseId = getNonNullRequestParamValue(Const.ParamsNames.COURSE_ID);
        String entityType = getNonNullRequestParamValue(Const.ParamsNames.ENTITY_TYPE);
        CourseAttributes course = logic.getCourse(courseId);

        if (Const.EntityType.INSTRUCTOR.equals(entityType)) {
            gateKeeper.verifyAccessible(getPossiblyUnregisteredInstructor(courseId), course);
            return;
        }

        if (Const.EntityType.STUDENT.equals(entityType)) {
            gateKeeper.verifyAccessible(getPossiblyUnregisteredStudent(courseId), course);
            return;
        }

        throw new UnauthorizedAccessException("Student or instructor account is required to access this resource.");
    }

    @Override
    public JsonResult execute() {
        String courseId = getNonNullRequestParamValue(Const.ParamsNames.COURSE_ID);

        teammates.common.datatransfer.sqlattributes.CourseAttributes sqlCourseAttributes = logicNew.getCourse(courseId);
        CourseAndFeedbackSessionData output = new CourseAndFeedbackSessionData(sqlCourseAttributes);

        return new JsonResult(output);
    }
}
