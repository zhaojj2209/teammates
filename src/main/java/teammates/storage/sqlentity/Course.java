package teammates.storage.sqlentity;

import java.time.Instant;
import java.util.Set;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;


/**
 * Represents a course entity.
 */
@Entity
@Table(name = "courses")
public class Course extends BaseEntity {

    @Id
    private String id;

    @Column(name = "name")
    private String name;

    @Column(name = "timezone")
    private String timeZone;

    @Column(name = "institute")
    private String institute;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "courseId")
    private Set<FeedbackSession> feedbackSessions;

    @CreationTimestamp
    @Column(name = "created_at")
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private Instant updatedAt;

    @Column(name = "deleted_at")
    private Instant deletedAt;

    public Course() {
        // recommended by Hibernate
    }

    public Course(String courseId, String courseName, String courseTimeZone, String institute, Instant deletedAt, Set<FeedbackSession> feedbackSessions) {
        this.setUniqueId(courseId);
        this.setName(courseName);
        this.setTimeZone(courseTimeZone);
        this.setInstitute(institute);
        this.setFeedbackSessions(feedbackSessions);
        if (deletedAt != null) {
            this.setDeletedAt(deletedAt);
        }
    }

    public String getUniqueId() {
        return id;
    }

    public void setUniqueId(String uniqueId) {
        this.id = uniqueId.trim();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name.trim();
    }

    public String getTimeZone() {
        return timeZone;
    }

    public void setTimeZone(String timeZone) {
        this.timeZone = timeZone;
    }

    public String getInstitute() {
        return institute;
    }

    public void setInstitute(String institute) {
        this.institute = institute;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Instant getDeletedAt() {
        return deletedAt;
    }

    public void setDeletedAt(Instant deletedAt) {
        this.deletedAt = deletedAt;
    }

    public Set<FeedbackSession> getFeedbackSessions() {
        return feedbackSessions;
    }

    public void setFeedbackSessions(Set<FeedbackSession> feedbackSessions) {
        this.feedbackSessions = feedbackSessions;
    }

    
}
