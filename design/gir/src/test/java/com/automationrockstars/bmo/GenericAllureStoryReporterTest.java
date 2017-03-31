package com.automationrockstars.bmo;

import static com.automationrockstars.asserts.Asserts.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import com.automationrockstars.base.ConfigLoader;
import com.google.common.collect.Lists;
public class GenericAllureStoryReporterTest {

	@SuppressWarnings("unchecked")
	@Test
	public void should_makeTheTheListMinimal() {
		List<Map<String,String>> data = Lists.newArrayList();
		Map<String,String> first = Collections.singletonMap("Feature1 ->scenario1|sss|::", "empty");
		Map<String,String> second = Collections.singletonMap("Feature1 ->scenario2|aaa|::", "empty");
		Map<String,String> third = Collections.singletonMap("Feature1 2->scenario1::", "empty");
		Map<String,String> augmented = Collections.singletonMap("Feature1 2::", "empty");
		data.add(first);
		data.add(first);
		data.add(second);
		data.add(second);
		data.add(second);
		data.add(third);
		data.add(third);
		data.add(second);
		ConfigLoader.config().addProperty("webdriver.videos", data);
		
		assertThat(GenericAllureStoryReporter.minimize().size(), is(3));
		assertThat(GenericAllureStoryReporter.minimize(),contains(first,second,augmented));
		assertThat(GenericAllureStoryReporter.minimize(),not(contains(third)));
			
	}

}
