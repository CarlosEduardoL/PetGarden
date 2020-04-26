package zero.network.petgarden.model.entity

import java.io.Serializable
import java.util.*

open class User(
    val id: String = UUID.randomUUID().toString(),
    var name: String = "",
    var lastName: String = "",
    var email: String = "",
    var password: String = "",
    var birthDay: Date = Date(),
    var imageURL: String = "",
    var location: Location = Location(0.0,0.0)
): Serializable

class Owner(
    id: String = UUID.randomUUID().toString(),
    name: String = "",
    lastName: String = "",
    email: String = "",
    password: String = "",
    birthDay: Date = Date(),
    imageURL: String = "",
    location: Location = Location(0.0,0.0)
): User(id, name, lastName, email, password, birthDay, imageURL, location)