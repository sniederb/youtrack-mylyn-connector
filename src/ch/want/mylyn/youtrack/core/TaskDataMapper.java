package ch.want.mylyn.youtrack.core;

import java.util.ArrayList;
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
import ch.want.mylyn.youtrack.model.Issue;
import ch.want.mylyn.youtrack.model.IssueComment;
import ch.want.mylyn.youtrack.model.IssueCustomField﻿;

public class TaskDataMapper {

    private final TaskRepository repository;
    private final AbstractTaskDataHandler dataHandler;
    private final ProjectConfigurationLoadingCache configurationCache;
    private Issue youtrackIssue;
    private TaskData taskData;

    public TaskDataMapper(final TaskRepository repository, final AbstractTaskDataHandler dataHandler, final ProjectConfigurationLoadingCache configCache,
            final Issue youtrackIssue) {
        this.repository = repository;
        this.dataHandler = dataHandler;
        this.configurationCache = configCache;
        this.youtrackIssue = youtrackIssue;
    }

    public TaskDataMapper(final TaskRepository repository, final AbstractTaskDataHandler dataHandler, final TaskData taskData) {
        this.repository = repository;
        this.dataHandler = dataHandler;
        this.taskData = taskData;
        this.configurationCache = null;
    }

    public static String getTaskUrl(final String repositoryUrl, final String taskId) {
        return repositoryUrl + PluginConstants.ISSUE_URL_PATH + taskId;
    }

    public static String convertToTaskDataId(final String issueId) {
        // replace RepositoryTaskHandleUtil.HANDLE_DELIM
        return issueId.replace('-', '~');
    }

    public static String convertFromTaskDataId(final String taskId) {
        return taskId.replace('~', '-');
    }

    public TaskData getTaskData(final YouTrackApi api) {
        final TaskAttributeMapper mapper = dataHandler.getAttributeMapper(repository);
        taskData = new TaskData(mapper, PluginConstants.CONNECTOR_KIND, repository.getRepositoryUrl(), convertToTaskDataId(youtrackIssue.getId()));
        taskData.setVersion(PluginConstants.TASKDATA_VERSION);
        createAttribute(YouTrackAttribute.ID, youtrackIssue.getId());
        createAttribute(YouTrackAttribute.KEY, youtrackIssue.getIdReadable());
        final TaskAttribute priorityAttribute = setCustomFieldValue("Priority", YouTrackAttribute.PRIORITY);
        setCustomFieldValue("Type", YouTrackAttribute.TYPE);
        final TaskAttribute statusAttribute = setCustomFieldValue("State", YouTrackAttribute.STATUS);
        setCustomFieldValue("Assignee", YouTrackAttribute.ASSIGNED);
        if ((youtrackIssue.getProject() != null) && (api != null)) {
            configurationCache.getPriorities(repository, youtrackIssue.getProject().getId(), api)
                .entrySet()
                .forEach(e -> priorityAttribute.putOption(e.getKey(), e.getValue()));
            configurationCache.getStatus(repository, youtrackIssue.getProject().getId(), api)
                .entrySet()
                .forEach(e -> statusAttribute.putOption(e.getKey(), e.getValue()));
        }
        createAttribute(YouTrackAttribute.SUMMARY, youtrackIssue.getSummary());
        createAttribute(YouTrackAttribute.DESCRIPTION, youtrackIssue.getDescription());
        createAttribute(YouTrackAttribute.TASK_URL, getTaskUrl(repository.getRepositoryUrl(), youtrackIssue.getIdReadable()));
        createDateTimeAttribute(YouTrackAttribute.CREATION_DATE, youtrackIssue.getCreated(), mapper);
        createDateTimeAttribute(YouTrackAttribute.MODIFIED_DATE, youtrackIssue.getUpdated(), mapper);
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
        addCommentAttributes();
        return taskData;
    }

