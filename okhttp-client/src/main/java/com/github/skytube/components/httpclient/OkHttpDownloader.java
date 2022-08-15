package com.github.skytube.components.httpclient;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.schabi.newpipe.extractor.downloader.Request;
import org.schabi.newpipe.extractor.downloader.Response;
import org.schabi.newpipe.extractor.exceptions.ReCaptchaException;

import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;

/**
 * Copied from 
 * https://github.com/TeamNewPipe/NewPipeExtractor/blob/dev/extractor/src/test/java/org/schabi/newpipe/downloader/DownloaderTestImpl.java
 *
 */
public class OkHttpDownloader extends JsonDownloader {
    private static final String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; rv:103.0) Gecko/20100101 Firefox/103.0";

    private final OkHttpClient client;

    private static OkHttpDownloader instance;

    private OkHttpDownloader(final OkHttpClient.Builder builder) {
        this.client = builder.readTimeout(30, TimeUnit.SECONDS).build();
    }

    /**
     * It's recommended to call exactly once in the entire lifetime of the application.
     *
     * @param builder if null, default builder will be used
     * @return a new instance of {@link DownloaderTestImpl}
     */
    public synchronized static OkHttpDownloader init(@Nullable final OkHttpClient.Builder builder) {
        instance = new OkHttpDownloader(
                builder != null ? builder : new OkHttpClient.Builder());
        return instance;
    }

    public synchronized static OkHttpDownloader getInstance() {
        if (instance == null) {
            init(null);
        }
        return instance;
    }

    @Override
    public Response execute(@Nonnull final Request request)
            throws IOException, ReCaptchaException {
        final String httpMethod = request.httpMethod();
        final String url = request.url();
        final Map<String, List<String>> headers = request.headers();
        final byte[] dataToSend = request.dataToSend();

        RequestBody requestBody = null;
        if (dataToSend != null) {
            requestBody = RequestBody.create(null, dataToSend);
        }

        final okhttp3.Request.Builder requestBuilder = new okhttp3.Request.Builder()
                .method(httpMethod, requestBody).url(url);
        if (headers.get(USER_AGENT_HEADER) == null) {
            requestBuilder.addHeader(USER_AGENT_HEADER, USER_AGENT);
        }

        for (Map.Entry<String, List<String>> pair : headers.entrySet()) {
            final String headerName = pair.getKey();
            final List<String> headerValueList = pair.getValue();

            if (headerValueList.size() > 1) {
                requestBuilder.removeHeader(headerName);
                for (String headerValue : headerValueList) {
                    requestBuilder.addHeader(headerName, headerValue);
                }
            } else if (headerValueList.size() == 1) {
                requestBuilder.header(headerName, headerValueList.get(0));
            }

        }

        final okhttp3.Response response = client.newCall(requestBuilder.build()).execute();

        if (response.code() == 429) {
            response.close();

            throw new ReCaptchaException("reCaptcha Challenge requested", url);
        }

        final ResponseBody body = response.body();
        String responseBodyToReturn = null;

        if (body != null) {
            responseBodyToReturn = body.string();
        }

        final String latestUrl = response.request().url().toString();
        return new Response(response.code(), response.message(), response.headers().toMultimap(),
                responseBodyToReturn, latestUrl);
    }
}