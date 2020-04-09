package zero.network.petgarden.model.entity

import java.io.Serializable
import java.util.*

data class User(
    val id: String = UUID.randomUUID().toString(),
    var name: String = "",
    var lastName: String = "",
    var email: String = "",
    var password: String = "",
    var birthDay: Date = Date()
): Serializable