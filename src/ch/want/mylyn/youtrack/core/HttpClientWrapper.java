package ch.want.mylyn.youtrack.core;

import java.io.IOException;

/**
 * This interface defines abstracted HTTP methods to decouple any implementation library.
 */
public interface HttpClientWrapper {

    String get(final String path) throws IOException;

    String post(final String path, final String jsonBody) throws IOException;
}
