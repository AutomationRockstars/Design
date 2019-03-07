/*******************************************************************************
 * Copyright (c) 2015, 2016 Automation RockStars Ltd.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Apache License v2.0
 * which accompanies this distribution, and is available at
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Contributors:
 *     Automation RockStars - initial API and implementation
 *******************************************************************************/
package com.automationrockstars.design.gir.webdriver;

import com.automationrockstars.base.ConfigLoader;
import com.automationrockstars.bmo.AllureStoryReporter;
import com.automationrockstars.bmo.GenericAllureStoryReporter;
import com.automationrockstars.design.gir.webdriver.plugin.UiDriverPluginService;
import com.automationrockstars.design.gir.webdriver.plugin.UiObjectActionPluginService;
import com.automationrockstars.design.gir.webdriver.plugin.UiObjectFindPluginService;
import com.automationrockstars.design.gir.webdriver.plugin.UiObjectInfoPluginService;
import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.base.Strings;
import com.google.common.collect.*;
import org.apache.commons.collections.list.UnmodifiableList;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.openqa.selenium.*;
import org.openqa.selenium.Platform;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.internal.WrapsDriver;
import org.openqa.selenium.remote.BrowserType;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.*;
import java.util.List;
import java.util.concurrent.ConcurrentMap;

import static com.automationrockstars.design.gir.webdriver.plugin.UiObjectFindPluginService.findPlugins;


public class DriverFactory {

    public static final String WEBDRIVER_SESSION = "webdriver.session";
    public static final String DEFAULT_AUTO_DOWNLOAD = "application/octet-stream,application/x-gzip,application/gzip,application/zip,application/pdf,application/vnd.cups-pdf";
    public static final ConcurrentMap<WebDriver, WebDriver> wdsInstances = Maps.newConcurrentMap();
    private static final ThreadLocal<WebDriver> instances = new InheritableThreadLocal<>();
    private static final List<WebDriver> activeDrivers = Lists.newCopyOnWriteArrayList();

    ;
    private static final Logger log = LoggerFactory.getLogger(DriverFactory.class);

    private static final ThreadLocal<String> url = new ThreadLocal<>();
    private static final String GRID_URL = ConfigLoader.config().getString("grid.url", null);
    private static final String MATRIX_PROP = "webdriver.matrix.browsers";
    private static final String BROWSER_PROP = "webdriver.browser";
    private static final String DEF_BROWSER_PROP = "webdriver.browser.default";
    private static final String DEF_BROWSER = "phantomjs";
    private static Capabilities capabilities;
    private static WebDriver DISPLAY_BROWSER = null;
    private static PeekingIterator<String> matrix = null;
    private static boolean pluginInitialized = false;
    private static ThreadLocal<WdsProvider> providers = new ThreadLocal<>();
    private static final ThreadLocal<String> browser = new InheritableThreadLocal<String>() {
        protected String initialValue() {
            String browser = DEF_BROWSER;
            if (!isWds()) {
                if (browserQueue().hasNext()) {
                    browser = browserQueue().next();
                } else {
                    if (ConfigLoader.config().containsKey("webdriver.browser")) {
                        browser = ConfigLoader.config().getString("webdriver.browser");
                    }
                }
            }
            return browser;
        }


    };

    private static final WebDriver instance() {
        return instances.get();
    }

    public static void url(String url) {
        DriverFactory.url.set(url);
    }

    public static String url() {
        return url.get();
    }

    public static final WebDriver getUnwrappedDriver() {
        WebDriver toUnwrap = null;
        if (isWds()) {
            toUnwrap = wdsInstance();
        } else {
            if (instance() == null) {
                getDriver();
            }
            toUnwrap = instance();
        }
        return unwrap(toUnwrap);
    }

    public static WebDriver unwrap(WebDriver wrapped) {
        if (WrapsDriver.class.isAssignableFrom(wrapped.getClass())) {
            return unwrap(((WrapsDriver) wrapped).getWrappedDriver());
        } else return wrapped;
    }

