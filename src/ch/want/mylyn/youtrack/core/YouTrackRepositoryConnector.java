package ch.want.mylyn.youtrack.core;

import java.io.IOException;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Status;
import org.eclipse.mylyn.commons.net.AuthenticationCredentials;
import org.eclipse.mylyn.commons.net.AuthenticationType;
import org.eclipse.mylyn.commons.net.Policy;
import org.eclipse.mylyn.tasks.core.AbstractRepositoryConnector;
import org.eclipse.mylyn.tasks.core.IRepositoryQuery;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.core.data.AbstractTaskDataHandler;
import org.eclipse.mylyn.tasks.core.data.TaskData;
import org.eclipse.mylyn.tasks.core.data.TaskDataCollector;
import org.eclipse.mylyn.tasks.core.data.TaskMapper;
import org.eclipse.mylyn.tasks.core.sync.ISynchronizationSession;

import ch.want.mylyn.youtrack.PluginConstants;
import ch.want.mylyn.youtrack.model.Issue;
import ch.want.mylyn.youtrack.model.SavedQuery;

public class YouTrackRepositoryConnector extends AbstractRepositoryConnector {

    private final YouTrackTaskDataHandler taskDataHandler = new YouTrackTaskDataHandler();

    @Override
    public String getConnectorKind() {
        return PluginConstants.CONNECTOR_KIND;
    }

    @Override
    public String getLabel() {
        return PluginConstants.YOUTRACK_LABEL;
    }

    /**
     * Get repository URL from something like {@code https://solivagant.youtrack.cloud/issue/DEMO-11/Import-issues-and-articles}
     */
    @Override
    public String getRepositoryUrlFromTaskUrl(final String url) {
        if (url == null) {
            return null;
        }
        final int index = url.indexOf(PluginConstants.ISSUE_URL_PATH);
        return index >= 0 ? url.substring(0, index) : null;
    }

    /**
     * Get task ID from something like {@code https://solivagant.youtrack.cloud/issue/DEMO-11/Import-issues-and-articles}
     */
    @Override
    public String getTaskIdFromTaskUrl(final String url) {
        if (url == null) {
            return null;
        }
        final int index = url.indexOf(PluginConstants.ISSUE_URL_PATH);
        if (index != -1) {
            final int taskIdStart = index + PluginConstants.ISSUE_URL_PATH.length();
            int taskIdEnd = url.indexOf('/', taskIdStart);
            if (taskIdEnd > taskIdStart) {
                return url.substring(taskIdStart, taskIdEnd);
            }
            taskIdEnd = url.indexOf('?', taskIdStart);
            if (taskIdEnd > taskIdStart) {
                return url.substring(taskIdStart, taskIdEnd);
            }
            return url.substring(taskIdStart);
        }
        return null;
    }

    @Override
    public TaskData getTaskData(final TaskRepository repository, final String taskId, final IProgressMonitor monitor) throws CoreException {
        final IProgressMonitor ourMonitor = Policy.monitorFor(monitor);
        try {
            ourMonitor.beginTask("Querying YouTrack", IProgressMonitor.UNKNOWN);
            Policy.checkCanceled(ourMonitor);
            final AuthenticationCredentials credentials = repository.getCredentials(AuthenticationType.REPOSITORY);
            final YouTrackApi api = new YouTrackApiImpl(repository.getRepositoryUrl(), credentials.getPassword());
            final Issue issue = api.getIssue(TaskDataMapper.convertFromTaskDataId(taskId));
            Policy.checkCanceled(ourMonitor);
            return createTaskData(repository, issue);
        } catch (final OperationCanceledException ex) {
            throw ex;
        } catch (final IOException ex) {
            throw new CoreException(new Status(IStatus.ERROR, PluginConstants.PLUGIN_ID, "Failed to query YouTrack: " + ex.getMessage(), ex));
        } finally {
            ourMonitor.done();
        }
    }

    @Override
    public String getTaskUrl(final String repositoryUrl, final String taskId) {
        return TaskDataMapper.getTaskUrl(repositoryUrl, taskId);
    }

    @Override
    public boolean hasTaskChanged(final TaskRepository repository, final ITask task, final TaskData taskData) {
        return taskData.isPartial();
    }

    @Override
    public IStatus performQuery(final TaskRepository repository, final IRepositoryQuery query, final TaskDataCollector dataCollector,
            final ISynchronizationSession session, final IProgressMonitor monitor) {
        final IProgressMonitor ourMonitor = Policy.monitorFor(monitor);
        try {
            ourMonitor.beginTask("Querying YouTrack", IProgressMonitor.UNKNOWN);
            Policy.checkCanceled(ourMonitor);
            final String savedQueryId = query.getAttribute(PluginConstants.ATTRIBUTE_SAVED_QUERY_ID);
            if (savedQueryId == null || savedQueryId.isBlank()) {
                return new Status(IStatus.ERROR, PluginConstants.PLUGIN_ID, "No YouTrack saved query selected");
            }
            final AuthenticationCredentials credentials = repository.getCredentials(AuthenticationType.REPOSITORY);
            final YouTrackApi api = new YouTrackApiImpl(repository.getRepositoryUrl(), credentials.getPassword());
            final SavedQuery savedQuery = api.getSavedQuery(savedQueryId);
            Policy.checkCanceled(ourMonitor);
            final List<Issue> issues = savedQuery.getIssues();
            ourMonitor.subTask("Processing " + issues.size() + " issues");
            for (final Issue issue : issues) {
                Policy.checkCanceled(ourMonitor);
                final TaskData taskData = createTaskData(repository, issue);
                taskData.setPartial(true);
                dataCollector.accept(taskData);
            }
            return Status.OK_STATUS;
        } catch (final OperationCanceledException ex) {
            throw ex;
        } catch (final IOException ex) {
            return new Status(IStatus.ERROR, PluginConstants.PLUGIN_ID, "Failed to query YouTrack: " + ex.getMessage(), ex);
        } finally {
            ourMonitor.done();
        }
    }

    @Override
    public void updateRepositoryConfiguration(final TaskRepository repository, final IProgressMonitor monitor) throws CoreException {
        // TODO
    }

    @Override
    public void updateTaskFromTaskData(final TaskRepository repository, final ITask task, final TaskData taskData) {
        final TaskMapper mapper = new TaskMapper(taskData);
        mapper.applyTo(task);
    }

    @Override
    public boolean canCreateNewTask(final TaskRepository repository) {
        return false;
    }

    @Override
    public boolean canCreateTaskFromKey(final TaskRepository repository) {
        return false;
    }

    @Override
    public AbstractTaskDataHandler getTaskDataHandler() {
        return taskDataHandler;
    }

    private TaskData createTaskData(final TaskRepository repository, final Issue issue) {
        return new TaskDataMapper(repository, getTaskDataHandler(), issue).getTaskData();
    }
}
