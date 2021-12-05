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
    private LatLong currentLatLong;
    private double currentAircraftHeading = 0.0; // in radians
    private double currentTas = 0.0;

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
                    String[] keyValue = received.split("=");
                    if (keyValue[0].startsWith("Qs121")) {
                        String[] values = keyValue[1].split(";");
                        try {
                            this.currentLatLong = new LatLong(Double.parseDouble(values[5]) * RAD2DEG, Double.parseDouble(values[6]) * RAD2DEG);
                            this.model.mapViewPosition.animateTo(this.currentLatLong);
                            //this.model.mapViewPosition.setZoomLevel((byte) 18, false);
                            App.currentLatLong = this.currentLatLong;
                            this.currentAircraftHeading = Double.parseDouble(values[2]);
                            this.currentTas = Double.parseDouble(values[4]);
                        } catch (Exception e) {
                            System.err.println("Exception in render:" + e.getMessage());
                        }
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Exception in MoverThread:" + e.getMessage());
        }
    }

    public double getAircraftHeading() {
        return this.currentAircraftHeading;
    }

    public double getAircraftTas() {
        return this.currentTas;
    }
}
