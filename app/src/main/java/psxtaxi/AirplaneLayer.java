package psxtaxi;

import org.mapsforge.core.graphics.Canvas;
import org.mapsforge.core.graphics.Color;
import org.mapsforge.core.graphics.FontFamily;
import org.mapsforge.core.graphics.FontStyle;
import org.mapsforge.core.graphics.GraphicFactory;
import org.mapsforge.core.graphics.Paint;
import org.mapsforge.core.graphics.Style;
import org.mapsforge.core.model.BoundingBox;
import org.mapsforge.core.model.Point;
import org.mapsforge.map.layer.Layer;
import org.mapsforge.map.model.DisplayModel;

import java.util.function.Supplier;

public class AirplaneLayer extends Layer {
    private DisplayModel displayModel;
    private final Supplier<Double> aircraftHeadingSupplier;
    private final Supplier<Double> aircraftTasSupplier;
    private final Supplier<Double> tillerInputSupplier;
    private final Paint paintFront;

    public AirplaneLayer(GraphicFactory graphicFactory, DisplayModel displayModel,
                         Supplier<Double> aircraftHeadingSupplier,
                         Supplier<Double> aircraftTasSupplier,
                         Supplier<Double> tillerInputSupplier) {
        super();
        this.displayModel = displayModel;
        this.aircraftHeadingSupplier = aircraftHeadingSupplier;
        this.aircraftTasSupplier = aircraftTasSupplier;
        this.tillerInputSupplier = tillerInputSupplier;

        this.paintFront = createPaintFront(graphicFactory, displayModel);
    }

    private static Paint createPaintFront(GraphicFactory graphicFactory, DisplayModel displayModel) {
        Paint paint = graphicFactory.createPaint();
        paint.setColor(Color.RED);
        paint.setTypeface(FontFamily.DEFAULT, FontStyle.BOLD);
        paint.setTextSize(12 * displayModel.getScaleFactor());
        return paint;
    }

    @Override
    public void draw(BoundingBox boundingBox, byte zoomLevel, Canvas canvas, Point topLeftPoint) {
        int centerX = canvas.getWidth() / 2;
        int centerY = canvas.getHeight() / 2;
        Double heading = this.aircraftHeadingSupplier.get();
        Double tas = this.aircraftTasSupplier.get();
        Double tillerInput = this.tillerInputSupplier.get();

        int pointerSize = canvas.getHeight() / 32;
        int pointerTipX = centerX + (int) (pointerSize * Math.cos(heading - Math.PI/2));
        int pointerTipY = centerY + (int) (pointerSize * Math.sin(heading - Math.PI/2));

        int tillerSize = pointerSize/2;
        int tillerTipX = pointerTipX + (int) (tillerSize * Math.cos(heading + (tillerInput/999.0)*Math.PI/2 - Math.PI/2));
        int tillerTipY = pointerTipY + (int) (tillerSize * Math.sin(heading + (tillerInput/999.0)*Math.PI/2 - Math.PI/2));
        canvas.drawLine(centerX, centerY, pointerTipX, pointerTipY, this.paintFront);
        canvas.drawLine(pointerTipX, pointerTipY, tillerTipX, tillerTipY, this.paintFront);
        canvas.drawCircle(centerX, centerY, 5, this.paintFront);
        canvas.drawText(String.format("%.2f", tas/1000), centerX+20, centerY, this.paintFront);
    }
}
