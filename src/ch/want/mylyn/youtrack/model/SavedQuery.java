package ch.want.mylyn.youtrack.model;

import java.util.List;

public record SavedQuery(String id, String query, String name, List<Issue> issues) {
}
