package com.automationrockstars.base;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;

import com.google.common.base.Preconditions;
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
	
	private static ClassPath cp;
	private static FluentIterable<ResourceInfo> resources = null;
	
	static {
		try {
			cp = ClassPath.from(Thread.currentThread().getContextClassLoader());
		} catch (IOException e) {
		}
	}

	private static FluentIterable<ResourceInfo> resources(){
		if (resources == null){
			Preconditions.checkNotNull(cp, "ClassPath scanner cannot be initialized");
			resources = FluentIterable.from(cp.getResources());
		} 
		return resources;
	}
	
	public static FluentIterable<ResourceInfo> findResources(final String... filterParts){
			return resources().filter(new Predicate<ResourceInfo>() {
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
	}

}
