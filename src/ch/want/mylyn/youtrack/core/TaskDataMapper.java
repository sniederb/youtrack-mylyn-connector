package ch.want.mylyn.youtrack.core;

import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.core.data.AbstractTaskDataHandler;
import org.eclipse.mylyn.tasks.core.data.TaskAttribute;
import org.eclipse.mylyn.tasks.core.data.TaskAttributeMapper;
import org.eclipse.mylyn.tasks.core.data.TaskCommentMapper;
import org.eclipse.mylyn.tasks.core.data.TaskData;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

import ch.want.mylyn.youtrack.PluginConstants;
import ch.want.mylyn.youtrack.YouTrackAttribute;
import ch.want.mylyn.youtrack.model.CustomField;
import ch.want.mylyn.youtrack.model.Issue;
import ch.want.mylyn.youtrack.model.IssueComment;

public class TaskDataMapper {

    private final TaskRepository repository;
    private final AbstractTaskDataHandler dataHandler;
    private Issue youtrackIssue;
    private TaskData taskData;

    public TaskDataMapper(final TaskRepository repository, final AbstractTaskDataHandler dataHandler, final Issue youtrackIssue) {
        this.repository = repository;
        this.dataHandler = dataHandler;
        this.youtrackIssue = youtrackIssue;
    }

    public TaskDataMapper(final TaskRepository repository, final AbstractTaskDataHandler dataHandler, final TaskData taskData) {
        this.repository = repository;
        this.dataHandler = dataHandler;
        this.taskData = taskData;
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
        taskData = new TaskData(mapper, PluginConstants.CONNECTOR_KIND, repository.getRepositoryUrl(), convertToTaskDataId(youtrackIssue.getId()));
        taskData.setVersion(PluginConstants.TASKDATA_VERSION);
        createAttribute(YouTrackAttribute.ID, youtrackIssue.getId());
        createAttribute(YouTrackAttribute.KEY, youtrackIssue.getIdReadable());
        setCustomFieldValue("Priority", YouTrackAttribute.PRIORITY);
        setCustomFieldValue("Type", YouTrackAttribute.TYPE);
        setCustomFieldValue("State", YouTrackAttribute.STATUS);
        setCustomFieldValue("Assignee", YouTrackAttribute.ASSIGNED);
        createAttribute(YouTrackAttribute.SUMMARY, youtrackIssue.getSummary());
        createAttribute(YouTrackAttribute.DESCRIPTION, youtrackIssue.getDescription());
        createAttribute(YouTrackAttribute.TASK_URL, getTaskUrl(repository.getRepositoryUrl(), youtrackIssue.getIdReadable()));
        createDateAttribute(YouTrackAttribute.CREATION_DATE, youtrackIssue.getCreated(), mapper);
        createDateAttribute(YouTrackAttribute.MODIFIED_DATE, youtrackIssue.getUpdated(), mapper);
        if (youtrackIssue.getReporter() == null) {
            createAttribute(YouTrackAttribute.REPORTER);
        } else {
            createAttribute(YouTrackAttribute.REPORTER, youtrackIssue.getReporter().getName());
        }
        if (youtrackIssue.getProject() == null) {
            createAttribute(YouTrackAttribute.PROJECT);
        } else {
            createAttribute(YouTrackAttribute.PROJECT, youtrackIssue.getProject().getName());
        }
        // STATE is part of CustomField > StateBundleElement
        final TaskAttribute root = taskData.getRoot();
        final List<IssueComment> comments = youtrackIssue.getComments();
        if (comments != null) {
            int commentNumber = 0;
            for (final IssueComment comment : comments) {
                final TaskAttribute commentAttribute = root.createAttribute(TaskAttribute.PREFIX_COMMENT + commentNumber);
                final TaskCommentMapper commentMapper = new TaskCommentMapper();
                if (comment.getAuthor() != null) {
                    commentMapper.setAuthor(repository.createPerson(comment.getAuthor().getName()));
                }
                if (comment.getCreated() != null) {
                    commentMapper.setCreationDate(new Date(comment.getCreated()));
                }
                commentMapper.setText(comment.getText());
                commentMapper.setNumber(commentNumber);
                commentMapper.applyTo(commentAttribute);
                commentNumber++;
            }
        }
        return taskData;
    }

