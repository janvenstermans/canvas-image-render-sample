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

import java.util.HashMap;
import java.util.Map;

/**
 * Util for {@link com.google.gwt.canvas.dom.client.ImageData}.
 * 
 * @author Jan Venstermans
 */
public final class ImageDataUtil {

	public static final int RGB_MAX_VALUE = 255;
	public static final int RGB_MIN_VALUE = 0;

	/**
	 * Band values for {@link CanvasPixelArray}.
	 */
	public enum RadiometryValue {
		RED(0),
		GREEN(1),
		BLUE(2),
		ALPHA(3);

		/**
		 * Int value for position in group of 4 in {@link CanvasPixelArray}.
		 */
		private final int value;

		private RadiometryValue(int value) {
			this.value = value;
		}

		public int getValue() {
			return value;
		}
	}

	private ImageDataUtil() {
	}

	public static ImageData invertColors(ImageData imageData) {
		CanvasPixelArray data = imageData.getData();

		for (int i = 0; i < data.getLength(); i += 4) {
			data.set(i + RadiometryValue.RED.value, 255 - data.get(i + RadiometryValue.RED.value));
			data.set(i + RadiometryValue.GREEN.value, 255 - data.get(i + RadiometryValue.GREEN.value));
			data.set(i + RadiometryValue.BLUE.value, 255 - data.get(i + RadiometryValue.BLUE.value));
		}
		return imageData;
	}

	public static ImageData applyImageRadiometry(ImageData imageData, ImageRadiometry imageRadiometry) {
		CanvasPixelArray data = imageData.getData();
		rescaleColor(data, RadiometryValue.RED,
				imageRadiometry.getMin(RadiometryValue.RED), imageRadiometry.getMax(RadiometryValue.RED));
		rescaleColor(data, RadiometryValue.GREEN,
				imageRadiometry.getMin(RadiometryValue.GREEN), imageRadiometry.getMax(RadiometryValue.GREEN));
		rescaleColor(data, RadiometryValue.BLUE,
				imageRadiometry.getMin(RadiometryValue.BLUE), imageRadiometry.getMax(RadiometryValue.BLUE));
		return imageData;
	}

	public static Map<RadiometryValue, HistogramData> getHistogramData(ImageData imageData) {
		Map<RadiometryValue, HistogramData> valueListMap = new HashMap<RadiometryValue, HistogramData>();
		for (RadiometryValue radiometryValue : RadiometryValue.values()) {
			valueListMap.put(radiometryValue, new HistogramData());
		}
		CanvasPixelArray data = imageData.getData();
		for (int i = 0; i < data.getLength(); i += RadiometryValue.values().length) {
			for (RadiometryValue radiometryValue : RadiometryValue.values()) {
				valueListMap.get(radiometryValue).add(data.get(i + radiometryValue.value));
			}
		}
		return valueListMap;
	}

	public static ImageDataUtil.RadiometryValue[] getRGB() {
		return new ImageDataUtil.RadiometryValue[]{
				ImageDataUtil.RadiometryValue.RED, ImageDataUtil.RadiometryValue.GREEN,
				ImageDataUtil.RadiometryValue.BLUE};
	}

	/**
	 *
	 * Code largly duplicated from Daniel Ackerman.
	 *
	 * @param imageRadiometry
	 * @param radiometryValue
	 * @param high
	 * @param plus
	 * @param changeFraction percentage as a 0.x value
	 * @return
	 */
	public static ImageRadiometry getImageRadiometryColorWithoutBrightnessChange(ImageRadiometry imageRadiometry,
			RadiometryValue radiometryValue, boolean high, boolean plus, double changeFraction) {
		ImageRadiometry result = new ImageRadiometry(imageRadiometry);
		Map<RadiometryValue, Double> differences = new HashMap<RadiometryValue, Double>();
		for (RadiometryValue value : getRGB()) {
			differences.put(value, (double) getRange(imageRadiometry, value));
		}

		double lengthBefore = getContrastIndication(differences);

		differences.put(radiometryValue, adjustValue(differences.get(radiometryValue),
				getIncreaseDifference(high, plus), changeFraction));
		double newLength = getContrastIndication(differences);
		double scale = lengthBefore / newLength;

		for (RadiometryValue value : getRGB()) {
			double newDifference = differences.get(value) * scale;
			if (high) {
				result.setMax(value, result.getMin(value) + (int) (newDifference + 0.5));
			} else {
				result.setMin(value, result.getMax(value) - (int) (newDifference + 0.5));
			}
		}
		return result;
	}

