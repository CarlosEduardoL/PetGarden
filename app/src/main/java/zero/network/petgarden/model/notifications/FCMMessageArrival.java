package zero.network.petgarden.model.notifications;

public class FCMMessageArrival {

    private String to;
    private MessageArrival data;
    public static final String API_KEY = "AAAAuYWDwzI:APA91bG5iGE7TT-M7YXO2JdtbWjQIkLRtQtobIWTwc0TS6Y8_FzkQqQIIq4WXlOlG01AlHcrjUVfoYTx4qXQ_PskUWrY1Y3X-amArmOcImPZOGFY0Me7_TTVS519xGmlGRXSsj6Ws0nX";


    public FCMMessageArrival(){

    }
    public FCMMessageArrival(String to, MessageArrival msj){
        this.to = to;
        this.data = msj;
    }


    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public MessageArrival getData() {
        return data;
    }

    public void setData(MessageArrival data) {
        this.data = data;
    }
}
