package com.hamsoft.reservation.reservation;

import java.time.LocalDate;

public class Reservation {

    public Long id;

    public String userId;

    public Long carId;

    public LocalDate startDay;

    public LocalDate endDay;

    public Reservation(Long id, String userId, Long carId, LocalDate startDay, LocalDate endDay) {
        this.id = id;
        this.userId = userId;
        this.carId = carId;
        this.startDay = startDay;
        this.endDay = endDay;
    }


    public  boolean isReserved() {
        return  !(this.startDay.isAfter(this.endDay) || this.endDay.isBefore(this.startDay));
    }
}
