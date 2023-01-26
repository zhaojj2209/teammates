package teammates.ui.output;

import java.util.List;
import java.util.stream.Collectors;

import teammates.common.datatransfer.InstructorPermissionSet;

/**
 * The API output format of a course.
 */
public class CourseAndFeedbackSessionData extends ApiOutput {

    private final String courseId;
    private final String courseName;
    private final String timeZone;
    private final String institute;
    private final List<FeedbackSessionData> feedbackSessions;
    private InstructorPermissionSet privileges;

    public CourseAndFeedbackSessionData(teammates.common.datatransfer.sqlattributes.CourseAttributes courseAttributes) {
        this.courseId = courseAttributes.getId();
        this.courseName = courseAttributes.getName();
        this.timeZone = courseAttributes.getTimeZone();
        this.institute = courseAttributes.getInstitute();
        this.feedbackSessions = courseAttributes.getFeedbackSessions().stream().map(FeedbackSessionData::new).collect(Collectors.toList());
    }

    public String getCourseId() {
        return courseId;
    }

    public String getCourseName() {
        return courseName;
    }

    public String getInstitute() {
        return institute;
    }

    public String getTimeZone() {
        return timeZone;
    }

    public List<FeedbackSessionData> getFeedbackSessions() {
        return feedbackSessions;
    }

    public InstructorPermissionSet getPrivileges() {
        return privileges;
    }

    public void setPrivileges(InstructorPermissionSet privileges) {
        this.privileges = privileges;
    }

}
