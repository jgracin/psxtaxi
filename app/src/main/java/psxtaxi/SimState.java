package psxtaxi;

import org.mapsforge.core.model.LatLong;

public class SimState {
    public volatile LatLong position;
    public volatile double heading = 0.0; // in radians
    public volatile double tas = 0.0;
    public volatile double tillerInput = 0;
}
