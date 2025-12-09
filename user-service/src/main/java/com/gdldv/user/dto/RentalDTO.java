package com.gdldv.user.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class RentalDTO {
    private Long id;
    private Long reservationId;
    private Long userId;
    private Long vehicleId;
    private Long employeeId;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private LocalDateTime actualStartDate;
    private LocalDateTime actualEndDate;
    private String pickupLocation;
    private String returnLocation;
    private BigDecimal basePrice;
    private BigDecimal additionalCharges;
    private BigDecimal totalPrice;
    private BigDecimal deposit;
    private String status;
    private Integer startKilometers;
    private Integer endKilometers;
    private String startFuelLevel;
    private String endFuelLevel;
    private String checkoutNotes;
    private String checkinNotes;
    private String clientName;
    private String vehicleInfo;
    private String employeeName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public RentalDTO() {
    }

    public RentalDTO(Long id, Long reservationId, Long userId, Long vehicleId, Long employeeId, LocalDateTime startDate, LocalDateTime endDate, LocalDateTime actualStartDate, LocalDateTime actualEndDate, String pickupLocation, String returnLocation, BigDecimal basePrice, BigDecimal additionalCharges, BigDecimal totalPrice, BigDecimal deposit, String status, Integer startKilometers, Integer endKilometers, String startFuelLevel, String endFuelLevel, String checkoutNotes, String checkinNotes, String clientName, String vehicleInfo, String employeeName, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.reservationId = reservationId;
        this.userId = userId;
        this.vehicleId = vehicleId;
        this.employeeId = employeeId;
        this.startDate = startDate;
        this.endDate = endDate;
        this.actualStartDate = actualStartDate;
        this.actualEndDate = actualEndDate;
        this.pickupLocation = pickupLocation;
        this.returnLocation = returnLocation;
        this.basePrice = basePrice;
        this.additionalCharges = additionalCharges;
        this.totalPrice = totalPrice;
        this.deposit = deposit;
        this.status = status;
        this.startKilometers = startKilometers;
        this.endKilometers = endKilometers;
        this.startFuelLevel = startFuelLevel;
        this.endFuelLevel = endFuelLevel;
        this.checkoutNotes = checkoutNotes;
        this.checkinNotes = checkinNotes;
        this.clientName = clientName;
        this.vehicleInfo = vehicleInfo;
        this.employeeName = employeeName;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getReservationId() {
        return reservationId;
    }

    public void setReservationId(Long reservationId) {
        this.reservationId = reservationId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getVehicleId() {
        return vehicleId;
    }

    public void setVehicleId(Long vehicleId) {
        this.vehicleId = vehicleId;
    }

    public Long getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(Long employeeId) {
        this.employeeId = employeeId;
    }

    public LocalDateTime getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDateTime startDate) {
        this.startDate = startDate;
    }

    public LocalDateTime getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDateTime endDate) {
        this.endDate = endDate;
    }

    public LocalDateTime getActualStartDate() {
        return actualStartDate;
    }

    public void setActualStartDate(LocalDateTime actualStartDate) {
        this.actualStartDate = actualStartDate;
    }

    public LocalDateTime getActualEndDate() {
        return actualEndDate;
    }

    public void setActualEndDate(LocalDateTime actualEndDate) {
        this.actualEndDate = actualEndDate;
    }

    public String getPickupLocation() {
        return pickupLocation;
    }

    public void setPickupLocation(String pickupLocation) {
        this.pickupLocation = pickupLocation;
    }

    public String getReturnLocation() {
        return returnLocation;
    }

    public void setReturnLocation(String returnLocation) {
        this.returnLocation = returnLocation;
    }

    public BigDecimal getBasePrice() {
        return basePrice;
    }

    public void setBasePrice(BigDecimal basePrice) {
        this.basePrice = basePrice;
    }

    public BigDecimal getAdditionalCharges() {
        return additionalCharges;
    }

    public void setAdditionalCharges(BigDecimal additionalCharges) {
        this.additionalCharges = additionalCharges;
    }

    public BigDecimal getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(BigDecimal totalPrice) {
        this.totalPrice = totalPrice;
    }

    public BigDecimal getDeposit() {
        return deposit;
    }

    public void setDeposit(BigDecimal deposit) {
        this.deposit = deposit;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Integer getStartKilometers() {
        return startKilometers;
    }

    public void setStartKilometers(Integer startKilometers) {
        this.startKilometers = startKilometers;
    }

    public Integer getEndKilometers() {
        return endKilometers;
    }

    public void setEndKilometers(Integer endKilometers) {
        this.endKilometers = endKilometers;
    }

    public String getStartFuelLevel() {
        return startFuelLevel;
    }

    public void setStartFuelLevel(String startFuelLevel) {
        this.startFuelLevel = startFuelLevel;
    }

    public String getEndFuelLevel() {
        return endFuelLevel;
    }

    public void setEndFuelLevel(String endFuelLevel) {
        this.endFuelLevel = endFuelLevel;
    }

    public String getCheckoutNotes() {
        return checkoutNotes;
    }

    public void setCheckoutNotes(String checkoutNotes) {
        this.checkoutNotes = checkoutNotes;
    }

    public String getCheckinNotes() {
        return checkinNotes;
    }

    public void setCheckinNotes(String checkinNotes) {
        this.checkinNotes = checkinNotes;
    }

    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    public String getVehicleInfo() {
        return vehicleInfo;
    }

    public void setVehicleInfo(String vehicleInfo) {
        this.vehicleInfo = vehicleInfo;
    }

    public String getEmployeeName() {
        return employeeName;
    }

    public void setEmployeeName(String employeeName) {
        this.employeeName = employeeName;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
