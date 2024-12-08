package com.hamsoft.reservation.rest;

import com.hamsoft.reservation.inventory.Car;
import com.hamsoft.reservation.inventory.GraphQLInventoryClient;
import com.hamsoft.reservation.inventory.InventoryClient;
import com.hamsoft.reservation.rental.RentalClient;
import com.hamsoft.reservation.reservation.Reservation;
import com.hamsoft.reservation.reservation.ReservationRepository;
import io.smallrye.graphql.client.GraphQLClient;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.SecurityContext;
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


    private final ReservationRepository reservationRepository;
    private final InventoryClient inventoryClient;
    private final RentalClient rentalClient;
    private final SecurityContext context;

    public ReservationResource(ReservationRepository reservationRepository,
                               @RestClient  RentalClient rentalClient,
                               @GraphQLClient("inventory") GraphQLInventoryClient inventoryClient, SecurityContext context
    ) {
        this.reservationRepository = reservationRepository;
        this.inventoryClient = inventoryClient;
        this.rentalClient = rentalClient;
        this.context = context;
    }

    @GET
    @Path("availability")
    public Collection<Car> availability(@RestQuery LocalDate startDate, @RestQuery LocalDate endDate){
        List<Car> cars = inventoryClient.allCars();
        Map<Long, Car> carsById = new HashMap<>();
        for (Car car : cars) {
            carsById.put(car.id, car);
        }
        List<Reservation> reservations = reservationRepository.findAll();
        for(Reservation reservation : reservations){
            if(reservation.isReserved()){
                carsById.remove(reservation.carId);
            }
        }
        return  carsById.values();
    }

    @Consumes(MediaType.APPLICATION_JSON)
    @POST
    public  Reservation make(Reservation reservation){
        Reservation result =  reservationRepository.save(reservation);
        String userId = context.getUserPrincipal().getName() != null ? context.getUserPrincipal().getName() : "anonymous";
        if(reservation.startDay.equals(LocalDate.now())){
            rentalClient.start(userId,result.id);
        }
        return result;
    }

    @GET
    @Path("all")
    public Collection<Reservation> getAllReservations() {
        var userId = context.getUserPrincipal().getName() != null ? context.getUserPrincipal().getName() : null;
        return reservationRepository.findAll().stream().filter(reservation -> (userId == null) || (userId.equals(reservation.userId))).toList();
    }
}
