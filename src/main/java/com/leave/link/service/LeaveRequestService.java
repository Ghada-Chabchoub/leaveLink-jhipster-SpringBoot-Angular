package com.leave.link.service;

import com.leave.link.domain.LeaveRequest;
import com.leave.link.repository.LeaveRequestRepository;
import com.leave.link.service.dto.LeaveRequestDTO;
import com.leave.link.service.mapper.LeaveRequestMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.leave.mang.domain.LeaveRequest}.
 */
@Service
@Transactional
public class LeaveRequestService {

    private static final Logger log = LoggerFactory.getLogger(LeaveRequestService.class);

    private final LeaveRequestRepository leaveRequestRepository;

    private final LeaveRequestMapper leaveRequestMapper;

    @Autowired
    public LeaveRequestService(LeaveRequestRepository leaveRequestRepository, LeaveRequestMapper leaveRequestMapper) {
        this.leaveRequestRepository = leaveRequestRepository;
        this.leaveRequestMapper = leaveRequestMapper;
    }

    /**
     * Save a leaveRequest.
     *
     * @param leaveRequestDTO the entity to save.
     * @return the persisted entity.
     */
    public LeaveRequestDTO save(LeaveRequestDTO leaveRequestDTO) {
        log.debug("Request to save LeaveRequest : {}", leaveRequestDTO);
        LeaveRequest leaveRequest = leaveRequestMapper.toEntity(leaveRequestDTO);
        leaveRequest = leaveRequestRepository.save(leaveRequest);
        return leaveRequestMapper.toDto(leaveRequest);
    }

    /**
     * Update a leaveRequest.
     *
     * @param leaveRequestDTO the entity to save.
     * @return the persisted entity.
     */
    public LeaveRequestDTO update(LeaveRequestDTO leaveRequestDTO) {
        log.debug("Request to update LeaveRequest : {}", leaveRequestDTO);
        LeaveRequest leaveRequest = leaveRequestMapper.toEntity(leaveRequestDTO);
        leaveRequest = leaveRequestRepository.save(leaveRequest);
        return leaveRequestMapper.toDto(leaveRequest);
    }

    /**
     * Partially update a leaveRequest.
     *
     * @param leaveRequestDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<LeaveRequestDTO> partialUpdate(LeaveRequestDTO leaveRequestDTO) {
        log.debug("Request to partially update LeaveRequest : {}", leaveRequestDTO);

        return leaveRequestRepository
            .findById(leaveRequestDTO.getId())
            .map(existingLeaveRequest -> {
                leaveRequestMapper.partialUpdate(existingLeaveRequest, leaveRequestDTO);

                return existingLeaveRequest;
            })
            .map(leaveRequestRepository::save)
            .map(leaveRequestMapper::toDto);
    }

    /**
     * Get all the leaveRequests.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<LeaveRequestDTO> findAll(Pageable pageable) {
        log.debug("Request to get all LeaveRequests");
        return leaveRequestRepository.findAll(pageable).map(leaveRequestMapper::toDto);
    }

    /**
     * Get all the leaveRequests with eager load of many-to-many relationships.
     *
     * @return the list of entities.
     */
    public Page<LeaveRequestDTO> findAllWithEagerRelationships(Pageable pageable) {
        return leaveRequestRepository.findAllWithEagerRelationships(pageable).map(leaveRequestMapper::toDto);
    }

    public Page<LeaveRequestDTO> findAllforEmp(Pageable pageable) {
        return leaveRequestRepository.findByEmployeeIsCurrentUser(pageable).map(leaveRequestMapper::toDto);
    }

    /**
     * Get one leaveRequest by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<LeaveRequestDTO> findOne(Long id) {
        log.debug("Request to get LeaveRequest : {}", id);
        return leaveRequestRepository.findOneWithEagerRelationships(id).map(leaveRequestMapper::toDto);
    }

    /**
     * Delete the leaveRequest by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete LeaveRequest : {}", id);
        leaveRequestRepository.deleteById(id);
    }
    /********/

} //fin class
