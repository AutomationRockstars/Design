package com.automationrockstars.bmo;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.Callable;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;

public class DownloadFromSeleniumGrid implements Callable<File>{

	private final String link;
	
	
	private static CloseableHttpClient cl;
	
	CloseableHttpClient client(){
		return cl;
	}
	public DownloadFromSeleniumGrid(String link) {
		this.link = link;
	}
	
	@Override
	public File call() throws Exception {
		// TODO Auto-generated method stub
		return null;
	}
	
	private static boolean canGetVideo(final String link){
		CloseableHttpResponse resp = null;
		try {
			resp = cl.execute(new HttpGet(link));
			if ( resp.getStatusLine().getStatusCode() != 200){
				throw new IllegalArgumentException("Negative response from server " + resp.getStatusLine());
			}
			return true;
		} catch (Throwable t){
			//LOG.debug("Video {} cannot be fetched due to {}",link,t.getMessage());
			return false;
		} finally {
			if (resp!=null){
				try {
					resp.close();
				} catch (IOException ignore) {
				}
			}
		}
	}

}
