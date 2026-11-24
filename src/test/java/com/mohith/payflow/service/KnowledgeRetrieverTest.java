package com.mohith.payflow.service;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class KnowledgeRetrieverTest {
    private final KnowledgeRetriever retriever = new KnowledgeRetriever();
    @Test void retrievesRelevantIdempotencyKnowledge() {
        var results = retriever.retrieve("How do I retry a duplicate payment request with an idempotency key?", 3);
        assertFalse(results.isEmpty());
        assertTrue(results.get(0).name().contains("idempotency"));
    }
    @Test void returnsNoDocumentsForUnrelatedQuestion() {
        assertTrue(retriever.retrieve("What is the weather on Mars today?", 3).isEmpty());
    }
}
