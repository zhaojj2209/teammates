package teammates.test;

import teammates.common.datatransfer.sqlattributes.CourseAttributes;
import teammates.common.datatransfer.sqlattributes.EntityAttributes;
import teammates.common.datatransfer.sqlattributes.InstructorAttributes;
import teammates.common.util.JsonUtils;

public abstract class BaseTestCaseWithPostgresSqlDatabaseAccess extends BaseTestCase{
    private static final int VERIFICATION_RETRY_COUNT = 5;
    private static final int VERIFICATION_RETRY_DELAY_IN_MS = 1000;
    private static final int OPERATION_RETRY_COUNT = 5;
    private static final int OPERATION_RETRY_DELAY_IN_MS = 1000;

    protected void verifyPresentInDatabase(EntityAttributes<?> expected) {
        int retryLimit = VERIFICATION_RETRY_COUNT;
        EntityAttributes<?> actual = getEntity(expected);
        while (actual == null && retryLimit > 0) {
            retryLimit--;
            ThreadHelper.waitFor(VERIFICATION_RETRY_DELAY_IN_MS);
            actual = getEntity(expected);
        }
        verifyEquals(expected, actual);
    }

    private EntityAttributes<?> getEntity(EntityAttributes<?> expected) {
        if (expected instanceof CourseAttributes) {
            return getCourse((CourseAttributes) expected);

        } else {
            throw new RuntimeException("Unknown entity type!");
        }
    }

    protected abstract CourseAttributes getCourse(CourseAttributes course);

    private void verifyEquals(EntityAttributes<?> expected, EntityAttributes<?> actual) {
        if (expected instanceof CourseAttributes) {
            CourseAttributes expectedCourse = (CourseAttributes) expected;
            CourseAttributes actualCourse = (CourseAttributes) actual;
            equalizeIrrelevantData(expectedCourse, actualCourse);
            assertEquals(JsonUtils.toJson(expectedCourse), JsonUtils.toJson(actualCourse));

        }
    }

//    private void equalizeIrrelevantData(AccountAttributes expected, AccountAttributes actual) {
//        // Ignore time field as it is stamped at the time of creation in testing
//        expected.setCreatedAt(actual.getCreatedAt());
//    }
//
//    private void equalizeIrrelevantData(StudentProfileAttributes expected, StudentProfileAttributes actual) {
//        expected.setModifiedDate(actual.getModifiedDate());
//    }

    private void equalizeIrrelevantData(CourseAttributes expected, CourseAttributes actual) {
        // Ignore time field as it is stamped at the time of creation in testing
        expected.setCreatedAt(actual.getCreatedAt());
    }

//    private void equalizeIrrelevantData(FeedbackQuestionAttributes expected, FeedbackQuestionAttributes actual) {
//        expected.setId(actual.getId());
//    }
//
//    private void equalizeIrrelevantData(FeedbackResponseAttributes expected, FeedbackResponseAttributes actual) {
//        expected.setId(actual.getId());
//    }
//
//    private void equalizeIrrelevantData(FeedbackSessionAttributes expected, FeedbackSessionAttributes actual) {
//        expected.setCreatedTime(actual.getCreatedTime());
//        // Not available in FeedbackSessionData and thus ignored
//        expected.setCreatorEmail(actual.getCreatorEmail());
//    }

    private void equalizeIrrelevantData(InstructorAttributes expected, InstructorAttributes actual) {
        // pretend keys match because the key is generated only before storing into database
        if (actual.getKey() != null) {
            expected.setKey(actual.getKey());
        }
    }

//    private void equalizeIrrelevantData(StudentAttributes expected, StudentAttributes actual) {
//        // For these fields, we consider null and "" equivalent.
//        if (expected.getGoogleId() == null && actual.getGoogleId().isEmpty()) {
//            expected.setGoogleId("");
//        }
//        if (expected.getTeam() == null && actual.getTeam().isEmpty()) {
//            expected.setTeam("");
//        }
//        if (expected.getComments() == null && actual.getComments().isEmpty()) {
//            expected.setComments("");
//        }
//
//        // pretend keys match because the key is generated only before storing into database
//        if (actual.getKey() != null) {
//            expected.setKey(actual.getKey());
//        }
//    }

//    private void equalizeIrrelevantData(DeadlineExtensionAttributes expected, DeadlineExtensionAttributes actual) {
//        // Ignore time field as it is stamped at the time of creation in testing
//        expected.setCreatedAt(actual.getCreatedAt());
//        expected.setUpdatedAt(actual.getUpdatedAt());
//    }
//
//    private void equalizeIrrelevantData(NotificationAttributes expected, NotificationAttributes actual) {
//        // Ignore time field as it is stamped at the time of creation in testing
//        expected.setCreatedAt(actual.getCreatedAt());
//        expected.setUpdatedAt(actual.getUpdatedAt());
//    }
}
