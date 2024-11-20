package com.hamsoft.rental;

import java.time.LocalDate;

public class Rental {

    private final Long id;

    private final String userId;

    private final Long reservationId;

    private final LocalDate localDate;


    public Rental(Long id, String userId, Long reservationId, LocalDate localDate) {
        this.id = id;
        this.userId = userId;
        this.reservationId = reservationId;
        this.localDate = localDate;
    }

    public Long getId() {
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
