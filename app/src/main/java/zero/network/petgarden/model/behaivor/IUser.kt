package zero.network.petgarden.model.behaivor

import zero.network.petgarden.model.component.Location
import zero.network.petgarden.model.entity.Owner
import zero.network.petgarden.model.entity.SitterIMP
import java.io.Serializable
import java.util.*

/**
 * @author CarlosEduardoL
 * All the variables that the users [Owner] and [SitterIMP] share
 */
interface IUser: Serializable {
    var name: String
    var lastName: String
    var email: String
    var birthDay: Date
    var location: Location
}