    public static boolean canScreenshot() {
        return instance() != null || providers.get() != null;
    }

    public static byte[] getScreenshot() {
        return ((RemoteWebDriver) getUnwrappedDriver()).getScreenshotAs(OutputType.BYTES);
    }

    public static String getScreenshotAsBase64() {
        return ((RemoteWebDriver) getUnwrappedDriver()).getScreenshotAs(OutputType.BASE64);
    }

    public static void displayScreenshotFile() {
        try {
            File screenshot = getScreenshotFile();
            if (FileUtils.waitFor(screenshot, 1)) {
                log.info("Screenshot file available {}", screenshot.getAbsolutePath());
                Desktop.getDesktop().open(screenshot);
            } else {
                log.warn("File {} not created", screenshot);
            }
        } catch (IOException e) {
            log.error("Cannot display screenshot due to", e);
        }
    }

    public static void displayScreenshotBrowser() {
        if (DISPLAY_BROWSER == null) {
            DISPLAY_BROWSER = new ChromeDriver();
        }
        if (DISPLAY_BROWSER != null) {
            DISPLAY_BROWSER.get("data:image/gif;base64," + getScreenshotAsBase64());
        }
    }

    public static File getScreenshotFile() {
        if (TakesScreenshot.class.isAssignableFrom(getUnwrappedDriver().getClass())) {
            return ((TakesScreenshot) getUnwrappedDriver()).getScreenshotAs(OutputType.FILE);
        } else return new File(UUID.randomUUID().toString());
    }

    public static void setCapabilities(Capabilities capabilities) {
        DriverFactory.capabilities = capabilities;
    }

    public static Actions actions() {
        return new Actions(getUnwrappedDriver());
    }

    private static final boolean isGridAvailable(String gridUrl) {
        boolean result = true;

        CloseableHttpResponse gridResponse;
        try {
            CloseableHttpClient cl = HttpClients.createDefault();
            gridResponse = cl.execute(new HttpGet(new URI(gridUrl)));

            if (gridResponse.getStatusLine().getStatusCode() == 404) {
                log.warn("Response from contacting grid {}", IOUtils.toString(gridResponse.getEntity().getContent()));
                result = false;
            }
            gridResponse.close();
            cl.close();
        } catch (Throwable e) {
            result = false;
            log.error("Selenium grid not available due to {}", e.getMessage());
        }

        return result;
    }

    private static boolean checkGridExtras(String gridUrl, RemoteWebDriver driver) {
        String gridExtras = GridUtils.getNodeExtras(gridUrl, driver);
        if (gridExtras == null) {
            log.info("No grid extras foud");
            return false;
        } else {
            log.info("Grid extras available at {}", gridExtras);
            return true;
        }

    }

    private static synchronized Iterator<String> browserQueue() {
        while (matrix == null || !matrix.hasNext() || matrix.peek() == null) {
            matrix = browserMatrix();
        }
        if (!pluginInitialized) {
            UiObjectFindPluginService.findPlugins();
            UiObjectActionPluginService.actionPlugins();
            UiObjectInfoPluginService.infoPlugins();
            UiDriverPluginService.driverPlugins();
            pluginInitialized = true;
        }

        return matrix;
    }

    public static synchronized PeekingIterator<String> browserMatrix() {
        PeekingIterator<String> result;
        if (ConfigLoader.config().containsKey(MATRIX_PROP)) {
            result = Iterators.peekingIterator(Iterators.forArray(ConfigLoader.config().getStringArray(MATRIX_PROP)));
        } else {
            String bName = ConfigLoader.config().getString(BROWSER_PROP, ConfigLoader.config().getString(DEF_BROWSER_PROP, DEF_BROWSER));
            if (bName.equalsIgnoreCase("ie")) {
                bName = BrowserType.IE;
                log.info("Using {} for browser", bName);
            }
            result = Iterators.peekingIterator(Iterators.cycle(bName));
        }
        return result;
    }

    public static void setBrowser(String browser) {
        DriverFactory.browser.set(browser);
    }

