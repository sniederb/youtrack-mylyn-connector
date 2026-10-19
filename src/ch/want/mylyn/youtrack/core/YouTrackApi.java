package ch.want.mylyn.youtrack.core;

import java.io.IOException;
import java.util.List;

import ch.want.mylyn.youtrack.model.Issue;
import ch.want.mylyn.youtrack.model.ProjectCustomField;
import ch.want.mylyn.youtrack.model.SavedQuery;
import ch.want.mylyn.youtrack.model.User;

public interface YouTrackApi {

    User getCurrentUser() throws IOException;

    List<SavedQuery> getSavedQueries() throws IOException;

    SavedQuery getSavedQuery(String savedQueryId) throws IOException;

    List<ProjectCustomField> getCustomFieldDefinitions(String projectId) throws IOException;

    Issue getIssue(String taskId) throws IOException;

    Issue saveIssue(Issue issue) throws IOException;
}
