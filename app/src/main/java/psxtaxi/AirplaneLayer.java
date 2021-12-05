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
    private final Paint paintFront;

    public AirplaneLayer(GraphicFactory graphicFactory, DisplayModel displayModel,
                         Supplier<Double> aircraftHeadingSupplier,
                         Supplier<Double> aircraftTasSupplier) {
        super();
        this.displayModel = displayModel;
        this.aircraftHeadingSupplier = aircraftHeadingSupplier;
        this.aircraftTasSupplier = aircraftTasSupplier;

        this.paintFront = createPaintFront(graphicFactory, displayModel);
    }

    private static Paint createPaintFront(GraphicFactory graphicFactory, DisplayModel displayModel) {
        Paint paint = graphicFactory.createPaint();
        paint.setColor(Color.RED);
        paint.setTypeface(FontFamily.DEFAULT, FontStyle.BOLD);
        paint.setTextSize(12 * displayModel.getScaleFactor());
        return paint;
    }

    private static Paint createPaintBack(GraphicFactory graphicFactory, DisplayModel displayModel) {
        Paint paint = graphicFactory.createPaint();
        paint.setColor(Color.WHITE);
        paint.setTypeface(FontFamily.DEFAULT, FontStyle.BOLD);
        paint.setTextSize(12 * displayModel.getScaleFactor());
        paint.setStrokeWidth(2 * displayModel.getScaleFactor());
        paint.setStyle(Style.STROKE);
        return paint;
    }

    @Override
    public void draw(BoundingBox boundingBox, byte zoomLevel, Canvas canvas, Point topLeftPoint) {
        int centerX = canvas.getWidth() / 2;
        int centerY = canvas.getHeight() / 2;
        Double heading = this.aircraftHeadingSupplier.get();
        Double tas = this.aircraftTasSupplier.get();
        int pointX = centerX + (int) (50 * Math.cos(heading-(Math.PI/2)));
        int pointY = centerY + (int) (50 * Math.sin(heading-(Math.PI/2)));
        canvas.drawLine(centerX, centerY, pointX, pointY, this.paintFront);
        canvas.drawCircle(centerX, centerY, 5, this.paintFront);
        canvas.drawText(String.format("%.2f", tas/1000), centerX+20, centerY, this.paintFront);
    }
}
