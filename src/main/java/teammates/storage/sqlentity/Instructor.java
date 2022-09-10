package teammates.storage.sqlentity;

import java.security.SecureRandom;
import java.time.Instant;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import teammates.common.util.StringHelper;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;


/**
 * An association class that represents the association Account
 * --> [is an instructor for] --> Course.
 */
@Entity
@Table(name = "instructors", uniqueConstraints = {
        @UniqueConstraint(columnNames = { "account_id", "course_id" })
})
public class Instructor extends BaseEntity {

    /**
     * The unique id of the entity.
     *
     * @see #generateId(String, String)
     */
    @Id
    @Column(name = "id", unique = true, nullable = false)
    private String id;

    /**
     * The account id of the instructor, used as the foreign key to locate the Account object.
     */
    @Column(name = "account_id")
    private String accountId;

    /** The foreign key to locate the Course object. */
    @Column(name = "course_id", nullable = false)
    private String courseId;

    /** Whether the associated course is archived. */
    @Column(name = "is_archived", nullable = false)
    private boolean isArchived;

    /** The instructor's name used for this course. */
    @Column(name = "name", nullable = false)
    private String name;

    /** The instructor's email used for this course. */
    @Column(name = "email", nullable = false)
    private String email;

    /** The instructor's registration key used for joining. */
    @Column(name = "registration_key", nullable = false)
    private String registrationKey;

    @Column(name = "role", nullable = false)
    private String role;

    @Column(name = "is_displayed_to_students", nullable = false)
    private Boolean isDisplayedToStudents;

    @Column(name = "displayed_name", nullable = false)
    private String displayedName;

    @Column(name = "instructor_privileges", nullable = false, columnDefinition = "TEXT")
    private String instructorPrivilegesAsText;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @SuppressWarnings("unused")
    private Instructor() {
        // recommended by Hibernate
    }

    public Instructor(String instructorAccountId, String courseId, boolean isArchived, String instructorName,
                      String instructorEmail, String role, boolean isDisplayedToStudents, String displayedName,
                      String instructorPrivilegesAsText) {
        this.setAccountId(instructorAccountId);
        this.setCourseId(courseId);
        this.setIsArchived(isArchived);
        this.setName(instructorName);
        this.setEmail(instructorEmail);
        this.setRole(role);
        this.setIsDisplayedToStudents(isDisplayedToStudents);
        this.setDisplayedName(displayedName);
        this.setInstructorPrivilegeAsText(instructorPrivilegesAsText);
        // setId should be called after setting email and courseId
        this.setUniqueId(generateId(this.getEmail(), this.getCourseId()));
        this.setRegistrationKey(generateRegistrationKey());
    }

    /**
     * Generates a unique ID for the instructor.
     */
    public static String generateId(String email, String courseId) {
        // Format: email%courseId e.g., adam@gmail.com%cs1101
        return email + '%' + courseId;
    }

    /**
     * Returns the unique ID of the entity (format: email%courseId).
     */
    public String getUniqueId() {
        return id;
    }

    /**
     * Sets the unique ID for the instructor entity.
     *
     * @param uniqueId
     *          The unique ID of the entity (format: email%courseId).
     */
    public void setUniqueId(String uniqueId) {
        this.id = uniqueId;
    }

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String instructorAccountId) {
        this.accountId = instructorAccountId;
    }

    public String getCourseId() {
        return courseId;
    }

    public void setCourseId(String courseId) {
        this.courseId = courseId;
    }

    /**
     * Gets the archived status of the instructor.
     */
    public boolean getIsArchived() {
        return isArchived;
    }

    public void setIsArchived(boolean isArchived) {
        this.isArchived = isArchived;
    }

    public String getName() {
        return name;
    }

    public void setName(String instructorName) {
        this.name = instructorName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String instructorEmail) {
        this.email = instructorEmail;
    }

    public String getRegistrationKey() {
        return registrationKey;
    }

    public void setRegistrationKey(String key) {
        this.registrationKey = key;
    }

    /**
     * Generate unique registration key for the instructor.
     * The key contains random elements to avoid being guessed.
     */
    private String generateRegistrationKey() {
        String uniqueId = getUniqueId();
        SecureRandom prng = new SecureRandom();

        return StringHelper.encrypt(uniqueId + prng.nextInt());
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    /**
     * Returns whether the instructor is displayed to students.
     */
    public boolean isDisplayedToStudents() {
        if (this.isDisplayedToStudents == null) {
            return true;
        }
        return isDisplayedToStudents;
    }

    public void setIsDisplayedToStudents(boolean shouldDisplayToStudents) {
        this.isDisplayedToStudents = shouldDisplayToStudents;
    }

    public String getDisplayedName() {
        return displayedName;
    }

    public void setDisplayedName(String displayedName) {
        this.displayedName = displayedName;
    }

    /**
     * Gets the instructor privileges stored in JSON format.
     */
    public String getInstructorPrivilegesAsText() {
        return instructorPrivilegesAsText;
    }

    public void setInstructorPrivilegeAsText(String instructorPrivilegesAsText) {
        this.instructorPrivilegesAsText = instructorPrivilegesAsText;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    /**
     * Sets the createdAt timestamp.
     */
    public void setCreatedAt(Instant created) {
        this.createdAt = created;
        setLastUpdate(created);
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setLastUpdate(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }

}