    /**
     * @param changedOldAttributes
     *            The collection of attributes Mylyn considers "changed", each holding the original value.
     * @return
     */
    public Issue getIssue(final Set<TaskAttribute> changedOldAttributes) {
        final List<IssueCustomField﻿> changedCustomFields = new ArrayList<>();
        youtrackIssue = new Issue();
        youtrackIssue.setId(getValue(YouTrackAttribute.ID));
        youtrackIssue.setIdReadable(getValue(YouTrackAttribute.KEY));
        getChangedValue(YouTrackAttribute.SUMMARY, changedOldAttributes).ifPresent(youtrackIssue::setSummary);
        getChangedValue(YouTrackAttribute.DESCRIPTION, changedOldAttributes).ifPresent(youtrackIssue::setDescription);
        getChangedValue(YouTrackAttribute.PRIORITY, changedOldAttributes)
            .map(v -> customField("Priority", true, v))
            .ifPresent(fld -> changedCustomFields.add(fld));
        getChangedValue(YouTrackAttribute.TYPE, changedOldAttributes)
            .map(v -> customField("Type", false, v))
            .ifPresent(fld -> changedCustomFields.add(fld));
        getChangedValue(YouTrackAttribute.STATUS, changedOldAttributes)
            .map(v -> customField("State", true, v))
            .ifPresent(fld -> changedCustomFields.add(fld));
        getChangedValue(YouTrackAttribute.ASSIGNED, changedOldAttributes)
            .map(v -> customField("Assignee", false, v))
            .ifPresent(fld -> changedCustomFields.add(fld));
        if (!changedCustomFields.isEmpty()) {
            youtrackIssue.setCustomFields(changedCustomFields);
        }
        final TaskAttribute attribute = taskData.getRoot().getMappedAttribute(TaskAttribute.COMMENT_NEW);
        if (attribute != null) {
            final IssueComment newComment = new IssueComment();
            newComment.setText(taskData.getAttributeMapper().getValue(attribute));
            if ((newComment.getText() != null) && !newComment.getText().isBlank()) {
                youtrackIssue.setComments(List.of(newComment));
            }
        }
        return youtrackIssue;
    }

    private void addCommentAttributes() {
        final TaskAttribute root = taskData.getRoot();
        final List<IssueComment> comments = youtrackIssue.getComments();
        if (comments != null) {
            int commentNumber = 1;
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
        if (youtrackIssue.getId() != null) {
            createAttribute(YouTrackAttribute.NEWCOMMENT);
        }
    }

    private TaskAttribute createAttribute(final YouTrackAttribute key, final String value) {
        final TaskAttribute attribute = createAttribute(key);
        if (value != null) {
            attribute.setValue(value);
        }
        return attribute;
    }

    private TaskAttribute createDateTimeAttribute(final YouTrackAttribute key, final Long timestamp, final TaskAttributeMapper mapper) {
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

    private TaskAttribute setCustomFieldValue(final String customFieldName, final YouTrackAttribute target) {
        if (youtrackIssue.getCustomFields() == null) {
            return createAttribute(target);
        }
        final String valueField = TaskAttribute.TYPE_SINGLE_SELECT.equals(target.getControlType()) ? "id" : "name";
        return youtrackIssue.getCustomFields().stream()
            .filter(fld -> customFieldName.equalsIgnoreCase(fld.getName()))
            .filter(fld -> fld.getValue() != null)
            .findFirst()
            .map(fld -> createAttribute(target, fld.getValue().path(valueField).asText()))
            .orElseGet(() -> createAttribute(target));
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

    /**
     * This code currently assumes that all custom fields are SingleEnumIssueCustomField / EnumBundleElement, except "State" which is mapped
     * to StateBundleElement.
     *
     * @param fieldName
     * @param isEnumId
     * @param value
     * @return
     */
    private IssueCustomField﻿ customField(final String fieldName, final boolean isEnumId, final String value) {
        final IssueCustomField﻿ field = new IssueCustomField﻿();
        field.setFieldType("SingleEnumIssueCustomField");
        field.setName(fieldName);
        final ObjectNode jsonNode = JsonNodeFactory.instance.objectNode();
        jsonNode.put(isEnumId ? "id" : "name", value);
        jsonNode.put("$type", "State".equals(fieldName) ? "StateBundleElement" : "EnumBundleElement");
        field.setValue(jsonNode);
        return field;
    }
}
