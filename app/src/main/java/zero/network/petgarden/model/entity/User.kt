package zero.network.petgarden.model.entity

import zero.network.petgarden.model.behaivor.IUser
import java.io.Serializable
import java.util.*

data class User(
    override var name: String = "",
    override var lastName: String = "",
    override var email: String = "",
    override var password: String = "",
    override var birthDay: Date = Date(),
    override var imageURL: String? = null,
    override var location: Location = Location(1.88,2.99)
): Serializable, IUser

