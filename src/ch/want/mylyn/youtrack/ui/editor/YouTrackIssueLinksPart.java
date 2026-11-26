package ch.want.mylyn.youtrack.ui.editor;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.eclipse.mylyn.tasks.core.data.TaskAttribute;
import org.eclipse.mylyn.tasks.core.data.TaskAttributeMapper;
import org.eclipse.mylyn.tasks.core.data.TaskData;
import org.eclipse.mylyn.tasks.ui.TasksUiUtil;
import org.eclipse.mylyn.tasks.ui.editors.AbstractTaskEditorPart;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.events.HyperlinkAdapter;
import org.eclipse.ui.forms.events.HyperlinkEvent;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Hyperlink;
import org.eclipse.ui.forms.widgets.Section;

import ch.want.mylyn.youtrack.PluginConstants;
import ch.want.mylyn.youtrack.YouTrackAttribute;

public class YouTrackIssueLinksPart extends AbstractTaskEditorPart {

    public YouTrackIssueLinksPart() {
        setPartName("Links");
    }

    @Override
    public void createControl(final Composite parent, final FormToolkit toolkit) {
        final Section section = createSection(parent, toolkit, true);
        final Composite content = toolkit.createComposite(section);
        content.setLayout(new GridLayout(3, false));
        content.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false));
        final List<LinkedIssue> links = getIssueLinks();
        if (links.isEmpty()) {
            toolkit.createLabel(content, "No linked issues", SWT.NONE);
        } else {
            for (final LinkedIssue linkedIssue : links) {
                toolkit.createLabel(content, linkedIssue.type(), SWT.NONE);
                final Hyperlink issueId = toolkit.createHyperlink(content, linkedIssue.id(), SWT.NONE);
                issueId.addHyperlinkListener(new HyperlinkAdapter() {

                    @Override
                    public void linkActivated(final HyperlinkEvent event) {
                        TasksUiUtil.openTask(getTaskEditorPage().getTaskRepository(), linkedIssue.id());
                    }
                });
                toolkit.createLabel(content, linkedIssue.summary(), SWT.NONE);
            }
        }
        toolkit.paintBordersFor(content);
        section.setClient(content);
        setSection(toolkit, section);
    }

    private List<LinkedIssue> getIssueLinks() {
        final TaskData taskData = getTaskData();
        final TaskAttributeMapper mapper = taskData.getAttributeMapper();
        final List<LinkedIssue> result = new ArrayList<>();
        for (final TaskAttribute linkAttribute : mapper.getAttributesByType(taskData, PluginConstants.TYPE_LINK)) {
            final String issueId = mapper.getValue(linkAttribute);
            if (issueId != null && !issueId.isBlank()) {
                final String type = Optional.ofNullable(linkAttribute.getAttribute(YouTrackAttribute.LINK_TYPE.id()))
                    .map(mapper::getValue)
                    .orElse("");
                final String targetId = Optional.ofNullable(linkAttribute.getAttribute(YouTrackAttribute.LINK_TARGET_ID.id()))
                    .map(mapper::getValue)
                    .orElse("");
                final String targetSummary = Optional.ofNullable(linkAttribute.getAttribute(YouTrackAttribute.LINK_TARGET_SUMMARY.id()))
                    .map(mapper::getValue)
                    .orElse("");
                result.add(new LinkedIssue(type, targetId, targetSummary));
            }
        }
        return result;
    }

    private static record LinkedIssue(String type, String id, String summary) {
    }
}
