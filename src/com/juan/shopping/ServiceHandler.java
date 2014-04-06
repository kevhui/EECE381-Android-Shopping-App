package com.juan.shopping;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

//This class was based on code developed by Ravi Tamada
//http://www.androidhive.info/2012/01/android-json-parsing-tutorial/

public class ServiceHandler {

	static String response = null;

	public ServiceHandler() {

	}

	/**
	 * Making service call
	 * 
	 * @url - url to make request
	 * */
	public String makeServiceCall(String url) {
		return this.makeServiceCall(url, null);
	}

	/**
	 * Making service call
	 * 
	 * @url - url to make request
	 * @params - http request params
	 * */
	public String makeServiceCall(String url,
			List<NameValuePair> params) {
		try {
			// http client
			DefaultHttpClient httpClient = new DefaultHttpClient();
			HttpEntity httpEntity = null;
			HttpResponse httpResponse = null;

			// appending params to url
			if (params != null) {
				String paramString = URLEncodedUtils.format(params, "utf-8");
				url += "?" + paramString;
			}
			HttpGet httpGet = new HttpGet(url);

			httpResponse = httpClient.execute(httpGet);

			httpEntity = httpResponse.getEntity();
			response = EntityUtils.toString(httpEntity);

		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return response;

	}
}