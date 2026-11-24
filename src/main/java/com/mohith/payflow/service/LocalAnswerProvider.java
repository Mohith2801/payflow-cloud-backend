package com.mohith.payflow.service;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

@Service
@ConditionalOnProperty(prefix = "app.bedrock", name = "enabled", havingValue = "false", matchIfMissing = true)
public class LocalAnswerProvider implements AiAnswerProvider {
    @Override public String generate(String question, String context) {
        return "Based only on the retrieved PayFlow support notes:\n\n" + context
                + "\n\nIf these notes do not resolve your issue, contact support. This assistant does not access bank accounts or execute transactions.";
    }
    @Override public String mode() { return "LOCAL_RETRIEVAL"; }
}
