package com.leave.link.web.rest;

import static com.leave.link.domain.LeaveRequestAsserts.*;
import static com.leave.link.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.leave.link.IntegrationTest;
import com.leave.link.domain.LeaveRequest;
import com.leave.link.domain.enumeration.Department;
import com.leave.link.domain.enumeration.LeaveStatus;
import com.leave.link.repository.LeaveRequestRepository;
import com.leave.link.repository.UserRepository;
import com.leave.link.service.LeaveRequestService;
import com.leave.link.service.dto.LeaveRequestDTO;
import com.leave.link.service.mapper.LeaveRequestMapper;
import jakarta.persistence.EntityManager;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link LeaveRequestResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class LeaveRequestResourceIT {

    private static final String DEFAULT_TITLE = "AAAAAAAAAA";
    private static final String UPDATED_TITLE = "BBBBBBBBBB";

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final LocalDate DEFAULT_FROM_DATE = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_FROM_DATE = LocalDate.now(ZoneId.systemDefault());

    private static final LocalDate DEFAULT_TO_DATE = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_TO_DATE = LocalDate.now(ZoneId.systemDefault());

    private static final LeaveStatus DEFAULT_STATUS = LeaveStatus.REQUESTED;
    private static final LeaveStatus UPDATED_STATUS = LeaveStatus.REJECTED;

    private static final Department DEFAULT_DEPARTMENT = Department.IT;
    private static final Department UPDATED_DEPARTMENT = Department.RH;

    private static final Instant DEFAULT_CHANGED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_CHANGED_AT = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String ENTITY_API_URL = "/api/leave-requests";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private LeaveRequestRepository leaveRequestRepository;

    @Autowired
    private UserRepository userRepository;

    @Mock
    private LeaveRequestRepository leaveRequestRepositoryMock;

    @Autowired
    private LeaveRequestMapper leaveRequestMapper;

    @Mock
    private LeaveRequestService leaveRequestServiceMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restLeaveRequestMockMvc;

    private LeaveRequest leaveRequest;

    private LeaveRequest insertedLeaveRequest;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static LeaveRequest createEntity(EntityManager em) {
        LeaveRequest leaveRequest = new LeaveRequest()
            .title(DEFAULT_TITLE)
            .description(DEFAULT_DESCRIPTION)
            .fromDate(DEFAULT_FROM_DATE)
            .toDate(DEFAULT_TO_DATE)
            .status(DEFAULT_STATUS)
            .department(DEFAULT_DEPARTMENT)
            .changedAt(DEFAULT_CHANGED_AT);
        return leaveRequest;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static LeaveRequest createUpdatedEntity(EntityManager em) {
        LeaveRequest leaveRequest = new LeaveRequest()
            .title(UPDATED_TITLE)
            .description(UPDATED_DESCRIPTION)
            .fromDate(UPDATED_FROM_DATE)
            .toDate(UPDATED_TO_DATE)
            .status(UPDATED_STATUS)
            .department(UPDATED_DEPARTMENT)
            .changedAt(UPDATED_CHANGED_AT);
        return leaveRequest;
    }

    @BeforeEach
    public void initTest() {
        leaveRequest = createEntity(em);
    }

    @AfterEach
    public void cleanup() {
        if (insertedLeaveRequest != null) {
            leaveRequestRepository.delete(insertedLeaveRequest);
            insertedLeaveRequest = null;
        }
    }

    @Test
    @Transactional
    void createLeaveRequest() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the LeaveRequest
        LeaveRequestDTO leaveRequestDTO = leaveRequestMapper.toDto(leaveRequest);
        var returnedLeaveRequestDTO = om.readValue(
            restLeaveRequestMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(leaveRequestDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            LeaveRequestDTO.class
        );

        // Validate the LeaveRequest in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedLeaveRequest = leaveRequestMapper.toEntity(returnedLeaveRequestDTO);
        assertLeaveRequestUpdatableFieldsEquals(returnedLeaveRequest, getPersistedLeaveRequest(returnedLeaveRequest));

        insertedLeaveRequest = returnedLeaveRequest;
    }

    @Test
    @Transactional
    void createLeaveRequestWithExistingId() throws Exception {
        // Create the LeaveRequest with an existing ID
        leaveRequest.setId(1L);
        LeaveRequestDTO leaveRequestDTO = leaveRequestMapper.toDto(leaveRequest);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restLeaveRequestMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(leaveRequestDTO)))
            .andExpect(status().isBadRequest());

        // Validate the LeaveRequest in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkTitleIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        leaveRequest.setTitle(null);

        // Create the LeaveRequest, which fails.
        LeaveRequestDTO leaveRequestDTO = leaveRequestMapper.toDto(leaveRequest);

        restLeaveRequestMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(leaveRequestDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkFromDateIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        leaveRequest.setFromDate(null);

        // Create the LeaveRequest, which fails.
        LeaveRequestDTO leaveRequestDTO = leaveRequestMapper.toDto(leaveRequest);

        restLeaveRequestMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(leaveRequestDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkToDateIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        leaveRequest.setToDate(null);

        // Create the LeaveRequest, which fails.
        LeaveRequestDTO leaveRequestDTO = leaveRequestMapper.toDto(leaveRequest);

        restLeaveRequestMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(leaveRequestDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkStatusIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        leaveRequest.setStatus(null);

        // Create the LeaveRequest, which fails.
        LeaveRequestDTO leaveRequestDTO = leaveRequestMapper.toDto(leaveRequest);

        restLeaveRequestMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(leaveRequestDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkDepartmentIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        leaveRequest.setDepartment(null);

        // Create the LeaveRequest, which fails.
        LeaveRequestDTO leaveRequestDTO = leaveRequestMapper.toDto(leaveRequest);

        restLeaveRequestMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(leaveRequestDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkChangedAtIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        leaveRequest.setChangedAt(null);

        // Create the LeaveRequest, which fails.
        LeaveRequestDTO leaveRequestDTO = leaveRequestMapper.toDto(leaveRequest);

        restLeaveRequestMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(leaveRequestDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllLeaveRequests() throws Exception {
        // Initialize the database
        insertedLeaveRequest = leaveRequestRepository.saveAndFlush(leaveRequest);

        // Get all the leaveRequestList
        restLeaveRequestMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(leaveRequest.getId().intValue())))
            .andExpect(jsonPath("$.[*].title").value(hasItem(DEFAULT_TITLE)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].fromDate").value(hasItem(DEFAULT_FROM_DATE.toString())))
            .andExpect(jsonPath("$.[*].toDate").value(hasItem(DEFAULT_TO_DATE.toString())))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())))
            .andExpect(jsonPath("$.[*].department").value(hasItem(DEFAULT_DEPARTMENT.toString())))
            .andExpect(jsonPath("$.[*].changedAt").value(hasItem(DEFAULT_CHANGED_AT.toString())));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllLeaveRequestsWithEagerRelationshipsIsEnabled() throws Exception {
        when(leaveRequestServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restLeaveRequestMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(leaveRequestServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllLeaveRequestsWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(leaveRequestServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restLeaveRequestMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(leaveRequestRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getLeaveRequest() throws Exception {
        // Initialize the database
        insertedLeaveRequest = leaveRequestRepository.saveAndFlush(leaveRequest);

        // Get the leaveRequest
        restLeaveRequestMockMvc
            .perform(get(ENTITY_API_URL_ID, leaveRequest.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(leaveRequest.getId().intValue()))
            .andExpect(jsonPath("$.title").value(DEFAULT_TITLE))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION))
            .andExpect(jsonPath("$.fromDate").value(DEFAULT_FROM_DATE.toString()))
            .andExpect(jsonPath("$.toDate").value(DEFAULT_TO_DATE.toString()))
            .andExpect(jsonPath("$.status").value(DEFAULT_STATUS.toString()))
            .andExpect(jsonPath("$.department").value(DEFAULT_DEPARTMENT.toString()))
            .andExpect(jsonPath("$.changedAt").value(DEFAULT_CHANGED_AT.toString()));
    }

    @Test
    @Transactional
    void getNonExistingLeaveRequest() throws Exception {
        // Get the leaveRequest
        restLeaveRequestMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingLeaveRequest() throws Exception {
        // Initialize the database
        insertedLeaveRequest = leaveRequestRepository.saveAndFlush(leaveRequest);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the leaveRequest
        LeaveRequest updatedLeaveRequest = leaveRequestRepository.findById(leaveRequest.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedLeaveRequest are not directly saved in db
        em.detach(updatedLeaveRequest);
        updatedLeaveRequest
            .title(UPDATED_TITLE)
            .description(UPDATED_DESCRIPTION)
            .fromDate(UPDATED_FROM_DATE)
            .toDate(UPDATED_TO_DATE)
            .status(UPDATED_STATUS)
            .department(UPDATED_DEPARTMENT)
            .changedAt(UPDATED_CHANGED_AT);
        LeaveRequestDTO leaveRequestDTO = leaveRequestMapper.toDto(updatedLeaveRequest);

        restLeaveRequestMockMvc
            .perform(
                put(ENTITY_API_URL_ID, leaveRequestDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(leaveRequestDTO))
            )
            .andExpect(status().isOk());

        // Validate the LeaveRequest in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedLeaveRequestToMatchAllProperties(updatedLeaveRequest);
    }

    @Test
    @Transactional
    void putNonExistingLeaveRequest() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        leaveRequest.setId(longCount.incrementAndGet());

        // Create the LeaveRequest
        LeaveRequestDTO leaveRequestDTO = leaveRequestMapper.toDto(leaveRequest);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restLeaveRequestMockMvc
            .perform(
                put(ENTITY_API_URL_ID, leaveRequestDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(leaveRequestDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the LeaveRequest in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchLeaveRequest() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        leaveRequest.setId(longCount.incrementAndGet());

        // Create the LeaveRequest
        LeaveRequestDTO leaveRequestDTO = leaveRequestMapper.toDto(leaveRequest);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restLeaveRequestMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(leaveRequestDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the LeaveRequest in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamLeaveRequest() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        leaveRequest.setId(longCount.incrementAndGet());

        // Create the LeaveRequest
        LeaveRequestDTO leaveRequestDTO = leaveRequestMapper.toDto(leaveRequest);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restLeaveRequestMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(leaveRequestDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the LeaveRequest in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateLeaveRequestWithPatch() throws Exception {
        // Initialize the database
        insertedLeaveRequest = leaveRequestRepository.saveAndFlush(leaveRequest);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the leaveRequest using partial update
        LeaveRequest partialUpdatedLeaveRequest = new LeaveRequest();
        partialUpdatedLeaveRequest.setId(leaveRequest.getId());

        partialUpdatedLeaveRequest
            .description(UPDATED_DESCRIPTION)
            .fromDate(UPDATED_FROM_DATE)
            .toDate(UPDATED_TO_DATE)
            .status(UPDATED_STATUS);

        restLeaveRequestMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedLeaveRequest.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedLeaveRequest))
            )
            .andExpect(status().isOk());

        // Validate the LeaveRequest in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertLeaveRequestUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedLeaveRequest, leaveRequest),
            getPersistedLeaveRequest(leaveRequest)
        );
    }

    @Test
    @Transactional
    void fullUpdateLeaveRequestWithPatch() throws Exception {
        // Initialize the database
        insertedLeaveRequest = leaveRequestRepository.saveAndFlush(leaveRequest);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the leaveRequest using partial update
        LeaveRequest partialUpdatedLeaveRequest = new LeaveRequest();
        partialUpdatedLeaveRequest.setId(leaveRequest.getId());

        partialUpdatedLeaveRequest
            .title(UPDATED_TITLE)
            .description(UPDATED_DESCRIPTION)
            .fromDate(UPDATED_FROM_DATE)
            .toDate(UPDATED_TO_DATE)
            .status(UPDATED_STATUS)
            .department(UPDATED_DEPARTMENT)
            .changedAt(UPDATED_CHANGED_AT);

        restLeaveRequestMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedLeaveRequest.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedLeaveRequest))
            )
            .andExpect(status().isOk());

        // Validate the LeaveRequest in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertLeaveRequestUpdatableFieldsEquals(partialUpdatedLeaveRequest, getPersistedLeaveRequest(partialUpdatedLeaveRequest));
    }

    @Test
    @Transactional
    void patchNonExistingLeaveRequest() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        leaveRequest.setId(longCount.incrementAndGet());

        // Create the LeaveRequest
        LeaveRequestDTO leaveRequestDTO = leaveRequestMapper.toDto(leaveRequest);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restLeaveRequestMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, leaveRequestDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(leaveRequestDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the LeaveRequest in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchLeaveRequest() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        leaveRequest.setId(longCount.incrementAndGet());

        // Create the LeaveRequest
        LeaveRequestDTO leaveRequestDTO = leaveRequestMapper.toDto(leaveRequest);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restLeaveRequestMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(leaveRequestDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the LeaveRequest in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamLeaveRequest() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        leaveRequest.setId(longCount.incrementAndGet());

        // Create the LeaveRequest
        LeaveRequestDTO leaveRequestDTO = leaveRequestMapper.toDto(leaveRequest);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restLeaveRequestMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(leaveRequestDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the LeaveRequest in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteLeaveRequest() throws Exception {
        // Initialize the database
        insertedLeaveRequest = leaveRequestRepository.saveAndFlush(leaveRequest);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the leaveRequest
        restLeaveRequestMockMvc
            .perform(delete(ENTITY_API_URL_ID, leaveRequest.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return leaveRequestRepository.count();
    }

    protected void assertIncrementedRepositoryCount(long countBefore) {
        assertThat(countBefore + 1).isEqualTo(getRepositoryCount());
    }

    protected void assertDecrementedRepositoryCount(long countBefore) {
        assertThat(countBefore - 1).isEqualTo(getRepositoryCount());
    }

    protected void assertSameRepositoryCount(long countBefore) {
        assertThat(countBefore).isEqualTo(getRepositoryCount());
    }

    protected LeaveRequest getPersistedLeaveRequest(LeaveRequest leaveRequest) {
        return leaveRequestRepository.findById(leaveRequest.getId()).orElseThrow();
    }

    protected void assertPersistedLeaveRequestToMatchAllProperties(LeaveRequest expectedLeaveRequest) {
        assertLeaveRequestAllPropertiesEquals(expectedLeaveRequest, getPersistedLeaveRequest(expectedLeaveRequest));
    }

    protected void assertPersistedLeaveRequestToMatchUpdatableProperties(LeaveRequest expectedLeaveRequest) {
        assertLeaveRequestAllUpdatablePropertiesEquals(expectedLeaveRequest, getPersistedLeaveRequest(expectedLeaveRequest));
    }
}
