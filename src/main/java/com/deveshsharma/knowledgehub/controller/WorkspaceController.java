package com.deveshsharma.knowledgehub.controller;


import com.deveshsharma.knowledgehub.dto.request.CreateWorkspaceRequest;

import com.deveshsharma.knowledgehub.dto.response.WorkspaceResponse;
import com.deveshsharma.knowledgehub.service.DocumentService;
import com.deveshsharma.knowledgehub.service.WorkspaceService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import com.deveshsharma.knowledgehub.dto.response.ErrorResponse;

@RestController
@RequestMapping("/api/v1/workspaces")
public class WorkspaceController {

    private final WorkspaceService workspaceService;
    private final DocumentService documentService;

    public WorkspaceController(WorkspaceService workspaceService, DocumentService documentService) {
        this.workspaceService = workspaceService;
        this.documentService = documentService;
    }

    @PostMapping
    @Operation(summary = "Create workspace", description = "Creates a workspace owned by the authenticated user.")
    @ApiResponse(responseCode = "201", description = "Workspace created")
    @ApiResponse(responseCode = "401", description = "Invalid or expired access token",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<WorkspaceResponse> createWorkspace(@RequestBody CreateWorkspaceRequest createWorkspaceRequest) {
        workspaceService.create(createWorkspaceRequest);
        return new ResponseEntity<WorkspaceResponse>(new WorkspaceResponse("Workspace created succesfully."), HttpStatus.CREATED);
    }

    @PostMapping("/{workspaceId}/documents")
    @Operation(summary = "Upload document", description = "Uploads document metadata and content to a workspace.")
    @ApiResponse(responseCode = "200", description = "Document uploaded")
    @ApiResponse(responseCode = "401", description = "Invalid or expired access token",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "500", description = "Upload failed",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @SecurityRequirement(name = "bearerAuth")
    public String uploadDocument(
            @PathVariable String workspaceId,
            @RequestParam("file") MultipartFile document
    ) throws Exception {
        documentService.addDocument(workspaceId, document);
        return "Document uploaded!";
    }

}
