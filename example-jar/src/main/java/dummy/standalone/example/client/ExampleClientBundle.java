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

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;

/**
 * {@link com.google.gwt.resources.client.ClientBundle} for the examples.
 *
 * @author Jan Venstermans
 */
public interface ExampleClientBundle extends ClientBundle {

	ExampleClientBundle INSTANCE = GWT.create(ExampleClientBundle.class);

	@Source("image/tuxTest.png")
	ImageResource imageTuxTest();

	@Source("image/satelliteImage1.png")
	ImageResource imageSatellite1();
}