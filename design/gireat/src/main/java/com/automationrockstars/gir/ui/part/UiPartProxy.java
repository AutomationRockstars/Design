package com.automationrockstars.gir.ui.part;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Proxy;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.Point;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.FluentWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.automationrockstars.base.ConfigLoader;
import com.automationrockstars.design.gir.webdriver.FilterableSearchContext;
import com.automationrockstars.design.gir.webdriver.UiObject;
import com.automationrockstars.gir.ui.Covered;
import com.automationrockstars.gir.ui.FindBy;
import com.automationrockstars.gir.ui.FindByAugmenter;
import com.automationrockstars.gir.ui.MinimumElements;
import com.automationrockstars.gir.ui.Name;
import com.automationrockstars.gir.ui.Optional;
import com.automationrockstars.gir.ui.UiPart;
import com.automationrockstars.gir.ui.UiParts;
import com.automationrockstars.gir.ui.WebElementDecorator;
import com.automationrockstars.gir.ui.WithDecorators;
import com.automationrockstars.gir.ui.WithFindByAugmenter;
import com.automationrockstars.gir.ui.context.Image;
import com.automationrockstars.gir.ui.context.SearchContextService;
import com.google.common.base.Function;
import com.google.common.base.MoreObjects;
import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.google.common.base.Stopwatch;
import com.google.common.base.Strings;
import com.google.common.base.Throwables;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

import ru.yandex.qatools.htmlelements.annotations.Timeout;

public class UiPartProxy implements InvocationHandler{

	private static final Logger LOG = LoggerFactory.getLogger(UiPart.class);

	private final WebUiPartDelegate ui;
	public UiPartProxy(Class<? extends UiPart> generic) {
		ui = new WebUiPartDelegate(generic);
	}

