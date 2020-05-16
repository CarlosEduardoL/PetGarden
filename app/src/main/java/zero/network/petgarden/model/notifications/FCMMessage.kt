package zero.network.petgarden.model.notifications

class FCMMessage(var to:String, var data:Message) {
    constructor():this("", Message())


    companion object{
        const val  API_KEY = "AAAAuYWDwzI:APA91bG5iGE7TT-M7YXO2JdtbWjQIkLRtQtobIWTwc0TS6Y8_FzkQqQIIq4WXlOlG01AlHcrjUVfoYTx4qXQ_PskUWrY1Y3X-amArmOcImPZOGFY0Me7_TTVS519xGmlGRXSsj6Ws0nX"
    }
}