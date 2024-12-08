package com.hamsoft.users;

import com.hamsoft.users.model.Car;
import com.hamsoft.users.model.Reservation;
import io.quarkus.oidc.token.propagation.AccessToken;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;
import org.jboss.resteasy.reactive.RestQuery;

import java.time.LocalDate;
import java.util.Collection;

@RegisterRestClient(baseUri = "http://localhost:8081")
@AccessToken
@Path("reservation")
public interface ReservationClient {


    @GET
    @Path("all")
    Collection<Reservation> allReservations();


    @POST
    Reservation makeReservation(Reservation reservation);


    @GET
    @Path("availability")
    Collection<Car> availability(@RestQuery LocalDate startDate, @RestQuery LocalDate endDate);

}
