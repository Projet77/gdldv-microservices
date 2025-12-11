package com.gdldv.user.dto;

import lombok.*;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdminDashboard {
    private Integer todayReservations;
    private Integer activeRentals;
    private Integer pendingPayments;
    private Long totalUsers;
    private Double todayRevenue;
    private List<UrgentTask> urgentTasks;
}
