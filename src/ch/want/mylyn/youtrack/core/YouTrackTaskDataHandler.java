package ch.want.mylyn.youtrack.core;

import java.io.IOException;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.mylyn.commons.net.AuthenticationCredentials;
import org.eclipse.mylyn.commons.net.AuthenticationType;
import org.eclipse.mylyn.commons.net.Policy;
import org.eclipse.mylyn.tasks.core.ITaskMapping;
import org.eclipse.mylyn.tasks.core.RepositoryResponse;
import org.eclipse.mylyn.tasks.core.RepositoryResponse.ResponseKind;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.core.data.AbstractTaskDataHandler;
import org.eclipse.mylyn.tasks.core.data.TaskAttribute;
import org.eclipse.mylyn.tasks.core.data.TaskAttributeMapper;
import org.eclipse.mylyn.tasks.core.data.TaskData;

import ch.want.mylyn.youtrack.PluginConstants;
import ch.want.mylyn.youtrack.model.Issue;

public class YouTrackTaskDataHandler extends AbstractTaskDataHandler {

    @Override
    public TaskAttributeMapper getAttributeMapper(final TaskRepository repository) {
        return new YouTrackTaskAttributeMapper(repository);
    }

    @Override
    public boolean initializeTaskData(final TaskRepository repository, final TaskData taskData, final ITaskMapping taskMapping, final IProgressMonitor monitor)
            throws CoreException {
        // Initialize a new task data object with default attributes and values
        // Creating new tasks is currently not supported
        return false;
    }

    @Override
    public RepositoryResponse postTaskData(final TaskRepository repository, final TaskData taskData, final Set<TaskAttribute> changedOldAttributes,
            final IProgressMonitor monitor) throws CoreException {
        final IProgressMonitor ourMonitor = Policy.monitorFor(monitor);
        try {
            ourMonitor.beginTask("Saving YouTrack issue", IProgressMonitor.UNKNOWN);
            final AuthenticationCredentials credentials = repository.getCredentials(AuthenticationType.REPOSITORY);
            final YouTrackApi api = new YouTrackApiImpl(repository.getRepositoryUrl(), credentials.getPassword());
            Issue issue = new TaskDataMapper(repository, this, taskData)
                .getIssue(changedOldAttributes);
            issue = api.saveIssue(issue);
            return new RepositoryResponse(ResponseKind.TASK_UPDATED, taskData.getTaskId());
        } catch (final IOException ex) {
            final IStatus status = new Status(IStatus.ERROR, PluginConstants.PLUGIN_ID, "Failed to save YouTrack issue: " + ex.getMessage(), ex);
            throw new CoreException(status);
        } finally {
            ourMonitor.done();
        }
    }
}
