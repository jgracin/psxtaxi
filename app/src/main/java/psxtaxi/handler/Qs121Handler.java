package psxtaxi.handler;

import org.mapsforge.core.model.LatLong;
import org.mapsforge.map.awt.view.MapView;
import psxtaxi.Message;
import psxtaxi.SimState;

import static java.lang.Double.parseDouble;

public class Qs121Handler {
    public static final double RAD2DEG = 57.2958;
    private final MapView mapView;
    private final SimState state;

    public Qs121Handler(MapView mapView, SimState state) {
        this.mapView = mapView;
        this.state = state;
    }

    public void handle(Message message) {
        double latitude = parseDouble(message.values[5]);
        double longitude = parseDouble(message.values[6]);
        double tas = parseDouble(message.values[4]);
        double heading = parseDouble(message.values[2]);

        this.state.position = new LatLong(latitude * RAD2DEG, longitude * RAD2DEG);
        this.state.heading = heading;
        this.state.tas = tas;

        if (!this.mapView.getBoundingBox().contains(this.state.position) && tas < 60 * 1000) {
            this.mapView.getModel().mapViewPosition.animateTo(this.state.position);
        }
        this.mapView.getLayerManager().redrawLayers();
    }
}
