package com.mohith.payflow.dto;

import java.util.List;

public record SupportAnswerResponse(String answer, List<String> sources, String mode) {}
