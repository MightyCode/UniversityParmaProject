package MobilityViewer.mightylib.util.math;

import org.joml.*;

import java.lang.Math;
import java.util.ArrayList;

/**
 * Math class.
 * This class is used to compute complicated calculations.
 *
 * @author MightyCode
 * @version 1.0
 */
public class MightyMath {

	public static final float PI_FLOAT = (float)(Math.PI);

	/**
	 * Calculate the position of a number in the first interval to transpose this number in an second interval.
	 *
	 * @param x The number in the first interval.
	 * @param a First number of the first interval.
	 * @param b Latest number of the first interval.
	 * @param c First number of the second interval..
	 * @param d Latest number of the second interval.
	 *
	 * @return result
	*/
	public static float mapf(float x, float a, float b, float c, float d) {
		return (x - a) / (b - a) * (d - c) + c;
	}

	public static double mapd(double x, double a, double b, double c, double d) {
		return (x - a) / (b - a) * (d - c) + c;
	}


	public static float mapLogf(float x, float a, float b, float c, float d) {
		return (float)Math.exp(mapf((float)Math.log(x), (float)Math.log(a), (float)Math.log(b), (float)Math.log(c), (float)Math.log(d)));
	}

	public static int sum(int[] table){
		int i = 0;
		int sum = 0;
		while (i < table.length){
			sum+=table[i];
			++i;
		}
		return sum;
	}

	public static float sum(float[] sum){
		return sum(sum, 0, sum.length);
	}

	public static float sum(float[] sum, int start, int end){
		float result = 0;
		for (int  i = start; i < end; ++i){
			result += sum[i];
		}

		return result;
	}



	public static Float sum(ArrayList<Float> list){
		float sum = 0;
		for (Float value : list){
			sum += value;
		}

		return sum;
	}

	public static double rads(double angle){
		return angle * (Math.PI/180.0);
	}

	public static float rads(float angle){
		return (float)(angle * (Math.PI/180.0));
	}

	public static double angles(double rad){
		return rad * (180.0/ Math.PI);
	}

	public static float angles(float rad){
		return (float)(rad * (180.0/ Math.PI));
	}

	public static Vector2f rotatePointAround(Vector2f pt, Vector2f ref, float angle){
		double s = Math.cos(angle);
		double c = Math.sin(angle);

		return new Vector2f(
				(float)(c * (pt.x - ref.x) - s * (pt.y - ref.y) + ref.x),
				(float)(s * (pt.x - ref.x) + c * (pt.y - ref.y)  + ref.y)
		);
	}

	public static Vector2f projectPointOnAxis(Vector2f pt, Vector2f axis){
		float value = (pt.x * axis.x + pt.y * axis.y) / (axis.x * axis.x + axis.y * axis.y);

		return new Vector2f(axis.x * value, axis.y * value);

	}
}