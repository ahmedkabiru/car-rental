package com.hamsoft.reservation.reservation;

import java.time.LocalDate;

public class Reservation {

    public Long id;

    public Long carId;

    public Long userId;

    public LocalDate startDay;

    public LocalDate endDay;


    public boolean isReserved() {
        return  !(this.startDay.isAfter(this.endDay) || this.endDay.isBefore(this.startDay));
    }
}
