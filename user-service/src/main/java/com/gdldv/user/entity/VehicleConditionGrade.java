package com.gdldv.user.entity;

public enum VehicleConditionGrade {
    A_PLUS("Excellent état"),
    A("Bon état"),
    B("État acceptable"),
    C("Mauvais état");

    private final String description;

    VehicleConditionGrade(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
