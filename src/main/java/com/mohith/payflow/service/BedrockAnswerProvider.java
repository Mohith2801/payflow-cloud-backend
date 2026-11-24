package com.mohith.payflow.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.bedrockruntime.BedrockRuntimeClient;
import software.amazon.awssdk.services.bedrockruntime.model.*;

@Service
@ConditionalOnProperty(prefix = "app.bedrock", name = "enabled", havingValue = "true")
public class BedrockAnswerProvider implements AiAnswerProvider {
    private final BedrockRuntimeClient client;
    private final String modelId;
    public BedrockAnswerProvider(@Value("${app.bedrock.region:us-east-1}") String region,
                                 @Value("${app.bedrock.model-id}") String modelId) {
        this.client = BedrockRuntimeClient.builder().region(Region.of(region)).build();
        this.modelId = modelId;
    }
    @Override public String generate(String question, String context) {
        String prompt = "Answer the user's support question using ONLY the context below. " +
                "If the answer is not present, say the knowledge base does not contain it. " +
                "Never claim to execute or reverse a payment.\n\nCONTEXT:\n" + context +
                "\n\nQUESTION:\n" + question;
        ConverseRequest request = ConverseRequest.builder()
                .modelId(modelId)
                .messages(Message.builder().role(ConversationRole.USER)
                        .content(ContentBlock.fromText(prompt)).build())
                .inferenceConfig(InferenceConfiguration.builder().maxTokens(350).temperature(0.2f).build())
                .build();
        ConverseResponse response = client.converse(request);
        return response.output().message().content().stream()
                .filter(block -> block.text() != null).map(ContentBlock::text)
                .findFirst().orElse("The AI service returned no text. Please contact support.");
    }
    @Override public String mode() { return "AMAZON_BEDROCK"; }
}
