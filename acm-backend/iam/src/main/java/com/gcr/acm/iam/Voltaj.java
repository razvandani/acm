package com.gcr.acm.iam;

public class Voltaj {

	public static void main(String[] args) {
		String band = "Hecate Enthroned";
		String result;

		for (int i = 0; i < band.length(); i++) {
			String samples = "abcdefghijklmnopqrsjklmnoprstunvwăâîșț";

			for (int j = 0; j < samples.length(); j++) {
				result = band.substring(0, i) + samples.charAt(j) + band.substring(i+1);
				System.out.println("result = " + result);
			}
		}
	}
}
