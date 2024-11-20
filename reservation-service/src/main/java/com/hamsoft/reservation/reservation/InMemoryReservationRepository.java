package com.hamsoft.reservation.reservation;

import jakarta.inject.Singleton;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicLong;

@Singleton
public class InMemoryReservationRepository implements ReservationRepository {

    private  final AtomicLong ids = new AtomicLong(0);
    private final List<Reservation> reservations = new CopyOnWriteArrayList<>();
    @Override
    public List<Reservation> findAll() {
        return Collections.unmodifiableList(reservations);
    }

    @Override
    public Reservation save(Reservation reservation) {
        reservation.id = ids.incrementAndGet();
        reservations.add(reservation);
        return reservation;
    }
}