package zero.network.petgarden.model.entity

import zero.network.petgarden.model.behaivor.IPlanner
import zero.network.petgarden.model.behaivor.IUser

data class Sitter(
    private val user: IUser,
    var rating: Double,
    var kindPets: String,
    var additional: String
): IUser by user, IPlanner by Planner()  {
    private val planner = Planner()

    val availability: Duration?
        get() = planner.availabilities.min()

}