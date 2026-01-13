package com.gdldv.user.service;

import com.gdldv.user.client.VehicleServiceClient;
import com.gdldv.user.dto.ClientDashboard;
import com.gdldv.user.entity.Reservation;
import com.gdldv.user.entity.ReservationStatus;
import com.gdldv.user.entity.User;
import com.gdldv.user.repository.ReservationRepository;
import com.gdldv.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Tests unitaires pour ClientDashboardService
 */
@ExtendWith(MockitoExtension.class)
class ClientDashboardServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private ReservationRepository reservationRepository;

    @Mock
    private VehicleServiceClient vehicleServiceClient;

    @InjectMocks
    private ClientDashboardService clientDashboardService;

    private User testUser;
    private List<Reservation> testReservations;

    @BeforeEach
    void setUp() {
        // Préparer un utilisateur de test
        testUser = new User();
        testUser.setId(1L);
        testUser.setFirstName("Jean");
        testUser.setLastName("Dupont");
        testUser.setEmail("jean@example.com");
        testUser.setCreatedAt(LocalDateTime.now().minusMonths(6));

        // Préparer des réservations de test
        testReservations = new ArrayList<>();

        // Réservation active
        Reservation activeReservation = new Reservation();
        activeReservation.setId(1L);
        activeReservation.setUserId(1L);
        activeReservation.setVehicleId(10L);
        activeReservation.setStatus(ReservationStatus.ACTIVE);
        activeReservation.setStartDate(LocalDateTime.now().minusDays(2));
        activeReservation.setEndDate(LocalDateTime.now().plusDays(3));
        activeReservation.setTotalPrice(125000.0);
        activeReservation.setConfirmationNumber("CONF-ABC123");
        activeReservation.setCreatedAt(LocalDateTime.now().minusDays(5));
        testReservations.add(activeReservation);

        // Réservation complétée
        Reservation completedReservation = new Reservation();
        completedReservation.setId(2L);
        completedReservation.setUserId(1L);
        completedReservation.setVehicleId(11L);
        completedReservation.setStatus(ReservationStatus.COMPLETED);
        completedReservation.setStartDate(LocalDateTime.now().minusDays(20));
        completedReservation.setEndDate(LocalDateTime.now().minusDays(15));
        completedReservation.setTotalPrice(150000.0);
        completedReservation.setConfirmationNumber("CONF-XYZ789");
        completedReservation.setCreatedAt(LocalDateTime.now().minusDays(25));
        testReservations.add(completedReservation);
    }

    @Test
    void testGetClientDashboard_Success() {
        // Arrange
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(reservationRepository.findByUserId(1L)).thenReturn(testReservations);
        when(reservationRepository.findByUserIdAndStatus(1L, ReservationStatus.ACTIVE))
                .thenReturn(List.of(testReservations.get(0)));
        when(vehicleServiceClient.getFavoritesByUserId(1L)).thenReturn(new ArrayList<>());

        // Act
        ClientDashboard dashboard = clientDashboardService.getClientDashboard(1L);

        // Assert
        assertNotNull(dashboard);
        assertEquals(1L, dashboard.getUserId());
        assertEquals("Jean", dashboard.getFirstName());
        assertEquals("Dupont", dashboard.getLastName());
        assertEquals("jean@example.com", dashboard.getEmail());
        assertEquals(1, dashboard.getActiveRentals());
        assertEquals(1, dashboard.getTotalRentals()); // 1 complétée
        assertEquals(150000.0, dashboard.getTotalSpent());
        assertEquals("Régulier", dashboard.getMembershipBadge());

        // Vérifier les appels
        verify(userRepository, times(1)).findById(1L);
        verify(reservationRepository, times(1)).findByUserId(1L);
        verify(vehicleServiceClient, times(1)).getFavoritesByUserId(1L);
    }

    @Test
    void testGetClientDashboard_UserNotFound() {
        // Arrange
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            clientDashboardService.getClientDashboard(999L);
        });

        verify(userRepository, times(1)).findById(999L);
        verify(reservationRepository, never()).findByUserId(any());
    }

    @Test
    void testGetClientDashboard_NoReservations() {
        // Arrange
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(reservationRepository.findByUserId(1L)).thenReturn(new ArrayList<>());
        when(reservationRepository.findByUserIdAndStatus(1L, ReservationStatus.ACTIVE))
                .thenReturn(new ArrayList<>());
        when(vehicleServiceClient.getFavoritesByUserId(1L)).thenReturn(new ArrayList<>());

        // Act
        ClientDashboard dashboard = clientDashboardService.getClientDashboard(1L);

        // Assert
        assertNotNull(dashboard);
        assertEquals(0, dashboard.getActiveRentals());
        assertEquals(0, dashboard.getTotalRentals());
        assertEquals(0.0, dashboard.getTotalSpent());
        assertEquals("Nouveau", dashboard.getMembershipBadge());
        assertTrue(dashboard.getCurrentRentals().isEmpty());
        assertTrue(dashboard.getRecentHistory().isEmpty());
    }

    @Test
    void testMembershipBadge_VIP() {
        // Préparer 20+ réservations complétées
        List<Reservation> manyReservations = new ArrayList<>();
        for (int i = 0; i < 25; i++) {
            Reservation res = new Reservation();
            res.setId((long) i);
            res.setUserId(1L);
            res.setStatus(ReservationStatus.COMPLETED);
            res.setTotalPrice(50000.0);
            res.setStartDate(LocalDateTime.now().minusDays(30 + i));
            res.setEndDate(LocalDateTime.now().minusDays(25 + i));
            res.setCreatedAt(LocalDateTime.now().minusDays(35 + i));
            manyReservations.add(res);
        }

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(reservationRepository.findByUserId(1L)).thenReturn(manyReservations);
        when(reservationRepository.findByUserIdAndStatus(1L, ReservationStatus.ACTIVE))
                .thenReturn(new ArrayList<>());
        when(vehicleServiceClient.getFavoritesByUserId(1L)).thenReturn(new ArrayList<>());

        // Act
        ClientDashboard dashboard = clientDashboardService.getClientDashboard(1L);

        // Assert
        assertEquals("VIP", dashboard.getMembershipBadge());
        assertEquals(25, dashboard.getTotalRentals());
    }

    @Test
    void testGetClientDashboard_WithVehicleServiceFailure() {
        // Arrange
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(reservationRepository.findByUserId(1L)).thenReturn(testReservations);
        when(reservationRepository.findByUserIdAndStatus(1L, ReservationStatus.ACTIVE))
                .thenReturn(List.of(testReservations.get(0)));
        when(vehicleServiceClient.getFavoritesByUserId(1L))
                .thenThrow(new RuntimeException("Service unavailable"));

        // Act
        ClientDashboard dashboard = clientDashboardService.getClientDashboard(1L);

        // Assert - le dashboard doit quand même être retourné avec favoris vides
        assertNotNull(dashboard);
        assertTrue(dashboard.getFavorites().isEmpty());
    }
}
