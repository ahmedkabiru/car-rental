package com.hamsoft.rental;

import io.quarkus.logging.Log;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;

import java.time.LocalDate;
import java.util.concurrent.atomic.AtomicLong;

@Path("/rental")
public class RentalResource {

    private final AtomicLong id = new AtomicLong(0);

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/start/{userId}/{reservationId}")
    public Rental start(@PathParam("userId") String userId, @PathParam("reservationId") Long reservationId) {
        Log.infof("Staring rental for %s with reservation %s", userId, reservationId);
        return  new Rental(id.incrementAndGet(),userId, reservationId, LocalDate.now());
    }
}
