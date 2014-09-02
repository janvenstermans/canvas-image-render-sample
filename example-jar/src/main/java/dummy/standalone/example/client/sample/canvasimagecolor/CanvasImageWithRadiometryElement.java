/*
 * This is part of the Spacemetric Keystone Web Portal.
 *
 * Copyright 2011 Spacemetric, http://www.spacemetric.com/, Sweden.
 */
package dummy.standalone.example.client.sample.canvasimagecolor;

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
public class CanvasImageWithRadiometryElement implements CanvasShape {

	private Bbox box;

	private Canvas originalHiddenImageCanvas;

	private Canvas colorAdjustedHiddenImageCanvas;

	private ImageRadiometry imageRadiometry;

	public CanvasImageWithRadiometryElement(ImageElement imageElement, Bbox box) {
		this.box = new Bbox(box.getX(), box.getY(), box.getWidth(), box.getHeight());
		this.imageRadiometry = new ImageRadiometry();
		originalHiddenImageCanvas = Canvas.createIfSupported();
		colorAdjustedHiddenImageCanvas = Canvas.createIfSupported();
		configureHiddenCanvas(originalHiddenImageCanvas, imageElement.getWidth(), imageElement.getHeight());
		configureHiddenCanvas(colorAdjustedHiddenImageCanvas, imageElement.getWidth(), imageElement.getHeight());
		// draw on canvasses
		originalHiddenImageCanvas.getContext2d().drawImage(imageElement, 0, 0);
		repaintColorAdjustedHiddenImageCanvasBasedOnImageRadiometry();
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
		context2d.drawImage(colorAdjustedHiddenImageCanvas.getCanvasElement(), xValue, yValue, box.getWidth(), box.getHeight());
		context2d.restore();
	}

	@Override
	public Bbox getBounds() {
		return box;
	}

	public ImageRadiometry getImageRadiometry() {
		return imageRadiometry;
	}

	public void setImageRadiometry(ImageRadiometry imageRadiometry) {
		this.imageRadiometry = imageRadiometry;
		repaintColorAdjustedHiddenImageCanvasBasedOnImageRadiometry();
	}

	public ImageData getOriginalImageData() {
		return getImageData(originalHiddenImageCanvas);
	}

	public ImageData getImageData() {
		return getImageData(colorAdjustedHiddenImageCanvas);
	}

	public void putImageData(ImageData imageData) {
		putImageData(imageData, colorAdjustedHiddenImageCanvas);
	}

	/* private methods */

	private void configureHiddenCanvas(Canvas canvas, int width, int height) {
		canvas.setVisible(false);
		canvas.setCoordinateSpaceWidth(width);
		canvas.setCoordinateSpaceHeight(height);
	}

	private void repaintColorAdjustedHiddenImageCanvasBasedOnImageRadiometry() {
		ImageData originalImageData = getImageData(originalHiddenImageCanvas);
		putImageData(ImageDataUtil.applyImageRadiometry(originalImageData, imageRadiometry),
				colorAdjustedHiddenImageCanvas);
	}

	private ImageData getImageData(Canvas canvas) {
		return canvas.getContext2d().getImageData(0, 0,
				canvas.getCoordinateSpaceWidth(), canvas.getCoordinateSpaceHeight());
	}

	public void putImageData(ImageData imageData, Canvas canvas) {
		canvas.getContext2d().putImageData(imageData, 0, 0);
	}
}