    private static String storyName() {
        String story = null;
        try {
            if (!Strings.isNullOrEmpty(GenericAllureStoryReporter.storyName())) {
                story = GenericAllureStoryReporter.storyName();
            } else if (!Strings.isNullOrEmpty(AllureStoryReporter.storyName())) {
                story = AllureStoryReporter.storyName();
            }
        } catch (Throwable t) {

        }
        return story;
    }

    private static String scenarioName() {
        String scenario = null;
        try {
            if (!Strings.isNullOrEmpty(GenericAllureStoryReporter.storyName())) {
                scenario = GenericAllureStoryReporter.scenarioName();
            } else if (!Strings.isNullOrEmpty(AllureStoryReporter.storyName())) {
                scenario = AllureStoryReporter.scenarioName();
            }
        } catch (Throwable t) {

        }
        return scenario;
    }

    private static synchronized void enableGridExtras(RemoteWebDriver driver) {
        if (checkGridExtras(GRID_URL, driver)) {
            String videoLink = String.format("%s/download_video/%s.mp4", GridUtils.getNodeExtras(GRID_URL, driver), driver.getSessionId().toString()).replace("//download","/download");
            ConfigLoader.config().addProperty("webdriver.video", videoLink);
            List<Object> videos = ConfigLoader.config().getList("webdriver.videos", new ArrayList<Map<String, String>>());
            String story = String.format("%s->%s::", storyName(), scenarioName());

            if (Strings.isNullOrEmpty(story)) {
                story = "recording " + driver.getSessionId();
            }

            String title = "video_" + story;
            int videoRepetition = 0;
            String titlePostfix = "";

            Optional<Object> titleAlreadyThere = Optional.absent();
            do {
                if (videoRepetition > 0) {
                    titlePostfix = String.format("_%s", videoRepetition);
                }
                final String titleBase = title + titlePostfix;
                titleAlreadyThere = FluentIterable.from(videos).firstMatch(new Predicate<Object>() {

                    @SuppressWarnings("unchecked")
                    @Override
                    public boolean apply(Object input) {
                        return ((Map<String, String>) input).keySet().contains(titleBase);
                    }
                });
                if (!titleAlreadyThere.isPresent()) {
                    title = titleBase;
                } else {
                    videoRepetition++;
                }
            } while (titleAlreadyThere.isPresent());
            log.info("Session for story {}", title);
            videos.add(Collections.singletonMap(title, videoLink));
            ConfigLoader.config().setProperty("webdriver.videos", videos);
        }
    }

    private static WebDriver createRemoteDriver() {
        String name = "";
        if (ConfigLoader.config().containsKey("noui")) {
            name = HeadlessWebDriver.name();
            browser.set(name);
        }

        log.info("Creating browser for {}", browser.get());
        try {
            name = browser.get().toLowerCase();
            if (name.equalsIgnoreCase("ie") || name.equals("internet_explorer") || name.equals("internet explorer")) {
                name = BrowserType.IE;
            } else if (name.equals("edge") || name.toLowerCase().equals("microsoftedge")) {
                name = BrowserType.EDGE;
            }
            if (Strings.isNullOrEmpty(GRID_URL) || !isGridAvailable(GRID_URL)) {
                log.info("Grid not detected");
                if (name.equals(BrowserType.IE)) {
                    return new InternetExplorerDriver(getCapabilities(BrowserType.IE));
                }
                String browserName = StringUtils.capitalize(name + "Driver");
                WebDriver res = null;
                try {
                    String className = browser.get();
                    if (!browserName.contains(".")) {
                        className = "org.openqa.selenium." + name + "." + browserName;
                    }
                    res = (WebDriver) Class.forName(className).getConstructor(Capabilities.class).newInstance(getCapabilities(name));
                } catch (Exception e) {
                    throw new IllegalStateException(String.format("Browser for name %s failed to start", name), e);
                }
                return res;
            } else {
                log.info("Using Selenium grid at {}", GRID_URL);
                URL gridUrl = new URL(GRID_URL);
                RemoteWebDriver result = new RemoteWebDriver(gridUrl, getCapabilities(name));
                log.info("Driver instance created {}", result);
                log.debug("Test session id {}", result.getSessionId());
                log.info("Executing on node {}", GridUtils.getNode(GRID_URL, result));
                enableGridExtras(result);
                return result;
            }
        } catch (MalformedURLException e) {
            throw new RuntimeException("Cannot connect to grid", e);
        }
    }

