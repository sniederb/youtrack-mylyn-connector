package ch.want.mylyn.youtrack.core;

import org.eclipse.mylyn.tasks.core.ITask.PriorityLevel;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.core.data.TaskAttribute;
import org.eclipse.mylyn.tasks.core.data.TaskAttributeMapper;

public class YouTrackTaskAttributeMapper extends TaskAttributeMapper {

    public YouTrackTaskAttributeMapper(final TaskRepository repository) {
        super(repository);
    }

    @Override
    public PriorityLevel getPriorityLevel(final TaskAttribute priorityAttribute, final String priorityOption) {
        if (priorityOption == null) {
            return null;
        }
        return switch (priorityOption.toLowerCase()) {
        case "show-stopper" -> PriorityLevel.P1;
        case "critical" -> PriorityLevel.P2;
        case "normal" -> PriorityLevel.P4;
        case "minor" -> PriorityLevel.P5;
        case "major" -> PriorityLevel.P3;
        default -> PriorityLevel.P3;
        };
    }
}
