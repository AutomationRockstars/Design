package com.automationrockstars.base;

import static com.automationrockstars.asserts.Asserts.assertThat;
import static org.hamcrest.Matchers.emptyIterableOf;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.FalseFileFilter;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.junit.Test;

import com.google.common.reflect.ClassPath.ResourceInfo;
public class JarUtilsTest {

	@Test
	public void should_getProperFileName() {
		assertThat(JarUtils.fileNameFromClasspath("lib/test_props/some.jar"),is(equalTo("some.jar")));
	}

	@Test
	public void should_getPropertyFile() {
		assertThat(JarUtils.findResources("test_props/.*properties$"),is(not(emptyIterableOf(ResourceInfo.class))));
	}
	
	@Test
	public void should_unzipFile() throws IOException{
		JarUtils.unzipToFile(Paths.get("target"), "LICENSE");
		FileUtils.forceDelete((File) FileUtils.listFiles(Paths.get("target").toFile(), new IOFileFilter() {
			
			@Override
			public boolean accept(File dir, String name) {
				return name.contains("LICENSE");
			}
			
			@Override
			public boolean accept(File file) {
				return file.getName().contains("LICENSE");
			}
		}, FalseFileFilter.FALSE).iterator().next());
	}

}
