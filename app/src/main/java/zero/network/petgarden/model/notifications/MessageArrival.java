package zero.network.petgarden.model.notifications;

public class MessageArrival {

    public static final String TYPE ="arrival";
    private String titulo;
    private double cost;
    private String petName;
    private String sitterID;
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

    public double getCost() {
        return cost;
    }

    public void setCost(double cost) {
        this.cost = cost;
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

    public String getSitterID() {
        return sitterID;
    }

    public void setSitterID(String sitterID) {
        this.sitterID = sitterID;
    }
}
