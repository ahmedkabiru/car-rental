package com.hamsoft.reservation;

import com.hamsoft.reservation.entity.Reservation;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;


@QuarkusTest
class ReservationRepositoryTest {


    @Test
    @Transactional
    void testCreateReservation() {
        var reservation = new Reservation();
        reservation.startDay = LocalDate.now().plusDays(5);
        reservation.endDay = LocalDate.now().plusDays(12);
        reservation.carId = 348L;
        reservation.persist();
        assertNotNull(reservation.id);
        assertEquals(1, Reservation.count());
        Reservation persistedReservation = Reservation.findById(reservation.id);
        assertNotNull(persistedReservation);
        assertEquals(reservation.carId, persistedReservation.carId);
    }

}
