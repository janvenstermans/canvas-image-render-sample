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

import java.util.Arrays;

/**
 * Contains integer values that indicate the color transformation of an image.
 *
 * @author Daniel Åkerman, 7 dec 2011
 * @author Jan Venstermans
 * @version $Revision: $, $Author: $, $Date: $
 * @see com.spacemetric.rcp.mosaic.util.mosaic.ImageRadiometry
 */
public class ImageRadiometry {

	/**
	 * Holder for minimum values (black-point).
	 */
	private int[] minArray;

	/**
	 * Holder for maximum values (white point).
	 */
	private int[] maxArray;

	/**
	 * Constructor.
	 */
	public ImageRadiometry() {
		minArray = new int[getRadiometryDimension()];
		maxArray = new int[getRadiometryDimension()];
		Arrays.fill(maxArray, ImageDataUtil.RGB_MAX_VALUE);
	}

	/**
	 * Construct a new ImageRadiometry with the same values as the inRadiometry.
	 * @param inRadiometry
	 */
	public ImageRadiometry(ImageRadiometry inRadiometry) {
		this();
		// copy values
		for (int i = 0; i < getRadiometryDimension(); i++) {
			minArray[i] = inRadiometry.minArray[i];
			maxArray[i] = inRadiometry.maxArray[i];
		}
	}

	/**
	 * Set the minimum value of a dimension.
	 */
	public void setMin(ImageDataUtil.RadiometryValue radiometryValue, int min) {
		minArray[radiometryValue.getValue()] = min;
	}

	/**
	 * Set the maximum value of a dimension.
	 */
	public void setMax(ImageDataUtil.RadiometryValue radiometryValue, int max) {
		maxArray[radiometryValue.getValue()] = max;
	}

	/**
	 * Get the minimum value of a dimension.
	 */
	public int getMin(ImageDataUtil.RadiometryValue radiometryValue) {
		return minArray[radiometryValue.getValue()];
	}

	/**
	 * Get the maximum value of a dimension.
	 */
	public int getMax(ImageDataUtil.RadiometryValue radiometryValue) {
		return maxArray[radiometryValue.getValue()];
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuffer b = new StringBuffer();
		for (int i = 0; i < 3; i++) {
			if (i > 0) {
				b.append(" ");
			}
			b.append(Integer.toString(this.minArray[i]));
		}
		for (int i = 0; i < 3; i++) {
			b.append(" ");
			b.append(Integer.toString(this.maxArray[i]));
		}
		return b.toString();
	}

	/* private methods */

	private int getRadiometryDimension() {
		return ImageDataUtil.RadiometryValue.values().length;
	}

}