    /**
     * Close all drivers created by DriverFactory
     */
    public static synchronized void closeAllDrivers(){
        Lists.newArrayList(activeDrivers).stream().forEach(d-> closeDriver(d));
    }

    /**
     * Close driver that has been created using different method
     * @param driver
     */
    public static synchronized void closeDriver(WebDriver driver) {
        UiDriverPluginService.driverPlugins().beforeCloseDriver(driver);
        if (driver != null) {
            try {
                driver.close();
            } catch (Throwable ignore) {}
            try {
                if (!dontUseQuit()) {
                    driver.quit();
                }
            } catch (Throwable ignore){}
            if (activeDrivers.contains(driver)) {
                activeDrivers.remove(driver);
            }
        }
        UiDriverPluginService.driverPlugins().afterCloseDriver();
    }

    /**
     * Close currently used driver
     */
    public static void closeDriver() {
        closeDriver(instance());
        instances.remove();
    }

    public static List<String> activeSessions() {
        return Lists.newArrayList(Iterables.transform(activeInstances(), new Function<WebDriver, String>() {

            @Override
            public String apply(WebDriver input) {
                return ((RemoteWebDriver) unwrap(input)).getSessionId().toString();
            }
        }));
    }

    public static boolean isSessionOpened(final String session) {
        return Iterables.tryFind(activeInstances(), new Predicate<WebDriver>() {

            @Override
            public boolean apply(WebDriver input) {
                return ((RemoteWebDriver) unwrap(input)).getSessionId().toString().equals(session);
            }
        }).isPresent();
    }

    @SuppressWarnings("unchecked")
    public static List<WebDriver> activeInstances() {
        return UnmodifiableList.decorate(activeDrivers);
    }

    public static void closeSession(final String session) {
        Optional<WebDriver> driver = Iterables.tryFind(activeInstances(), input -> ((RemoteWebDriver) unwrap(input)).getSessionId().toString().equals(session));
        if (driver.isPresent()) {
            closeDriver(driver.get());
        }
    }

    public static void destroy() {
        closeDriver();
        matrix = null;
        browser.remove();
    }

    private static final boolean dontUseQuit() {
        return ConfigLoader.config().getBoolean("webdriver.close.only", false);
    }

    private static String autoDownloadFiles() {
        return ConfigLoader.config().getString("webdriver.autodownload.files", DEFAULT_AUTO_DOWNLOAD);
    }

