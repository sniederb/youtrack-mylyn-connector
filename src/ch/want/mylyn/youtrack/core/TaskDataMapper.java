package ch.want.mylyn.youtrack.core;

import java.util.Date;
import java.util.List;

import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.core.data.AbstractTaskDataHandler;
import org.eclipse.mylyn.tasks.core.data.TaskAttribute;
import org.eclipse.mylyn.tasks.core.data.TaskAttributeMapper;
import org.eclipse.mylyn.tasks.core.data.TaskCommentMapper;
import org.eclipse.mylyn.tasks.core.data.TaskData;

import ch.want.mylyn.youtrack.PluginConstants;
import ch.want.mylyn.youtrack.YouTrackAttribute;
import ch.want.mylyn.youtrack.model.Issue;
import ch.want.mylyn.youtrack.model.IssueComment;

public class TaskDataMapper {

    private final TaskRepository repository;
    private final AbstractTaskDataHandler dataHandler;
    private final Issue youtrackIssue;

    public TaskDataMapper(final TaskRepository repository, final AbstractTaskDataHandler dataHandler, final Issue youtrackIssue) {
        this.repository = repository;
        this.dataHandler = dataHandler;
        this.youtrackIssue = youtrackIssue;
    }

    public static String getTaskUrl(final String repositoryUrl, final String taskId) {
        return repositoryUrl + "#" + taskId;
    }

    public static String convertToTaskDataId(final String issueId) {
        // replace RepositoryTaskHandleUtil.HANDLE_DELIM
        return issueId.replace('-', '~');
    }

    public static String convertFromTaskDataId(final String taskId) {
        return taskId.replace('~', '-');
    }

    public TaskData getTaskData() {
        final TaskAttributeMapper mapper = dataHandler.getAttributeMapper(repository);
        final TaskData taskData = new TaskData(mapper, PluginConstants.CONNECTOR_KIND, repository.getRepositoryUrl(), convertToTaskDataId(youtrackIssue.id()));
        taskData.setVersion(PluginConstants.TASKDATA_VERSION);
        createAttribute(taskData, YouTrackAttribute.ID, youtrackIssue.id());
        createAttribute(taskData, YouTrackAttribute.KEY, youtrackIssue.idReadable());
        if (youtrackIssue.customFields() != null) {
            youtrackIssue.customFields().stream()
                .filter(fld -> "Priority".equalsIgnoreCase(fld.name()))
                .filter(fld -> fld.value() != null)
                .findFirst()
                .ifPresentOrElse(fld -> createAttribute(taskData, YouTrackAttribute.PRIORITY, fld.value().path("name").asText()),
                    () -> createAttribute(taskData, YouTrackAttribute.PRIORITY));
            youtrackIssue.customFields().stream()
                .filter(fld -> "Type".equalsIgnoreCase(fld.name()))
                .filter(fld -> fld.value() != null)
                .findFirst()
                .ifPresentOrElse(
                    fld -> createAttribute(taskData, YouTrackAttribute.TYPE, fld.value().path("name").asText()),
                    () -> createAttribute(taskData, YouTrackAttribute.TYPE));
            youtrackIssue.customFields().stream()
                .filter(fld -> "State".equalsIgnoreCase(fld.name()))
                .filter(fld -> fld.value() != null)
                .findFirst()
                .ifPresentOrElse(fld -> createAttribute(taskData, YouTrackAttribute.STATUS, fld.value().path("name").asText()),
                    () -> createAttribute(taskData, YouTrackAttribute.STATUS));
            youtrackIssue.customFields().stream()
                .filter(fld -> "Assignee".equalsIgnoreCase(fld.name()))
                .filter(fld -> fld.value() != null)
                .findFirst()
                .ifPresentOrElse(fld -> createAttribute(taskData, YouTrackAttribute.ASSIGNED, fld.value().path("name").asText()),
                    () -> createAttribute(taskData, YouTrackAttribute.ASSIGNED));
        } else {
            createAttribute(taskData, YouTrackAttribute.PRIORITY);
            createAttribute(taskData, YouTrackAttribute.TYPE);
            createAttribute(taskData, YouTrackAttribute.STATUS);
            createAttribute(taskData, YouTrackAttribute.ASSIGNED);
        }
        createAttribute(taskData, YouTrackAttribute.SUMMARY, youtrackIssue.summary());
        createAttribute(taskData, YouTrackAttribute.DESCRIPTION, youtrackIssue.description());
        createAttribute(taskData, YouTrackAttribute.TASK_URL, getTaskUrl(repository.getRepositoryUrl(), youtrackIssue.idReadable()));
        createDateAttribute(taskData, YouTrackAttribute.CREATION_DATE, youtrackIssue.created(), mapper);
        createDateAttribute(taskData, YouTrackAttribute.MODIFIED_DATE, youtrackIssue.updated(), mapper);
        if (youtrackIssue.reporter() == null) {
            createAttribute(taskData, YouTrackAttribute.REPORTER);
        } else {
            createAttribute(taskData, YouTrackAttribute.REPORTER, youtrackIssue.reporter().name());
        }
        if (youtrackIssue.project() == null) {
            createAttribute(taskData, YouTrackAttribute.PROJECT);
        } else {
            createAttribute(taskData, YouTrackAttribute.PROJECT, youtrackIssue.project().name());
        }
        // STATE is part of CustomField > StateBundleElement
        final TaskAttribute root = taskData.getRoot();
        final List<IssueComment> comments = youtrackIssue.comments();
        if (comments != null) {
            int commentNumber = 0;
            for (final IssueComment comment : comments) {
                final TaskAttribute commentAttribute = root.createAttribute(TaskAttribute.PREFIX_COMMENT + commentNumber);
                final TaskCommentMapper commentMapper = new TaskCommentMapper();
                if (comment.author() != null) {
                    commentMapper.setAuthor(repository.createPerson(comment.author().name()));
                }
                if (comment.created() != null) {
                    commentMapper.setCreationDate(new Date(comment.created()));
                }
                commentMapper.setText(comment.text());
                commentMapper.setNumber(commentNumber);
                commentMapper.applyTo(commentAttribute);
                commentNumber++;
            }
        }
        return taskData;
    }

    private TaskAttribute createAttribute(final TaskData data, final YouTrackAttribute key, final String value) {
        final TaskAttribute attribute = createAttribute(data, key);
        if (value != null) {
            attribute.setValue(value);
        }
        return attribute;
    }

    private TaskAttribute createDateAttribute(final TaskData data, final YouTrackAttribute key, final Long timestamp, final TaskAttributeMapper mapper) {
        final TaskAttribute attribute = createAttribute(data, key);
        if (timestamp != null) {
            mapper.setDateValue(attribute, new Date(timestamp));
        }
        return attribute;
    }

    private TaskAttribute createAttribute(final TaskData data, final YouTrackAttribute key) {
        final TaskAttribute attribute = data.getRoot().createAttribute(key.id());
        attribute.getMetaData().defaults() //
            .setReadOnly(key.isReadOnly())
            .setKind(key.getKind())
            .setLabel(key.getLabel())
            .setType(key.getControlType());
        return attribute;
    }
}
