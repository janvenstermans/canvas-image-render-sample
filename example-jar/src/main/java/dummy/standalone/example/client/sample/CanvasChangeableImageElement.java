/*
 * This is part of the Spacemetric Keystone Web Portal.
 *
 * Copyright 2011 Spacemetric, http://www.spacemetric.com/, Sweden.
 */
package dummy.standalone.example.client.sample;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.canvas.dom.client.ImageData;
import com.google.gwt.dom.client.ImageElement;
import org.geomajas.geometry.Bbox;
import org.geomajas.geometry.Matrix;
import org.geomajas.gwt2.client.gfx.CanvasShape;

/**
 * Canvas element based on {@link com.google.gwt.dom.client.ImageElement}.
 *
 * @author Jan Venstermans
 *
 */
public class CanvasChangeableImageElement implements CanvasShape {

	private Bbox box;

	private Canvas hiddenImageCanvas;

	public CanvasChangeableImageElement(ImageElement imageElement, Bbox box) {
		this.box = new Bbox(box.getX(), box.getY(), box.getWidth(), box.getHeight());
		hiddenImageCanvas = Canvas.createIfSupported();
		hiddenImageCanvas.setVisible(false);
		hiddenImageCanvas.setCoordinateSpaceWidth(imageElement.getWidth());
		hiddenImageCanvas.setCoordinateSpaceHeight(imageElement.getHeight());
		hiddenImageCanvas.getContext2d().drawImage(imageElement, 0, 0);
	}

	@Override
	public void paint(Canvas canvas, Matrix matrix) {
		Context2d context2d = canvas.getContext2d();
		context2d.save();
		boolean xReversal = matrix.getXx() < 0;
		boolean yReversal = matrix.getYy() < 0;
		context2d.scale(xReversal ? -1 : 1, yReversal ? -1 : 1);
		double xValue = xReversal ? box.getMaxX() * -1 : box.getX();
		double yValue = yReversal ? box.getMaxY() * -1 : box.getY();
		context2d.drawImage(hiddenImageCanvas.getCanvasElement(), xValue, yValue, box.getWidth(), box.getHeight());
		context2d.restore();
	}

	public ImageData getImageData() {
		return hiddenImageCanvas.getContext2d().getImageData(0, 0,
				hiddenImageCanvas.getCoordinateSpaceWidth(), hiddenImageCanvas.getCoordinateSpaceHeight());
	}

	@Override
	public Bbox getBounds() {
		return box;
	}

	public void putImageData(ImageData imageData) {
		hiddenImageCanvas.getContext2d().putImageData(imageData, 0, 0);
	}
}
