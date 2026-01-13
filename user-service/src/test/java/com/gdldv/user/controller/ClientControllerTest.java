package com.gdldv.user.controller;

import com.gdldv.user.dto.ClientDashboard;
import com.gdldv.user.service.ClientDashboardService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.ArrayList;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Tests d'intégration pour ClientController
 */
@WebMvcTest(ClientController.class)
class ClientControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ClientDashboardService clientDashboardService;

    private ClientDashboard testDashboard;

    @BeforeEach
    void setUp() {
        testDashboard = ClientDashboard.builder()
                .userId(1L)
                .firstName("Jean")
                .lastName("Dupont")
                .email("jean@example.com")
                .memberSince(LocalDateTime.now().minusMonths(6))
                .membershipBadge("Régulier")
                .averageRating(4.5)
                .activeRentals(1)
                .totalRentals(10)
                .totalSpent(500000.0)
                .averageSpentPerRental(50000.0)
                .averageDuration(4.0)
                .favoriteCategory("SUV")
                .currentRentals(new ArrayList<>())
                .recentHistory(new ArrayList<>())
                .favorites(new ArrayList<>())
                .recentPayments(new ArrayList<>())
                .monthlyTotal(125000.0)
                .yearlyTotal(500000.0)
                .build();
    }

    @Test
    @WithMockUser(roles = "CLIENT")
    void testGetDashboard_Success() throws Exception {
        // Arrange
        when(clientDashboardService.getClientDashboard(1L)).thenReturn(testDashboard);

        // Act & Assert
        mockMvc.perform(get("/api/client/dashboard")
                        .param("userId", "1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(1))
                .andExpect(jsonPath("$.firstName").value("Jean"))
                .andExpect(jsonPath("$.lastName").value("Dupont"))
                .andExpect(jsonPath("$.membershipBadge").value("Régulier"))
                .andExpect(jsonPath("$.totalRentals").value(10))
                .andExpect(jsonPath("$.totalSpent").value(500000.0));
    }

    @Test
    @WithMockUser(roles = "CLIENT")
    void testGetDashboard_UserNotFound() throws Exception {
        // Arrange
        when(clientDashboardService.getClientDashboard(999L))
                .thenThrow(new IllegalArgumentException("User not found"));

        // Act & Assert
        mockMvc.perform(get("/api/client/dashboard")
                        .param("userId", "999")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void testGetDashboard_Unauthorized() throws Exception {
        // Act & Assert - sans authentification
        mockMvc.perform(get("/api/client/dashboard")
                        .param("userId", "1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "AGENT") // Mauvais rôle
    void testGetDashboard_Forbidden() throws Exception {
        // Act & Assert - avec rôle AGENT au lieu de CLIENT
        mockMvc.perform(get("/api/client/dashboard")
                        .param("userId", "1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "CLIENT")
    void testGetStatistics_Success() throws Exception {
        // Arrange
        when(clientDashboardService.getClientDashboard(1L)).thenReturn(testDashboard);

        // Act & Assert
        mockMvc.perform(get("/api/client/statistics")
                        .param("userId", "1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalRentals").value(10))
                .andExpect(jsonPath("$.totalSpent").value(500000.0))
                .andExpect(jsonPath("$.averageSpentPerRental").value(50000.0))
                .andExpect(jsonPath("$.favoriteCategory").value("SUV"))
                .andExpect(jsonPath("$.membershipBadge").value("Régulier"));
    }
}
