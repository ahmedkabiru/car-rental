package com.hamsoft.rental;

import io.quarkus.logging.Log;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

@Path("/rental")
public class RentalResource {

    private final AtomicLong id = new AtomicLong(0);

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/start/{userId}/{reservationId}")
    @Transactional
    public Rental start(@PathParam("userId") String userId, @PathParam("reservationId") Long reservationId) {
        Log.infof("Staring rental for %s with reservation %s", userId, reservationId);
        Rental rental = new Rental();
        rental.userId = userId;
        rental.reservationId = reservationId;
        rental.startDate = LocalDate.now();
        rental.active = true;
        rental.persist();
        return rental;
    }


    @PUT
    @Path("/end/{userId}/{reservationId}")
    public Rental end(@PathParam("userId") String userId, @PathParam("reservationId") Long reservationId) {
        Log.infof("Ending rental for %s with reservation %s", userId, reservationId);
        Optional<Rental> optionalRental = Rental.findByUserAndReservationIds(userId, reservationId);
        if (optionalRental.isPresent()) {
            Rental rental = optionalRental.get();
            rental.endDate = LocalDate.now();
            rental.active = false;
            rental.update();
            return rental;
        } else {
            throw new NotFoundException("Rental not found");
        }
    }

    @GET
    public List<Rental> list() {
        return Rental.listAll();
    }

    @GET
    @Path("/active")
    public List<Rental> listActive() {
        return Rental.listActive();
    }

}
