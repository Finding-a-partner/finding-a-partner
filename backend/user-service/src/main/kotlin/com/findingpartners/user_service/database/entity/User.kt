package com.findingpartners.user_service.database.entity

import jakarta.persistence.*

@Entity
@Table(name = "`user`")
class User(
    @Column(nullable = true)
    var description: String? = null,

    @Column(nullable = false, unique = true)
    var email: String,

    @Column(nullable = false, unique = true)
    var login: String,

    @Column(nullable = false)
    var name: String,

    @Column(nullable = true)
    var surname: String,
) : AbstractEntity() {
    @ManyToMany
    @JoinTable(
        name = "user_to_user",
        joinColumns = [JoinColumn(name = "user_id")],
        inverseJoinColumns = [JoinColumn(name = "friend_id")],
    )
    var friends: MutableSet<User> = mutableSetOf()

    @ManyToMany(mappedBy = "friends")
    var friendOf: MutableSet<User> = mutableSetOf()
}
