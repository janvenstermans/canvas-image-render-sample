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

/**
 * Contains data from the histogram calculations.
 *
 * @author Jan Venstermans
 */
public class HistogramData {

	public static final int RANGE = 256;

	private int[] hitArray;

	public HistogramData() {
		clear();
	}

	public void clear() {
		hitArray = new int[RANGE];
	}

	public void add(int value) {
		if (value > -1 && value < hitArray.length) {
			hitArray[value] = hitArray[value] + 1;
		}
	}

	public int getMinHit() {
		for (int i = 0 ; i < hitArray.length; i++) {
			if (hitArray[i] > 0) {
				return i;
			}
		}
		return -1;
	}

	public int getMaxHit() {
		for (int i = hitArray.length - 1 ; i > -1 ; i--) {
			if (hitArray[i] > 0) {
				return i;
			}
		}
		return -1;
	}

	public int getMaxAmountUnsaturated() {
		int max = 0;
		/* don't use the extreme values, as they might contain saturated values*/
		for (int i = 1 ; i < hitArray.length - 1; i++) {
			if (hitArray[i] > max) {
				max = hitArray[i];
			}
		}
		return max;
	}

	public int getCount() {
		int count = 0;
		for (int i = 0 ; i < hitArray.length; i++) {
			count += hitArray[i];
		}
		return count;
	}

	public int[] getHitArray() {
		return hitArray;
	}
}