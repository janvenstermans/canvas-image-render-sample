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

package dummy.standalone.example.client;

import dummy.standalone.example.client.sample.CanvasImageChangeColorPanel;
import org.geomajas.gwt2.example.base.client.sample.SamplePanel;
import org.geomajas.gwt2.example.base.client.sample.SamplePanelRegistry;
import org.geomajas.gwt2.example.base.client.sample.ShowcaseSampleDefinition;
import dummy.standalone.example.client.i18n.SampleMessages;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;

/**
 * Entry point and main class for the GWT client example application.
 * 
 * @author Jan De Moerloose
 */
public class ExampleJar implements EntryPoint {

	private static final SampleMessages MESSAGES = GWT.create(SampleMessages.class);

	public static final String CATEGORY_RENDERING = "Drawing on the map";

	public void onModuleLoad() {
		// Register all samples:
		registerGeneralSamples();
	}

	public static SampleMessages getMessages() {
		return MESSAGES;
	}

	private void registerGeneralSamples() {
		SamplePanelRegistry.registerFactory(CATEGORY_RENDERING, new ShowcaseSampleDefinition() {

			public SamplePanel create() {
				return new CanvasImageChangeColorPanel();
			}

			public String getTitle() {
				return "Changing images in canvas";
			}

			public String getShortDescription() {
				return "Changing images in canvas";
			}

			public String getDescription() {
				return "Changing images in canvas";
			}

			public String getCategory() {
				return CATEGORY_RENDERING;
			}

			@Override
			public String getKey() {
				return "canvasimagechange";
			}
		});
	}
}