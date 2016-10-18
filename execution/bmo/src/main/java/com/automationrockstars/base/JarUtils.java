package com.automationrockstars.base;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.ArrayList;

import com.google.common.base.Predicate;
import com.google.common.base.Splitter;
import com.google.common.base.Throwables;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.Iterables;
import com.google.common.io.ByteStreams;
import com.google.common.reflect.ClassPath;
import com.google.common.reflect.ClassPath.ResourceInfo;

public class JarUtils {

	public static void unzipToFile(Path outsideDirectory, String... fileOnClassPath) {
		unzipResource(outsideDirectory, findResources(fileOnClassPath).get(0));		
	}

	public static void unzipResource(Path outsideDirectory, ResourceInfo resource){
		outsideDirectory.toFile().mkdirs();
		try (InputStream internalFile = resource.url().openStream(); FileOutputStream out = new FileOutputStream(outsideDirectory.toFile()+ "/" + fileNameFromClasspath(resource.getResourceName()))){
			ByteStreams.copy(internalFile, out );
			
		} catch (IOException e) {
			Throwables.propagate(e);
		}
	}

	public static String fileNameFromClasspath(String classPathFile){
		return Iterables.getLast(Splitter.on("/").splitToList(classPathFile));		
	}

	public static void unzipDirectory(Path outsideDirectory,String... dirOnClassPath){
		for (ResourceInfo resource : findResources(dirOnClassPath)){
			unzipResource(outsideDirectory, resource);
		}
	}

	public static FluentIterable<ResourceInfo> findResources(final String... filterParts){
		try {
			ClassPath cp = ClassPath.from(Thread.currentThread().getContextClassLoader());
			return FluentIterable.from(cp.getResources()).filter(new Predicate<ResourceInfo>() {
				@Override
				public boolean apply(ResourceInfo input) {
					boolean matches = true;
					String resourceName = input.getResourceName();
					for (String filterPart : filterParts){
						matches = matches && resourceName.matches(".*"+filterPart + ".*");
					}
					return matches;
				}
			});
		} catch (IOException e) {
			Throwables.propagate(e);
		}
		return FluentIterable.from(new ArrayList<ResourceInfo>());
	}

}
