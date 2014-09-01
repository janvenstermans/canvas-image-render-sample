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

import com.google.gwt.canvas.dom.client.CanvasPixelArray;
import com.google.gwt.canvas.dom.client.ImageData;

import java.util.HashMap;
import java.util.Map;

/**
 * Util for {@link com.google.gwt.canvas.dom.client.ImageData}.
 * 
 * @author Jan Venstermans
 */
public class ImageDataUtil {

	public enum HistogramValue {
		RED(0),
		GREEN(1),
		BLUE(2),
		ALPHA(3);

		/**
		 * Value for this difficulty
		 */
		public final int value;

		private HistogramValue(int value)
		{
			this.value = value;
		}
	}

	private ImageDataUtil() {
	}

	public static ImageData invertColors(ImageData imageData) {
		CanvasPixelArray data = imageData.getData();

		for(int i = 0; i < data.getLength(); i += 4) {
			// red
			data.set(i + HistogramValue.RED.value, 255 - data.get(i));
			// green
			data.set(i + 1, 255 - data.get(i + 1));
			// blue
			data.set(i + 2, 255 - data.get(i + 2));
		}
		return imageData;
	}

	public static Map<HistogramValue, HistogramData> getHistogramData(ImageData imageData) {
		Map<HistogramValue, HistogramData> valueListMap = new HashMap<HistogramValue, HistogramData>();
		for (HistogramValue histogramValue : HistogramValue.values()) {
			valueListMap.put(histogramValue, new HistogramData());
		}
		CanvasPixelArray data = imageData.getData();
		for(int i = 0; i < data.getLength(); i += HistogramValue.values().length) {
			for (HistogramValue histogramValue : HistogramValue.values()) {
				valueListMap.get(histogramValue).add(data.get(i + histogramValue.value));
			}
		}
		return valueListMap;
	}
}