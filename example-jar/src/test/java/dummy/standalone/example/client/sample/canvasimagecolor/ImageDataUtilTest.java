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

import com.google.gwt.canvas.dom.client.CanvasPixelArray;
import com.google.gwt.canvas.dom.client.ImageData;
import dummy.standalone.example.client.sample.canvasimagecolor.HistogramData;
import dummy.standalone.example.client.sample.canvasimagecolor.ImageRadiometry;
import org.junit.Assert;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

/**
 * Test for {@link ImageDataUtil}.
 * 
 * @author Jan Venstermans
 */
public class ImageDataUtilTest {

	@Test
	public void testRescalingTo100_255() throws Exception {
		Assert.assertEquals(0, ImageDataUtil.getRescaledValue(0, 100, 255));
		Assert.assertEquals(0, ImageDataUtil.getRescaledValue(100, 100, 255));
		Assert.assertEquals(255, ImageDataUtil.getRescaledValue(255, 100, 255));
	}
}