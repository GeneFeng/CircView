package cn.edu.whu.util;

import java.util.Vector;

public class MyMath {
	public static void main(String[] args) {
		String[] a = { "A", "B", "C", "D", "E" };
		String[] res = new String[10];
		combine(a, 0, res, 0, 2);
		// for (int i = 1; i <= a.length; i++) {
		// System.out.println(a.length + "选" + i);
		// String[] res = new String[i];
		// combine(a, 0, res, 0, 1);
		// }
	}

	final static public void combine(final Object src[], final int srcIndex, final Object des[], final int desIndex,
			final int num) {
		if (desIndex >= num) {
			for (int i = 0; i < num; i++)
				System.out.print(des[i] + " ");
			System.out.println();
		} else
			for (int ap = srcIndex; ap < src.length; ap++) {
				des[desIndex] = src[ap];
				combine(src, ap + 1, des, desIndex + 1, num);
			}
	}

	final static public void getNfromM(final Vector<String> src, final int srcIndex, final Vector<String> des,
			final int desIndex, final int num) {
		
	}

}
