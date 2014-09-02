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

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import org.moxieapps.gwt.highcharts.client.Chart;
import org.moxieapps.gwt.highcharts.client.ChartTitle;
import org.moxieapps.gwt.highcharts.client.Series;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * ContentPanel that demonstrates canvas rendering abilities.
 * 
 * @author Jan Venstermans
 */
public class ImageAnalysisPanel implements IsWidget {

	/**
	 * UI binder for this widget.
	 * 
	 * @author Jan De Moerloose
	 */
	interface MyUiBinder extends UiBinder<Widget, ImageAnalysisPanel> {
	}

	private static final MyUiBinder UI_BINDER = GWT.create(MyUiBinder.class);

	@UiField
	protected HorizontalPanel totalPanel;

	@UiField
	protected SimplePanel chart;

	@UiField
	protected CheckBox checkBoxAlpha;

	@UiField
	protected CheckBox checkBoxR;

	@UiField
	protected CheckBox checkBoxG;

	@UiField
	protected CheckBox checkBoxB;

	@UiField
	protected RadioButton dataOriginalRadioButton;

	@UiField
	protected RadioButton dataCurrentRadioButton;

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

	private CanvasImageWithRadiometryElement element;
	//;
	private Map<ImageDataUtil.RadiometryValue, TextBox> minTextBoxes;
	private Map<ImageDataUtil.RadiometryValue, TextBox> maxTextBoxes;

	public ImageAnalysisPanel() {
		UI_BINDER.createAndBindUi(this);
		minTextBoxes = new HashMap<ImageDataUtil.RadiometryValue, TextBox>();
		minTextBoxes.put(ImageDataUtil.RadiometryValue.RED, redMinText);
		minTextBoxes.put(ImageDataUtil.RadiometryValue.GREEN, greenMinText);
		minTextBoxes.put(ImageDataUtil.RadiometryValue.BLUE, blueMinText);
		maxTextBoxes = new HashMap<ImageDataUtil.RadiometryValue, TextBox>();
		maxTextBoxes.put(ImageDataUtil.RadiometryValue.RED, redMaxText);
		maxTextBoxes.put(ImageDataUtil.RadiometryValue.GREEN, greenMaxText);
		maxTextBoxes.put(ImageDataUtil.RadiometryValue.BLUE, blueMaxText);
		for (TextBox textBox : minTextBoxes.values()) {
			textBox.setEnabled(false);
		}
		for (TextBox textBox : maxTextBoxes.values()) {
			textBox.setEnabled(false);
		}
		dataOriginalRadioButton.setValue(true);
	}

	public Widget asWidget() {
		return totalPanel;
	}

	public void setCanvasImageWithRadiometry(CanvasImageWithRadiometryElement element) {
		this.element = element;
		updateImageRadiometry(element.getImageRadiometry());
	}

	@UiHandler("drawDataBtn")
	public void onDataFromImageBtnBClicked(ClickEvent event) {
		updateChart();
	}

	public void updateImageRadiometry(ImageRadiometry imageRadiometry) {
		for (ImageDataUtil.RadiometryValue radiometryValue : ImageDataUtil.getRGB()) {
			minTextBoxes.get(radiometryValue).setText("" + imageRadiometry.getMin(radiometryValue));
			maxTextBoxes.get(radiometryValue).setText("" + imageRadiometry.getMax(radiometryValue));
		}
		if (dataCurrentRadioButton.getValue()) {
			updateChart();
		}
	}

	/* private methods */

	private void updateChart() {
		chart.clear();
		List<ImageDataUtil.RadiometryValue> radiometryValues = new ArrayList<ImageDataUtil.RadiometryValue>();
		if (checkBoxAlpha.getValue()) {
			radiometryValues.add(ImageDataUtil.RadiometryValue.ALPHA);
		}
		if (checkBoxR.getValue()) {
			radiometryValues.add(ImageDataUtil.RadiometryValue.RED);
		}
		if (checkBoxG.getValue()) {
			radiometryValues.add(ImageDataUtil.RadiometryValue.GREEN);
		}
		if (checkBoxB.getValue()) {
			radiometryValues.add(ImageDataUtil.RadiometryValue.BLUE);
		}
		chart.add(createChartForHistogramValues(radiometryValues.toArray(
				new ImageDataUtil.RadiometryValue[radiometryValues.size()])));
	}

	private Chart createChartForHistogramValues(ImageDataUtil.RadiometryValue... radiometryValues) {
		Map<ImageDataUtil.RadiometryValue, HistogramData> colorInfoMap = null;
		if (dataOriginalRadioButton.getValue()) {
			colorInfoMap = ImageDataUtil.getHistogramData(element.getOriginalImageData());
		} else if (dataCurrentRadioButton.getValue()) {
			colorInfoMap = ImageDataUtil.getHistogramData(element.getImageData());
		}

		final Chart chart = new Chart()
				.setType(Series.Type.SPLINE)
				.setMarginBottom(25)
				.setChartTitle(new ChartTitle()
								.setText("Color analysis")
				);

		int maxAmountUnsaturated = 0;
		for (ImageDataUtil.RadiometryValue radiometryValue : radiometryValues) {
			HistogramData histogramData = colorInfoMap.get(radiometryValue);
			int amountUnsaturated = histogramData.getMaxAmountUnsaturated();
			if (amountUnsaturated > maxAmountUnsaturated) {
				maxAmountUnsaturated = amountUnsaturated;
			}
			chart.addSeries(chart.createSeries().setName(radiometryValue.name()).
					setPoints(toClass(histogramData.getHitArray())));
		}
		chart.getXAxis().setMax(256).setMin(0);
		chart.getYAxis().setMax(maxAmountUnsaturated).setMin(0);

		chart.setHeight100();
		chart.setWidth("500px");
		chart.setStyleName("chartStyle");
		return chart;
	}

	private Integer[] toClass(int[] values) {
		Integer[] result = new Integer[values.length];
		for (int i = 0 ; i < result.length ; i++) {
			result[i] = values[i];
		}
		return result;
	}
}