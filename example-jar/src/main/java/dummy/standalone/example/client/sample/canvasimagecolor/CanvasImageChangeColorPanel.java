/*
 * This is part of Geomajas, a GIS framework, http://www.geomajas.org/.
 *
 * Copyright 2008-2014 Geosparc nv, http://www.geosparc.com/, Belgium.
 *
 * The program is available in open source according to the GNU Affero
 * General Public License. All contributions in this program are covered
 * by the Geomajas Contributors License Agreement. For full licensing
 * details, see LICENSE.txt in the project root.
 */

package dummy.standalone.example.client.sample.canvasimagecolor;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.ImageElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.safehtml.shared.SafeUri;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DecoratorPanel;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ResizeLayoutPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import dummy.standalone.example.client.ExampleClientBundle;
import org.geomajas.geometry.Bbox;
import org.geomajas.gwt2.client.GeomajasImpl;
import org.geomajas.gwt2.client.GeomajasServerExtension;
import org.geomajas.gwt2.client.event.MapInitializationEvent;
import org.geomajas.gwt2.client.event.MapInitializationHandler;
import org.geomajas.gwt2.client.gfx.CanvasContainer;
import org.geomajas.gwt2.client.gfx.CanvasRect;
import org.geomajas.gwt2.client.map.MapPresenter;
import org.geomajas.gwt2.example.base.client.sample.SamplePanel;
import org.geomajas.gwt2.example.base.client.widget.ShowcaseDialogBox;

import java.util.HashMap;
import java.util.Map;

/**
 * ContentPanel that demonstrates canvas rendering abilities.
 * 
 * @author Jan Venstermans
 */
public class CanvasImageChangeColorPanel implements SamplePanel {

	/**
	 * UI binder for this widget.
	 * 
	 * @author Jan De Moerloose
	 */
	interface MyUiBinder extends UiBinder<Widget, CanvasImageChangeColorPanel> {
	}

	private static final MyUiBinder UI_BINDER = GWT.create(MyUiBinder.class);

	private MapPresenter mapPresenter;

	private CanvasContainer container;

	@UiField
	protected ResizeLayoutPanel mapPanel;

	@UiField
	protected Label label;

	@UiField
	protected TextBox redMinText;

	@UiField
	protected TextBox redMaxText;

	@UiField
	protected TextBox greenMinText;

	@UiField
	protected TextBox greenMaxText;

	@UiField
	protected TextBox blueMinText;

	@UiField
	protected TextBox blueMaxText;

	private Map<ImageDataUtil.RadiometryValue, TextBox> minTextBoxes;

	private Map<ImageDataUtil.RadiometryValue, TextBox> maxTextBoxes;

	private CanvasImageWithRadiometryElement latestCanvasImageElement;

	private DialogBox closeableDialogBox;

	private ImageAnalysisPanel imageAnalysisPanel = new ImageAnalysisPanel();

	public Widget asWidget() {
		Widget layout = UI_BINDER.createAndBindUi(this);

		// Create the MapPresenter and add an InitializationHandler:
		mapPresenter = GeomajasImpl.getInstance().createMapPresenter();
		mapPresenter.setSize(480, 480);
		mapPresenter.getEventBus().addMapInitializationHandler(new MyMapInitializationHandler());

		// Define the whole layout:
		DecoratorPanel mapDecorator = new DecoratorPanel();
		mapDecorator.add(mapPresenter.asWidget());
		mapPanel.add(mapDecorator);

		// Initialize the map, and return the layout:
		GeomajasServerExtension.getInstance().initializeMap(mapPresenter, "gwt-app", "mapOsm");
		label.setVisible(false);

		minTextBoxes = new HashMap<ImageDataUtil.RadiometryValue, TextBox>();
		minTextBoxes.put(ImageDataUtil.RadiometryValue.RED, redMinText);
		minTextBoxes.put(ImageDataUtil.RadiometryValue.GREEN, greenMinText);
		minTextBoxes.put(ImageDataUtil.RadiometryValue.BLUE, blueMinText);
		maxTextBoxes = new HashMap<ImageDataUtil.RadiometryValue, TextBox>();
		maxTextBoxes.put(ImageDataUtil.RadiometryValue.RED, redMaxText);
		maxTextBoxes.put(ImageDataUtil.RadiometryValue.GREEN, greenMaxText);
		maxTextBoxes.put(ImageDataUtil.RadiometryValue.BLUE, blueMaxText);

		closeableDialogBox = new ShowcaseDialogBox();
		closeableDialogBox.setGlassEnabled(false);
		closeableDialogBox.setModal(false);
		closeableDialogBox.add(imageAnalysisPanel);

		return layout;
	}

	@UiHandler("clearCanvas")
	public void onClearCanvasBtnClicked(ClickEvent event) {
		container.clear();
		latestCanvasImageElement = null;
	}

	@UiHandler("drawImageButton")
	public void onDrawInternalImageButtonBtnClicked(ClickEvent event) {
		if (container != null) {
			Bbox boundsCenter = getCurrentQuarterCenterBounds();

			// draw a tux  in the center
			SafeUri uri = ExampleClientBundle.INSTANCE.imageTuxTest().getSafeUri();
			ImageElement imgElement = ImageElement.as((new Image(uri)).getElement());
			latestCanvasImageElement = drawImageFromImageElement(imgElement, boundsCenter, container);
			applyImageRadiometry(latestCanvasImageElement.getImageRadiometry());
			container.repaint();
		}
	}

