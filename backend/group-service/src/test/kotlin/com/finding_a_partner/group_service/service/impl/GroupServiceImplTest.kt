package com.finding_a_partner.group_service.service.impl

import com.finding_a_partner.group_service.database.entity.Group
import com.finding_a_partner.group_service.database.repository.GroupDao
import com.finding_a_partner.group_service.enums.GroupRoleType
import com.finding_a_partner.group_service.errors.ResourceNotFoundException
import com.finding_a_partner.group_service.mappers.GroupMapper
import com.finding_a_partner.group_service.model.request.GroupMembershipRequest
import com.finding_a_partner.group_service.model.request.GroupRequest
import com.finding_a_partner.group_service.model.response.GroupResponse
import com.finding_a_partner.group_service.service.GroupMembershipService
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import io.mockk.slot
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.util.*

class GroupServiceImplTest {

    private lateinit var groupDao: GroupDao
    private lateinit var groupMapper: GroupMapper
    private lateinit var groupMembershipService: GroupMembershipService
    private lateinit var groupService: GroupServiceImpl

    @BeforeEach
    fun setUp() {
        groupDao = mockk()
        groupMapper = mockk()
        groupMembershipService = mockk()
        groupService = GroupServiceImpl(groupDao, groupMapper, groupMembershipService)
    }

    @Test
    fun `create should create group and add creator as owner`() {
        // Given
        val userId = 1L
        val groupRequest = GroupRequest(
            name = "Test Group",
            description = "Test Description",
            creatorUserId = userId
        )
        val savedGroup = Group(
            name = "Test Group",
            description = "Test Description",
            creatorUserId = userId
        ).apply { id = 1L }
        val groupResponse = GroupResponse(
            id = 1L,
            name = "Test Group",
            description = "Test Description",
            creatorUserId = userId,
            createdAt = null
        )

        every { groupDao.save(any()) } returns savedGroup
        every { groupMembershipService.create(1L, userId, any()) } returns mockk()
        every { groupMapper.entityToResponse(savedGroup) } returns groupResponse

        // When
        val result = groupService.create(groupRequest, userId)

        // Then
        assertNotNull(result)
        assertEquals(groupResponse.id, result.id)
        assertEquals(groupResponse.name, result.name)
        
        val groupSlot = slot<Group>()
        val membershipRequestSlot = slot<GroupMembershipRequest>()
        verify(exactly = 1) { groupDao.save(capture(groupSlot)) }
        verify(exactly = 1) { groupMembershipService.create(1L, userId, capture(membershipRequestSlot)) }
        verify(exactly = 1) { groupMapper.entityToResponse(savedGroup) }
        
        assertEquals(groupRequest.name, groupSlot.captured.name)
        assertEquals(groupRequest.description, groupSlot.captured.description)
        assertEquals(GroupRoleType.OWNER, membershipRequestSlot.captured.role)
    }

    @Test
    fun `getById should return group when found`() {
        // Given
        val groupId = 1L
        val group = Group(
            name = "Test Group",
            description = "Test Description",
            creatorUserId = 1L
        ).apply { id = groupId }
        val groupResponse = GroupResponse(
            id = groupId,
            name = "Test Group",
            description = "Test Description",
            creatorUserId = 1L,
            createdAt = null
        )

        every { groupDao.findById(groupId) } returns Optional.of(group)
        every { groupMapper.entityToResponse(group) } returns groupResponse

        // When
        val result = groupService.getById(groupId)

        // Then
        assertNotNull(result)
        assertEquals(groupResponse.id, result.id)
        assertEquals(groupResponse.name, result.name)
        verify(exactly = 1) { groupDao.findById(groupId) }
        verify(exactly = 1) { groupMapper.entityToResponse(group) }
    }

    @Test
    fun `getById should throw ResourceNotFoundException when group not found`() {
        // Given
        val groupId = 999L

        every { groupDao.findById(groupId) } returns Optional.empty()

        // When & Then
        assertThrows<ResourceNotFoundException> {
            groupService.getById(groupId)
        }
        verify(exactly = 1) { groupDao.findById(groupId) }
        verify(exactly = 0) { groupMapper.entityToResponse(any()) }
    }

