package ch.want.mylyn.youtrack.core;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Objects;
import java.util.function.Consumer;

public class JavaNetHttpClientWrapper implements HttpClientWrapper {

    private static final Duration CONNECT_TIMEOUT = Duration.ofSeconds(10);
    private static final Duration REQUEST_TIMEOUT = Duration.ofSeconds(30);
    private final HttpClient httpClient;
    private final URI baseUri;
    private Consumer<HttpRequest.Builder> customizer;

    public JavaNetHttpClientWrapper(final String baseUrl) {
        this.baseUri = URI.create(normalizeBaseUrl(baseUrl));
        if (!"http".equalsIgnoreCase(this.baseUri.getScheme()) && !"https".equalsIgnoreCase(this.baseUri.getScheme())) {
            throw new IllegalArgumentException("This class does not support " + this.baseUri.getScheme());
        }
        this.httpClient = HttpClient.newBuilder()
            .connectTimeout(CONNECT_TIMEOUT)
            .followRedirects(HttpClient.Redirect.NORMAL)
            .build();
    }

    public JavaNetHttpClientWrapper customizer(final Consumer<HttpRequest.Builder> customizer) {
        this.customizer = customizer;
        return this;
    }

    /**
     * @param path
     *            API path, e.g. "/api/users/me?fields=id,login,name"
     */
    @Override
    public String get(final String path) throws IOException {
        final HttpRequest request = createRequest(path)
            .GET()
            .build();
        try {
            final HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            return getBody(response);
        } catch (final InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IOException(e.getMessage(), e);
        }
    }

    /**
     * @param path
     *            API path
     * @param jsonBody
     *            JSON request body
     */
    @Override
    public String post(final String path, final String jsonBody) throws IOException {
        final HttpRequest request = createRequest(path)
            .header("Content-Type", "application/json")
            .POST(HttpRequest.BodyPublishers.ofString(Objects.requireNonNull(jsonBody, "jsonBody")))
            .build();
        try {
            final HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            return getBody(response);
        } catch (final InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IOException(e.getMessage(), e);
        }
    }

    private HttpRequest.Builder createRequest(final String path) {
        final URI uri = baseUri.resolve(path);
        final HttpRequest.Builder httpRequestBuilder = HttpRequest.newBuilder()
            .uri(uri)
            .timeout(REQUEST_TIMEOUT)
            .header("Accept", "application/json");
        if (customizer != null) {
            customizer.accept(httpRequestBuilder);
        }
        return httpRequestBuilder;
    }

    private static String getBody(final HttpResponse<String> response) throws IOException {
        final int statusCode = response.statusCode();
        if (statusCode < 200 || statusCode >= 300) {
            throw new IOException("YouTrack returned HTTP " + statusCode + (response.body().isBlank() ? "" : ": " + response.body()));
        }
        return response.body();
    }

    private static String normalizeBaseUrl(final String url) {
        Objects.requireNonNull(url, "baseUrl");
        String result = url.trim();
        while (result.endsWith("/")) {
            result = result.substring(0, result.length() - 1);
        }
        return result;
    }
}
