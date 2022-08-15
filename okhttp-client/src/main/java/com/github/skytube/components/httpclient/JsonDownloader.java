package com.github.skytube.components.httpclient;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.schabi.newpipe.extractor.downloader.Downloader;
import org.schabi.newpipe.extractor.downloader.Response;
import org.schabi.newpipe.extractor.exceptions.ReCaptchaException;

public abstract class JsonDownloader extends Downloader {

    static final String USER_AGENT_HEADER = "User-Agent";

    protected String apiUserAgent;

    public void setApiUserAgent(String apiUserAgent) {
        this.apiUserAgent = apiUserAgent;
    }

    public String getApiUserAgent() {
        return apiUserAgent;
    }

    private Map<String, List<String>> getHeaders() {
        if (apiUserAgent != null) {
            // java 8 compatibility:
            Map<String, List<String>> map = new HashMap<String, List<String>>();
            map.put(USER_AGENT_HEADER, Arrays.asList(apiUserAgent));
            return map;
        } else {
            return Collections.emptyMap();
        }
    }

    public String getBody(String url) throws IOException {
        try {
            Response response = get(url, getHeaders());
            if (response.responseCode() == 200) {
                String body = response.responseBody();
                return body;
            } else {
                throw new IOException("Unable to fetch: " + url + ", responseCode=" + response.responseCode() + ", msg:" + response.responseMessage());
            }
        } catch (ReCaptchaException e) {
            throw new IOException("recaptcha : " + e.getMessage(), e);
        }
    }

    public JSONArray getJSONArray(String url) throws IOException, JSONException {
        return new JSONArray(getBody(url));
    }

    public JSONObject getJSONObject(String url) throws IOException, JSONException {
        return new JSONObject(getBody(url));
    }

}
