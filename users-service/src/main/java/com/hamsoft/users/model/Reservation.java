package com.hamsoft.users.model;

import java.time.LocalDate;

public class Reservation {
    public Long id;
    public String userId;
    public Long carId;
    public LocalDate startDate;
    public LocalDate endDate;
}
