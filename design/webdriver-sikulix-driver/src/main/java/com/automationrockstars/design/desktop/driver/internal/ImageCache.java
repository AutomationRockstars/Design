package com.automationrockstars.design.desktop.driver.internal;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.WriteLock;

import javax.imageio.ImageIO;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.automationrockstars.base.ConfigLoader;
import com.automationrockstars.design.gir.webdriver.plugin.UiDriverPlugin;
import com.google.common.base.Predicate;
import com.google.common.base.Throwables;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public class ImageCache implements UiDriverPlugin{

	public static final String DEFAULT_IMAGES_DIR = "images";
	public static final String IMAGES_DIR = ConfigLoader.config().getString("images.location",DEFAULT_IMAGES_DIR);
	private static final Map<String,String> cache = Maps.newHashMap();
	private static final WriteLock lock = new ReentrantReadWriteLock().writeLock();
	private static final boolean CLEAN_CACHE_ON_EXIT = ConfigLoader.config().getBoolean("images.cleanOnExit",true);
	private static final Logger LOG = LoggerFactory.getLogger(ImageCache.class);
	public static List<String> list(){
		List<String> result = Lists.newArrayList();
		return result;
	}

	public static String path(String imageName){
		try{
			lock.lock();
			if (cache.containsValue(imageName)){
				return imageName;
			}
			String result = cache.get(pathToString(imageName));
			LOG.debug("Found {}",result);
			return result;
		} finally {
			lock.unlock();
		}
	}

	private static String pathToString(String path){
		return path.replaceAll("/", "|").replaceAll("\\\\", "|");
	}
	
	private static String stringToPath(String key){
		return key.replaceAll("\\|","/");
	}
	public static boolean has(String imageName){
		try{
			String imageKey = pathToString(imageName);
			lock.lock();
			return cache.containsKey(imageKey) || cache.containsValue(imageName);
		} finally {
			lock.unlock();
		}
	}

	public static void add(String imageName, String byte64){
		try{
			lock.lock();
			System.out.println(imageName);
			cache.put(pathToString(imageName), saveToFile(imageName, byte64));
		} finally {
			lock.unlock();
		}
	}

	private static final File FILE_CACHE = new File("cached_images");
	private static String cachedPath(String name){
		Paths.get(FILE_CACHE.getAbsolutePath(),name).toFile().getParentFile().mkdirs();
		return Paths.get(FILE_CACHE.getAbsolutePath(),name).toFile().getAbsolutePath();
	}
	private static String saveToFile(String name,String byte64){
		String result = cachedPath(name); 
		try (OutputStream stream = new FileOutputStream(result)) {
			stream.write(Base64.decodeBase64(byte64));
		} catch (IOException e) {
			result = null;
			Throwables.propagate(e);
		}
		LOG.debug("File {} cached to {}",name, result);
		return result;
	}

	private static void loadFrom(File topDir){
		for (File image : FileUtils.listFiles(topDir, TrueFileFilter.INSTANCE, TrueFileFilter.INSTANCE)){
			String key = Paths.get(topDir.getAbsolutePath()).relativize(Paths.get(image.getAbsolutePath())).toString(); 
			cache.put(pathToString(key), image.getAbsolutePath());
			LOG.debug("Loading {} from {}",pathToString(key),image.getAbsolutePath());
		}
		
	}

	private static Iterable<File> imageDirs(){
		return Iterables.filter(FileUtils.listFilesAndDirs(new File("").getAbsoluteFile(), TrueFileFilter.INSTANCE, TrueFileFilter.INSTANCE),new Predicate<File>() {

			@Override
			public boolean apply(File input) {
				return input.isDirectory() && input.getName().equals(IMAGES_DIR) && ! input.getPath().contains(File.separator + "target" + File.separator);
			}
		});

	}

	static {
		load();
	}
	private static void load(){
		for (File imageDir : imageDirs()){
			loadFrom(imageDir);
		}
		if (FILE_CACHE.exists() && FILE_CACHE.canRead()){
			loadFrom(FILE_CACHE);
		} else {
			FILE_CACHE.mkdirs();
		}
	}

	public static Cookie toCookie(final String name,final File image){
		try {
			final ByteArrayOutputStream bos = new ByteArrayOutputStream();
			final BufferedImage imageContent = ImageIO.read(image);
			ImageIO.write(imageContent, "png", bos);
			final byte[] imageBytes = bos.toByteArray();
			bos.close();
			final String imageString = Base64.encodeBase64String(imageBytes); 
			return new Cookie(name, imageString);
		} catch (IOException e) {
			LOG.error("File {} cannot be converted",image,e);
			return null;
		} 

	}

	public static void remove(String name) {
		try {
			FileUtils.forceDelete(new File(path(name)));
			load();
		} catch (IOException e) {
			LOG.error("File {} cannot be removed",name,e);
		}

	}

	public static void removeAll() {
		try {
			FileUtils.forceDelete(FILE_CACHE);
			load();
		} catch (IOException e) {
			LOG.error("Cache removal error",e);
		}

	}

	private static final List<String> drivers = Lists.newArrayList(); 
	
	public static synchronized void syncRemote(WebDriver remoteDriver) {
		if (! drivers.contains(remoteDriver.toString())){
			LOG.info("Syncing cache with {}",remoteDriver);
			drivers.add(remoteDriver.toString());
			for (Entry<String,String> file : cache.entrySet()){
				remoteDriver.manage().addCookie(toCookie(stringToPath(file.getKey()), Paths.get(file.getValue()).toFile()));
			}
		}
	}

	public static synchronized void cleanRemote(WebDriver remoteDriver){
		LOG.info("Cleaning remote cache for {}",remoteDriver);
		remoteDriver.manage().deleteAllCookies();
		drivers.remove(remoteDriver.toString());
	}

	@Override
	public void afterInstantiateDriver(WebDriver driver) {
		if (driver.toString().contains("sikulix")){
			syncRemote(driver);
		}
	}
	@Override
	public void beforeCloseDriver(WebDriver driver) {
		if (CLEAN_CACHE_ON_EXIT && driver.toString().contains("sikulix")){
			cleanRemote(driver);
		}
	}
	
	@Override
	public void beforeInstantiateDriver() {		
	}
	@Override
	public void beforeGetDriver() {		
	}

	@Override
	public void afterGetDriver(WebDriver driver) {	
	}
	@Override
	public void afterCloseDriver() {
	}

}
