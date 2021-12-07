package psxtaxi;

import org.mapsforge.core.model.LatLong;
import org.mapsforge.map.awt.view.MapView;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import static java.lang.Double.parseDouble;

public class MoverThread extends Thread {
    private MapView mapView;
    private Socket socket = null;
    private BufferedReader in = null;
    private PrintWriter ou = null;
    public static final double RAD2DEG = 57.2958;
    private double currentAircraftHeading = 0.0; // in radians
    private double currentTas = 0.0;
    private double tillerInput = 0;
    private LatLong aircraftPosition;

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

    public void setModel(MapView mapView) {
        this.mapView = mapView;
    }

    public void run() {
        String received;
        Exception lastException = null;
        while (true) {
            try {
                if ((received = in.readLine()) != null) {
                    Message message = parseMessage(received);
                    if (message.qcode.equals("Qs121")) { // heading,tas,lat,long
                        this.aircraftPosition = new LatLong(
                                parseDouble(message.values[5]) * RAD2DEG,
                                parseDouble(message.values[6]) * RAD2DEG);
                        this.currentAircraftHeading = parseDouble(message.values[2]);
                        this.currentTas = parseDouble(message.values[4]);
                        if (!mapView.getBoundingBox().contains(this.aircraftPosition) && this.currentTas < 60*1000) {
                            mapView.getModel().mapViewPosition.animateTo(this.aircraftPosition);
                        }
                        mapView.getLayerManager().redrawLayers();
                    } else if (message.qcode.equals("Qh426")) { // tiller input
                        this.tillerInput = parseDouble(message.values[0]);
                    }
                }
            } catch (Exception e) {
                if (lastException == null) {
                    lastException = e;
                    e.printStackTrace(System.err);
                } else if (!lastException.getMessage().equals(e.getMessage())) {
                    e.printStackTrace(System.err);
                }
            }
        }
    }

    public void setMapView(MapView mapView) {
        this.mapView = mapView;
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

    public LatLong getAircraftPosition() {
        return this.aircraftPosition;
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
        String[] m = psxMessage.split("=");
        if (m.length == 2) {
            return new Message(m[0], m[1].split(";"));
        } else {
            return new Message(m[0], new String[]{});
        }
    }

}
