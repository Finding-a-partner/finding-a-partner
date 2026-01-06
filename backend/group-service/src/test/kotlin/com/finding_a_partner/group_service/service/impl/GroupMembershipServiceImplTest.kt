package com.finding_a_partner.group_service.service.impl

import com.finding_a_partner.group_service.database.entity.Group
import com.finding_a_partner.group_service.database.entity.GroupMembership
import com.finding_a_partner.group_service.database.entity.GroupMembershipId
import com.finding_a_partner.group_service.database.repository.GroupDao
import com.finding_a_partner.group_service.database.repository.GroupMembershipDao
import com.finding_a_partner.group_service.enums.GroupRoleType
import com.finding_a_partner.group_service.errors.ResourceNotFoundException
import com.finding_a_partner.group_service.mappers.GroupMapper
import com.finding_a_partner.group_service.mappers.GroupMembershipMapper
import com.finding_a_partner.group_service.model.request.GroupMembershipRequest
import com.finding_a_partner.group_service.model.response.GroupMemberResponse
import com.finding_a_partner.group_service.model.response.GroupMembershipResponse
import com.finding_a_partner.group_service.model.response.GroupResponse
import com.finding_a_partner.group_service.service.UserService
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.util.*

class GroupMembershipServiceImplTest {

    private lateinit var groupMembershipDao: GroupMembershipDao
    private lateinit var groupMembershipMapper: GroupMembershipMapper
    private lateinit var groupMapper: GroupMapper
    private lateinit var groupDao: GroupDao
    private lateinit var userService: UserService
    private lateinit var groupMembershipService: GroupMembershipServiceImpl

    @BeforeEach
    fun setUp() {
        groupMembershipDao = mockk()
        groupMembershipMapper = mockk()
        groupMapper = mockk()
        groupDao = mockk()
        userService = mockk()
        groupMembershipService = GroupMembershipServiceImpl(
            groupMembershipDao,
            groupMembershipMapper,
            groupMapper,
            groupDao,
            userService
        )
    }

    @Test
    fun `create should add member to group successfully`() {
        // Given
        val groupId = 1L
        val userId = 2L
        val request = GroupMembershipRequest(role = GroupRoleType.MEMBER)
        val group = Group(
            name = "Test Group",
            description = "Description",
            creatorUserId = 1L
        ).apply { id = groupId }
        val membershipId = GroupMembershipId(groupId, userId)
        val membership = GroupMembership(
            id = membershipId,
            group = group,
            userId = userId,
            role = GroupRoleType.MEMBER
        )
        val response = GroupMembershipResponse(
            id = membershipId,
            groupId = groupId,
            userId = userId,
            role = GroupRoleType.MEMBER
        )

        every { groupDao.findById(groupId) } returns Optional.of(group)
        every { groupMembershipDao.save(any()) } returns membership
        every { groupMembershipMapper.entityToResponse(membership) } returns response

        // When
        val result = groupMembershipService.create(groupId, userId, request)

        // Then
        assertNotNull(result)
        assertEquals(response.id, result.id)
        assertEquals(response.groupId, result.groupId)
        assertEquals(response.userId, result.userId)
        verify(exactly = 1) { groupDao.findById(groupId) }
        verify(exactly = 1) { groupMembershipDao.save(any()) }
        verify(exactly = 1) { groupMembershipMapper.entityToResponse(any()) }
    }

    @Test
    fun `create should throw ResourceNotFoundException when group not found`() {
        // Given
        val groupId = 999L
        val userId = 2L
        val request = GroupMembershipRequest(role = GroupRoleType.MEMBER)

        every { groupDao.findById(groupId) } returns Optional.empty()

        // When & Then
        assertThrows<ResourceNotFoundException> {
            groupMembershipService.create(groupId, userId, request)
        }
        verify(exactly = 1) { groupDao.findById(groupId) }
        verify(exactly = 0) { groupMembershipDao.save(any()) }
    }

    @Test
    fun `getAllByGroupId should return all members for group`() {
        // Given
        val groupId = 1L
        val group = Group(
            name = "Test Group",
            description = "Description",
            creatorUserId = 1L
        ).apply { id = groupId }
        val memberships = listOf(
            GroupMembership(
                id = GroupMembershipId(groupId, 1L),
                group = group,
                userId = 1L,
                role = GroupRoleType.OWNER
            ),
            GroupMembership(
                id = GroupMembershipId(groupId, 2L),
                group = group,
                userId = 2L,
                role = GroupRoleType.MEMBER
            )
        )
        val userInfo = listOf(
            com.finding_a_partner.group_service.service.User(
                id = 1L,
                createdAt = java.time.LocalDateTime.now(),
                login = "user1",
                email = "user1@example.com",
                description = null,
                name = "User",
                surname = "One"
            ),
            com.finding_a_partner.group_service.service.User(
                id = 2L,
                createdAt = java.time.LocalDateTime.now(),
                login = "user2",
                email = "user2@example.com",
                description = null,
                name = "User",
                surname = "Two"
            )
        )

        every { groupDao.findById(groupId) } returns Optional.of(group)
        every { groupMembershipDao.findAllByIdGroupId(groupId) } returns memberships
        every { userService.getUsersByIds(listOf(1L, 2L)) } returns userInfo

        // When
        val result = groupMembershipService.getAllByGroupId(groupId)

        // Then
        assertEquals(2, result.size)
        assertEquals(1L, result[0].userId)
        assertEquals(2L, result[1].userId)
        verify(exactly = 1) { groupDao.findById(groupId) }
        verify(exactly = 1) { groupMembershipDao.findAllByIdGroupId(groupId) }
        verify(exactly = 1) { userService.getUsersByIds(listOf(1L, 2L)) }
    }

