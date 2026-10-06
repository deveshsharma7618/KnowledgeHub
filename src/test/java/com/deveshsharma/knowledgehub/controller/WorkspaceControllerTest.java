package com.deveshsharma.knowledgehub.controller;

import com.deveshsharma.knowledgehub.service.DocumentService;
import com.deveshsharma.knowledgehub.service.WorkspaceService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class WorkspaceControllerTest {

    private WorkspaceService workspaceService;
    private DocumentService documentService;
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        workspaceService = mock(WorkspaceService.class);
        documentService = mock(DocumentService.class);
        mockMvc = MockMvcBuilders.standaloneSetup(
                new WorkspaceController(workspaceService, documentService)
        ).build();
    }

    @Test
    void createsWorkspace() throws Exception {
        mockMvc.perform(post("/api/v1/workspaces")
                        .contentType("application/json")
                        .content("{\"workspaceName\":\"Engineering\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message").value("Workspace created succesfully."));
        verify(workspaceService).create(any());
    }

    @Test
    void uploadsDocument() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "a.txt", "text/plain", "a".getBytes());

        mockMvc.perform(multipart("/api/v1/workspaces/{id}/documents", "workspace-1").file(file))
                .andExpect(status().isOk())
                .andExpect(content().string("Document uploaded!"));
        verify(documentService).addDocument("workspace-1", file);
    }
}
