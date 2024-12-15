package com.hamsoft.users;

import com.hamsoft.users.model.Car;
import com.hamsoft.users.model.Reservation;
import io.quarkus.qute.CheckedTemplate;
import io.quarkus.qute.TemplateInstance;
import io.smallrye.common.annotation.Blocking;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.SecurityContext;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.resteasy.reactive.RestForm;
import org.jboss.resteasy.reactive.RestQuery;
import org.jboss.resteasy.reactive.RestResponse;

import java.time.LocalDate;
import java.util.Collection;

@Path("/")
@Blocking
public class ReservationsResource {

    @CheckedTemplate
    public static class Template {
        public static native TemplateInstance index(
                LocalDate startDate, LocalDate endDate, String name
        );

        public static native TemplateInstance listofreservations(
                Collection<Reservation> reservations
        );

        public static native TemplateInstance availablecars(
                Collection<Car> cars,
                LocalDate startDate,
                LocalDate endDate
        );
    }

    final SecurityContext securityContext;
    final ReservationClient reservationClient;

    public ReservationsResource(SecurityContext securityContext, @RestClient ReservationClient reservationClient) {
        this.securityContext = securityContext;
        this.reservationClient = reservationClient;
    }

    @GET
    @Produces(MediaType.TEXT_HTML)
    public TemplateInstance index(@RestQuery LocalDate startDate, @RestQuery LocalDate endDate) {
        if (startDate == null) {
            startDate = LocalDate.now().plusDays(1L);
        }
        if (endDate == null) {
            endDate = LocalDate.now().plusDays(7);
        }
        return Template.index(startDate, endDate, securityContext.getUserPrincipal().getName());
    }

    @GET
    @Produces(MediaType.TEXT_HTML)
    @Path("/get")
    public TemplateInstance getAllReservations() {
        var reservations = reservationClient.allReservations();
        return Template.listofreservations(reservations);
    }

    @GET
    @Produces(MediaType.TEXT_HTML)
    @Path("/available")
    public TemplateInstance getAvailableCars(@RestQuery LocalDate startDate, @RestQuery LocalDate endDate) {
        var availableCars = reservationClient.availability(startDate, endDate);
        return Template.availablecars(availableCars, startDate, endDate);
    }


    @POST
    @Produces(MediaType.TEXT_HTML)
    @Path("/reserve")
    public RestResponse<TemplateInstance> create(@RestForm LocalDate startDate, @RestForm LocalDate endDate, @RestForm Long carId) {
        var reservation = new Reservation();
        reservation.startDay = startDate;
        reservation.endDay = endDate;
        reservation.carId = carId;
        reservationClient.makeReservation(reservation);
        return RestResponse.ResponseBuilder
                .ok(getAllReservations())
                .header("HX-Trigger-After-Swap",
                        "update-available-cars-list")
                .build();
    }


}
