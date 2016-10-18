package com.automationrockstars.gir.ui.part;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.Point;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.automationrockstars.base.ConfigLoader;
import com.automationrockstars.design.gir.webdriver.FilterableSearchContext;
import com.automationrockstars.gir.ui.Covered;
import com.automationrockstars.gir.ui.Find;
import com.automationrockstars.gir.ui.Name;
import com.automationrockstars.gir.ui.Optional;
import com.automationrockstars.gir.ui.UiPart;
import com.automationrockstars.gir.ui.UiParts;
import com.google.common.base.Function;
import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.google.common.base.Strings;
import com.google.common.base.Throwables;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.Lists;

import ru.yandex.qatools.htmlelements.annotations.Timeout;

public class UiPartProxy implements InvocationHandler{



	private final UiPartDelegate ui;
	public UiPartProxy(Class<? extends UiPart> generic) {
		ui = new UiPartDelegate(generic);
	}


	private static boolean isCustom(Method method){
		Class<?> declaringClass = method.getDeclaringClass();
		List<Class<?>> nativeMethodOwners = Lists.newArrayList(UiPart.class.getInterfaces());
		nativeMethodOwners.add(UiPart.class);
		nativeMethodOwners.add(Object.class);
		nativeMethodOwners.add(UiPartDelegate.class);
		return ! nativeMethodOwners.contains(declaringClass);
	}

	private static int calculateTimeout(Object target){
		int defaultTimeout = ConfigLoader.config().getInt(FilterableSearchContext.STUBBORN_WAIT_PARAM,5);
		if (target instanceof Method){
			Method tm = (Method) target;
			if (tm.getAnnotation(Optional.class)!= null){
				defaultTimeout = 1;
			}
			if (tm.getAnnotation(Timeout.class)!= null){
				return tm.getAnnotation(Timeout.class).value();
			} else {
				return defaultTimeout;
			}
		} else if (target instanceof Class){
			Class<?> tm = (Class<?>) target;
			if (tm.getAnnotation(Optional.class)!= null){
				defaultTimeout = 1;
			}
			if (tm.getAnnotation(Timeout.class) != null){
				return ((Timeout) tm.getAnnotation(Timeout.class)).value();
			} else if (tm.getAnnotation(com.automationrockstars.gir.ui.Timeout.class) != null){
				return ((com.automationrockstars.gir.ui.Timeout) tm.getAnnotation(com.automationrockstars.gir.ui.Timeout.class)).value();
			} else {
				return defaultTimeout;
			}
		} else if (target instanceof Field){
			Field tm = (Field) target;
			if (tm.getAnnotation(Optional.class)!= null){
				defaultTimeout = 1;
			}
			if (tm.getAnnotation(Timeout.class) != null){
				return ((Timeout) tm.getAnnotation(Timeout.class)).value();
			} else if (tm.getAnnotation(com.automationrockstars.gir.ui.Timeout.class) != null){
				return ((com.automationrockstars.gir.ui.Timeout) tm.getAnnotation(com.automationrockstars.gir.ui.Timeout.class)).value();
			} else {
				return defaultTimeout;
			}
		} else {
			return ConfigLoader.config().getInt(FilterableSearchContext.STUBBORN_WAIT_PARAM,5);
		}
	}


	static boolean any(Method method){
		return method.getAnnotation(Find.class).any();
	}
	static boolean any(Class<? extends UiPart> clazz){
		return clazz.getAnnotation(Find.class).any();
	}

	@SuppressWarnings("unchecked")
	private static <T> T convert(WebElement initial,final Class<T> wanted ){
		if (wanted.isAssignableFrom(initial.getClass())){
			return (T) initial;	
		}
		try {
			if (initial.getClass().equals(EmptyUiObject.class) &&
					(wanted.equals(ru.yandex.qatools.htmlelements.element.Select.class) || wanted.equals(org.openqa.selenium.support.ui.Select.class))){
				((EmptyUiObject)initial).withTagName("select");
			} 
			return wanted.getConstructor(WebElement.class).newInstance(initial);
		} catch (Exception ignore){
			LOG.trace("Cannot do ",ignore);
		} 
		return (T) initial;
	}

