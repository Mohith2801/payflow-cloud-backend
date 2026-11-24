package com.mohith.payflow.service;

import com.mohith.payflow.dto.SupportAnswerResponse;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SupportAssistant {
    private final KnowledgeRetriever retriever;
    private final AiAnswerProvider answerProvider;
    public SupportAssistant(KnowledgeRetriever retriever, AiAnswerProvider answerProvider) {
        this.retriever = retriever; this.answerProvider = answerProvider;
    }
    public SupportAnswerResponse answer(String question) {
        var docs = retriever.retrieve(question, 3);
        if (docs.isEmpty()) return new SupportAnswerResponse(
                "I could not find relevant information in the PayFlow support notes. Please contact support; I will not guess.",
                List.of(), "ABSTAINED");
        String context = docs.stream().map(d -> "SOURCE: " + d.name() + "\n" + d.content()).collect(Collectors.joining("\n---\n"));
        String answer = answerProvider.generate(question, context);
        return new SupportAnswerResponse(answer, docs.stream().map(KnowledgeRetriever.RetrievedDocument::name).toList(), answerProvider.mode());
    }
}
