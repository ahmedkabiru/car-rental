package com.hamsoft.reservation;

import com.hamsoft.reservation.reservation.Reservation;
import com.hamsoft.reservation.reservation.ReservationRepository;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;


@QuarkusTest
class ReservationRepositoryTest {

    @Inject
    ReservationRepository reservationRepository;

    @Test
    void testCreateReservation() {
        var reservation = new Reservation();
        reservation.startDay = LocalDate.now().plusDays(5);
        reservation.endDay = LocalDate.now().plusDays(12);
        reservation.carId = 348L;
        reservationRepository.save(reservation);
        assertNotNull(reservation.id);
        assertTrue(reservationRepository.findAll().contains(reservation));
    }
}
