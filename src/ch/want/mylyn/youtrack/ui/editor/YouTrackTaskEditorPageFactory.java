package ch.want.mylyn.youtrack.ui.editor;

import org.eclipse.mylyn.commons.ui.CommonImages;
import org.eclipse.mylyn.tasks.ui.TasksUiImages;
import org.eclipse.mylyn.tasks.ui.TasksUiUtil;
import org.eclipse.mylyn.tasks.ui.editors.AbstractTaskEditorPageFactory;
import org.eclipse.mylyn.tasks.ui.editors.TaskEditor;
import org.eclipse.mylyn.tasks.ui.editors.TaskEditorInput;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.forms.editor.IFormPage;

import ch.want.mylyn.youtrack.PluginConstants;

public class YouTrackTaskEditorPageFactory extends AbstractTaskEditorPageFactory {

    @Override
    public boolean canCreatePageFor(final TaskEditorInput input) {
        return input.getTask().getConnectorKind().equals(PluginConstants.CONNECTOR_KIND) ||
            TasksUiUtil.isOutgoingNewTask(input.getTask(), PluginConstants.CONNECTOR_KIND);
    }

    @Override
    public IFormPage createPage(final TaskEditor parentEditor) {
        return new YouTrackTaskEditorPage(parentEditor);
    }

    @Override
    public int getPriority() {
        return PRIORITY_TASK;
    }

    @Override
    public Image getPageImage() {
        return CommonImages.getImage(TasksUiImages.REPOSITORY_SMALL);
    }

    @Override
    public String getPageText() {
        return PluginConstants.YOUTRACK_LABEL;
    }
}
