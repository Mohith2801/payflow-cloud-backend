package com.mohith.payflow.service;

public interface AiAnswerProvider {
    String generate(String question, String context);
    String mode();
}
