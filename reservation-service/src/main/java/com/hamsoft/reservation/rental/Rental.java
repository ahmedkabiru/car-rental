package com.hamsoft.reservation.rental;

import java.time.LocalDate;

public class Rental {

    private final String id;

    private final String userId;

    private final Long reservationId;

    private final LocalDate localDate;


    public Rental(String id, String userId, Long reservationId, LocalDate localDate) {
        this.id = id;
        this.userId = userId;
        this.reservationId = reservationId;
        this.localDate = localDate;
    }

    public String getId() {
        return id;
    }

    public String getUserId() {
        return userId;
    }

    public Long getReservationId() {
        return reservationId;
    }

    public LocalDate getLocalDate() {
        return localDate;
    }
}
