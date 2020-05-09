package zero.network.petgarden.model.behaivor

import zero.network.petgarden.model.entity.Location
import java.io.Serializable
import java.util.*
import zero.network.petgarden.model.entity.Owner
import zero.network.petgarden.model.entity.Sitter

/**
 * @author CarlosEduardoL
 * All the variables that the users [Owner] and [Sitter] share
 */
interface IUser: Serializable {
    val id: String
    var name: String
    var lastName: String
    var email: String
    var password: String
    var birthDay: Date
    var imageURL: String?
    var location: Location
}