	public UiPartProxy(Class<? extends UiPart> generic, UiObject toWrap) {
		ui = new WebUiPartDelegate(generic,toWrap);
	}
	private static boolean isCustom(Method method){
		Class<?> declaringClass = method.getDeclaringClass();
		List<Class<?>> nativeMethodOwners = Lists.newArrayList(UiPart.class.getInterfaces());
		nativeMethodOwners.add(UiPart.class);
		nativeMethodOwners.add(Object.class);
		nativeMethodOwners.add(WebUiPartDelegate.class);
		return ! nativeMethodOwners.contains(declaringClass);
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

	private static WebElement decorate(WebElement initial, Class<? extends WebElementDecorator>... decorators){
		WebElement result = initial;
		if (decorators != null){
			for (Class<? extends WebElementDecorator> decorator : Lists.newArrayList(decorators)){

				try {
					result = decorator.getConstructor(WebElement.class).newInstance(result);
				} catch (NoSuchMethodException  e) {
					LOG.error("Decoratoe {} does not have constructor acceptin WebElement. Ignoring",decorator);
				}catch (InstantiationException | IllegalAccessException | IllegalArgumentException
						| InvocationTargetException | SecurityException e) {
					LOG.error("Cannot decorate {} with {} due to {}",initial,decorator,e);

				}
			}
		}
		return result;
	}

	@SuppressWarnings("unchecked")
	private static <T> T convert(WebElement initial,final Class<T> wanted,Class<? extends WebElementDecorator>... decorators ){
		initial = decorate(initial, decorators);
		if (UiPart.class.isAssignableFrom(wanted)){
			Class<? extends UiPart> resulting = (Class<? extends UiPart>) wanted;
			UiObject toWrap = null;
			if (initial instanceof UiObject){
				toWrap = (UiObject) initial;
			} else {
				toWrap = new UiObject(initial, UiParts.buildBy(resulting));
			}
			return (T) Proxy.newProxyInstance(wanted.getClassLoader(), 
					new Class[] {resulting}, new UiPartProxy(resulting,toWrap));
		}
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
	private static Object adjustResults(final List<WebElement> result,final Type type, final Class... decorators){
		Preconditions.checkState(result.size() > 0, "WebElement not found");
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
						return convert(input,collectionOf,decorators);
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
			return convert(result.get(0),wanted,decorators);
		}
	}

	private List<WebElement> search(UiPart host, org.openqa.selenium.By by, int timeout, int minimumSize){
		List<WebElement> result;
		if (timeout >=0 || minimumSize > 0){
			result = (host).delay()
					.withTimeout(timeout, TimeUnit.SECONDS)
					.until(UiParts.allVisible(by,minimumSize));
		} else {
			result = ((UiPart) host).findElements(by);
		}
		LOG.trace("Required size {} actual {}",minimumSize,result.size());
		return result;
	}
	
	private org.openqa.selenium.By byBuilder(Object host, Class<? extends UiPart> uiPartClass){
		if (uiPartClass.getAnnotation(WithFindByAugmenter.class) != null){
			return FindByAugmenters.instance(uiPartClass.getAnnotation(WithFindByAugmenter.class).value()).augment(uiPartOf(host),uiPartClass.getAnnotation(FindBy.class));
		} else return UiParts.buildBy(uiPartClass);
	}
	
	private org.openqa.selenium.By byBuilder(Object host, Method uiPartChild){
		if (uiPartChild.getAnnotation(WithFindByAugmenter.class) != null){
			return FindByAugmenters.instance(uiPartChild.getAnnotation(WithFindByAugmenter.class).value()).augment(uiPartOf(host),uiPartChild.getAnnotation(FindBy.class));
		} else return UiParts.buildBy(uiPartChild);
	}
	
	@SuppressWarnings("unchecked")
	private Object invokeCustomMethod(Object host, Method method, Object[] args) throws Throwable {
		LOG.info("Working on {} inside {}",MoreObjects.firstNonNull((method.getAnnotation(Name.class)==null)?null:method.getAnnotation(Name.class).value(), method.getName()),host);
		Preconditions.checkArgument(args == null || args.length == 0,"UiPart method cannot accept arguments");
		final Class<?> wantedResult = method.getReturnType();
		org.openqa.selenium.By by = null;

		if (UiPart.class.isAssignableFrom(method.getReturnType())){
			ui.initialPageSetUp();
			Class<? extends UiPart> resultClass = (Class<? extends UiPart>) method.getReturnType();
			List<WebElement> result = Lists.newArrayList((WebElement)UiParts.get(resultClass));
			return adjustResults(result, method.getReturnType(), decorators(uiPartOf(host)));
		} else if (Iterable.class.isAssignableFrom(wantedResult) || wantedResult.isArray()){
			if (method.getGenericReturnType() instanceof ParameterizedType){
				final Class<?> collectionOf = (Class<?>) ((ParameterizedType)method.getGenericReturnType()).getActualTypeArguments()[0];
				if (UiPart.class.isAssignableFrom(collectionOf)){
					ui.initialPageSetUp();
					by = byBuilder(host, (Class<? extends UiPart>)collectionOf);
				}
			}
		}
		List<WebElement> result = Lists.newArrayList();
		if (by == null){
			by = byBuilder(host, method);
		}
		final int timeout = calculateTimeout(method);
		final int minimumSize = minimumSize(method);
		LOG.debug("Using timeout {} to wait for at least {} elements",timeout,minimumSize);
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
			if (isImage(method)){
				result = searchByImage(host,by,timeout,minimumSize);
			} else {
				result = search((UiPart)host,by, timeout, minimumSize);
			}
			if (result.isEmpty() && method.getAnnotation(Optional.class)!= null){
				result.add(new EmptyUiObject());
			}
		} catch (NoSuchElementException|TimeoutException e){
			if (method.getAnnotation(Optional.class) != null){
				result.add(new EmptyUiObject());
			} else {
				LOG.error("Error on {} inside {}: {}",MoreObjects.firstNonNull((method.getAnnotation(Name.class)==null)?null:method.getAnnotation(Name.class).value(), method.getName()),host,e.toString());
				Throwables.propagate(e);
			}
		} finally {
			ConfigLoader.config().setProperty("webdriver.visibleOnly",visibleOnly);
			FilterableSearchContext.unsetWait();
		}
		if (method.getAnnotation(Covered.class) != null && method.getAnnotation(Covered.class).lookForVisibleParent()){
			result = findVisibleParent(result,visibleOnly);
		} else if (method.getAnnotation(Covered.class) == null)	{
			result = Lists.newArrayList(Iterables.filter(result,UiParts.visible()));
		}
		if (result.size() < 1){
			LOG.error("Error on {} inside {}: NoSuchElement",MoreObjects.firstNonNull((method.getAnnotation(Name.class)==null)?null:method.getAnnotation(Name.class).value(), method.getName()),host);
			throw new NoSuchElementException("WebElement identified " + by+ " not found");
		}

		return adjustResults(setNames(result,method), method.getGenericReturnType(),decorators(uiPartOf(host)));

	}

