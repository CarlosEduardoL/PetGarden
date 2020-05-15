package zero.network.petgarden.model.notifications

class FCMMessage(private val to:String, private val data:Message) {
    constructor():this("", Message())


}