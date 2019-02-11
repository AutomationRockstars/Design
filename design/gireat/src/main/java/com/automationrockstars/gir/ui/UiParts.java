/*
 * <!--
 *     Copyright (c) 2015-2019 Automation RockStars Ltd.
 *     All rights reserved. This program and the accompanying materials
 *     are made available under the terms of the Apache License v2.0
 *     which accompanies this distribution, and is available at
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *     Contributors:
 *         Automation RockStars
 *  -->
 */

package com.automationrockstars.gir.ui;

import com.automationrockstars.base.ConfigLoader;
import com.automationrockstars.design.gir.webdriver.DriverFactory;
import com.automationrockstars.design.gir.webdriver.HasLocator;
import com.automationrockstars.design.gir.webdriver.UiObject;
import com.automationrockstars.gir.ui.part.EmptyUiObject;
import com.automationrockstars.gir.ui.part.UiPartProxy;
import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.google.common.base.Throwables;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.internal.WrapsElement;
import org.openqa.selenium.support.AbstractFindByBuilder;
import org.openqa.selenium.support.How;
import org.openqa.selenium.support.pagefactory.ByAll;
import org.openqa.selenium.support.ui.FluentWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.*;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;

public class UiParts {

    private static final Logger LOG = LoggerFactory.getLogger(UiParts.class);
    private static final BackAnnotation backward = new BackAnnotation();
    public static long DEFAULT_DELAY = ConfigLoader.config().getLong("webdriver.uipart.delay", 5);

    @SuppressWarnings("unchecked")
    public static <T extends UiPart> T get(Class<T> part) {
        return (T) Proxy.newProxyInstance(part.getClassLoader(),
                new Class[]{part}, new UiPartProxy(part));
    }

