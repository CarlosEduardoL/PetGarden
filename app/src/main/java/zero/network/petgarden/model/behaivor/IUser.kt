package zero.network.petgarden.model.behaivor

import zero.network.petgarden.model.entity.Location
import java.io.Serializable
import java.util.*

interface IUser: Serializable {
    val id: String
    var name: String
    var lastName: String
    var email: String
    var password: String
    var birthDay: Date
    var imageURL: String
    var location: Location
}