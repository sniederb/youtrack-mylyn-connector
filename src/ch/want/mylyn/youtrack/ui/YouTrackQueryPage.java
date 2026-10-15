package ch.want.mylyn.youtrack.ui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.mylyn.commons.net.AuthenticationCredentials;
import org.eclipse.mylyn.commons.net.AuthenticationType;
import org.eclipse.mylyn.tasks.core.IRepositoryQuery;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.ui.wizards.AbstractRepositoryQueryPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;

import ch.want.mylyn.youtrack.PluginConstants;
import ch.want.mylyn.youtrack.core.YouTrackApi;
import ch.want.mylyn.youtrack.core.YouTrackApiImpl;
import ch.want.mylyn.youtrack.model.SavedQuery;

public class YouTrackQueryPage extends AbstractRepositoryQueryPage {

    private static final String PAGE_DESCRIPTION = "Select a saved search from YouTrack.";
    private final IRepositoryQuery query;
    private Combo savedQueriesCombo;
    private final List<SavedQuery> savedQueries = new ArrayList<>();

    public YouTrackQueryPage(final TaskRepository repository, final IRepositoryQuery query) {
        super("youtrackQueryPage", repository);
        this.query = query;
        setDescription(PAGE_DESCRIPTION);
        setTitle("YouTrack Query");
    }

    @Override
    public void createControl(final Composite parent) {
        final Composite container = new Composite(parent, SWT.NONE);
        container.setLayout(new GridLayout(2, false));
        new Label(container, SWT.NONE).setText("Saved search");
        savedQueriesCombo = new Combo(container, SWT.DROP_DOWN | SWT.READ_ONLY);
        savedQueriesCombo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        savedQueriesCombo.addSelectionListener(SelectionListener.widgetSelectedAdapter(event -> setPageComplete(savedQueriesCombo.getSelectionIndex() >= 0)));
        setControl(container);
        setPageComplete(false);
        loadSavedQueries();
    }

    @Override
    public void applyTo(final IRepositoryQuery repositoryQuery) {
        final int index = savedQueriesCombo.getSelectionIndex();
        if (index >= 0) {
            final SavedQuery selected = savedQueries.get(index);
            repositoryQuery.setAttribute(PluginConstants.ATTRIBUTE_SAVED_QUERY_ID, selected.id());
            repositoryQuery.setSummary(selected.name());
        }
    }

    @Override
    public String getQueryTitle() {
        final int index = savedQueriesCombo.getSelectionIndex();
        return index < 0 ? "" : savedQueries.get(index).name();
    }

    private void loadSavedQueries() {
        final String repositoryUrl = getTaskRepository().getRepositoryUrl();
        final AuthenticationCredentials credentials = getTaskRepository().getCredentials(AuthenticationType.REPOSITORY);
        if ((credentials == null) || (credentials.getPassword() == null) || (credentials.getPassword().isBlank())) {
            setErrorMessage("No permanent token configured for this repository.");
            return;
        }
        final String permanentToken = credentials.getPassword();
        final Job job = new LoadSavedQueriesJob(repositoryUrl, permanentToken);
        job.setUser(true);
        job.schedule();
    }

    private class LoadSavedQueriesJob extends Job {

        private final String repositoryUrl;
        private final String permanentToken;

        public LoadSavedQueriesJob(final String repositoryUrl, final String permanentToken) {
            super("Loading YouTrack saved searches");
            this.repositoryUrl = repositoryUrl;
            this.permanentToken = permanentToken;
        }

        @Override
        protected IStatus run(final IProgressMonitor monitor) {
            try {
                savedQueries.clear();
                final YouTrackApi api = new YouTrackApiImpl(repositoryUrl, permanentToken);
                final List<SavedQuery> results = api.getSavedQueries();
                Display.getDefault().asyncExec(() -> {
                    if (savedQueriesCombo.isDisposed()) {
                        return;
                    }
                    savedQueriesCombo.removeAll();
                    savedQueries.addAll(results);
                    savedQueries.stream()
                        .map(SavedQuery::name)
                        .forEach(savedQueriesCombo::add);
                    final String selectedId = query == null ? null : query.getAttribute(PluginConstants.ATTRIBUTE_SAVED_QUERY_ID);
                    for (int i = 0; i < savedQueries.size(); i++) {
                        if (Objects.equals(savedQueries.get(i).id(), selectedId)) {
                            savedQueriesCombo.select(i);
                            break;
                        }
                    }
                    setPageComplete(savedQueriesCombo.getSelectionIndex() >= 0);
                });
                return Status.OK_STATUS;
            } catch (final IOException ex) {
                return new Status(IStatus.ERROR, PluginConstants.PLUGIN_ID, "Failed to load YouTrack saved searches", ex);
            }
        }
    }
}