    public static <T extends UiPart> T on(Class<T> uiPart) {
        return get(uiPart);
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    public static Predicate<WebElement> visible() {
        return new Predicate() {
            @Override
            public boolean apply(Object input) {
                try {
                    if (WebElement.class.isAssignableFrom(input.getClass())) {
                        return ((WebElement) input).isDisplayed();
                    } else if (WrapsElement.class.isAssignableFrom(input.getClass())) {
                        return ((WrapsElement) input).getWrappedElement().isDisplayed();
                    } else return false;
                } catch (WebDriverException e) {
                    return false;
                }
            }
        };
    }

    public static Function<SearchContext, WebElement> visible(final org.openqa.selenium.By by) {
        return new Function<SearchContext, WebElement>() {
            public UiObject apply(SearchContext input) {
                return UiObject.wrap(input.findElement(by), by);
            }

            public String toString() {
                return String.format("Element identified by %s visible", by);
            }
        };
    }

    public static Function<SearchContext, WebElement> anyVisible(final org.openqa.selenium.By... bys) {
        return new Function<SearchContext, WebElement>() {
            public UiObject apply(SearchContext input) {
                UiObject result = null;
                for (org.openqa.selenium.By by : bys) {
                    try {
                        result = UiObject.wrap(input.findElement(by), by);
                        break;
                    } catch (Throwable ignore) {
                    }
                }
                return result;
            }

            public String toString() {
                return String.format("Any of elements identified by %s visible", Arrays.toString(bys));
            }

        };
    }

    public static Function<SearchContext, List<WebElement>> allVisible(final org.openqa.selenium.By by, final int minimumSize) {
        return new Function<SearchContext, List<WebElement>>() {
            public List<WebElement> apply(SearchContext input) {
                List<WebElement> result = input.findElements(by);
                if (result.size() >= minimumSize) {
                    org.openqa.selenium.By parentBy = org.openqa.selenium.By.tagName("html");
                    if (HasLocator.class.isAssignableFrom(input.getClass())) {
                        parentBy = ((HasLocator) input).getLocator();
                    }
                    return UiObject.wrapAll(result, parentBy, by);
                } else return null;
            }

            public String toString() {
                return String.format("All of elements identified by %s visible", by);
            }
        };
    }

    public static org.openqa.selenium.By toSeleniumBy(By locator) throws Exception {
        Constructor<? extends org.openqa.selenium.By> constructor = null;
        try {
            constructor = locator.how().getConstructor(String.class);
            return constructor.newInstance((Object[]) locator.using());
        } catch (NoSuchMethodException e) {
            constructor = locator.how().getConstructor(String[].class);
            return constructor.newInstance(new Object[]{locator.using()});
        }


    }

    private static org.openqa.selenium.By transform(boolean any, By... allLocators) {
        org.openqa.selenium.By result = null;
        if (allLocators.length == 1) {
            try {
                result = toSeleniumBy(allLocators[0]);
            } catch (Exception e) {
                Throwables.propagate(e);
            }
        } else {
            org.openqa.selenium.By[] bys = new org.openqa.selenium.By[allLocators.length];
            for (int i = 0; i < allLocators.length; i++) {
                try {
                    bys[i] = toSeleniumBy(allLocators[i]);
                } catch (Exception e) {
                    Throwables.propagate(e);
                }
            }
            if (any) {
                result = new ByAny(bys);
            } else {
                result = new ByAll(bys);
            }
        }
        return result;
    }

    @SuppressWarnings("unchecked")
    private static org.openqa.selenium.By byForUiPart(Method method) {
        Class<?> wantedResult = method.getReturnType();
        Class<? extends UiPart> resultClass = null;
        if (UiPart.class.isAssignableFrom(method.getReturnType())) {
            resultClass = (Class<? extends UiPart>) method.getReturnType();
        } else if (Iterable.class.isAssignableFrom(wantedResult) || wantedResult.isArray()) {
            if (method.getGenericReturnType() instanceof ParameterizedType) {
                final Class<?> collectionOf = (Class<?>) ((ParameterizedType) method.getGenericReturnType()).getActualTypeArguments()[0];
                if (UiPart.class.isAssignableFrom(collectionOf)) {
                    resultClass = (Class<? extends UiPart>) collectionOf;
                }
            }
        } else throw new RuntimeException("Cannot initialize annotation to get locator on " + method);
        return buildBy(resultClass);
    }

    public static org.openqa.selenium.By buildBy(Method method) {
        org.openqa.selenium.By result = null;
        if (method.getAnnotation(Find.class) != null) {
            result = transform(method.getAnnotation(Find.class).any(), method.getAnnotation(Find.class).value());
        } else if (method.getAnnotation(FindBy.class) != null) {
            result = transform(method.getAnnotation(FindBy.class));
        } else if (method.getAnnotation(FindAll.class) != null) {
            result = transform(method.getAnnotation(FindAll.class));
        } else if (method.getAnnotation(org.openqa.selenium.support.FindBy.class) != null) {
            result = transform(method.getAnnotation(org.openqa.selenium.support.FindBy.class));
        } else if (method.getAnnotation(Filter.class) != null) {
            result = org.openqa.selenium.By.xpath(".//*");
        } else {
            result = byForUiPart(method);
        }
        if (method.getAnnotation(Filter.class) != null) {
            result = new FilteredBy(result, method.getAnnotation(Filter.class).value());
        }
        return result;
    }

    public static org.openqa.selenium.By buildBy(Class<? extends UiPart> view) {
        org.openqa.selenium.By result = org.openqa.selenium.By.tagName("html");
        if (view.getAnnotation(Find.class) != null) {
            result = transform(view.getAnnotation(Find.class).any(), view.getAnnotation(Find.class).value());
        } else if (view.getAnnotation(FindBy.class) != null) {
            result = transform(view.getAnnotation(FindBy.class));
        } else if (view.getAnnotation(org.openqa.selenium.support.FindBy.class) != null) {
            result = transform(view.getAnnotation(org.openqa.selenium.support.FindBy.class));
        } else if (view.getAnnotation(org.openqa.selenium.support.FindAll.class) != null) {
            result = transform(view.getAnnotation(org.openqa.selenium.support.FindAll.class));
        } else if (view.getAnnotation(FindAll.class) != null) {
            result = transform(view.getAnnotation(FindAll.class));
        }
        if (view.getAnnotation(Filter.class) != null) {
            result = new FilteredBy(result, view.getAnnotation(Filter.class).value());
        }
        return result;
    }

    private static org.openqa.selenium.By transform(FindAll locator) {
        return backward.buildBy(locator);
    }

    public static org.openqa.selenium.By transform(FindBy locator) {
        return backward.buildBy(locator);
    }

    public static org.openqa.selenium.By transform(org.openqa.selenium.support.FindBy locator) {
        return backward.buildBy(locator);
    }

    public static org.openqa.selenium.By transform(org.openqa.selenium.support.FindAll locator) {
        org.openqa.selenium.By[] subBys = new org.openqa.selenium.By[locator.value().length];
        int i = 0;
        for (org.openqa.selenium.support.FindBy findBy : locator.value()) {
            subBys[i++] = transform(findBy);
        }
        return new org.openqa.selenium.support.pagefactory.ByAll(subBys);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public static Predicate<WebElement> withText(final String text) {
        return new Predicate() {

            public boolean apply(Object input) {
                Optional<Method> getText = FluentIterable.from(Lists.newArrayList(input.getClass().getMethods())).
                        firstMatch(new Predicate<Method>() {
                            public boolean apply(Method input) {
                                return input.getName().contains("getText") && input.getParameterTypes().length == 0;
                            }
                        });
                Preconditions.checkState(getText.isPresent(), "Method getText() cannot be invoked on %s", input.getClass());
                try {
                    String actualText = getText.get().invoke(input, (Object[]) null).toString();
                    return actualText.contains(text);
                } catch (Exception e) {
                    return false;
                }

            }
        };
    }

    public static List<org.openqa.selenium.By> globals() {
        List<org.openqa.selenium.By> result = Lists.newArrayList();

        return result;
    }

    public static Head head() {
        return on(Head.class);
    }

    public static Body body() {
        return on(Body.class);
    }

    @SafeVarargs
    public static <T extends UiPart> T getFirstVisible(final Class<? extends UiPart>... parts) {
        final Map<org.openqa.selenium.By, Class<? extends UiPart>> byToPart = Maps.newHashMap();
        for (Class<? extends UiPart> part : parts) {
            byToPart.put(buildBy(part), part);
        }
        AtomicInteger timeOut = new AtomicInteger();
        Arrays.stream(parts).forEach(part -> {
            if (part.getAnnotation(Timeout.class) != null) {
                timeOut.set(Math.max(part.getAnnotation(Timeout.class).value(), timeOut.get()));
            }
        });
        long actualTimeout = Math.max(DEFAULT_DELAY,timeOut.get());
        LOG.info("Looking for parts {} with timeout {}",Arrays.toString(parts),actualTimeout);
        return new FluentWait<SearchContext>(DriverFactory.getUnwrappedDriver())
                .withTimeout(actualTimeout, TimeUnit.SECONDS)
                .withMessage(String.format("None of UiParts %s found", Arrays.toString(parts)))
                .until(new Function<SearchContext, T>() {
                    @SuppressWarnings("unchecked")
                    public T apply(SearchContext driver) {
                        T result = null;
                        Iterator<org.openqa.selenium.By> bys = byToPart.keySet().iterator();
                        while (result == null && bys.hasNext()) {
                            org.openqa.selenium.By by = bys.next();
                            if (!driver.findElements(by).isEmpty() && get(byToPart.get(by)).isVisible()) {
                                result = (T) get(byToPart.get(by));
                            }
                        }
                        return result;
                    }
                });

    }

    /**
     * Utility to check if element returned from method annotated with {@link com.automationrockstars.gir.ui.Optional} is operable WebElement
     *
     * @param element
     * @return
     */
    public static boolean isEmpty(WebElement element) {
        return EmptyUiObject.isEmpty(element);
    }

    /**
     * Utility to check if element returned from method annotated with {@link com.automationrockstars.gir.ui.Optional} is operable WebElement
     *
     * @param element
     * @return
     */
    public static boolean isEmpty(WrapsElement element) {
        return EmptyUiObject.isEmpty(element.getWrappedElement());
    }

    public static boolean isEmpty(Iterable<?> elements) {
        if (Iterables.isEmpty(elements)) {
            return true;
        } else {
            Object element = elements.iterator().next();
            if (WebElement.class.isAssignableFrom(element.getClass())) {
                return isEmpty((WebElement) element);
            } else if (WrapsElement.class.isAssignableFrom(element.getClass())) {
                return isEmpty((WrapsElement) element);
            } else {
                throw new IllegalAccessError("Iterable does not contain WebElements, but " + element.getClass());
            }
        }
    }

    private static class BackAnnotation extends AbstractFindByBuilder {

        private static org.openqa.selenium.support.FindBy convert(final FindBy locator) {
            return (org.openqa.selenium.support.FindBy) Proxy.newProxyInstance(BackAnnotation.class.getClassLoader(),
                    new Class[]{org.openqa.selenium.support.FindBy.class},
                    new InvocationHandler() {

                        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                            return locator.getClass()
                                    .getMethod(method.getName().replaceAll("org.openqa.selenium.support", "com.automationrockstars.gir.ui")
                                            , method.getParameterTypes())
                                    .invoke(locator, args);
                        }
                    }
            );
        }

        public org.openqa.selenium.By buildIt(Object o, Field f) {
            return null;
        }

        public org.openqa.selenium.By buildBy(FindAll locator) {
            org.openqa.selenium.By[] subBys = new org.openqa.selenium.By[locator.value().length];
            for (int i = 0; i < subBys.length; i++) {
                subBys[i] = buildBy(locator.value()[i]);
            }
            return new org.openqa.selenium.support.pagefactory.ByAll(subBys);

        }

        public org.openqa.selenium.By buildBy(FindBy locator) {
            if (locator.how() != How.UNSET) {
                return super.buildByFromLongFindBy(convert(locator));
            } else {
                return super.buildByFromShortFindBy(convert(locator));
            }
        }

        public org.openqa.selenium.By buildBy(org.openqa.selenium.support.FindBy locator) {
            if (locator.how() != How.UNSET) {
                return super.buildByFromLongFindBy(locator);
            } else {
                return super.buildByFromShortFindBy(locator);
            }
        }


        public org.openqa.selenium.By buildBy() {
            return null;
        }


        public boolean isLookupCached() {
            return false;
        }

    }


}
