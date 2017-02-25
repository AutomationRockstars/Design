package com.automationrockstars.gir.desktop.internal;

import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.FluentWait;
import org.sikuli.basics.Debug;
import org.sikuli.script.FindFailed;
import org.sikuli.script.Match;
import org.sikuli.script.Screen;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.automationrockstars.base.ConfigLoader;
import com.automationrockstars.gir.desktop.ByImage;
import com.automationrockstars.gir.desktop.ImageSearchContext;
import com.automationrockstars.gir.desktop.ImageUiObject;
import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;
import com.google.common.collect.UnmodifiableIterator;

public class SikuliDriver implements ImageSearchContext {
	
	private static final Logger LOG = LoggerFactory.getLogger(SikuliDriver.class);
	private final static SikuliDriver instance = new SikuliDriver();
	
	private SikuliDriver() {
		int logLevel = -1;
		if (LOG.isErrorEnabled()){
			logLevel++;
		}
		if (LOG.isInfoEnabled()){
			logLevel++;
		}
		if (LOG.isDebugEnabled()){
			logLevel++;
		}
		Debug.setDebugLevel(logLevel);
	}
	

	public static SikuliDriver driver() {
		return instance;
	}

	private static final List<Screen> screens = Lists.newArrayList();

	static {
		for (int i = 0; i < Screen.getNumberScreens(); i++) {
			screens.add(new Screen(i));
		}
	}

	private static final double TIMEOUT = ConfigLoader.config().getDouble("imagedriver.default.timeout", 5);

	private static final ExecutorService searchService = Executors.newCachedThreadPool();

	private static final AtomicInteger found = new AtomicInteger(-1);

	private static final AtomicInteger existResult = new AtomicInteger(-1);

	private static Callable<Match> searchTask(final int screenNo, final String psi, final double timeout) {
		return new Callable<Match>() {

			@Override
			public Match call() throws Exception {
				try {
					LOG.trace("Starting search for {} on screen {}",psi,screenNo);
					Match result = screens.get(screenNo).wait(psi, timeout);
					LOG.trace("Found {} on screen {}",result,screenNo);
					found.set(screenNo);
					return result;
				} catch (FindFailed ignore) {
					return null;
				}

			}

		};
	}

	private static Callable<Match> existTask(final int screenNo, final String psi) {
		return new Callable<Match>() {

			@Override
			public Match call() throws Exception {
				Match result = screens.get(screenNo).exists(psi);
				if (result != null) {
					existResult.set(screenNo);
				}
				return result;
			}
		};
	}

	private static Callable<Iterator<Match>> searchAllTask(final int screenNo, final String psi) {
		return new Callable<Iterator<Match>>() {

			@Override
			public Iterator<Match> call() throws Exception {
				try {
					return  screens.get(screenNo).findAll(psi);
				} catch (FindFailed ignore) {
					return null;
				}

			}

		};
	}

	public static Match wait(String image) {
		return wait(image, TIMEOUT);
	}

	public static synchronized Match wait(String image, double timeout) {
		LOG.info("Looking for match of image {}",image);
		image = ImageCache.path(image);
		List<Future<Match>> futures = Lists.newArrayList();
		found.set(-1);
		for (int i = 0; i < screens.size(); i++) {
			futures.add(searchService.submit(searchTask(i, image, timeout)));
		}
				
		new FluentWait<AtomicInteger>(found)
		.withTimeout(Double.valueOf(timeout).longValue(), TimeUnit.SECONDS)
		.pollingEvery(100, TimeUnit.MILLISECONDS)
		.withMessage("Element " + image + " not visible on screen")
		.until(new Predicate<AtomicInteger>() {

			@Override
			public boolean apply(AtomicInteger input) {
				return found.get() > -1;
			}
		});
		

		if (found.get() < 0) {
			throw new NoSuchElementException(String.format(
					"Element identified by image %s not found on the screen within %s seconds", image, timeout));
		} else {
			try {
				return futures.get(found.get()).get();
			} catch (InterruptedException | ExecutionException e) {
				throw new NoSuchElementException(String.format(
						"Element identified by image %s not found on the screen within %s seconds", image, timeout));
			}
		}

	}


	private static class FindAllIterator extends UnmodifiableIterator<Match> {

		private final String psi;
		private final double timeout;
		private final List<Future<Iterator<Match>>> futures = Lists.newArrayList();

		public FindAllIterator(final String psi,final double timeout){
			this.psi = psi;
			this.timeout = timeout;
			doSearch();
		}
		
		private final List<Iterator<Match>> allResults = Lists.newArrayList();
		private int screenNo = -1;
	
