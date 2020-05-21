package zero.network.petgarden.model.notifications;

public class MessageArrival {

    public static final String TYPE ="arrival";
    private String titulo;
    private String body;
    private String petName;
    private String type;

    public MessageArrival(){
        type =TYPE;
    }


    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getPetName() {
        return petName;
    }

    public void setPetName(String petName) {
        this.petName = petName;
    }

    public static String getTYPE() {
        return TYPE;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
