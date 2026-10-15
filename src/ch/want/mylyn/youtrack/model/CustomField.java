package ch.want.mylyn.youtrack.model;

import com.fasterxml.jackson.databind.JsonNode;

public record CustomField(
        String name,
        JsonNode value) {
}