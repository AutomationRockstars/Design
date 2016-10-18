package com.automationrockstars.gir.mobile;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.regex.Pattern;

import org.junit.Test;

public class PageUtilsTest {

	@Test
	public void test() {
		System.out.println(Pattern.matches(".*Current.* ~ \\d.* .*", "<UIALink name=\"Current account ~ 0671 €2,828.95\" label=\"Current account ~ 0671 €2,828.95\" value=\"\" dom=\"[object Object]\"  visible=\"false\" hint=\"\" path=\"/0/0/3/0/2\" x=\"11\" y=\"129\" width=\"353\" height=\"56\">"));
		String pattern = "~ \\d* [\\w,\\s]*";
		String[] a = "Current account ~ 0671 €2,828.95".split(pattern);
		String[] b = "CDC SHAKEDOWN TEST ~ 3053 Credit Card €-41.02".split(pattern);
		System.out.println(Arrays.toString(a) +"\n"+ Arrays.toString(b));
		System.out.println(Pattern.matches("\\d\\d/\\d\\d/\\d\\d\\d\\d", "16/03/2016"));
	}

}