	private List<WebElement> searchByImage(Object host, final By by, int timeout, final int minimumSize) {
		return new FluentWait<SearchContext>(SearchContextService.provideForImage())
				.withTimeout(timeout,TimeUnit.SECONDS)
				.withMessage(String.format("Element identified %s not found", by))
				.until(new Function<SearchContext,List<WebElement>>() {
					@Override
					public List<WebElement> apply(SearchContext input) {
						List<WebElement> result = input.findElements(by);
						if (result.size() < minimumSize){
							result = null;
						}
						return result;
					}
				});
				
		
	}

	private boolean isImage(Method method) {
		return method.getAnnotation(Image.class) != null;
	}

	private List<WebElement> setNames(List<WebElement> elements,final Method method){
		String preName = method.getName();
		if (method.getAnnotation(Name.class) != null){
			preName = method.getAnnotation(Name.class).value();
		} else if (method.getAnnotation(ru.yandex.qatools.htmlelements.annotations.Name.class) != null){
			preName = method.getAnnotation(ru.yandex.qatools.htmlelements.annotations.Name.class).value();
		}
		final String name = preName;
		return Lists.newArrayList(Iterables.transform(elements, new Function<WebElement,WebElement>(){
			@Override
			public WebElement apply(WebElement input) {
				WebElement result = input;
				boolean nameSet = false;
				com.google.common.base.Optional<Method> setName = Iterables.tryFind(Lists.newArrayList(input.getClass().getMethods()), new Predicate<Method>(){
					@Override
					public boolean apply(Method input) {
						return input.getName().equals("setName") && input.getParameterTypes().length == 1 && input.getParameterTypes()[0].equals(String.class);
					}});
				if (setName.isPresent()){
					try {
						setName.get().invoke(result, name);
						nameSet = true;
					} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
					}
				}
				if (! nameSet){
					result = UiObject.wrap(input, UiParts.buildBy(method),name);
				}
				return result;
			}}));
	}
	private List<WebElement> findVisibleParent(final List<WebElement> initial,final boolean visibleOnly){
		ConfigLoader.config().setProperty("webdriver.visibleOnly",false);
		List<WebElement> previous = initial;
		FluentIterable<WebElement> hidden = FluentIterable.from(previous);

		while (hidden.firstMatch(new Predicate<WebElement>(){
			public boolean apply(WebElement input) {
				//						boolean vis = input.isDisplayed();
				boolean click = input.isEnabled();
				Point loc  = input.getLocation();
				if (input.getTagName().equalsIgnoreCase("body")){
					throw new NoSuchElementException(String.format("Usable element covering %s cannot be found", input));
				}
				return !((click && loc.getX() >=0 && loc.getY()>=0));
			}}).isPresent()){
			hidden = FluentIterable.from(previous).transform(new Function<WebElement, WebElement>() {

				public WebElement apply(WebElement input) {
					return input.findElement(org.openqa.selenium.By.xpath(".."));
				}
			});
			previous = Lists.newArrayList(hidden.toList());
		}
		ConfigLoader.config().setProperty("webdriver.visibleOnly",visibleOnly);
		return hidden.toList();
	}

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
			ErrorHandlingService.handle(t,host,method,args);
			Throwables.propagate(t);
			return null;
		} finally {
			if (method.getDeclaringClass() != Object.class){
				MetricsService.finished(host,method,args,stopwatch.elapsed(TimeUnit.NANOSECONDS));
			}
		}


	}

	private Class<? extends WebElementDecorator>[] decorators(Class<? extends UiPart> hostClass){
		Class<? extends WebElementDecorator>[] result = null;
		if ( hostClass.getAnnotation(WithDecorators.class) != null)
			result =  hostClass.getAnnotation(WithDecorators.class).value();
		return result;
	}

	@SuppressWarnings("unchecked")
	private Class<? extends UiPart> uiPartOf(Object host) {
		if (host == null){
			return null;
		}
		return (Class<? extends UiPart>) Iterables.find(Lists.newArrayList(host.getClass().getInterfaces()),new Predicate<Class>() {

			@Override
			public boolean apply(Class input) {
				return UiPart.class.isAssignableFrom(input);
			}
		});
	}
}
