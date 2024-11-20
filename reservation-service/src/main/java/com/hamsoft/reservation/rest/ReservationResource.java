package com.hamsoft.reservation.rest;

import com.hamsoft.reservation.inventory.Car;
import com.hamsoft.reservation.inventory.InventoryClient;
import com.hamsoft.reservation.rental.RentalClient;
import com.hamsoft.reservation.reservation.Reservation;
import com.hamsoft.reservation.reservation.ReservationRepository;
import io.quarkus.logging.Log;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.resteasy.reactive.RestQuery;

import java.time.LocalDate;
import java.util.*;

@Path("reservation")
@Produces(MediaType.APPLICATION_JSON)
public class ReservationResource {


    private final ReservationRepository reservationRepository;
    private final InventoryClient inventoryClient;
    private final RentalClient rentalClient;

    public ReservationResource(ReservationRepository reservationRepository,
                               InventoryClient inventoryClient,
                               @RestClient  RentalClient rentalClient) {
        this.reservationRepository = reservationRepository;
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
        String userId = "johndoe";
        if(reservation.startDay.equals(LocalDate.now())){
            rentalClient.start(userId,result.id);
        }
        return result;
    }
}
