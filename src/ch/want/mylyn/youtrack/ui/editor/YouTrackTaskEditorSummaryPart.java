package ch.want.mylyn.youtrack.ui.editor;

import org.eclipse.mylyn.internal.tasks.ui.editors.TaskEditorSummaryPart;
import org.eclipse.mylyn.tasks.core.data.TaskAttribute;
import org.eclipse.mylyn.tasks.ui.editors.AbstractAttributeEditor;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Layout;
import org.eclipse.ui.forms.widgets.FormToolkit;

@SuppressWarnings("restriction")
public class YouTrackTaskEditorSummaryPart extends TaskEditorSummaryPart {

    @Override
    protected Composite createHeaderLayout(final Composite parent, final FormToolkit toolkit) {
        // super() will already add a series of attributes
        final Composite composite = super.createHeaderLayout(parent, toolkit);
        addReadOnlyAttribute(composite, toolkit, TaskAttribute.USER_REPORTER);
        addReadOnlyAttribute(composite, toolkit, TaskAttribute.USER_ASSIGNED);
        final Layout layout = composite.getLayout();
        if (layout instanceof final GridLayout gridLayout) {
            gridLayout.numColumns = composite.getChildren().length;
        }
        return composite;
    }

    private void addReadOnlyAttribute(final Composite composite, final FormToolkit toolkit, final String attributeId) {
        final TaskAttribute attribute = getTaskData().getRoot().getMappedAttribute(attributeId);
        if (attribute == null) {
            return;
        }
        final AbstractAttributeEditor editor = createAttributeEditor(attribute);
        if (editor == null) {
            return;
        }
        editor.setReadOnly(true);
        editor.setDecorationEnabled(false);
        editor.createLabelControl(composite, toolkit);
        editor.createControl(composite, toolkit);
        getTaskEditorPage().getAttributeEditorToolkit().adapt(editor);
    }
}
