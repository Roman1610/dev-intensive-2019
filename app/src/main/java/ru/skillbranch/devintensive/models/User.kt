package ru.skillbranch.devintensive.models

import ru.skillbranch.devintensive.utils.Utils
import java.util.*

data class User (
    val id : String,
    var firstName : String?,
    var lastName : String?,
    var avatar : String?,
    var rating : Int = 0,
    var respect : Int = 0,
    var lastVisit : Date? = Date(),
    var isOnline : Boolean = false
) {

    constructor(id: String, firstName: String?, lastName: String?):
            this(id = id, firstName = firstName, lastName = lastName, avatar = null)

    private constructor(builder: Builder):
            this(builder.id, builder.firstName, builder.lastName, builder.avatar,
                builder.rating, builder.respect, builder.lastVisit, builder.isOnline)

    companion object Factory {
        private var lastId = -1

        fun makeUser(fullName: String?): User {
            lastId++

            val (firstName, lastName) = Utils.parseFullName(fullName)

            return User("$lastId", firstName, lastName)
        }
    }

    class Builder {
        var id : String = "0"
            private set
        var firstName : String? = ""
            private set
        var lastName : String? = ""
            private set
        var avatar : String? = null
            private set
        var rating : Int = 0
            private set
        var respect : Int = 0
            private set
        var lastVisit : Date? = Date()
            private set
        var isOnline : Boolean = false
            private set

        fun id(id: String) = apply { this@Builder.id = id }
        fun firstName(firstName: String) = apply { this@Builder.firstName = firstName }
        fun lastName(lastName: String) = apply { this@Builder.lastName = lastName }
        fun avatar(avatar: String) = apply { this@Builder.avatar = avatar }
        fun rating(rating: Int) = apply { this@Builder.rating = rating }
        fun respect(respect: Int) = apply { this@Builder.respect = respect }
        fun lastVisit(lastVisit: Date) = apply { this@Builder.lastVisit = lastVisit }
        fun isOnline(isOnline: Boolean) = apply { this@Builder.isOnline = isOnline }

        fun build() = User(builder = this)
    }
}