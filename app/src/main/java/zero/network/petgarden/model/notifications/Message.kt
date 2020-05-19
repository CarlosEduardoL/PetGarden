package zero.network.petgarden.model.notifications

import java.io.Serializable

class Message (
     var ownerId: String ="",
     var ownerName: String="",
     var schedule: String="",
     var cost: String="",
     var responseContracting: String=""): Serializable{
}
