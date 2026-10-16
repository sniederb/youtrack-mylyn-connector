package ch.want.mylyn.youtrack.core;

import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.mylyn.tasks.core.ITaskMapping;
import org.eclipse.mylyn.tasks.core.RepositoryResponse;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.core.data.AbstractTaskDataHandler;
import org.eclipse.mylyn.tasks.core.data.TaskAttribute;
import org.eclipse.mylyn.tasks.core.data.TaskAttributeMapper;
import org.eclipse.mylyn.tasks.core.data.TaskData;

public class YouTrackTaskDataHandler extends AbstractTaskDataHandler {

    @Override
    public TaskAttributeMapper getAttributeMapper(final TaskRepository repository) {
        return new YouTrackTaskAttributeMapper(repository);
    }

    @Override
    public boolean initializeTaskData(final TaskRepository repository, final TaskData taskData, final ITaskMapping taskMapping, final IProgressMonitor monitor)
            throws CoreException {
        return false;
    }

    @Override
    public RepositoryResponse postTaskData(final TaskRepository repository, final TaskData taskData, final Set<TaskAttribute> arg2,
            final IProgressMonitor monitor) throws CoreException {
        return null;
    }
}
