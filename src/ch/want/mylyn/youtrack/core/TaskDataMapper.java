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

    public TaskData getTaskData() {
        final TaskAttributeMapper mapper = dataHandler.getAttributeMapper(repository);
        final TaskData taskData = new TaskData(mapper, PluginConstants.CONNECTOR_KIND, repository.getRepositoryUrl(), convertToTaskDataId(youtrackIssue.id()));
        taskData.setVersion("1");
        final TaskAttribute root = taskData.getRoot();
        createAttribute(root, TaskAttribute.TASK_KEY, youtrackIssue.idReadable());
        createAttribute(root, "youtrack.id", youtrackIssue.id());
        createAttribute(root, TaskAttribute.SUMMARY, youtrackIssue.summary());
        createAttribute(root, TaskAttribute.DESCRIPTION, youtrackIssue.description());
        createAttribute(root, TaskAttribute.TASK_URL, getTaskUrl(repository.getRepositoryUrl(), youtrackIssue.idReadable()));
        if (youtrackIssue.created() != null) {
            createDateAttribute(root, TaskAttribute.DATE_CREATION, youtrackIssue.created(), mapper);
        }
        if (youtrackIssue.updated() != null) {
            createDateAttribute(root, TaskAttribute.DATE_MODIFICATION, youtrackIssue.updated(), mapper);
        }
        if (youtrackIssue.reporter() != null) {
            createAttribute(root, TaskAttribute.USER_REPORTER, youtrackIssue.reporter().name());
        }
        if (youtrackIssue.project() != null) {
            createAttribute(root, "youtrack.project", youtrackIssue.project().name());
        }
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

    private static String convertToTaskDataId(final String issueId) {
        // replace RepositoryTaskHandleUtil.HANDLE_DELIM
        return issueId.replace('-', '~');
    }

    private static void createAttribute(final TaskAttribute parent, final String attributeId, final String value) {
        if (value == null) {
            return;
        }
        final TaskAttribute attribute = parent.createAttribute(attributeId);
        attribute.setValue(value);
    }

    private static void createDateAttribute(final TaskAttribute root, final String attributeId, final Long timestamp, final TaskAttributeMapper mapper) {
        if (timestamp == null) {
            return;
        }
        final TaskAttribute attribute = root.createAttribute(attributeId);
        mapper.setDateValue(attribute, new Date(timestamp));
    }
}
