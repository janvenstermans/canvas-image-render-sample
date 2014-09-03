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
import com.google.gwt.core.client.Callback;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.ImageElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.safehtml.shared.SafeUri;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.CaptionPanel;
import com.google.gwt.user.client.ui.DecoratorPanel;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ResizeLayoutPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import dummy.standalone.example.client.ExampleClientBundle;
import org.geomajas.geometry.Bbox;
import org.geomajas.gwt2.client.GeomajasImpl;
import org.geomajas.gwt2.client.GeomajasServerExtension;
import org.geomajas.gwt2.client.event.MapInitializationEvent;
import org.geomajas.gwt2.client.event.MapInitializationHandler;
import org.geomajas.gwt2.client.gfx.CanvasContainer;
import org.geomajas.gwt2.client.map.MapPresenter;
import org.geomajas.gwt2.example.base.client.sample.SamplePanel;
import org.geomajas.gwt2.example.base.client.widget.ShowcaseDialogBox;

import java.util.logging.Logger;

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

	private static Logger logger = Logger.getLogger(CanvasImageChangeColorPanel.class.getName());

	@UiField
	protected ResizeLayoutPanel mapPanel;

	@UiField
	protected Label label;

	@UiField
	protected CaptionPanel imageRadiometryCaptionPanel;

	@UiField
	protected CaptionPanel imageRadiometryChangeCaptionPanel;

	@UiField
	protected SimplePanel imageRadiometryChangeButtonSimplePanel;

	@UiField
	protected TextBox imageRadiometryChangePercentageTextBox;

	private ImageRadiometryPanel imageRadiometryPanel;

	private ImageRadiometryChangeButtonPanel imageRadiometryChangeButtonPanel;

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

		imageRadiometryPanel = new ImageRadiometryPanel();
		imageRadiometryPanel.setContrastTextBoxEnabled(false);
		imageRadiometryPanel.setRgbTextBoxEnabled(true);
		imageRadiometryCaptionPanel.setCaptionText("Image Radiometry");
		imageRadiometryCaptionPanel.add(imageRadiometryPanel);

		imageRadiometryChangeCaptionPanel.setCaptionText("Change Image");
		imageRadiometryChangeButtonPanel = new ImageRadiometryChangeButtonPanel(this);
		imageRadiometryChangeButtonSimplePanel.add(imageRadiometryChangeButtonPanel);

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

			// draw an image  in the center
			//SafeUri uri = ExampleClientBundle.INSTANCE.imageTuxTest().getSafeUri();
			SafeUri uri = ExampleClientBundle.INSTANCE.imageSatellite1().getSafeUri();
			drawImageFromUrl(uri.asString(), boundsCenter, container);

//			drawImageFromUrl("http://geosparcidp.com/logo-small.png", boundsCenter, container);
		}
	}

	@UiHandler("analyseBtn")
	public void onDataFromImageBtnAlphaClicked(ClickEvent event) {
		if (latestCanvasImageElement != null) {
			imageAnalysisPanel.setCanvasImageWithRadiometry(latestCanvasImageElement);
			closeableDialogBox.center();
		}
	}

	@UiHandler("resetBtn")
	public void onResetBtnClicked(ClickEvent event) {
		if (latestCanvasImageElement != null) {
			applyImageRadiometry(new ImageRadiometry());
		}
	}

	@UiHandler("imageRadiometryBtn")
	public void onImageRadiometryBtnClicked(ClickEvent event) {
		if (latestCanvasImageElement != null) {
			applyImageRadiometry(imageRadiometryPanel.getImageRadiometryFromTexts());
		}
	}

	/* private methods */

	private void applyImageRadiometry(ImageRadiometry imageRadiometry) {
		latestCanvasImageElement.setImageRadiometry(imageRadiometry);
		container.repaint();
		imageRadiometryPanel.showImageRadiometry(imageRadiometry);
		if (closeableDialogBox.isShowing()) {
			// update data
			imageAnalysisPanel.setCanvasImageWithRadiometry(latestCanvasImageElement);
		}
	}

	public void onColorChangeNoBrightness(ImageDataUtil.RadiometryValue radiometryValue, boolean high, boolean plus) {
		if (latestCanvasImageElement != null) {
			ImageRadiometry newImageRadiometry = ImageDataUtil.getImageRadiometryColorWithoutBrightnessChange(
					latestCanvasImageElement.getImageRadiometry(), radiometryValue, high, plus,
					Integer.parseInt(imageRadiometryChangePercentageTextBox.getText()) / 100.0);
			applyImageRadiometry(newImageRadiometry);
		}
	}

	public void onChangeBrightness(boolean high, boolean plus) {
		if (latestCanvasImageElement != null) {
			ImageRadiometry newImageRadiometry = ImageDataUtil.getImageRadiometryBrightnessChange(
					latestCanvasImageElement.getImageRadiometry(), high, plus,
					Integer.parseInt(imageRadiometryChangePercentageTextBox.getText()) / 100.0);
			applyImageRadiometry(newImageRadiometry);
		}
	}

	private void showImageElementAnalysis() {
		imageAnalysisPanel.setCanvasImageWithRadiometry(latestCanvasImageElement);
		closeableDialogBox.center();
	}

	private Bbox getCurrentQuarterCenterBounds() {
		Bbox bbox = mapPresenter.getViewPort().getBounds();
		double centerX = ( bbox.getX() + bbox.getMaxX()) / 2;
		double centerY = ( bbox.getY() + bbox.getMaxY()) / 2;
		double width = bbox.getWidth() / 4;
		double height = bbox.getHeight() / 4;
		return new Bbox(centerX - width / 2, centerY - height / 2, width, height);
	}

	private CanvasImageWithRadiometryElement drawImageFromImageElement(ImageElement imageElement,
										   final Bbox bounds, final CanvasContainer canvasContainer) {
		CanvasImageWithRadiometryElement canvasImageElement =
				new CanvasImageWithRadiometryElement(imageElement, bounds);
		canvasContainer.addShape(canvasImageElement);
		return canvasImageElement;
	}

	private void drawImageFromUrl(String url, final Bbox bounds, final CanvasContainer canvasContainer) {
		new ImageLoaderCrossOrigin(url,
				new Callback<ImageElement, String>() {
					@Override
					public void onFailure(String s) {

					}

					@Override
					public void onSuccess(ImageElement imageElement) {
						latestCanvasImageElement = drawImageFromImageElement(imageElement, bounds, canvasContainer);
						imageRadiometryPanel.showImageRadiometry(latestCanvasImageElement.getImageRadiometry());
						container.repaint();
					}
				});
		container.repaint();
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