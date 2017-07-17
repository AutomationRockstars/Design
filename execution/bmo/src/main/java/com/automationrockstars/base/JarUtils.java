package com.automationrockstars.base;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

	private static final ClassLoader cl = ClassLoader.getSystemClassLoader();
	private static ClassLoader classLoader(){
		return cl;
	}
	
	
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
	
	private static ClassPath cp(){
		try {
			cp = ClassPath.from(classLoader());
		} catch (IOException e) {
		}
		return cp;
	}

	private static FluentIterable<ResourceInfo> resources(){
		if (resources == null){			
			Preconditions.checkNotNull(cp(), "ClassPath scanner cannot be initialized");
			resources = FluentIterable.from(cp().getResources());
		} 
		return resources;
	}

	static Predicate<String> matchesName(final String... filterParts){
		return new Predicate<String>() {

			@Override
			public boolean apply(String resourceName) {
				boolean matches = true;
				for (String filterPart : filterParts){
					matches = matches && resourceName.matches(".*"+filterPart + ".*");
				}
				return matches;
			}
		};
		
	}
	private static final Logger LOG = LoggerFactory.getLogger(JarUtils.class);
		public static FluentIterable<ResourceInfo> findResources(final String... filterParts){
		String currentPath = Paths.get("").toAbsolutePath().toString();
		if (currentPath.contains(" ")){
			LOG.error("Current path contains SPACE characters that usually make loading classpath resources failing. Current path is {}",currentPath );
		}
			return resources().filter(new Predicate<ResourceInfo>() {
				@Override
				public boolean apply(ResourceInfo input) {
					return matchesName(filterParts).apply(input.getResourceName());
				}
			});
	}
	
	public static void reload(){
		resources = null;
		cp = null;
		resources();
	}

}