    private static Capabilities getCapabilities(String driverType) {
        DesiredCapabilities result = new DesiredCapabilities();

        switch (driverType) {
            case BrowserType.FIREFOX:
                result = DesiredCapabilities.firefox();
                FirefoxProfile profile = (FirefoxProfile) ConfigLoader.config().getProperty("firefox.profile");
                if (profile == null) {
                    profile = new FirefoxProfile();
                    if (ConfigLoader.config().getBoolean("webdriver.accept.java", true)) {
                        profile.setPreference("plugin.state.java", 2);
                    }
                    if (ConfigLoader.config().getBoolean("webdriver.accept.ssl", true)) {
                        profile.setAcceptUntrustedCertificates(true);
                        profile.setAssumeUntrustedCertificateIssuer(true);
                    }
                    if (ConfigLoader.config().getBoolean("webdriver.autodownload", true)) {
                        profile.setPreference("browser.download.folderList", 2);
                        profile.setPreference("browser.helperApps.alwaysAsk.force", false);
                        profile.setPreference("browser.download.manager.showWhenStarting", false);
                        profile.setPreference("browser.helperApps.neverAsk.saveToDisk", autoDownloadFiles());
                    }
                }
                result.setCapability(FirefoxDriver.PROFILE, profile);
                result.setCapability("acceptInsecureCerts", true);
                result.setCapability(FirefoxDriver.SystemProperty.DRIVER_USE_MARIONETTE, true);

                break;
            case BrowserType.IE:
                result = DesiredCapabilities.internetExplorer();
                result.setCapability(InternetExplorerDriver.INTRODUCE_FLAKINESS_BY_IGNORING_SECURITY_DOMAINS, true);
                result.setCapability(CapabilityType.ACCEPT_SSL_CERTS, true);
                result.setCapability("ie.validateCookieDocumentType", false);
                break;
            case BrowserType.CHROME:
                result = DesiredCapabilities.chrome();
                ChromeOptions chromOptions = new ChromeOptions();
                //			chromOptions.setExperimentalOption("excludeSwitches",Lists.newArrayList("ignore-certificate-errors"));
                chromOptions.addArguments("chrome.switches", "--disable-extensions");
                result.setCapability(ChromeOptions.CAPABILITY, chromOptions);
                result.setCapability(CapabilityType.ACCEPT_SSL_CERTS, true);
                break;
            case "com.machinepublishers.jbrowserdriver.jbrowserdriver":
                result.merge(HeadlessWebDriver.jDriverCapabilities());
                break;
            default:
                result = new DesiredCapabilities(driverType, "", Platform.ANY);
                break;
        }
        String proxy = ConfigLoader.config().getString("webdriver.proxy");
        result.setCapability(CapabilityType.ACCEPT_SSL_CERTS, true);
        if (!Strings.isNullOrEmpty(proxy)) {
            Proxy p = new Proxy();
            p.setHttpProxy(proxy)
                    .setSslProxy(proxy);
            result.setCapability(CapabilityType.PROXY, p);
            log.info("Using proxy {}", proxy);
        }
        result.setCapability(CapabilityType.ACCEPT_SSL_CERTS, true);
        result.setCapability("acceptInsecureCerts", true);
        return concatenate(result, capabilities);
    }

    private static Capabilities concatenate(Capabilities... toJoin) {
        return new DesiredCapabilities(toJoin);
    }

    private static Thread createDriverCloser(final WebDriver driver) {
        if (ConfigLoader.config().getBoolean("webdriver.dontclose", false)) {
            return new Thread(new Runnable() {

                @Override
                public void run() {
                    log.info("Leaving open");
                }
            });
        } else {
            return new Thread(new Runnable() {

                @Override
                public void run() {
                    closeDriver(driver);
                }
            });
        }
    }

    public static boolean isWrong(WebDriver driver) {
        try {
            driver.getCurrentUrl();
            return false;
        } catch (Exception e) {
            return true;
        }
    }

    private static boolean isWds() {
        return providers.get() != null;
    }

    public static final void setWds(WdsProvider provider) {
        UiDriverPluginService.driverPlugins().beforeInstantiateDriver();
        providers.set(provider);
        UiDriverPluginService.driverPlugins().afterInstantiateDriver(provider.wds());
    }

    private static WebDriver wdsInstance() {
        WebDriver wds = providers.get().wds();
        UiDriverPluginService.driverPlugins().beforeGetDriver();
        if (wdsInstances.get(wds) == null) {
            wdsInstances.put(wds, new VisibleElementFilter(providers.get().wds()));
        }
        UiDriverPluginService.driverPlugins().afterGetDriver(wdsInstances.get(wds));
        return wdsInstances.get(wds);
    }

    public static final WebDriver getDriver() {
        UiDriverPluginService.driverPlugins().beforeGetDriver();
        if (isWds()) return wdsInstance();
        if (instance() == null || isWrong(instance())) {
            UiDriverPluginService.driverPlugins().beforeInstantiateDriver();
            WebDriver dr = createRemoteDriver();
            if (wrap(dr)) {
                dr = new VisibleElementFilter(dr);
            }
            instances.set(dr);
            activeDrivers.add(dr);
            Runtime.getRuntime().addShutdownHook(createDriverCloser(instance()));
            UiDriverPluginService.driverPlugins().afterInstantiateDriver(dr);

        }
        UiDriverPluginService.driverPlugins().afterGetDriver(instance());
        return instance();
    }

