package zero.network.petgarden.model.notifications

import zero.network.petgarden.model.entity.Duration

class Message (
     var petID: String  ="",
     var duration: Duration = Duration(0,0,0)) {
}