	@UiHandler("invertColorsBtn")
	public void onInvertColorsBtnClicked(ClickEvent event) {
		if (latestCanvasImageElement != null) {
			try {
				latestCanvasImageElement.putImageData(ImageDataUtil.invertColors(
						latestCanvasImageElement.getImageData()));
				container.repaint();
			} catch (Exception ex)  {
				// do nothing
			}
		}
	}

	@UiHandler("analyseBtn")
	public void onDataFromImageBtnAlphaClicked(ClickEvent event) {
		if (latestCanvasImageElement != null) {
			imageAnalysisPanel.setCanvasImageWithRadiometry(latestCanvasImageElement);
			closeableDialogBox.center();
		}
	}

	@UiHandler("imageRadiometryBtn")
	public void onImageRadiometryBtnClicked(ClickEvent event) {
		if (latestCanvasImageElement != null) {
			latestCanvasImageElement.setImageRadiometry(getImageRadiometryFromTexts());
			container.repaint();
			if (closeableDialogBox.isShowing()) {
				// update data
				imageAnalysisPanel.updateImageRadiometry(latestCanvasImageElement.getImageRadiometry());
			}
		}
	}

	/* general color changes, without brightness change */
	@UiHandler("redLowMinBtn")
	public void onRedLowMinBtnClicked(ClickEvent event) {
		onColorChangeNoBrightness(ImageDataUtil.RadiometryValue.RED, false, false);
	}
	@UiHandler("redLowPlusBtn")
	public void onRedLowPlousBtnClicked(ClickEvent event) {
		onColorChangeNoBrightness(ImageDataUtil.RadiometryValue.RED, false, true);
	}
	@UiHandler("redHighMinBtn")
	public void onRedHighMinBtnClicked(ClickEvent event) {
		onColorChangeNoBrightness(ImageDataUtil.RadiometryValue.RED, true, false);
	}
	@UiHandler("redHighPlusBtn")
	public void onRedHighPlousBtnClicked(ClickEvent event) {
		onColorChangeNoBrightness(ImageDataUtil.RadiometryValue.RED, true, true);
	}

	/* private methods */

	private void onColorChangeNoBrightness(ImageDataUtil.RadiometryValue radiometryValue, boolean high, boolean plus) {
		if (latestCanvasImageElement != null) {
			imageAnalysisPanel.updateImageRadiometry(ImageDataUtil.getImageRadiometryColorWithoutBrightnessChange(
					latestCanvasImageElement.getImageRadiometry(), radiometryValue, high, plus));
		}
	}

	private void showImageElementAnalysis() {
		imageAnalysisPanel.setCanvasImageWithRadiometry(latestCanvasImageElement);
		closeableDialogBox.center();
	}

	private void applyImageRadiometry(ImageRadiometry imageRadiometry) {
		for (ImageDataUtil.RadiometryValue radiometryValue : ImageDataUtil.getRGB()) {
			minTextBoxes.get(radiometryValue).setText("" + imageRadiometry.getMin(radiometryValue));
			maxTextBoxes.get(radiometryValue).setText("" + imageRadiometry.getMax(radiometryValue));
		}
	}

	private Bbox getCurrentQuarterCenterBounds() {
		Bbox bbox = mapPresenter.getViewPort().getBounds();
		double centerX = ( bbox.getX() + bbox.getMaxX()) / 2;
		double centerY = ( bbox.getY() + bbox.getMaxY()) / 2;
		double width = bbox.getWidth() / 4;
		double height = bbox.getHeight() / 4;
		return new Bbox(centerX - width /2, centerY - height / 2 ,width ,height);
	}

	private ImageRadiometry getImageRadiometryFromTexts() {
		ImageRadiometry imageRadiometry = new ImageRadiometry();
		ImageDataUtil.RadiometryValue[] radiometryValues = new ImageDataUtil.RadiometryValue[] {
				ImageDataUtil.RadiometryValue.RED, ImageDataUtil.RadiometryValue.GREEN,
				ImageDataUtil.RadiometryValue.BLUE
		};
		for (ImageDataUtil.RadiometryValue radiometryValue : radiometryValues) {
		   imageRadiometry.setMin(radiometryValue, Integer.parseInt(minTextBoxes.get(radiometryValue).getText()));
		   imageRadiometry.setMax(radiometryValue, Integer.parseInt(maxTextBoxes.get(radiometryValue).getText()));
		}
		return imageRadiometry;
	}

	private CanvasImageWithRadiometryElement drawImageFromImageElement(ImageElement imageElement,
										   final Bbox bounds, final CanvasContainer canvasContainer) {
		CanvasImageWithRadiometryElement canvasImageElement =
				new CanvasImageWithRadiometryElement(imageElement, bounds);
		canvasContainer.addShape(canvasImageElement);
		return canvasImageElement;
	}

	/**
	 * Map initialization handler that adds a CheckBox to the layout for every layer. With these CheckBoxes, the user
	 * can toggle the layer's visibility.
	 * 
	 * @author Pieter De Graef
	 */
	private class MyMapInitializationHandler implements MapInitializationHandler {

		public void onMapInitialized(MapInitializationEvent event) {
			if (Canvas.isSupported()) {
				container = mapPresenter.getContainerManager().addWorldCanvasContainer();
			} else {
				//label.setText(ExampleJar.getMessages().renderingMissingCanvas());
				label.setText("renderingMissingCanvas");
				label.setVisible(true);
			}
		}
	}
}