package teammates.storage.sqlentity;

import java.time.Instant;

import teammates.common.util.Const;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

/**
 * Represents a course entity.
 */
@Entity
@Table(name = "courses", uniqueConstraints = { @UniqueConstraint(columnNames = "id") })
public class Course extends BaseEntity {

    @Id
    @Column(name = "id", unique = true, nullable = false)
    private String id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "timezone", nullable = false)
    private String timeZone;

    @Column(name = "institute", nullable = false)
    private String institute;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @Column(name = "deleted_at")
    private Instant deletedAt;

    public Course() {
        // recommended by Hibernate
    }

    public Course(String courseId, String courseName, String courseTimeZone, String institute,
            Instant createdAt, Instant deletedAt) {
        this.setUniqueId(courseId);
        this.setName(courseName);
        if (courseTimeZone == null) {
            this.setTimeZone(Const.DEFAULT_TIME_ZONE);
        } else {
            this.setTimeZone(courseTimeZone);
        }
        this.setInstitute(institute);
        if (createdAt == null) {
            this.setCreatedAt(Instant.now());
        } else {
            this.setCreatedAt(createdAt);
        }
        this.setUpdatedAt(this.createdAt);
        this.setDeletedAt(deletedAt);
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

}
