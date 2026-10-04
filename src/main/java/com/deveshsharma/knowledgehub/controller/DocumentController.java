package com.deveshsharma.knowledgehub.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/document")
public class DocumentController {

    @GetMapping
    public String index() {
        return "Hello World!";
    }
}
