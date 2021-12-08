package psxtaxi.ui;

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
import psxtaxi.SimState;

public class AirplaneLayer extends Layer {
    private final Paint paintRed;
    private final Paint paintBlack;
    private final SimState state;
    private int cutoffIas;
    private boolean showIas;

    public AirplaneLayer(GraphicFactory graphicFactory, DisplayModel displayModel, SimState state,
                         int cutoffIas, boolean showIas) {
        super();
        this.state = state;
        this.cutoffIas = cutoffIas;
        this.showIas = showIas;
        this.paintRed = createPaintFront(graphicFactory, displayModel, Color.RED);
        this.paintBlack = createPaintFront(graphicFactory, displayModel, Color.BLACK);
    }

    private Paint createPaintFront(GraphicFactory graphicFactory, DisplayModel displayModel, Color color) {
        Paint paint = graphicFactory.createPaint();
        paint.setColor(color);
        paint.setTypeface(FontFamily.DEFAULT, FontStyle.BOLD);
        paint.setTextSize(12 * displayModel.getScaleFactor());
        return paint;
    }

    @Override
    public void draw(BoundingBox boundingBox, byte zoomLevel, Canvas canvas, Point topLeftPoint) {
        if (this.state.tas > this.cutoffIas * 1000) { // no redrawing above cutoff IAS
            return;
        }

        LatLong aircraftPosition = this.state.position;
        if (aircraftPosition == null) {
            return;
        }

        int centerX = (int) ((aircraftPosition.longitude - boundingBox.minLongitude) / boundingBox.getLongitudeSpan() * canvas.getWidth());
        int centerY = (int) ((boundingBox.maxLatitude - aircraftPosition.latitude) / boundingBox.getLatitudeSpan() * canvas.getHeight());

        int pointerSize = canvas.getHeight() / 32;
        int pointerTipX = centerX + (int) (pointerSize * Math.cos(this.state.heading - Math.PI / 2));
        int pointerTipY = centerY + (int) (pointerSize * Math.sin(this.state.heading - Math.PI / 2));

        int tillerSize = pointerSize / 2;
        int tillerTipX = pointerTipX +
                (int) (tillerSize * Math.cos(this.state.heading + (this.state.tillerInput / 999.0) * Math.PI / 2 - Math.PI / 2));
        int tillerTipY = pointerTipY +
                (int) (tillerSize * Math.sin(this.state.heading + (this.state.tillerInput / 999.0) * Math.PI / 2 - Math.PI / 2));
        canvas.drawLine(centerX, centerY, pointerTipX, pointerTipY, this.paintRed);
        canvas.drawLine(pointerTipX, pointerTipY, tillerTipX, tillerTipY, this.paintBlack);
        canvas.drawCircle(centerX, centerY, 5, this.paintRed);
        if (this.showIas)
            canvas.drawText(String.format("%.2f", this.state.tas / 1000), centerX + 20, centerY, this.paintRed);
    }
}
