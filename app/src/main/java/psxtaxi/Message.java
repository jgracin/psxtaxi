package psxtaxi;

public class Message {
    public String qcode;
    public String[] values;

    public Message(String qcode, String[] values) {
        this.qcode = qcode;
        this.values = values;
    }
}
