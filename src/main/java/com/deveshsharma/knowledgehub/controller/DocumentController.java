package com.deveshsharma.knowledgehub.controller;

import com.deveshsharma.knowledgehub.service.DocumentService;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import com.deveshsharma.knowledgehub.dto.response.ErrorResponse;

/**
 * REST endpoints for document operations.
 */
@RestController
@RequestMapping("/api/v1/")
public class DocumentController {

    private final DocumentService documentService;
    public DocumentController(DocumentService documentService) {
        this.documentService = documentService;
    }


    /**
     * Provides the current document endpoint placeholder response.
     *
     * @return placeholder document response
     */
    @GetMapping
    @Operation(summary = "Document API health check")
    @ApiResponse(responseCode = "200", description = "Endpoint available")
    @ApiResponse(responseCode = "401", description = "Invalid or expired access token",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @SecurityRequirement(name = "bearerAuth")
    public String index() {
        return "Hello World!";
    }


}
