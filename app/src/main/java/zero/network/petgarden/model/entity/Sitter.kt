package zero.network.petgarden.model.entity

import zero.network.petgarden.model.behaivor.IPlanner
import zero.network.petgarden.model.behaivor.IUser
import java.io.Serializable

data class Sitter(
    private val user: User,
    var rating: Double = 0.0,
    var kindPets: String = "Nothing Especial",
    var additional: String = "Nothing Especial",
    private val planner: Planner = Planner()
): IUser by user, IPlanner by planner, Serializable {

    val availability: Duration?
        get() = planner.availabilities.min()

}
