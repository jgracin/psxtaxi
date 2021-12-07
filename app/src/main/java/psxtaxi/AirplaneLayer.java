package psxtaxi;

import org.mapsforge.core.graphics.Canvas;
import org.mapsforge.core.graphics.Color;
import org.mapsforge.core.graphics.FontFamily;
import org.mapsforge.core.graphics.FontStyle;
import org.mapsforge.core.graphics.GraphicFactory;
import org.mapsforge.core.graphics.Paint;
import org.mapsforge.core.model.BoundingBox;
import org.mapsforge.core.model.LatLong;
import org.mapsforge.core.model.Point;
import org.mapsforge.map.layer.Layer;
import org.mapsforge.map.model.DisplayModel;

import java.util.function.Supplier;

public class AirplaneLayer extends Layer {
    private DisplayModel displayModel;
    private final Supplier<Double> aircraftHeadingSupplier;
    private final Supplier<Double> aircraftTasSupplier;
    private final Supplier<Double> tillerInputSupplier;
    private Supplier<LatLong> aircraftPositionSupplier;
    private final Paint paintRed;
    private final Paint paintBlack;

    public AirplaneLayer(GraphicFactory graphicFactory, DisplayModel displayModel,
                         Supplier<Double> aircraftHeadingSupplier,
                         Supplier<Double> aircraftTasSupplier,
                         Supplier<Double> tillerInputSupplier,
                         Supplier<LatLong> aircraftPositionSupplier) {
        super();
        this.displayModel = displayModel;
        this.aircraftHeadingSupplier = aircraftHeadingSupplier;
        this.aircraftTasSupplier = aircraftTasSupplier;
        this.tillerInputSupplier = tillerInputSupplier;
        this.aircraftPositionSupplier = aircraftPositionSupplier;

        this.paintRed = createPaintFront(graphicFactory, displayModel, Color.RED);
        this.paintBlack = createPaintFront(graphicFactory, displayModel, Color.BLACK);
    }

    private static Paint createPaintFront(GraphicFactory graphicFactory, DisplayModel displayModel, Color color) {
        Paint paint = graphicFactory.createPaint();
        paint.setColor(color);
        paint.setTypeface(FontFamily.DEFAULT, FontStyle.BOLD);
        paint.setTextSize(12 * displayModel.getScaleFactor());
        return paint;
    }

    @Override
    public void draw(BoundingBox boundingBox, byte zoomLevel, Canvas canvas, Point topLeftPoint) {
        Double tas = this.aircraftTasSupplier.get();
        if (tas > 60 * 1000) { // no redrawing above 60kts IAS
            return;
        }

        LatLong aircraftPosition = this.aircraftPositionSupplier.get();
        if (aircraftPosition == null) {
            return;
        }

        Double heading = this.aircraftHeadingSupplier.get();
        if (heading == null) {
            return;
        }

        double tillerInput = this.tillerInputSupplier.get() != null ? this.tillerInputSupplier.get() : 0.0;

        int centerX = (int) ((aircraftPosition.longitude - boundingBox.minLongitude) / boundingBox.getLongitudeSpan() * canvas.getWidth());
        int centerY = (int) ((boundingBox.maxLatitude - aircraftPosition.latitude) / boundingBox.getLatitudeSpan() * canvas.getHeight());

        int pointerSize = canvas.getHeight() / 32;
        int pointerTipX = centerX + (int) (pointerSize * Math.cos(heading - Math.PI / 2));
        int pointerTipY = centerY + (int) (pointerSize * Math.sin(heading - Math.PI / 2));

        int tillerSize = pointerSize / 2;
        int tillerTipX = pointerTipX + (int) (tillerSize * Math.cos(heading + (tillerInput / 999.0) * Math.PI / 2 - Math.PI / 2));
        int tillerTipY = pointerTipY + (int) (tillerSize * Math.sin(heading + (tillerInput / 999.0) * Math.PI / 2 - Math.PI / 2));
        canvas.drawLine(centerX, centerY, pointerTipX, pointerTipY, this.paintRed);
        canvas.drawLine(pointerTipX, pointerTipY, tillerTipX, tillerTipY, this.paintBlack);
        canvas.drawCircle(centerX, centerY, 5, this.paintRed);
        canvas.drawText(String.format("%.2f", tas / 1000), centerX + 20, centerY, this.paintRed);
    }
}