	@SuppressWarnings("unchecked")
	private static Object adjustResults(final List<WebElement> result,final Type type){
		Class<?> wanted = null;

		if (type instanceof Class) {
			wanted = (Class<?>) type;
		} else {
			if (type instanceof ParameterizedType){
				wanted = (Class<?>) ((ParameterizedType)type).getRawType();
			}
		}
		if (Collection.class.isAssignableFrom(wanted) ||
				Iterable.class.isAssignableFrom(wanted) ||
				wanted.isArray()){
			if (type instanceof ParameterizedType){
				final Class<?> collectionOf = (Class<?>) ((ParameterizedType)type).getActualTypeArguments()[0];
				@SuppressWarnings("rawtypes")
				FluentIterable filteredResult =  FluentIterable.from(result).transform(new Function<WebElement,Object>(){
					public Object apply(WebElement input) {
						return convert(input,collectionOf);
					}});
				if (wanted.equals(FluentIterable.class)){
					return filteredResult;
				} else if (wanted.isArray()){
					return filteredResult.toArray(collectionOf);
				} else {
					return filteredResult.toList();
				}
			} else return result;
		} else {
			return convert(result.get(0),wanted);
		}
	}

	private static Logger LOG = LoggerFactory.getLogger(UiPart.class);
	public Object invoke(Object host, Method method, Object[] args) throws Throwable {
		try {
		if (isCustom(method)){
			LOG.warn("Working on {} inside {}",Objects.firstNonNull((method.getAnnotation(Name.class)==null)?null:method.getAnnotation(Name.class).value(), method.getName()),host);
			Preconditions.checkArgument(args == null || args.length == 0,"UiPart method cannot accept arguments");
			List<WebElement> result = Lists.newArrayList();
			final org.openqa.selenium.By by = UiParts.buildBy(method);

			final int timeout = calculateTimeout(method); 
			final boolean visibleOnly = ConfigLoader.config().getBoolean("webdriver.visibleOnly",true);
			if (method.getAnnotation(Covered.class) != null){
				ConfigLoader.config().setProperty("webdriver.visibleOnly",false);
			}
			try {
				if (method.getAnnotation(Optional.class)!= null){
					if(! Strings.isNullOrEmpty(method.getAnnotation(Optional.class).condition()) ){
						throw new UnsupportedOperationException("condition is for future use");
					}
					FilterableSearchContext.setWait(method.getAnnotation(Optional.class).timeout());
				}
				if (timeout >=0 ){
					result = ((UiPart) host).delay()
							.withTimeout(timeout, TimeUnit.SECONDS)
							.until(UiParts.allVisible(by));
					if (result.isEmpty() && method.getAnnotation(Optional.class)!= null){
						result.add(new EmptyUiObject());
					}
				} else {
					result = ((UiPart) host).findElements(by);
				}
			} catch (NoSuchElementException|TimeoutException e){
				if (method.getAnnotation(Optional.class) != null){
					result.add(new EmptyUiObject());
				} else {
					Throwables.propagate(e);
				}
			}


			ConfigLoader.config().setProperty("webdriver.visibleOnly",visibleOnly);
			FilterableSearchContext.unsetWait();

			if (result.size() < 1){
				throw new NoSuchElementException("WebElement identified " + by+ " not found");
			}
			if (method.getAnnotation(Covered.class) != null && method.getAnnotation(Covered.class).lookForVisibleParent()){
				ConfigLoader.config().setProperty("webdriver.visibleOnly",false);
				FluentIterable<WebElement> hidden = FluentIterable.from(result);
				while (hidden.firstMatch(new Predicate<WebElement>(){

					public boolean apply(WebElement input) {
						//						boolean vis = input.isDisplayed();
						boolean click = input.isEnabled();
						Point loc  = input.getLocation();

						return !((click && loc.getX() >0 && loc.getY()>0));
					}}).isPresent()){
					hidden = FluentIterable.from(result).transform(new Function<WebElement, WebElement>() {

						public WebElement apply(WebElement input) {
							return input.findElement(org.openqa.selenium.By.xpath(".."));
						}
					});
				}
				ConfigLoader.config().setProperty("webdriver.visibleOnly",visibleOnly);
				result = hidden.toList();
			}

			return adjustResults(result, method.getGenericReturnType());
		} else {
			Class<?>[] classes = null;
			if (args != null && args.length > 0){
				classes = new Class[args.length];
				for (int i=0;i<args.length;i++){
					classes[i] = args[i].getClass();
				}
			}
			try {
				return method.invoke(ui, args);
			} catch (InvocationTargetException e){
				throw e.getTargetException();
			}
		}
		} catch (Throwable t){
			ErrorHandlingService.handle(t,host,method,args);
			Throwables.propagate(t);
			return null;
		}
		

	}





}
