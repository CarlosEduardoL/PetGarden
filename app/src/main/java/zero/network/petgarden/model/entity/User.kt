package zero.network.petgarden.model.entity

import zero.network.petgarden.model.behaivor.IUser
import java.io.Serializable
import java.util.*

data class User(
    override val id: String = UUID.randomUUID().toString(),
    override var name: String = "",
    override var lastName: String = "",
    override var email: String = "",
    override var password: String = "",
    override var birthDay: Date = Date(),
    override var imageURL: String = "",
    override var location: Location = Location(0.0,0.0),
    override val pets: MutableList<Pet> = mutableListOf()
): Serializable, IUser

