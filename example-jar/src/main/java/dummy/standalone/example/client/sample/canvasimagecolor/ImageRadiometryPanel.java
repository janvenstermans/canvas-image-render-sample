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
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

import java.util.HashMap;
import java.util.Map;

/**
 * Panel for showing {@link dummy.standalone.example.client.sample.canvasimagecolor.ImageRadiometry} info.
 * 
 * @author Jan Venstermans
 */
public class ImageRadiometryPanel implements IsWidget {

	/**
	 * UI binder for this widget.
	 * 
	 * @author Jan De Moerloose
	 */
	interface MyUiBinder extends UiBinder<Widget, ImageRadiometryPanel> {
	}

	private static final MyUiBinder UI_BINDER = GWT.create(MyUiBinder.class);

	@UiField
	protected HTMLPanel panel;

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

	@UiField
	protected TextBox contrastTextBox;

	private Map<ImageDataUtil.RadiometryValue, TextBox> minTextBoxes;

	private Map<ImageDataUtil.RadiometryValue, TextBox> maxTextBoxes;

	public ImageRadiometryPanel() {
		UI_BINDER.createAndBindUi(this);
		minTextBoxes = new HashMap<ImageDataUtil.RadiometryValue, TextBox>();
		minTextBoxes.put(ImageDataUtil.RadiometryValue.RED, redMinText);
		minTextBoxes.put(ImageDataUtil.RadiometryValue.GREEN, greenMinText);
		minTextBoxes.put(ImageDataUtil.RadiometryValue.BLUE, blueMinText);
		maxTextBoxes = new HashMap<ImageDataUtil.RadiometryValue, TextBox>();
		maxTextBoxes.put(ImageDataUtil.RadiometryValue.RED, redMaxText);
		maxTextBoxes.put(ImageDataUtil.RadiometryValue.GREEN, greenMaxText);
		maxTextBoxes.put(ImageDataUtil.RadiometryValue.BLUE, blueMaxText);
	}

	public Widget asWidget() {
		return panel;
	}

	public void setRgbTextBoxEnabled(boolean enabled) {
		for (ImageDataUtil.RadiometryValue radiometryValue : ImageDataUtil.getRGB()) {
			minTextBoxes.get(radiometryValue).setEnabled(enabled);
			maxTextBoxes.get(radiometryValue).setEnabled(enabled);
		}
	}

	public void setContrastTextBoxEnabled(boolean enabled) {
		contrastTextBox.setEnabled(enabled);
	}

	public void showImageRadiometry(ImageRadiometry imageRadiometry) {
		for (ImageDataUtil.RadiometryValue radiometryValue : ImageDataUtil.getRGB()) {
			minTextBoxes.get(radiometryValue).setText("" + imageRadiometry.getMin(radiometryValue));
			maxTextBoxes.get(radiometryValue).setText("" + imageRadiometry.getMax(radiometryValue));
		}
		contrastTextBox.setText("" + ImageDataUtil.getContrastIndication(imageRadiometry));
	}

	public ImageRadiometry getImageRadiometryFromTexts() {
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
}