package psxtaxi.handler;

import psxtaxi.Message;
import psxtaxi.SimState;

import static java.lang.Double.parseDouble;

public class Qh426Handler {
    private SimState state;

    public Qh426Handler(SimState state) {
        this.state = state;
    }

    public void handle(Message message) {
        state.tillerInput = parseDouble(message.values[0]);
    }
}
