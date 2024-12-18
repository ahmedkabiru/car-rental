package com.hamsoft.reservation.entity;


import io.quarkus.hibernate.reactive.panache.PanacheEntity;
import io.smallrye.mutiny.Uni;
import jakarta.persistence.Entity;

import java.time.LocalDate;
import java.util.List;

@Entity
public class Reservation extends PanacheEntity {

    public Long id;

    public Long carId;

    public String userId;

    public LocalDate startDay;

    public LocalDate endDay;


    public boolean isReserved() {
        return  !(this.startDay.isAfter(this.endDay) || this.endDay.isBefore(this.startDay));
    }


    public static Uni<List<Reservation>> findByCar(Long carId) {
        return list("carId", carId);
    }

    @Override
    public String toString() {
        return "Reservation{" +
                "id=" + id +
                ", carId=" + carId +
                ", userId=" + userId +
                ", startDay=" + startDay +
                ", endDay=" + endDay +
                '}';
    }
}
