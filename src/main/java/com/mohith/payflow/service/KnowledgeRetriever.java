package com.mohith.payflow.service;

import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.stereotype.Component;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class KnowledgeRetriever {
    public record RetrievedDocument(String name, String content, int score) {}

    public List<RetrievedDocument> retrieve(String question, int limit) {
        try {
            Resource[] resources = new PathMatchingResourcePatternResolver().getResources("classpath*:support-knowledge/*.md");
            Set<String> queryTokens = tokens(question);
            List<RetrievedDocument> results = new ArrayList<>();
            for (Resource resource : resources) {
                String content = new String(resource.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
                Set<String> docTokens = tokens(content);
                int score = (int) queryTokens.stream().filter(docTokens::contains).count();
                if (score > 0) results.add(new RetrievedDocument(resource.getFilename(), content, score));
            }
            return results.stream().sorted(Comparator.comparingInt(RetrievedDocument::score).reversed())
                    .limit(limit).toList();
        } catch (Exception ex) {
            throw new IllegalStateException("Unable to load support knowledge", ex);
        }
    }
    private Set<String> tokens(String text) {
        return Arrays.stream(text.toLowerCase(Locale.ROOT).replaceAll("[^a-z0-9 ]", " ").split("\\s+"))
                .filter(t -> t.length() > 2).collect(Collectors.toSet());
    }
}
