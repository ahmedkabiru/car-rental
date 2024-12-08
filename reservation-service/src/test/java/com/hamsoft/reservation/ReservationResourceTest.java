package com.hamsoft.reservation;

import com.hamsoft.reservation.inventory.Car;
import com.hamsoft.reservation.inventory.GraphQLInventoryClient;
import com.hamsoft.reservation.reservation.Reservation;
import com.hamsoft.reservation.rest.ReservationResource;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.common.http.TestHTTPResource;
import io.quarkus.test.junit.DisabledOnIntegrationTest;
import io.quarkus.test.junit.QuarkusMock;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.RestAssured;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.net.URL;
import java.time.LocalDate;
import java.util.Collections;

import static org.hamcrest.Matchers.*;

@QuarkusTest
class ReservationResourceTest {

    @TestHTTPEndpoint(ReservationResource.class)
    @TestHTTPResource
    URL reservationResource;

    @TestHTTPEndpoint(ReservationResource.class)
    @TestHTTPResource("availability")
    URL availability;

    @Test
    void testReservationIds() {
        var reservation = new Reservation();
        reservation.carId = 12345L;
        reservation.startDay = LocalDate.parse("2025-03-20");
        reservation.endDay = LocalDate.parse("2025-03-29");

        RestAssured.given()
                .contentType("application/json")
                .body(reservation)
                .when()
                .post(reservationResource)
                .then()
                .statusCode(200)
                .body("id", notNullValue());
    }

    @Test
    @DisabledOnIntegrationTest(forArtifactTypes = DisabledOnIntegrationTest.ArtifactType.NATIVE_BINARY)
    void testMakeReservationAndCheckingAvailability() {
        GraphQLInventoryClient mock = Mockito.mock(GraphQLInventoryClient.class);
        Car peugeot = new Car(1L, "ABC 123", "Peugeot", "406");
        Mockito.when(mock.allCars()).thenReturn(Collections.singletonList(peugeot));
        QuarkusMock.installMockForType(mock, GraphQLInventoryClient.class);

        var startDate = "2022-01-01";
        var endDate = "2022-01-10";

        // Get all list of available cars

        Car[] cars = RestAssured.given()
                .queryParam("startDate", startDate)
                .queryParam("endDate", endDate)
                .when()
                .get(availability)
                .then()
                .statusCode(200)
                .extract().as(Car[].class);

        Car car = cars[0];

        var reservation = new Reservation();
        reservation.carId = car.id;
        reservation.startDay = LocalDate.parse(startDate);
        reservation.endDay = LocalDate.parse(endDate);

        RestAssured.given()
                .contentType("application/json")
                .body(reservation)
                .when()
                .post(reservationResource)
                .then()
                .statusCode(200)
                .body("carId", is(car.id.intValue()));


        RestAssured.given()
                .queryParam("startDate", startDate)
                .queryParam("endDate", endDate)
                .when()
                .get(availability)
                .then()
                .statusCode(200)
                .body("findAll { car -> car.id == " + car.id + "}", hasSize(0));
    }
}
