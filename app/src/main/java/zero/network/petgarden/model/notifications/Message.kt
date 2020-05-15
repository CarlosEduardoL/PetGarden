package zero.network.petgarden.model.notifications

class Message (
     var type:Int = 0,
     var id: String  ="",
     var body: String = "",
     var userId: String = "",
     var timestamp: Long = 0) {

   companion object {
    const val TYPE_TEXT = 0
    const val TYPE_IMAGE = 1
   }



}