    @Test
    fun `getAll should return all groups`() {
        // Given
        val groups = listOf(
            Group("Group 1", "Description 1", 1L).apply { id = 1L },
            Group("Group 2", "Description 2", 2L).apply { id = 2L }
        )
        val groupResponses = listOf(
            GroupResponse(1L, "Group 1", "Description 1", 1L, null),
            GroupResponse(2L, "Group 2", "Description 2", 2L, null)
        )

        every { groupDao.findAll() } returns groups
        every { groupMapper.entityToResponse(groups[0]) } returns groupResponses[0]
        every { groupMapper.entityToResponse(groups[1]) } returns groupResponses[1]

        // When
        val result = groupService.getAll()

        // Then
        assertEquals(2, result.size)
        verify(exactly = 1) { groupDao.findAll() }
        verify(exactly = 2) { groupMapper.entityToResponse(any()) }
    }

    @Test
    fun `searchGroups should return groups matching query`() {
        // Given
        val query = "Test"
        val groups = listOf(
            Group("Test Group", "Description", 1L).apply { id = 1L },
            Group("Another Group", "Test Description", 2L).apply { id = 2L }
        )
        val groupResponses = listOf(
            GroupResponse(1L, "Test Group", "Description", 1L, null),
            GroupResponse(2L, "Another Group", "Test Description", 2L, null)
        )

        every { groupDao.findByNameContainingOrDescriptionContaining(query, query) } returns groups
        every { groupMapper.entityToResponse(groups[0]) } returns groupResponses[0]
        every { groupMapper.entityToResponse(groups[1]) } returns groupResponses[1]

        // When
        val result = groupService.searchGroups(query)

        // Then
        assertEquals(2, result.size)
        verify(exactly = 1) { groupDao.findByNameContainingOrDescriptionContaining(query, query) }
        verify(exactly = 2) { groupMapper.entityToResponse(any()) }
    }

    @Test
    fun `update should update group successfully`() {
        // Given
        val groupId = 1L
        val existingGroup = Group(
            name = "Old Name",
            description = "Old Description",
            creatorUserId = 1L
        ).apply { id = groupId }
        val updateRequest = GroupRequest(
            name = "New Name",
            description = "New Description",
            creatorUserId = 1L
        )
        val updatedGroupResponse = GroupResponse(
            id = groupId,
            name = "New Name",
            description = "New Description",
            creatorUserId = 1L,
            createdAt = null
        )

        every { groupDao.findById(groupId) } returns Optional.of(existingGroup)
        every { groupDao.save(any()) } returns existingGroup
        every { groupMapper.entityToResponse(any()) } returns updatedGroupResponse

        // When
        val result = groupService.update(groupId, updateRequest)

        // Then
        assertNotNull(result)
        assertEquals(updateRequest.name, result.name)
        assertEquals(updateRequest.description, result.description)
        verify(exactly = 1) { groupDao.findById(groupId) }
        verify(exactly = 1) { groupDao.save(any()) }
        verify(exactly = 1) { groupMapper.entityToResponse(any()) }
    }

    @Test
    fun `delete should delete group successfully`() {
        // Given
        val groupId = 1L
        val group = Group(
            name = "Test Group",
            description = "Description",
            creatorUserId = 1L
        ).apply { id = groupId }

        every { groupDao.findById(groupId) } returns Optional.of(group)
        every { groupDao.delete(group) } returns Unit

        // When
        groupService.delete(groupId)

        // Then
        verify(exactly = 1) { groupDao.findById(groupId) }
        verify(exactly = 1) { groupDao.delete(group) }
    }

    @Test
    fun `getEntityById should return group entity when found`() {
        // Given
        val groupId = 1L
        val group = Group(
            name = "Test Group",
            description = "Description",
            creatorUserId = 1L
        ).apply { id = groupId }

        every { groupDao.findById(groupId) } returns Optional.of(group)

        // When
        val result = groupService.getEntityById(groupId)

        // Then
        assertNotNull(result)
        assertEquals(group.id, result.id)
        assertEquals(group.name, result.name)
        verify(exactly = 1) { groupDao.findById(groupId) }
    }

    @Test
    fun `getEntityById should throw ResourceNotFoundException when group not found`() {
        // Given
        val groupId = 999L

        every { groupDao.findById(groupId) } returns Optional.empty()

        // When & Then
        assertThrows<ResourceNotFoundException> {
            groupService.getEntityById(groupId)
        }
        verify(exactly = 1) { groupDao.findById(groupId) }
    }
}

