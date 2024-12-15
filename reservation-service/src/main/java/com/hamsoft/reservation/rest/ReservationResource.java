package com.hamsoft.reservation.rest;

import com.hamsoft.reservation.entity.Reservation;
import com.hamsoft.reservation.inventory.Car;
import com.hamsoft.reservation.inventory.GraphQLInventoryClient;
import com.hamsoft.reservation.inventory.InventoryClient;
import com.hamsoft.reservation.rental.RentalClient;
import io.smallrye.graphql.client.GraphQLClient;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.resteasy.reactive.RestQuery;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Path("reservation")
@Produces(MediaType.APPLICATION_JSON)
public class ReservationResource {


    final InventoryClient inventoryClient;
    private final RentalClient rentalClient;

    @Inject
    jakarta.ws.rs.core.SecurityContext context;

    public ReservationResource(@RestClient RentalClient rentalClient,
                               @GraphQLClient("inventory") GraphQLInventoryClient inventoryClient
    ) {
        this.inventoryClient = inventoryClient;
        this.rentalClient = rentalClient;
    }

    @GET
    @Path("availability")
    public Collection<Car> availability(@RestQuery LocalDate startDate, @RestQuery LocalDate endDate){
        List<Car> cars = inventoryClient.allCars();
        Map<Long, Car> carsById = new HashMap<>();
        for (Car car : cars) {
            carsById.put(car.id, car);
        }
        List<Reservation> reservations = Reservation.listAll();
        for(Reservation reservation : reservations){
            if(reservation.isReserved()){
                carsById.remove(reservation.carId);
            }
        }
        return  carsById.values();
    }

    @Consumes(MediaType.APPLICATION_JSON)
    @POST
    @Transactional
    public  Reservation make(Reservation reservation){
        reservation.userId = context.getUserPrincipal() != null ? context.getUserPrincipal().getName() : "anonymous";
        reservation.persist();
        if(reservation.startDay.equals(LocalDate.now())){
            rentalClient.start(reservation.userId, reservation.id);
        }
        return reservation;
    }

    @GET
    @Path("all")
    public Collection<Reservation> allReservations() {
        String userId = context.getUserPrincipal() != null ?
                context.getUserPrincipal().getName() : null;
        return Reservation.<Reservation>streamAll()
                .filter(reservation -> userId == null ||
                        userId.equals(reservation.userId))
                .toList();
    }
}