	private static boolean getIncreaseDifference(boolean high, boolean plus) {
		return (high && plus) || (!high && !plus);
	}

	/**
	 *
	 * @param imageRadiometry
	 * @param high
	 * @param plus
	 * @param changeFraction percentage as a 0.x value
	 * @return
	 */
	public static ImageRadiometry getImageRadiometryBrightnessChange(ImageRadiometry imageRadiometry,
																	 boolean high, boolean plus,
																	 double changeFraction) {
		ImageRadiometry result = new ImageRadiometry(imageRadiometry);
		Map<RadiometryValue, Double> differences = new HashMap<RadiometryValue, Double>();
		for (RadiometryValue value : getRGB()) {
			differences.put(value, (double) getRange(imageRadiometry, value));
		}

		double scale = realFraction(getIncreaseDifference(high, plus), changeFraction);

		for (RadiometryValue value : getRGB()) {
			double newDifference = differences.get(value) * scale;
			if (high) {
				result.setMax(value, result.getMin(value) + (int) (newDifference + 0.5));
			} else {
				result.setMin(value, result.getMax(value) - (int) (newDifference + 0.5));
			}
		}
		return result;
	}

	public static double getContrastIndication(ImageRadiometry imageRadiometry) {
		double squares = 0;
		for (RadiometryValue value : getRGB()) {
			int range = getRange(imageRadiometry, value);
			squares = squares + range * range;
		}
		return Math.sqrt(squares);
	}

	private static double getContrastIndication(Map<RadiometryValue, Double> differences) {
		double squares = 0;
		for (Map.Entry<RadiometryValue, Double> entry : differences.entrySet()) {
			squares = squares + entry.getValue() * entry.getValue();
		}
		return Math.sqrt(squares);
	}

	/**
	 *
	 * @param original
	 * @param increaseDifference
	 * @param changeFraction percentage as a 0.x value
	 * @return
	 */
	private static double adjustValue(double original, boolean increaseDifference, double changeFraction) {
		return original * realFraction(increaseDifference, changeFraction);
	}

	private static double realFraction(boolean increaseDifference, double changeFraction) {
		return increaseDifference ? 1 + changeFraction : 1 - changeFraction;
	}

	private static int getRange(ImageRadiometry imageRadiometry, RadiometryValue radiometryValue) {
		return imageRadiometry.getMax(radiometryValue) - imageRadiometry.getMin(radiometryValue);
	}

	/* private methods */

	private static void rescaleColor(CanvasPixelArray data, RadiometryValue radiometryValue, int newMin, int newMax) {
		if (newMin != RGB_MIN_VALUE || newMax != RGB_MAX_VALUE) {
			for (int i = 0; i < data.getLength(); i += 4) {
				data.set(i + radiometryValue.value,
						getRescaledValue(data.get(i + radiometryValue.value), newMin, newMax));
			}
		}
	}

	public static int getRescaledValue(int original, int newMin, int newMax) {
		if (newMin == newMax) {
			return newMin;
		}
		if (original < newMin) {
			return RGB_MIN_VALUE;
		}
		if (original > newMax) {
			return RGB_MAX_VALUE;
		}
		double scale = (RGB_MAX_VALUE - RGB_MIN_VALUE) / (newMax * 1.0 - newMin) ;
		return (int) ((original - newMin) * scale);
	}
}