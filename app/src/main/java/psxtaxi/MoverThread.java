package psxtaxi;

import org.mapsforge.core.model.LatLong;
import org.mapsforge.map.model.Model;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class MoverThread extends Thread {
    private Model model;
    private Socket socket = null;
    private BufferedReader in = null;
    private PrintWriter ou = null;
    public static final double RAD2DEG = 57.2958;
    private double currentAircraftHeading = 0.0; // in radians
    private double currentTas = 0.0;
    private double tillerInput = 0;

    public MoverThread() {
        super();
        try {
            socket = new Socket("localhost", 10747);
            socket.setTcpNoDelay(true);
            ou = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }

    public void setModel(Model model) {
        this.model = model;
    }

    public void run() {
        try {
            String received;
            while (true) {
                if ((received = in.readLine()) != null) {
                    Message message = parseMessage(received);
                    if (message.qcode.equals("Qs121")) { // heading,tas,lat,long
                        try {
                            LatLong newLatLong =
                                    new LatLong(
                                            Double.parseDouble(message.values[5]) * RAD2DEG,
                                            Double.parseDouble(message.values[6]) * RAD2DEG);

                            this.model.mapViewPosition.animateTo(newLatLong);
                            this.currentAircraftHeading = Double.parseDouble(message.values[2]);
                            this.currentTas = Double.parseDouble(message.values[4]);
                        } catch (Exception e) {
                            System.err.println("Exception in render:" + e.getMessage());
                        }
                    } else if (message.qcode.equals("Qh426")) { // tiller input
                        this.tillerInput = Double.parseDouble(message.values[0]);
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Exception in MoverThread:" + e.getMessage());
        }
    }

    private static class Message {
        String qcode;
        String[] values;

        public Message(String qcode, String[] values) {
            this.qcode = qcode;
            this.values = values;
        }
    }

    private static Message parseMessage(String psxMessage) {
        if (psxMessage == null) {
            return null;
        }
        String[] m = psxMessage.split("=");
        if (m.length == 0 || m[0] == null || m[0].isBlank()) {
            return new Message("INVALID", new String[]{});
        }
        if (m.length == 2) {
            return new Message(m[0], m[1].split(";"));
        } else {
            return new Message(m[0], new String[]{});
        }
    }

    public double getAircraftHeading() {
        return this.currentAircraftHeading;
    }

    public double getAircraftTas() {
        return this.currentTas;
    }

    public double getTillerInput() {
        return this.tillerInput;
    }
}
