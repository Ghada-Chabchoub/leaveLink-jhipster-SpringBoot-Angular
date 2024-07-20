package com.leave.mang.web.rest;

import com.leave.mang.repository.LeaveRequestRepository;
import com.leave.mang.service.LeaveRequestService;
import com.leave.mang.service.dto.LeaveRequestDTO;
import com.leave.mang.web.rest.errors.BadRequestAlertException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link com.leave.mang.domain.LeaveRequest}.
 */
@RestController
@RequestMapping("/api/leave-requests")
public class LeaveRequestResource {

    private static final Logger log = LoggerFactory.getLogger(LeaveRequestResource.class);

    private static final String ENTITY_NAME = "leaveRequest";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final LeaveRequestService leaveRequestService;

    private final LeaveRequestRepository leaveRequestRepository;

    public LeaveRequestResource(LeaveRequestService leaveRequestService, LeaveRequestRepository leaveRequestRepository) {
        this.leaveRequestService = leaveRequestService;
        this.leaveRequestRepository = leaveRequestRepository;
    }

    /**
     * {@code POST  /leave-requests} : Create a new leaveRequest.
     *
     * @param leaveRequestDTO the leaveRequestDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new leaveRequestDTO, or with status {@code 400 (Bad Request)} if the leaveRequest has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<LeaveRequestDTO> createLeaveRequest(@Valid @RequestBody LeaveRequestDTO leaveRequestDTO)
        throws URISyntaxException {
        log.debug("REST request to save LeaveRequest : {}", leaveRequestDTO);
        if (leaveRequestDTO.getId() != null) {
            throw new BadRequestAlertException("A new leaveRequest cannot already have an ID", ENTITY_NAME, "idexists");
        }
        leaveRequestDTO = leaveRequestService.save(leaveRequestDTO);
        return ResponseEntity.created(new URI("/api/leave-requests/" + leaveRequestDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, leaveRequestDTO.getId().toString()))
            .body(leaveRequestDTO);
    }

    /**
     * {@code PUT  /leave-requests/:id} : Updates an existing leaveRequest.
     *
     * @param id the id of the leaveRequestDTO to save.
     * @param leaveRequestDTO the leaveRequestDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated leaveRequestDTO,
     * or with status {@code 400 (Bad Request)} if the leaveRequestDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the leaveRequestDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<LeaveRequestDTO> updateLeaveRequest(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody LeaveRequestDTO leaveRequestDTO
    ) throws URISyntaxException {
        log.debug("REST request to update LeaveRequest : {}, {}", id, leaveRequestDTO);
        if (leaveRequestDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, leaveRequestDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!leaveRequestRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        leaveRequestDTO = leaveRequestService.update(leaveRequestDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, leaveRequestDTO.getId().toString()))
            .body(leaveRequestDTO);
    }

    /**
     * {@code PATCH  /leave-requests/:id} : Partial updates given fields of an existing leaveRequest, field will ignore if it is null
     *
     * @param id the id of the leaveRequestDTO to save.
     * @param leaveRequestDTO the leaveRequestDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated leaveRequestDTO,
     * or with status {@code 400 (Bad Request)} if the leaveRequestDTO is not valid,
     * or with status {@code 404 (Not Found)} if the leaveRequestDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the leaveRequestDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<LeaveRequestDTO> partialUpdateLeaveRequest(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody LeaveRequestDTO leaveRequestDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update LeaveRequest partially : {}, {}", id, leaveRequestDTO);
        if (leaveRequestDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, leaveRequestDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!leaveRequestRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<LeaveRequestDTO> result = leaveRequestService.partialUpdate(leaveRequestDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, leaveRequestDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /leave-requests} : get all the leaveRequests.
     *
     * @param pageable the pagination information.
     * @param eagerload flag to eager load entities from relationships (This is applicable for many-to-many).
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of leaveRequests in body.
     */
    @GetMapping("")
    public ResponseEntity<List<LeaveRequestDTO>> getAllLeaveRequests(
        @org.springdoc.core.annotations.ParameterObject Pageable pageable,
        @RequestParam(name = "eagerload", required = false, defaultValue = "true") boolean eagerload
    ) {
        log.debug("REST request to get a page of LeaveRequests");
        Page<LeaveRequestDTO> page;
        if (eagerload) {
            page = leaveRequestService.findAllWithEagerRelationships(pageable);
        } else {
            page = leaveRequestService.findAll(pageable);
        }
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /leave-requests/:id} : get the "id" leaveRequest.
     *
     * @param id the id of the leaveRequestDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the leaveRequestDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<LeaveRequestDTO> getLeaveRequest(@PathVariable("id") Long id) {
        log.debug("REST request to get LeaveRequest : {}", id);
        Optional<LeaveRequestDTO> leaveRequestDTO = leaveRequestService.findOne(id);
        return ResponseUtil.wrapOrNotFound(leaveRequestDTO);
    }

    /**
     * {@code DELETE  /leave-requests/:id} : delete the "id" leaveRequest.
     *
     * @param id the id of the leaveRequestDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLeaveRequest(@PathVariable("id") Long id) {
        log.debug("REST request to delete LeaveRequest : {}", id);
        leaveRequestService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
