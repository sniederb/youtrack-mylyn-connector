package ch.want.mylyn.youtrack.ui.editor;

import java.util.Objects;
import java.util.Set;

import org.eclipse.mylyn.internal.tasks.ui.editors.TaskEditorAttributePart;
import org.eclipse.mylyn.internal.tasks.ui.editors.TaskEditorDescriptionPart;
import org.eclipse.mylyn.tasks.ui.editors.AbstractTaskEditorPage;
import org.eclipse.mylyn.tasks.ui.editors.AbstractTaskEditorPart;
import org.eclipse.mylyn.tasks.ui.editors.TaskEditor;
import org.eclipse.mylyn.tasks.ui.editors.TaskEditorPartDescriptor;
import org.eclipse.ui.forms.widgets.ExpandableComposite;

import ch.want.mylyn.youtrack.PluginConstants;

@SuppressWarnings("restriction")
public class YouTrackTaskEditorPage extends AbstractTaskEditorPage {

    public YouTrackTaskEditorPage(final TaskEditor editor) {
        super(editor, PluginConstants.CONNECTOR_KIND);
        setNeedsPrivateSection(false);
        setNeedsSubmitButton(true);
    }

    @Override
    protected Set<TaskEditorPartDescriptor> createPartDescriptors() {
        final Set<TaskEditorPartDescriptor> parts = super.createPartDescriptors();
        removePart(parts, ID_PART_SUMMARY);
        parts.add(new TaskEditorPartDescriptor(ID_PART_SUMMARY) {

            @Override
            public AbstractTaskEditorPart createPart() {
                return new YouTrackTaskEditorSummaryPart();
            }
        }.setPath(PATH_HEADER));
        removePart(parts, ID_PART_ATTRIBUTES);
        parts.add(new TaskEditorPartDescriptor(ID_PART_ATTRIBUTES) {

            @Override
            public AbstractTaskEditorPart createPart() {
                return new TaskEditorAttributePart() {

                    @Override
                    protected boolean shouldExpandOnCreate() {
                        return true;
                    }
                };
            }
        }.setPath(PATH_ATTRIBUTES));
        removePart(parts, ID_PART_DESCRIPTION);
        parts.add(new TaskEditorPartDescriptor(ID_PART_DESCRIPTION) {

            @Override
            public AbstractTaskEditorPart createPart() {
                final TaskEditorDescriptionPart part = new TaskEditorDescriptionPart();
                part.setExpandVertically(true);
                part.setSectionStyle(ExpandableComposite.TITLE_BAR | ExpandableComposite.EXPANDED);
                return part;
            }
        }.setPath(PATH_ATTRIBUTES));
        return parts;
    }

    private boolean removePart(final Set<TaskEditorPartDescriptor> parts, final String partId) {
        return parts.removeIf(desc -> Objects.equals(partId, desc.getId()));
    }
}
