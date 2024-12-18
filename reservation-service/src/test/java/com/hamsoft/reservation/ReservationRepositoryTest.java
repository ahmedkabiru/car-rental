package com.hamsoft.reservation;

import com.hamsoft.reservation.entity.Reservation;
import io.quarkus.test.hibernate.reactive.panache.TransactionalUniAsserter;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.vertx.RunOnVertxContext;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;


@QuarkusTest
class ReservationRepositoryTest {


    @Test
    @RunOnVertxContext
    void testCreateReservation(TransactionalUniAsserter asserter) {
        Reservation reservation = new Reservation();
        reservation.startDay = LocalDate.now().plusDays(5);
        reservation.endDay = LocalDate.now().plusDays(12);
        reservation.carId = 384L;

        asserter.<Reservation>assertThat(reservation::persist,
                r -> {
                    Assertions.assertNotNull(r.id);
                    asserter.putData("reservation.id", r.id);
                });

        asserter.assertEquals(Reservation::count, 1L);
        asserter.assertThat(() -> Reservation.<Reservation>findById(asserter.getData("reservation.id")),
                persistedReservation -> {
                    Assertions.assertNotNull(persistedReservation);
                    Assertions.assertEquals(reservation.carId, persistedReservation.carId);
                });
    }

}
