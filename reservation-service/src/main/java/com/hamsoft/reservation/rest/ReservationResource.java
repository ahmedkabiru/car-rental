package com.hamsoft.reservation.rest;

import com.hamsoft.reservation.inventory.Car;
import com.hamsoft.reservation.inventory.InventoryClient;
import com.hamsoft.reservation.reservation.Reservation;
import com.hamsoft.reservation.reservation.ReservationRepository;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import org.jboss.resteasy.reactive.RestQuery;

import java.time.LocalDate;
import java.util.*;

@Path("reservation")
@Produces(MediaType.APPLICATION_JSON)
public class ReservationResource {


    private final ReservationRepository reservationRepository;
    private final InventoryClient inventoryClient;

    public ReservationResource(ReservationRepository reservationRepository, InventoryClient inventoryClient) {
        this.reservationRepository = reservationRepository;
        this.inventoryClient = inventoryClient;
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
        return reservationRepository.save(reservation);
    }
}
