package ch.want.mylyn.youtrack.core;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

import com.fasterxml.jackson.core.type.TypeReference;

import ch.want.mylyn.youtrack.ObjectMapperAccessor;
import ch.want.mylyn.youtrack.model.Issue;
import ch.want.mylyn.youtrack.model.SavedQuery;
import ch.want.mylyn.youtrack.model.User;

public class YouTrackApiImpl implements YouTrackApi {

    private static final TypeReference<List<SavedQuery>> SAVEDQUERY_TYPE = new TypeReference<>() {
    };
    private final HttpClientWrapper httpClient;

    public YouTrackApiImpl(final String baseUrl, final String permanentToken) {
        httpClient = new JavaNetHttpClientWrapper(baseUrl)
            .customizer(b -> b.header("Authorization", "Bearer " + permanentToken));
    }

    @Override
    public User getCurrentUser() throws IOException {
        final String jsonResponse = httpClient.get("/api/users/me?fields=id,login,name");
        return ObjectMapperAccessor.objectMapper().readValue(jsonResponse, User.class);
    }

    @Override
    public List<SavedQuery> getSavedQueries() throws IOException {
        final String jsonResponse = httpClient.get("/api/savedQueries?fields=id,name,query");
        return ObjectMapperAccessor.objectMapper().readValue(jsonResponse, SAVEDQUERY_TYPE);
    }

    @Override
    public SavedQuery getSavedQuery(final String savedQueryId) throws IOException {
        final String encodedSavedQueryId = URLEncoder.encode(savedQueryId, StandardCharsets.ISO_8859_1);
        final String jsonResponse = httpClient.get("/api/savedQueries/" + encodedSavedQueryId +
            "?fields=id,name,issues(id,idReadable,summary,customFields(name,value(name,login,fullName,presentation)))");
        return ObjectMapperAccessor.objectMapper().readValue(jsonResponse, SavedQuery.class);
    }

    @Override
    public Issue getIssue(final String taskId) throws IOException {
        final String encodedTaskId = URLEncoder.encode(taskId, StandardCharsets.ISO_8859_1);
        final String jsonResponse = httpClient.get("/api/issues/" + encodedTaskId +
            "?fields=id,idReadable,summary,description,created,updated,resolved,project(id,name,shortName),reporter(id,login,name),assignee(id,login,name),customFields(name,value(name,login,fullName,presentation)),comments(id,created,text,author(id,login,name))");
        return ObjectMapperAccessor.objectMapper().readValue(jsonResponse, Issue.class);
    }
}
