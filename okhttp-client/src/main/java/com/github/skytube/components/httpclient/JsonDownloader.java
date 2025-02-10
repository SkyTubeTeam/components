package com.github.skytube.components.httpclient;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.grack.nanojson.JsonArray;
import com.grack.nanojson.JsonObject;
import com.grack.nanojson.JsonParser;
import com.grack.nanojson.JsonParserException;
import org.schabi.newpipe.extractor.downloader.Downloader;
import org.schabi.newpipe.extractor.downloader.Response;
import org.schabi.newpipe.extractor.exceptions.ReCaptchaException;

/**
 * Base class for downloading JSONs.
 */
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
            if (response.responseCode() == HttpURLConnection.HTTP_OK) {
                String body = response.responseBody();
                return body;
            } else {
                throw new IOException("Unable to fetch: " + url
                        + ", responseCode=" + response.responseCode()
                        + ", msg:" + response.responseMessage());
            }
        } catch (ReCaptchaException e) {
            throw new IOException("recaptcha : " + e.getMessage(), e);
        }
    }

    public JsonArray getJSONArray(String url) throws IOException, JsonParserException {
        String body = getBody(url);
        return JsonParser.array().from(body);
    }

    public JsonObject getJSONObject(String url) throws IOException, JsonParserException {
        String body = getBody(url);
        return JsonParser.object().from(body);
    }

}
