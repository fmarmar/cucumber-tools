package com.github.fmarmar.cucumber.tools.report.support;

import java.nio.charset.StandardCharsets;

import com.google.common.hash.HashFunction;
import com.google.common.hash.Hashing;

public class ReportUtils {

	private static final HashFunction HF = Hashing.murmur3_128();

	private ReportUtils() { }

	public static String hash(String value) {
		return HF.newHasher().putString(value, StandardCharsets.UTF_8).hash().toString();
	}
	
	

}