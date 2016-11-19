package com.test.wu.remotetest;

import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings("deprecation")
public class LinkCloud {

	public static String INDEX 				= "device_index.php";
	public static String LOGIN 				= "login.php";
	public static String MEMBER_CENTER 	= "employee_center.php";
	public static String MEETING				= "/device/employee/em_meeting_running.php";

	//---------------------------------------------------------------------------------------------------------------------//

	private static String DEFAUL_WEB_LINK = "http://10.0.2.2/cloud/meeting_cloud/";

	private static String BASIC_WEB_LINK = DEFAUL_WEB_LINK;
	private static DefaultHttpClient conn_cloud = new DefaultHttpClient();
	private static int json_index;
	public static JSONObject json_web_data = new JSONObject();
	public static int response_status;
	
	public static Boolean setLink(String basic_link) {
		if (basic_link == "")
			return false;

		BASIC_WEB_LINK = basic_link;
		return true;
	}

	public static JSONObject request(String url) throws IOException, JSONException {
		url = BASIC_WEB_LINK + url;
	    HttpPost post = new HttpPost(url);
		HttpParams httpParameters = new BasicHttpParams();
		// Set the timeout in milliseconds until a connection is established.
		// The default value is zero, that means the timeout is not used.
		int timeoutConnection = 3000;
		HttpConnectionParams.setConnectionTimeout(httpParameters, timeoutConnection);
		conn_cloud.setParams(httpParameters);

		// Set the default socket timeout (SO_TIMEOUT)
		// in milliseconds which is the timeout for waiting for data.
		int timeoutSocket = 5000;
		HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);
	    HttpResponse res = conn_cloud.execute(post);
	    post.abort();

