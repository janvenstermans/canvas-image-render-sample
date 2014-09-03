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
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;

/**
 * ContentPanel that demonstrates canvas rendering abilities.
 * 
 * @author Jan Venstermans
 */
public class ImageRadiometryChangeButtonPanel implements IsWidget {

	/**
	 * UI binder for this widget.
	 * 
	 * @author Jan De Moerloose
	 */
	interface MyUiBinder extends UiBinder<Widget, ImageRadiometryChangeButtonPanel> {
	}

	private static final MyUiBinder UI_BINDER = GWT.create(MyUiBinder.class);

	@UiField
	protected HTMLPanel totalPanel;

	private CanvasImageChangeColorPanel canvasImageChangeColorPanel;

	public ImageRadiometryChangeButtonPanel(CanvasImageChangeColorPanel canvasImageChangeColorPanel) {
		this.canvasImageChangeColorPanel = canvasImageChangeColorPanel;
		UI_BINDER.createAndBindUi(this);
	}

	public Widget asWidget() {
		return totalPanel;
	}

	/* general color changes, without brightness change */
	@UiHandler("redLowMinBtn")
	public void onRedLowMinBtnClicked(ClickEvent event) {
		canvasImageChangeColorPanel.onColorChangeNoBrightness(ImageDataUtil.RadiometryValue.RED, false, false);
	}
	@UiHandler("redLowPlusBtn")
	public void onRedLowPlousBtnClicked(ClickEvent event) {
		canvasImageChangeColorPanel.onColorChangeNoBrightness(ImageDataUtil.RadiometryValue.RED, false, true);
	}
	@UiHandler("redHighMinBtn")
	public void onRedHighMinBtnClicked(ClickEvent event) {
		canvasImageChangeColorPanel.onColorChangeNoBrightness(ImageDataUtil.RadiometryValue.RED, true, false);
	}
	@UiHandler("redHighPlusBtn")
	public void onRedHighPlousBtnClicked(ClickEvent event) {
		canvasImageChangeColorPanel.onColorChangeNoBrightness(ImageDataUtil.RadiometryValue.RED, true, true);
	}

	@UiHandler("greenLowMinBtn")
	public void onGreenLowMinBtnClicked(ClickEvent event) {
		canvasImageChangeColorPanel.onColorChangeNoBrightness(ImageDataUtil.RadiometryValue.GREEN, false, false);
	}
	@UiHandler("greenLowPlusBtn")
	public void onGreenLowPlousBtnClicked(ClickEvent event) {
		canvasImageChangeColorPanel.onColorChangeNoBrightness(ImageDataUtil.RadiometryValue.GREEN, false, true);
	}
	@UiHandler("greenHighMinBtn")
	public void onGreenHighMinBtnClicked(ClickEvent event) {
		canvasImageChangeColorPanel.onColorChangeNoBrightness(ImageDataUtil.RadiometryValue.GREEN, true, false);
	}
	@UiHandler("greenHighPlusBtn")
	public void onGreenHighPlousBtnClicked(ClickEvent event) {
		canvasImageChangeColorPanel.onColorChangeNoBrightness(ImageDataUtil.RadiometryValue.GREEN, true, true);
	}

	@UiHandler("blueLowMinBtn")
	public void onBlueLowMinBtnClicked(ClickEvent event) {
		canvasImageChangeColorPanel.onColorChangeNoBrightness(ImageDataUtil.RadiometryValue.BLUE, false, false);
	}
	@UiHandler("blueLowPlusBtn")
	public void onBlueLowPlousBtnClicked(ClickEvent event) {
		canvasImageChangeColorPanel.onColorChangeNoBrightness(ImageDataUtil.RadiometryValue.BLUE, false, true);
	}
	@UiHandler("blueHighMinBtn")
	public void onBlueHighMinBtnClicked(ClickEvent event) {
		canvasImageChangeColorPanel.onColorChangeNoBrightness(ImageDataUtil.RadiometryValue.BLUE, true, false);
	}
	@UiHandler("blueHighPlusBtn")
	public void onBlueHighPlousBtnClicked(ClickEvent event) {
		canvasImageChangeColorPanel.onColorChangeNoBrightness(ImageDataUtil.RadiometryValue.BLUE, true, true);
	}

	@UiHandler("brightLowMinBtn")
	public void onBrightLowMinBtnClicked(ClickEvent event) {
		canvasImageChangeColorPanel.onChangeBrightness(false, false);
	}
	@UiHandler("brightLowPlusBtn")
	public void onBrightLowPlousBtnClicked(ClickEvent event) {
		canvasImageChangeColorPanel.onChangeBrightness(false, true);
	}
	@UiHandler("brightHighMinBtn")
	public void onBrightHighMinBtnClicked(ClickEvent event) {
		canvasImageChangeColorPanel.onChangeBrightness(true, false);
	}
	@UiHandler("brightHighPlusBtn")
	public void onBrightHighPlousBtnClicked(ClickEvent event) {
		canvasImageChangeColorPanel.onChangeBrightness(true, true);
	}
}