package ch.want.mylyn.youtrack.core;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;

import org.eclipse.mylyn.tasks.core.TaskRepository;

import ch.want.mylyn.youtrack.model.ProjectCustomField;

public class ProjectConfigurationLoadingCache {

    private static final Duration TTL = Duration.ofMinutes(60);
    private final Map<String, RepositoryEntry> repositoryCache = new ConcurrentHashMap<>();

    public void clear(final String repositoryUrl) {
        repositoryCache.remove(repositoryUrl);
    }

    public Map<String, String> getPriorities(final TaskRepository repository, final String projectId, final YouTrackApi api) {
        return getProjectConfiguration(repository, projectId).getPriorities(api);
    }

    public Map<String, String> getStatus(final TaskRepository repository, final String projectId, final YouTrackApi api) {
        return getProjectConfiguration(repository, projectId).getStatus(api);
    }

    private ProjectConfiguration getProjectConfiguration(final TaskRepository repository, final String projectId) {
        final String key = repository.getUrl();
        final RepositoryEntry repositoryEntry = repositoryCache.computeIfAbsent(key, k -> new RepositoryEntry());
        return repositoryEntry.projectCache.compute(projectId, (k, entry) -> {
            if ((entry == null) || entry.isExpired()) {
                return new ProjectConfiguration(projectId);
            }
            return entry;
        });
    }

    private static class RepositoryEntry {

        private final Map<String, ProjectConfiguration> projectCache = new ConcurrentHashMap<>();
    }

    private static class ProjectConfiguration {

        private final String projectId;
        private LocalDateTime expiration;
        private final SortedMap<String, String> priorities = new TreeMap<>();
        private final SortedMap<String, String> status = new TreeMap<>();

        ProjectConfiguration(final String projectId) {
            this.projectId = projectId;
        }

        Map<String, String> getPriorities(final YouTrackApi api) {
            if (expiration == null) {
                loadConfiguration(projectId, api);
            }
            return Collections.unmodifiableSortedMap(priorities);
        }

        Map<String, String> getStatus(final YouTrackApi api) {
            if (expiration == null) {
                loadConfiguration(projectId, api);
            }
            return Collections.unmodifiableSortedMap(status);
        }

        boolean isExpired() {
            return expiration != null && expiration.isBefore(LocalDateTime.now());
        }

        private synchronized void loadConfiguration(final String projectId, final YouTrackApi api) {
            try {
                final List<ProjectCustomField> customFieldDefinitions = api.getCustomFieldDefinitions(projectId);
                customFieldDefinitions.stream()
                    .filter(fld -> "Priority".equalsIgnoreCase(fld.getField().getName()))
                    .findFirst()
                    .ifPresent(fld -> mapEnumBundle(fld, priorities));
                customFieldDefinitions.stream()
                    .filter(fld -> "State".equalsIgnoreCase(fld.getField().getName()))
                    .findFirst()
                    .ifPresent(fld -> mapEnumBundle(fld, status));
                this.expiration = LocalDateTime.now().plus(ProjectConfigurationLoadingCache.TTL);
            } catch (final IOException e) {
                throw new IllegalStateException("Failed to query project configuration for " + projectId, e);
            }
        }

        private void mapEnumBundle(final ProjectCustomField fld, final SortedMap<String, String> targetMap) {
            fld.getBundle().getValues().forEach(el -> targetMap.put(el.getId(), el.getName()));
        }
    }
}
