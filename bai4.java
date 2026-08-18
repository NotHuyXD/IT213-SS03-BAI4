package com.example.ai.controller;

import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.converter.BeanOutputConverter;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
public class EtlExtractionController {

    private final ChatModel chatModel;

    public EtlExtractionController(ChatModel chatModel) {
        this.chatModel = chatModel;
    }

    public record Entity(String name, String type, String details) {}
    public record ExtractedEntities(List<Entity> entities) {}

    @PostMapping("/ai/etl/extract")
    public ExtractedEntities extractEntities(@RequestBody String rawText) {
        BeanOutputConverter<ExtractedEntities> outputConverter = new BeanOutputConverter<>(ExtractedEntities.class);
        String format = outputConverter.getFormat();

        PromptTemplate promptTemplate = new PromptTemplate(
            "Trích xuất toàn bộ các thực thể (như tên người, tổ chức, công nghệ, địa danh) từ đoạn văn bản thô sau đây:\n\n" +
            "{rawText}\n\n" +
            "{format}"
        );
        promptTemplate.add("rawText", rawText);
        promptTemplate.add("format", format);

        Prompt prompt = promptTemplate.create();
        String response = chatModel.call(prompt).getResult().getOutput().getContent();
        return outputConverter.convert(response);
    }
}