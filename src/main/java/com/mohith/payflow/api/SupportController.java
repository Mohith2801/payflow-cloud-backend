package com.mohith.payflow.api;

import com.mohith.payflow.dto.SupportAnswerResponse;
import com.mohith.payflow.dto.SupportQuestionRequest;
import com.mohith.payflow.service.SupportAssistant;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/support")
public class SupportController {
    private final SupportAssistant assistant;
    public SupportController(SupportAssistant assistant) { this.assistant = assistant; }
    @PostMapping("/ask")
    public SupportAnswerResponse ask(@Valid @RequestBody SupportQuestionRequest request) {
        return assistant.answer(request.question());
    }
}