		private final void doSearch(){
			LOG.trace("Staring search for multiple objects identified by image {}",psi);
			new FluentWait<Object>(new Object())
			.ignoring(Throwable.class)
			.withTimeout(new Double(timeout).longValue(), TimeUnit.SECONDS)
			.withMessage("Element " + psi + " not visible on screen")
			.pollingEvery(500, TimeUnit.MILLISECONDS)
			.until(new Predicate<Object>() {

				@Override
				public boolean apply(Object input) {
					
					for (int i=0;i<screens.size();i++){
						futures.add(searchService.submit(searchAllTask(i, psi)));
					}
					for (Future<Iterator<Match>> future : futures){
						Iterator<Match> result = null;
						try {
							result = future.get();
						} catch (Exception e) {
						}
						if (result != null){
							allResults.add(result);
							screenNo = 0;
						}
					}
					return screenNo > -1;
				}
			});
			LOG.trace("Search for all elements identified by image {} finished",psi);
			
		}
		
		@Override
		public boolean hasNext() {
			return (! allResults.isEmpty()) 
					&& screenNo < allResults.size() 
					&& ( allResults.get(screenNo).hasNext() || ((screenNo +1 < allResults.size()) && (allResults.get(screenNo +1).hasNext())));
		}

		public Match currentNext(){
			if ((allResults.get(screenNo).hasNext())){
				return allResults.get(screenNo).next();
			} else {
				return null;
			}
		}
		@Override
		public Match next() {
			Match result = null;
			while ((result ==null) && (screenNo > -1) && (screenNo < allResults.size())){
				result = currentNext();
				if (result == null){
					screenNo++;
				}
			}
			return result;
		}

	}

	public static Iterator<Match> waitAll(String psi){
		return waitAll(psi,TIMEOUT);
	}
	public static Iterator<Match> waitAll(String psi, double timeout) {
		return new FindAllIterator(psi, timeout);
	}

	public static synchronized Match isVisible(String image) {
		image = ImageCache.path(image);
		existResult.set(-1);
		final List<Future<Match>> futures = Lists.newArrayList();
		for (int i = 0; i < screens.size(); i++) {
			futures.add(searchService.submit(existTask(i, image)));
		}
		new FluentWait<AtomicInteger>(existResult)
		.withTimeout(20, TimeUnit.SECONDS)
		.pollingEvery(100, TimeUnit.MILLISECONDS)
		.withMessage("Searching the screen for " + image)
		.until(new Predicate<AtomicInteger>() {

			@Override
			public boolean apply(AtomicInteger input) {
				return input.get() > -1 || FluentIterable.from(futures).allMatch(new Predicate<Future<Match>>() {

					@Override
					public boolean apply(Future<Match> input) {
						return input.isDone();
					}
				});
			}
		}); 
		
		if (existResult.get() >= 0) {
			try {
				return futures.get(existResult.get()).get();
			} catch (InterruptedException | ExecutionException e) {
				return null;
			}
		} else {
			return null;
		}
	}
	
	public static void waitUntilInvisible(String imagePath, double timeout) {
		
		new FluentWait<String>(imagePath)
		.withTimeout(new Double(timeout).longValue(), TimeUnit.SECONDS)
		.pollingEvery(500, TimeUnit.MILLISECONDS)
		.withMessage("Element " + imagePath + " still visible on screen")
		.until(new Predicate<String>() {
			@Override
			public boolean apply(String input) {
				return isVisible(input) == null;
			}
		});
		
	}

	@Override
	public List<WebElement> findElements(By by) {
		Preconditions.checkArgument(by instanceof ByImage);
		return Lists.newArrayList(
				Iterators.transform(findElements(((ByImage) by).path()), new Function<ImageUiObject, WebElement>() {
					@Override
					public WebElement apply(ImageUiObject input) {
						return input;
					}
				}));
	}

	@Override
	public WebElement findElement(By by) {
		Preconditions.checkArgument(by instanceof ByImage);
		return findElement(((ByImage) by).path());
	}

	@Override
	public ImageUiObject findElement(ByImage by) {
		return findElement(by.path());
	}

	@Override
	public ImageUiObject findElement(String imagePath) {
		Preconditions.checkArgument(ImageCache.has(imagePath),"Cannot read file %s",imagePath);
		return SikuliImageUiObject.wrap(wait(ImageCache.path(imagePath)), imagePath, null);
	}

	@Override
	public Iterator<ImageUiObject> findElements(ByImage by) {
		return findElements(by.path());
	}

	@Override
	public Iterator<ImageUiObject> findElements(String imagePath) {
		Preconditions.checkArgument(ImageCache.has(imagePath),"Cannot read file %s",imagePath);
		return SikuliImageUiObject.wrap(waitAll(ImageCache.path(imagePath)), imagePath, null);
	}

	

}