    /**
     * @param changedOldAttributes
     *            The collection of attributes Mylyn considers "changed", each holding the original value.
     * @return
     */
    public Issue getIssue(final Set<TaskAttribute> changedOldAttributes) {
        youtrackIssue = new Issue();
        youtrackIssue.setId(getValue(YouTrackAttribute.ID));
        youtrackIssue.setIdReadable(getValue(YouTrackAttribute.KEY));
        getChangedValue(YouTrackAttribute.SUMMARY, changedOldAttributes).ifPresent(youtrackIssue::setSummary);
        getChangedValue(YouTrackAttribute.DESCRIPTION, changedOldAttributes).ifPresent(youtrackIssue::setDescription);
        getChangedValue(YouTrackAttribute.PRIORITY, changedOldAttributes)
            .map(v -> customField("Priority", v))
            .ifPresent(fld -> youtrackIssue.getCustomFields().add(fld));
        getChangedValue(YouTrackAttribute.TYPE, changedOldAttributes)
            .map(v -> customField("Type", v))
            .ifPresent(fld -> youtrackIssue.getCustomFields().add(fld));
        getChangedValue(YouTrackAttribute.STATUS, changedOldAttributes)
            .map(v -> customField("State", v))
            .ifPresent(fld -> youtrackIssue.getCustomFields().add(fld));
        getChangedValue(YouTrackAttribute.ASSIGNED, changedOldAttributes)
            .map(v -> customField("Assignee", v))
            .ifPresent(fld -> youtrackIssue.getCustomFields().add(fld));
        final TaskAttribute attribute = taskData.getRoot().getMappedAttribute(TaskAttribute.COMMENT_NEW);
        if (attribute != null) {
            final IssueComment newComment = new IssueComment();
            newComment.setText(taskData.getAttributeMapper().getValue(attribute));
            youtrackIssue.setComments(List.of(newComment));
        }
        return youtrackIssue;
    }

    private TaskAttribute createAttribute(final YouTrackAttribute key, final String value) {
        final TaskAttribute attribute = createAttribute(key);
        if (value != null) {
            attribute.setValue(value);
        }
        return attribute;
    }

    private TaskAttribute createDateAttribute(final YouTrackAttribute key, final Long timestamp, final TaskAttributeMapper mapper) {
        final TaskAttribute attribute = createAttribute(key);
        if (timestamp != null) {
            mapper.setDateValue(attribute, new Date(timestamp));
        }
        return attribute;
    }

    private TaskAttribute createAttribute(final YouTrackAttribute key) {
        final TaskAttribute attribute = taskData.getRoot().createAttribute(key.id());
        attribute.getMetaData().defaults() //
            .setReadOnly(key.isReadOnly())
            .setKind(key.getKind())
            .setLabel(key.getLabel())
            .setType(key.getControlType());
        return attribute;
    }

    private void setCustomFieldValue(final String customFieldName, final YouTrackAttribute target) {
        if (youtrackIssue.getCustomFields() == null) {
            createAttribute(target);
        } else {
            youtrackIssue.getCustomFields().stream()
                .filter(fld -> customFieldName.equalsIgnoreCase(fld.getName()))
                .filter(fld -> fld.getValue() != null)
                .findFirst()
                .ifPresentOrElse(fld -> createAttribute(target, fld.getValue().path("name").asText()),
                    () -> createAttribute(target));
        }
    }

    private String getValue(final YouTrackAttribute key) {
        return taskData.getRoot().getAttribute(key.id()).getValue();
    }

    private Optional<String> getChangedValue(final YouTrackAttribute key, final Set<TaskAttribute> changedOldAttributes) {
        final boolean isChangeKey = changedOldAttributes.stream().anyMatch(attr -> Objects.equals(key.id(), attr.getId()));
        if (isChangeKey) {
            return Optional.ofNullable(getValue(key));
        }
        return Optional.empty();
    }

    private CustomField customField(final String fieldName, final String value) {
        final CustomField field = new CustomField();
        field.setName(fieldName);
        final ObjectNode jsonNode = JsonNodeFactory.instance.objectNode();
        jsonNode.put("value", value);
        field.setValue(jsonNode);
        return field;
    }
}
