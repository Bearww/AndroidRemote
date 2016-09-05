package com.test.wu.remotetest;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Map;
import java.util.Scanner;

@SuppressWarnings("deprecation")
public class LinkCloud {
	private static String web_link = "";
	private static String basic_web_link = "";
	private static String web_data = "";
	private static HttpClient conn_cloud = null; 
	private static Scanner scanner;
	private static int json_index;
	static JSONObject json_web_data;
	
	public LinkCloud(String basic_web_link) {
		conn_cloud = new DefaultHttpClient();

		if (basic_web_link == "")
			this.basic_web_link = "http://10.0.2.2/cloud/meeting_cloud/device/";
		else
			this.basic_web_link = basic_web_link;

		json_web_data = new JSONObject();
	}

	public static JSONObject request(String url) throws IOException, JSONException {
		url = basic_web_link + url;
	    HttpPost post = new HttpPost(url);
	    HttpResponse res = conn_cloud.execute(post);
	    post.abort();

	    while (res.getStatusLine().getStatusCode() == 302) {
	    	url = basic_web_link + res.getLastHeader("Location").getValue();  
	    	post = new HttpPost(url);
		    res = conn_cloud.execute(post);
	    	post.abort();
	    }

	    BufferedReader br = new BufferedReader(new InputStreamReader(res.getEntity().getContent(), "utf-8"));
	    post.abort();
	    String data = "";
	    String line = "";
	    while ((line = br.readLine()) != null) {
            data = data + line + '\n';
        }
	    
		json_index = data.indexOf('{');
		try {
			json_web_data = new JSONObject(data.substring(json_index));
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return json_web_data;
	}

	public static JSONObject submitFormPost(Map<String, String> form_data, String url)
	throws ClientProtocolException, IOException {
		url = basic_web_link + url;
	    HttpPost post = new HttpPost(url);
	    
	    ArrayList<NameValuePair> post_form = new ArrayList<NameValuePair>();
	    for(Map.Entry<String, String> entry:form_data.entrySet()) {
	    	if(entry.getKey() != "post_link")
	    	    post_form.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
	    }   
	    post.setEntity(new UrlEncodedFormEntity(post_form, "UTF-8"));

	    HttpResponse res = conn_cloud.execute(post);
	    BufferedReader br = new BufferedReader(new InputStreamReader(res.getEntity().getContent()));
	    
	    post.abort();

	    url = res.getLastHeader("Location").getValue();

		try {
			return request(url);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return null;
	}
}