    @Test
    fun `getAllByUserId should return all groups for user`() {
        // Given
        val userId = 1L
        val group1 = Group("Group 1", "Description 1", 1L).apply { id = 1L }
        val group2 = Group("Group 2", "Description 2", 2L).apply { id = 2L }
        val memberships = listOf(
            GroupMembership(
                id = GroupMembershipId(1L, userId),
                group = group1,
                userId = userId,
                role = GroupRoleType.MEMBER
            ),
            GroupMembership(
                id = GroupMembershipId(2L, userId),
                group = group2,
                userId = userId,
                role = GroupRoleType.MEMBER
            )
        )
        val groupResponses = listOf(
            GroupResponse(1L, "Group 1", "Description 1", 1L, null),
            GroupResponse(2L, "Group 2", "Description 2", 2L, null)
        )

        every { groupMembershipDao.findAllWithGroupByUserId(userId) } returns memberships
        every { groupMapper.entityToResponse(group1) } returns groupResponses[0]
        every { groupMapper.entityToResponse(group2) } returns groupResponses[1]

        // When
        val result = groupMembershipService.getAllByUserId(userId)

        // Then
        assertEquals(2, result.size)
        verify(exactly = 1) { groupMembershipDao.findAllWithGroupByUserId(userId) }
        verify(exactly = 2) { groupMapper.entityToResponse(any()) }
    }

    @Test
    fun `update should update membership role successfully`() {
        // Given
        val groupId = 1L
        val userId = 2L
        val request = GroupMembershipRequest(role = GroupRoleType.ADMIN)
        val membershipId = GroupMembershipId(groupId, userId)
        val existingMembership = GroupMembership(
            id = membershipId,
            group = Group("Group", "Desc", 1L).apply { id = groupId },
            userId = userId,
            role = GroupRoleType.MEMBER
        )
        val response = GroupMembershipResponse(
            id = membershipId,
            groupId = groupId,
            userId = userId,
            role = GroupRoleType.ADMIN
        )

        every { groupMembershipDao.findById(membershipId) } returns Optional.of(existingMembership)
        every { groupMembershipDao.save(any()) } returns existingMembership
        every { groupMembershipMapper.entityToResponse(any()) } returns response

        // When
        val result = groupMembershipService.update(groupId, userId, request)

        // Then
        assertNotNull(result)
        assertEquals(GroupRoleType.ADMIN, result.role)
        verify(exactly = 1) { groupMembershipDao.findById(membershipId) }
        verify(exactly = 1) { groupMembershipDao.save(any()) }
        verify(exactly = 1) { groupMembershipMapper.entityToResponse(any()) }
    }

    @Test
    fun `delete should remove member from group successfully`() {
        // Given
        val groupId = 1L
        val userId = 2L
        val membershipId = GroupMembershipId(groupId, userId)
        val membership = GroupMembership(
            id = membershipId,
            group = Group("Group", "Desc", 1L).apply { id = groupId },
            userId = userId,
            role = GroupRoleType.MEMBER
        )

        every { groupMembershipDao.findById(membershipId) } returns Optional.of(membership)
        every { groupMembershipDao.delete(membership) } returns Unit

        // When
        groupMembershipService.delete(groupId, userId)

        // Then
        verify(exactly = 1) { groupMembershipDao.findById(membershipId) }
        verify(exactly = 1) { groupMembershipDao.delete(membership) }
    }

    @Test
    fun `delete should throw ResourceNotFoundException when membership not found`() {
        // Given
        val groupId = 1L
        val userId = 2L
        val membershipId = GroupMembershipId(groupId, userId)

        every { groupMembershipDao.findById(membershipId) } returns Optional.empty()

        // When & Then
        assertThrows<ResourceNotFoundException> {
            groupMembershipService.delete(groupId, userId)
        }
        verify(exactly = 1) { groupMembershipDao.findById(membershipId) }
        verify(exactly = 0) { groupMembershipDao.delete(any()) }
    }
}

