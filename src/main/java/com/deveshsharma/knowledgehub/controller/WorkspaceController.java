package com.deveshsharma.knowledgehub.controller;


import com.deveshsharma.knowledgehub.dto.request.CreateWorkspaceRequest;
import com.deveshsharma.knowledgehub.dto.request.AddWorkspaceMemberRequest;

import com.deveshsharma.knowledgehub.dto.response.WorkspaceResponse;
import com.deveshsharma.knowledgehub.service.DocumentService;
import com.deveshsharma.knowledgehub.service.WorkspaceService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
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
    public ResponseEntity<WorkspaceResponse> createWorkspace(@Valid @RequestBody CreateWorkspaceRequest createWorkspaceRequest) {
        workspaceService.create(createWorkspaceRequest);
        return new ResponseEntity<>(new WorkspaceResponse("Workspace created succesfully."), HttpStatus.CREATED);
    }

    @GetMapping
    public List<com.deveshsharma.knowledgehub.dto.response.WorkspaceDetailsResponse> listWorkspaces() {
        return workspaceService.findMine();
    }

    @GetMapping("/{workspaceId}")
    public com.deveshsharma.knowledgehub.dto.response.WorkspaceDetailsResponse getWorkspace(
            @PathVariable UUID workspaceId) {
        return workspaceService.find(workspaceId);
    }

    @PutMapping("/{workspaceId}")
    public com.deveshsharma.knowledgehub.dto.response.WorkspaceDetailsResponse updateWorkspace(
            @PathVariable UUID workspaceId, @Valid @RequestBody com.deveshsharma.knowledgehub.dto.request.UpdateWorkspaceRequest request) {
        return workspaceService.update(workspaceId, request);
    }

    @DeleteMapping("/{workspaceId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteWorkspace(@PathVariable UUID workspaceId) {
        workspaceService.delete(workspaceId);
    }

    @PostMapping("/{workspaceId}/members")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void addMember(@PathVariable UUID workspaceId,
                          @Valid @RequestBody AddWorkspaceMemberRequest request) {
        workspaceService.addMember(workspaceId, request.userId());
    }

    @PostMapping("/{workspaceId}/members/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void addMember(@PathVariable UUID workspaceId, @PathVariable UUID userId) {
        workspaceService.addMember(workspaceId, userId);
    }

    @DeleteMapping("/{workspaceId}/members/me")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void leaveWorkspace(@PathVariable UUID workspaceId) {
        workspaceService.leave(workspaceId);
    }

    @DeleteMapping("/{workspaceId}/members/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeMember(@PathVariable UUID workspaceId, @PathVariable UUID userId) {
        workspaceService.removeMember(workspaceId, userId);
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

    @GetMapping("/{workspaceId}/documents")
    public java.util.List<com.deveshsharma.knowledgehub.dto.response.DocumentResponse> listDocuments(
            @PathVariable UUID workspaceId) {
        return documentService.findByWorkspace(workspaceId);
    }

    @GetMapping("/{workspaceId}/documents/{documentId}")
    public com.deveshsharma.knowledgehub.dto.response.DocumentResponse getDocument(
            @PathVariable UUID workspaceId, @PathVariable UUID documentId) {
        return documentService.find(workspaceId, documentId);
    }

    @DeleteMapping("/{workspaceId}/documents/{documentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteDocument(@PathVariable UUID workspaceId, @PathVariable UUID documentId) throws Exception {
        documentService.delete(workspaceId, documentId);
    }

}
