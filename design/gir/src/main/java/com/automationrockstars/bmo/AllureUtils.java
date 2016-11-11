package com.automationrockstars.bmo;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.charset.Charset;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Collection;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.automationrockstars.base.ConfigLoader;
import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.io.Files;

import ru.yandex.qatools.allure.report.AllureReportBuilder;
import ru.yandex.qatools.allure.report.AllureReportBuilderException;

public class AllureUtils {
	
	private static final Logger LOG = LoggerFactory.getLogger(AllureUtils.class);
	
	private static Properties populateProperty(String name, Properties initial){
		if (ConfigLoader.config().containsKey(name)){
			initial.setProperty(name, ConfigLoader.config().getString(name));
		}
		return initial;
	}
	private static CloseableHttpClient cl;
	private static void populateVideo(Properties initial, String link, String title){
		try {
			HttpResponse videoResponse = cl.execute(new HttpGet(link));
			if (videoResponse.getStatusLine().getStatusCode() == 200){
				InputStream videoFile = videoResponse.getEntity().getContent();
				Paths.get("target/allure-report/data/").toFile().mkdirs();
				java.nio.file.Files.copy(videoFile, Paths.get("target/allure-report/data/"+title+".mp4"), StandardCopyOption.REPLACE_EXISTING);
				initial.setProperty(title, "<a href=\"data/"+title+".mp4\"><video id=\""+title+"\" width=\"160\" height=\"88\" autoplay=\"true\" controls=\"true\">"+
						"<source src=\"data/"+title+".mp4\" type=\"video/mp4\">"+
						"Your browser does not support HTML5 video.</video></a>");
			}
		} catch (UnsupportedOperationException | IOException e) {
			LOG.error("Video cannot be added {}",e.getMessage());
		}
	}
	private static void populateVideos(Properties initial){	
		
		Set<?> videos = ImmutableSet.copyOf(ConfigLoader.config().getList("webdriver.videos",Lists.newArrayList()));
		if (videos.isEmpty()){
			initial = populateProperty("webdriver.video",initial);
		} else {
			cl = HttpClients.createDefault();
			for (Object video : videos){
				@SuppressWarnings("unchecked")
				Map.Entry<String, String> videoData = ((Map<String, String>) video).entrySet().iterator().next();
				populateVideo(initial, videoData.getValue(), videoData.getKey());
			}
			try {
				cl.close();
			} catch (IOException e) {
			}
		}
	}
	private static void generateProperties(String directory){
		LOG.info("Generating properties for report");
		Properties environmentToShow = new Properties();
		environmentToShow = populateProperty("url", environmentToShow);
		environmentToShow = populateProperty("grid.url", environmentToShow);
		environmentToShow = populateProperty("webdriver.session", environmentToShow);
		populateVideos(environmentToShow);
		LOG.info("Properties ready");
		try {
			environmentToShow.store(Files.newWriter(Paths.get(directory,"environment.properties").toFile(), Charset.defaultCharset()), "execution properties");
			LOG.info("Properties written");
		} catch (IOException e) {
			LOG.debug("Cannot generate properties");
		}
	}

	private static Optional<File> resultsDir = null;

	private static Optional<File> getResultsDir(){

		Optional<File> dire = Optional.absent();
		if (resultsDir == null || ! dire.isPresent()){
			Collection<File> dirs = FileUtils.listFiles(new File(new File("").getAbsolutePath()), TrueFileFilter.INSTANCE, TrueFileFilter.INSTANCE); 
			dire = Iterables.tryFind(dirs, new Predicate<File>() {

				@Override
				public boolean apply(File input) {
					return input.isDirectory() && input.getName().endsWith("allure-results");
				}
			});
			resultsDir = dire;
		}
		return resultsDir;
	}
	private static File reportDir = null;
	private static File getReportDir(){
		if (reportDir == null){
			if (getResultsDir().isPresent())
				reportDir = Paths.get(getResultsDir().get().getParent(),"allure-report").toFile();
		}
		return reportDir;
	}
	public static void cleanPreviousReport(){
		if (getResultsDir().isPresent()){
			try {
				FileUtils.forceDelete(getReportDir());
				FileUtils.forceDelete(getResultsDir().get());
			} catch (IOException e) {
				LOG.warn("Deleting previous results failed");
			}
		}
	}
	public static void generateReport(){
		String userSettings = String.format("%s/.m2/settings.xml", System.getProperty("user.home"));
		if (! Paths.get(userSettings).toFile().canRead()){
			String globalSettings = String.format("%s/conf/settings.xml", ConfigLoader.config().getString("M2_HOME"));
			if (! Paths.get(globalSettings).toFile().canRead()){
				LOG.error("Maven settings are not configured. Report cannot be generated");
			} else {
				try {
					FileUtils.copyFile(Paths.get(globalSettings).toFile(), Paths.get(userSettings).toFile());
				} catch (IOException e) {
					LOG.error("Cannot copy file due to ",e);

				}

			}
		}

		if (getResultsDir().isPresent()){
			generateProperties(getResultsDir().get().getAbsolutePath());
			AllureReportBuilder bl;
			try {
				bl = new AllureReportBuilder("1.4.19", getReportDir());
				bl.processResults(getResultsDir().get());
				bl.unpackFace();
				URI report = Paths.get(getReportDir().getAbsolutePath(),"index.html").toUri();
				LOG.info("Report generated to {}",report);
				if (ConfigLoader.config().containsKey("bdd.open.report")){
					if(Desktop.isDesktopSupported())
					{
						try {
							Desktop.getDesktop().browse(report);
						} catch (IOException e) {
							LOG.warn("Report opening failed",e);
						}
					}
				}
			} catch (AllureReportBuilderException  cantCreateReport) {
				LOG.error("Report is not generated due to ",cantCreateReport);
			}
		}

	}

}