	    while (res.getStatusLine().getStatusCode() == 302) {
	    	url = BASIC_WEB_LINK + res.getLastHeader("Location").getValue();
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
		if(json_index < 0) {
			Log.i("[LC]request", "index invalid " + json_index);
			return null;
		}
		try {
			json_web_data = new JSONObject(data.substring(json_index));
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return json_web_data;
	}

	public static String submitFormPost(Map<String, String> form_data, String url)
	throws IOException {
		url = BASIC_WEB_LINK + url;
	    HttpPost post = new HttpPost(url);
		Log.d("[LC]URL", url);
	    
	    ArrayList<NameValuePair> post_form = new ArrayList<NameValuePair>();
	    for(Map.Entry<String, String> entry:form_data.entrySet()) {
	    	if(!entry.getKey().contains("post_link")) {
				post_form.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
				//Log.d("[LC]q", entry.getKey() + " " + entry.getValue());
			}
	    }   
	    post.setEntity(new UrlEncodedFormEntity(post_form, "UTF-8"));

		//post.getParams().setParameter(CookieSpecPNames.DATE_PATTERNS, Arrays.asList("EEE, d MMM yyyy HH:mm:ss z"));
	    HttpResponse response = conn_cloud.execute(post);

		// Get Cookies
		List<Cookie> cookiejar = conn_cloud.getCookieStore().getCookies();

	    BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
	    
	    post.abort();

		String sResponse;
		String s = "";

		while ((sResponse = reader.readLine()) != null)
			s = s + sResponse + '\n';

		Log.i("[LC]SubmitFormResponse", s);
		response_status = response.getStatusLine().getStatusCode();

		return s;
	}

	public static Map<String, String> getLink(JSONObject json_data)
	throws JSONException {

		if(json_data == null)
			return new HashMap<>();

		Map<String, String> links = new LinkedHashMap();

		String reg_link_name = "";
		String reg_link = "";

		int map_links_ptr = 1;
		int array_length = 1;
		if (!json_data.isNull("link")) {
			JSONObject LinkObject = json_data.getJSONObject("link");
			JSONObject link_set;
			JSONArray link_set_array;
			Iterator link_key = LinkObject.keys();
			Iterator link_set_key;

			while(link_key.hasNext()) {
				reg_link_name = link_key.next().toString();
				//有obj, obj 裏含array
				if (reg_link_name.contains("obj_")) {
					link_set = LinkObject.getJSONObject(reg_link_name);
					link_set_key = link_set.keys();
					for (int j = 0, array_start = 0; j < array_length; j++, array_start = 0) {
						while(link_set_key.hasNext()) {
							reg_link_name = link_set_key.next().toString();			//取得array 名字
							link_set_array = link_set.getJSONArray(reg_link_name);	//透過名字 取得 array
							reg_link = link_set_array.getString(j);					//從array 中取得元素
							if (array_start != 0) {
								links.put(Integer.toString(map_links_ptr), reg_link);
								map_links_ptr++ ;
							}
							if (j == 0) {
								array_length = link_set_array.length(); //得知object 裏面一條array 有多長
							}

							array_start = 1;
						}
						link_set_key = link_set.keys();
					}
				}
				else {
					reg_link = LinkObject.getString(reg_link_name);
					links.put(Integer.toString(map_links_ptr), reg_link);
					map_links_ptr++ ;
				}
			}
		}
		return links;
	}

	public static Map<String, String> getForm(JSONObject json_data)
	throws JSONException {

		if(json_data == null)
			return new HashMap<>();

		Map<String, String> forms = new LinkedHashMap();

		String form_func = "";
		String form_send_to = "";
		String key = "";
		String reg_form_name = "";

		if (!json_data.isNull("form")) {
			JSONObject formarray = json_data.getJSONObject("form");
			JSONObject form;
			JSONObject form_textbox;
			Iterator form_key = formarray.keys();
			int i = 0;
			while(form_key.hasNext()) {
				reg_form_name = form_key.next().toString();			// Ex: login (i = 0)
				form = formarray.getJSONObject(reg_form_name);

				form_func = form.getString("func");
				form_send_to = form.getString("addr");
				forms.put(Integer.toString(i), form_func);				// 0,               login
				forms.put("post_link" + i, form_send_to);				// post_link0, login.php

				form_textbox = form.getJSONObject("form");

				Iterator form_data_key = form_textbox.keys();
				while(form_data_key.hasNext()) {
					key = form_data_key.next().toString();
					forms.put(key + i, form_textbox.getString(key));	// id0,            id0value
				}
				i++;
			}
		}
		return forms;
	}

	public static Map<String, String> getMemberList(JSONObject json_data, String member_name, String ip)
	throws JSONException {

		if(json_data == null)
			return new HashMap<>();

		Map<String, String> member_list = new HashMap();

		String content = "";
		String content_array_name = "";
		String reg_content = "";

		int map_links_ptr = 1;
		int array_length = 1;
		if (!(json_data.isNull("content"))) {
			JSONObject ContentObject = json_data.getJSONObject("content");			//整個大 object
			JSONObject content_object;												//大object 裏面的 小object
			JSONArray content_object_array;											//小object 裏面的 array
			Iterator Content_Key = ContentObject.keys();
			Iterator content_object_key;

			while(Content_Key.hasNext()) {
				content = Content_Key.next().toString();
				//有obj, obj 裏含array
				if (content.contains("obj_meeting_member_list")) {
					content_object = ContentObject.getJSONObject(content);
					content_object_key = content_object.keys();
					for (int j = 0, array_start = 0; j < array_length; j++, array_start = 0) {
						while(content_object_key.hasNext()) {
							content_array_name = content_object_key.next().toString();			//取得array 名字
							content_object_array = content_object.getJSONArray(content_array_name);	//透過名字 取得 array
							reg_content = content_object_array.getString(j);					//從array 中取得元素

							if (array_start == 0) {
								member_name = reg_content;
							}
							else if (reg_content.equals(ip)) {
								member_list.put(member_name, reg_content);
							}
							if (j == 0)	array_length = content_object_array.length();
							array_start = 1;
						}
						content_object_key = content_object.keys();
					}
				}
			}
		}
		return member_list;
	}

	public static String useClientIpToGetName(JSONObject json_data, String client_ip)
	throws JSONException {

		String client_name = null;

		String content = "";
		String content_array_name = "";
		String reg_content = "";

		int array_length = 1;
		if (!(json_data.isNull("content"))) {
			JSONObject ContentObject = json_data.getJSONObject("content");			//整個大 object
			JSONObject content_object;												//大object 裏面的 小object
			JSONArray content_object_array;											//小object 裏面的 array
			Iterator Content_Key = ContentObject.keys();
			Iterator content_object_key;

			while(Content_Key.hasNext()) {
				content = Content_Key.next().toString();
				//有obj, obj 裏含array
				if (content.contains("obj_meeting_member_list")) {
					content_object = ContentObject.getJSONObject(content);
					content_object_key = content_object.keys();
					for (int j = 0, array_start = 0; j < array_length; j++, array_start = 0) {
						while(content_object_key.hasNext()) {
							content_array_name = content_object_key.next().toString();			//取得array 名字
							content_object_array = content_object.getJSONArray(content_array_name);	//透過名字 取得 array
							reg_content = content_object_array.getString(j);					//從array 中取得元素

							if (array_start == 0) {
								client_name = reg_content;
							}
							else if (reg_content.equals(client_ip)) {
								return client_name;
							}
							if (j == 0)
								array_length = content_object_array.length();
							array_start = 1;
						}
						content_object_key = content_object.keys();
					}
				}
			}
		}
		return client_name;
	}

	public static Boolean hasData() {
		Log.i("[LinkCloud]", "Status " + response_status);
		return response_status == HttpStatus.SC_OK;
	}

	public static JSONObject getJSON(String content) {
		try {
			json_index = content.indexOf('{');
			if(json_index == -1) return null;
			json_web_data = new JSONObject(content.substring(json_index));
		} catch (JSONException e) {
			json_web_data = null;
			e.printStackTrace();
		}
		return json_web_data;
	}
}