    private static boolean wrap(WebDriver driver) {
        if (HasCapabilities.class.isAssignableFrom(driver.getClass())) {
            Capabilities cp = ((HasCapabilities) driver).getCapabilities();
            return !((cp != null) && cp.is("cannot_wrap"));
        } else return true;

    }

    private static boolean hasCapabilities(WebDriver driver, Capabilities capabilities) {
        return ((RemoteWebDriver) unwrap(driver)).getCapabilities().equals(capabilities);
    }

    public static final WebDriver getDriver(Capabilities desiredCapabilities) {
        if (instance() == null || !hasCapabilities(instance(), desiredCapabilities)) {
            setCapabilities(desiredCapabilities);
            instances.set(new VisibleElementFilter(createRemoteDriver()));
        }
        return instance();
    }

    public static final WebDriverWait delay(long timeInSeconds) {
        return new WebDriverWait(getDriver(), timeInSeconds);
    }

    public static final WebDriverWait delay() {
        return delay(ConfigLoader.config().getLong("webdriver.timeouts.implicitlywait", 5));

    }

    public static String browserName() {
        Capabilities caps = ((HasCapabilities) getUnwrappedDriver()).getCapabilities();
        return (caps == null) ? null : caps.getBrowserName();
    }

    public static boolean isIe() {
        return BrowserType.IE.equals(browserName());
    }

    public static boolean isPhantom() {
        return BrowserType.PHANTOMJS.equals(browserName());
    }

    public static boolean isEdge() {
        return BrowserType.EDGE.equals(browserName());
    }

    public static interface WdsProvider {
        WebDriver wds();
    }

    public static class VisibleElementFilter implements HasCapabilities, JavascriptExecutor, WebDriver, WrapsDriver {

        private static final UiObject browser = new UiObject(null, By.tagName("html"), "WebDriver");
        private final WebDriver driver;
        private final SearchContext searchContext;

        public VisibleElementFilter(WebDriver driver) {
            this.driver = driver;
            searchContext = new FilterableSearchContext(driver);
        }

        public void get(String url) {
            driver.get(url);

        }

        public String toString() {
            return String.format("VisibleElementFilter of %s", driver);
        }

        public String getCurrentUrl() {
            return driver.getCurrentUrl();
        }

        public String getTitle() {
            return driver.getTitle();
        }

        public List<WebElement> findElements(By by) {
            findPlugins().beforeFindElement(browser, by);
            List<WebElement> result = UiObject.wrapAll(searchContext.findElements(by), By.tagName("html"), by);
            findPlugins().afterFindElements(browser, by, result);
            return result;
        }

        public WebElement findElement(By by) {
            findPlugins().beforeFindElement(browser, by);
            WebElement result = UiObject.wrap(searchContext.findElement(by), by);
            findPlugins().afterFindElement(browser, by, result);
            return result;
        }

        public String getPageSource() {
            return driver.getPageSource();
        }

        public void close() {
            driver.close();

        }

        public void quit() {
            driver.quit();
        }

        public Set<String> getWindowHandles() {
            return driver.getWindowHandles();
        }

        public String getWindowHandle() {
            return driver.getWindowHandle();
        }

        public TargetLocator switchTo() {
            return driver.switchTo();
        }

        public Navigation navigate() {
            return driver.navigate();
        }

        public Options manage() {
            return driver.manage();
        }

        public WebDriver getWrappedDriver() {
            return driver;
        }

        @Override
        public Object executeScript(String script, Object... args) {
            return ((JavascriptExecutor) driver).executeScript(script, args);
        }

        @Override
        public Object executeAsyncScript(String script, Object... args) {
            return ((JavascriptExecutor) driver).executeAsyncScript(script, args);
        }

        @Override
        public Capabilities getCapabilities() {
            return ((HasCapabilities) driver).getCapabilities();
        }
    }

}
