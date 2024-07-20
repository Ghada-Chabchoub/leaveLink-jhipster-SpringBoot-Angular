package com.leave.mang.repository;

import com.leave.mang.domain.LeaveRequest;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the LeaveRequest entity.
 */
@Repository
public interface LeaveRequestRepository extends JpaRepository<LeaveRequest, Long> {
    @Query("select leaveRequest from LeaveRequest leaveRequest where leaveRequest.employee.login = ?#{authentication.name}")
    List<LeaveRequest> findByEmployeeIsCurrentUser();

    default Optional<LeaveRequest> findOneWithEagerRelationships(Long id) {
        return this.findOneWithToOneRelationships(id);
    }

    default List<LeaveRequest> findAllWithEagerRelationships() {
        return this.findAllWithToOneRelationships();
    }

    default Page<LeaveRequest> findAllWithEagerRelationships(Pageable pageable) {
        return this.findAllWithToOneRelationships(pageable);
    }

    @Query(
        value = "select leaveRequest from LeaveRequest leaveRequest left join fetch leaveRequest.employee",
        countQuery = "select count(leaveRequest) from LeaveRequest leaveRequest"
    )
    Page<LeaveRequest> findAllWithToOneRelationships(Pageable pageable);

    @Query("select leaveRequest from LeaveRequest leaveRequest left join fetch leaveRequest.employee")
    List<LeaveRequest> findAllWithToOneRelationships();

    @Query("select leaveRequest from LeaveRequest leaveRequest left join fetch leaveRequest.employee where leaveRequest.id =:id")
    Optional<LeaveRequest> findOneWithToOneRelationships(@Param("id") Long id);
}
