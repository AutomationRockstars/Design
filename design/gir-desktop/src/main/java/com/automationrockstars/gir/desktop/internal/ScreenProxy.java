package com.automationrockstars.gir.desktop.internal;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.automationrockstars.base.ConfigLoader;
import com.automationrockstars.design.gir.webdriver.FilterableSearchContext;
import com.automationrockstars.gir.desktop.ExtendedUiPart;
import com.automationrockstars.gir.desktop.FindByImage;
import com.automationrockstars.gir.desktop.ImageSearchContext;
import com.automationrockstars.gir.desktop.ImageUiObject;
import com.automationrockstars.gir.ui.MinimumElements;
import com.automationrockstars.gir.ui.Name;
import com.automationrockstars.gir.ui.Optional;
import com.automationrockstars.gir.ui.UiPart;
import com.automationrockstars.gir.ui.part.ErrorHandlingService;
import com.automationrockstars.gir.ui.part.MetricsService;
import com.google.common.base.Function;
import com.google.common.base.MoreObjects;
import com.google.common.base.Preconditions;
import com.google.common.base.Stopwatch;
import com.google.common.base.Throwables;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;

import ru.yandex.qatools.htmlelements.annotations.Timeout;

public class ScreenProxy implements InvocationHandler{

	private final ExtendedUiPart ui;
	public ScreenProxy(Class<? extends ExtendedUiPart> part) {
		ui = new ScreenProxyDelegate(part);
	}

	@Override
	public Object invoke(Object host, Method method, Object[] args) throws Throwable {
		Stopwatch stopwatch = Stopwatch.createStarted();	
		try {
			if (isCustom(method)){
				return invokeCustomMethod(host, method, args);
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
//			ErrorHandlingService.handle(t,host,method,args);
			Throwables.propagate(t);
			return null;
		} finally {
			if (method.getDeclaringClass() != Object.class){
				MetricsService.finished(host,method,args,stopwatch.elapsed(TimeUnit.NANOSECONDS));
			}
		}
	}
	private static Logger LOG = LoggerFactory.getLogger(ScreenProxy.class);
	
	private static ImageSearchContext searchContext(Object host){
		ImageSearchContext result = SikuliDriver.driver();
		if (host instanceof ScreenProxyDelegate){
			if (((ScreenProxyDelegate)host).getWrappedElement() != null){
				result = (ImageSearchContext) ((ScreenProxyDelegate)host).getWrappedElement();
			}
		}
		return result;
	}
	
	private Object invokeCustomMethod(Object host, Method method, Object[] args) throws Throwable {
		LOG.info("Working on {} inside {}",MoreObjects.firstNonNull((method.getAnnotation(Name.class)==null)?null:method.getAnnotation(Name.class).value(), method.getName()),host);
		Preconditions.checkArgument(args == null || args.length == 0,"UiPart method cannot accept arguments");
		final Class<? extends WebElement> wantedResult = (Class<? extends WebElement>) method.getReturnType();
		final Type wantedType = method.getGenericReturnType();
		String imagePath = method.getAnnotation(FindByImage.class).value();
		final int timeout = calculateTimeout(method);
		final int minimumSize = minimumSize(method);
		ImageSearchContext context = searchContext(host);
		Object result = null;
		if (Iterable.class.isAssignableFrom(wantedResult) || Iterator.class.isAssignableFrom(wantedResult)){
			result = convertAll(context.findElements(imagePath),wantedType);
		} else {
			result = convert(context.findElement(imagePath),wantedResult);
		}
		return result;
	}

	private static Object convertAll(Iterator<ImageUiObject> elements, Type wantedResult){
		Class<?> collectionOf = ImageUiObject.class;
		if (wantedResult instanceof ParameterizedType){
			collectionOf = (Class<?>) ((ParameterizedType)wantedResult).getActualTypeArguments()[0];
		}
		Class wantedAllType = (Class<?>) wantedResult;
		final Class wantedType = collectionOf;
		if (Iterator.class.equals(wantedResult)){
			return Iterators.transform(elements, new Function<ImageUiObject,Object>() {
				@Override
				public Object apply(ImageUiObject input) {
					return convert(input, wantedType);
				}
			});
		} else {
			FluentIterable<?> results = FluentIterable.from(Lists.newArrayList(elements)).transform(new Function<ImageUiObject, Object>() {
				@Override
				public Object apply(ImageUiObject input) {
					return convert(input, wantedType);
				}
			});
			if (wantedAllType.isArray()){
				return results.toArray(wantedType);
			} else if (List.class.isAssignableFrom(wantedAllType)){
				return results.toList();
			} else return results;
			
		}
	}
	
	private static <T extends WebElement> T convert(ImageUiObject element, Class<T> wantedResult){
			return (T) element;
	}
	
	
	
	private static boolean isCustom(Method method){
		Class<?> declaringClass = method.getDeclaringClass();
		List<Class<?>> nativeMethodOwners = Lists.newArrayList(ExtendedUiPart.class.getInterfaces());
		for (Class c : UiPart.class.getInterfaces()){
			nativeMethodOwners.add(c);	
		}
		nativeMethodOwners.add(UiPart.class);
		nativeMethodOwners.add(Object.class);
		nativeMethodOwners.add(ExtendedUiPart.class);
		nativeMethodOwners.add(ScreenProxyDelegate.class);		
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
			} else if (tm.getAnnotation(com.automationrockstars.gir.ui.Timeout.class)!=null){ 
				return tm.getAnnotation(com.automationrockstars.gir.ui.Timeout.class).value();
			}else {
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
	
	private static int minimumSize(Object target){
		int result = 0;
		if (target instanceof Method){
			Method m = (Method) target; 
			if (m.getAnnotation(MinimumElements.class) != null){
				result = m.getAnnotation(MinimumElements.class).value();
			}
		} else if (target instanceof Field){
			Field f = (Field) target;
			if (f.getAnnotation(MinimumElements.class) != null){
				result = f.getAnnotation(MinimumElements.class).value();
			}
		}
		return result;
	}
}
