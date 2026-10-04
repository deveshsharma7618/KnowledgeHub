package com.deveshsharma.knowledgehub.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/document")
/**
 * REST endpoints for document operations.
 */
public class DocumentController {

    @GetMapping
    /**
     * Provides the current document endpoint placeholder response.
     *
     * @return placeholder document response
     */
    public String index() {
        return "Hello World!";
    }
}
