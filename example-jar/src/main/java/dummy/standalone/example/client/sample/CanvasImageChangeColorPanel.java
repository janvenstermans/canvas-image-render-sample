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

package dummy.standalone.example.client.sample;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.ImageElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.safehtml.shared.SafeUri;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.DecoratorPanel;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ResizeLayoutPanel;
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
import org.geomajas.gwt2.example.client.ExampleJar;
import org.moxieapps.gwt.highcharts.client.Chart;
import org.moxieapps.gwt.highcharts.client.ChartSubtitle;
import org.moxieapps.gwt.highcharts.client.ChartTitle;
import org.moxieapps.gwt.highcharts.client.Legend;
import org.moxieapps.gwt.highcharts.client.Series;
import org.moxieapps.gwt.highcharts.client.ToolTip;
import org.moxieapps.gwt.highcharts.client.ToolTipData;
import org.moxieapps.gwt.highcharts.client.ToolTipFormatter;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

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
	protected HTMLPanel histogramPanel;

	private CanvasChangeableImageElement latestCanvasImageElement;

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

			drawRedRectangle(boundsCenter, container);

			// draw a tux  in the center
			SafeUri uri = ExampleClientBundle.INSTANCE.imageTuxTest().getSafeUri();
			ImageElement imgElement = ImageElement.as((new Image(uri)).getElement());
			latestCanvasImageElement = drawImageFromImageElement(imgElement, boundsCenter, container);

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

	@UiHandler("dataFromImageBtn")
	public void onDataFromImageBtnClicked(ClickEvent event) {
		if (latestCanvasImageElement != null) {
			try {
				/*Map<ImageDataUtil.HistogramValue, HistogramData> map = ImageDataUtil.getHistogramData(
						latestCanvasImageElement.getImageData());*/
				// testing:
				HistogramData histogramDataInput = new HistogramData();
				Random random = new Random();
				for (int i = 0 ; i < 1000; i++) {
					histogramDataInput.add(random.nextInt(256));
				}
				Map<ImageDataUtil.HistogramValue, HistogramData> map =
						new HashMap<ImageDataUtil.HistogramValue, HistogramData>();
				map.put(ImageDataUtil.HistogramValue.ALPHA, histogramDataInput);
				StringBuilder stringBuilder = new StringBuilder();
				for (Map.Entry<ImageDataUtil.HistogramValue, HistogramData> entry : map.entrySet()) {
					HistogramData histogramData = entry.getValue();
					stringBuilder.append(entry.getKey().toString() + " min:" + histogramData.getMin() +
							" max:" + histogramData.getMax() + " count:" + histogramData.getCount() + "\n");
				}
				label.setText(stringBuilder.toString());
				label.setVisible(true);
				histogramPanel.add(createChart(ImageDataUtil.HistogramValue.ALPHA, histogramDataInput.getHitArray()));
			} catch (Exception ex)  {
				// do nothing
			}
		}
	}

	private Chart createChart(ImageDataUtil.HistogramValue histogramValue, int[] values) {
		final Chart chart = new Chart()
				.setType(Series.Type.LINE)
				.setMarginRight(130)
				.setMarginBottom(25)
				.setChartTitle(new ChartTitle()
						.setText("Monthly Average Temperature")
						.setX(-20)  // center
				)
				.setChartSubtitle(new ChartSubtitle()
								.setText("Source: WorldClimate.com")
								.setX(-20)
				)
				.setLegend(new Legend()
								.setLayout(Legend.Layout.VERTICAL)
								.setAlign(Legend.Align.RIGHT)
								.setVerticalAlign(Legend.VerticalAlign.TOP)
								.setX(-10)
								.setY(100)
								.setBorderWidth(0)
				)
				.setToolTip(new ToolTip()
								.setFormatter(new ToolTipFormatter() {
									public String format(ToolTipData toolTipData) {
										return "<b>" + toolTipData.getSeriesName() + "</b><br/>" +
												toolTipData.getXAsString() + ": " + toolTipData.getYAsDouble() + "Â°C";
									}
								})
				);

		chart.getXAxis()
				.setCategories(
						"Jan", "Feb", "Mar", "Apr", "May", "Jun",
						"Jul", "Aug", "Sep", "Oct", "Nov", "Dec"
				);

		chart.getYAxis()
				.setAxisTitleText("Temperature Â°C");

		chart.addSeries(chart.createSeries()
						.setName("Tokyo")
						.setPoints(new Number[]{
								7.0, 6.9, 9.5, 14.5, 18.2, 21.5, 25.2, 26.5, 23.3, 18.3, 13.9, 9.6
						})
		);
		chart.addSeries(chart.createSeries()
						.setName("New York")
						.setPoints(new Number[]{
								-0.2, 0.8, 5.7, 11.3, 17.0, 22.0, 24.8, 24.1, 20.1, 14.1, 8.6, 2.5
						})
		);
		chart.addSeries(chart.createSeries()
						.setName("Berlin")
						.setPoints(new Number[]{
								-0.9, 0.6, 3.5, 8.4, 13.5, 17.0, 18.6, 17.9, 14.3, 9.0, 3.9, 1.0
						})
		);
		chart.addSeries(chart.createSeries()
						.setName("London")
						.setPoints(new Number[]{
								3.9, 4.2, 5.7, 8.5, 11.9, 15.2, 17.0, 16.6, 14.2, 10.3, 6.6, 4.8
						})
		);

		chart.setHeight100();
		chart.setWidth100();
		chart.setStyleName("chartStyle");
		return chart;
	}

	private Bbox getCurrentQuarterCenterBounds() {
		Bbox bbox = mapPresenter.getViewPort().getBounds();
		double centerX = ( bbox.getX() + bbox.getMaxX()) / 2;
		double centerY = ( bbox.getY() + bbox.getMaxY()) / 2;
		double width = bbox.getWidth() / 4;
		double height = bbox.getHeight() / 4;
		return new Bbox(centerX - width /2, centerY - height / 2 ,width ,height);
	}

	private void drawRedRectangle(Bbox bounds, CanvasContainer canvasContainer) {
		CanvasRect canvasRect = new CanvasRect(bounds);
		canvasRect.setFillStyle("rgba(20,20,150,50)");
		canvasRect.setStrokeStyle("rgba(0,0,0,255)");
		canvasRect.setStrokeWidthPixels(1);
		canvasContainer.addShape(canvasRect);
	}

	private CanvasChangeableImageElement drawImageFromImageElement(ImageElement imageElement,
										   final Bbox bounds, final CanvasContainer canvasContainer) {
		CanvasChangeableImageElement canvasImageElement =
				new CanvasChangeableImageElement(imageElement, bounds);
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