package com.leave.mang.domain;

import com.leave.mang.domain.enumeration.Department;
import com.leave.mang.domain.enumeration.LeaveStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import java.time.LocalDate;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A LeaveRequest.
 */
@Entity
@Table(name = "leave_request")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class LeaveRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "description")
    private String description;

    @NotNull
    @Column(name = "from_date", nullable = false)
    private LocalDate fromDate;

    @NotNull
    @Column(name = "to_date", nullable = false)
    private LocalDate toDate;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private LeaveStatus status;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "department", nullable = false)
    private Department department;

    @NotNull
    @Column(name = "changed_at", nullable = false)
    private Instant changedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    private User employee;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public LeaveRequest id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return this.title;
    }

    public LeaveRequest title(String title) {
        this.setTitle(title);
        return this;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return this.description;
    }

    public LeaveRequest description(String description) {
        this.setDescription(description);
        return this;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDate getFromDate() {
        return this.fromDate;
    }

    public LeaveRequest fromDate(LocalDate fromDate) {
        this.setFromDate(fromDate);
        return this;
    }

    public void setFromDate(LocalDate fromDate) {
        this.fromDate = fromDate;
    }

    public LocalDate getToDate() {
        return this.toDate;
    }

    public LeaveRequest toDate(LocalDate toDate) {
        this.setToDate(toDate);
        return this;
    }

    public void setToDate(LocalDate toDate) {
        this.toDate = toDate;
    }

    public LeaveStatus getStatus() {
        return this.status;
    }

    public LeaveRequest status(LeaveStatus status) {
        this.setStatus(status);
        return this;
    }

    public void setStatus(LeaveStatus status) {
        this.status = status;
    }

    public Department getDepartment() {
        return this.department;
    }

    public LeaveRequest department(Department department) {
        this.setDepartment(department);
        return this;
    }

    public void setDepartment(Department department) {
        this.department = department;
    }

    public Instant getChangedAt() {
        return this.changedAt;
    }

    public LeaveRequest changedAt(Instant changedAt) {
        this.setChangedAt(changedAt);
        return this;
    }

    public void setChangedAt(Instant changedAt) {
        this.changedAt = changedAt;
    }

    public User getEmployee() {
        return this.employee;
    }

    public void setEmployee(User user) {
        this.employee = user;
    }

    public LeaveRequest employee(User user) {
        this.setEmployee(user);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof LeaveRequest)) {
            return false;
        }
        return getId() != null && getId().equals(((LeaveRequest) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "LeaveRequest{" +
            "id=" + getId() +
            ", title='" + getTitle() + "'" +
            ", description='" + getDescription() + "'" +
            ", fromDate='" + getFromDate() + "'" +
            ", toDate='" + getToDate() + "'" +
            ", status='" + getStatus() + "'" +
            ", department='" + getDepartment() + "'" +
            ", changedAt='" + getChangedAt() + "'" +
            "}";
    }
}
