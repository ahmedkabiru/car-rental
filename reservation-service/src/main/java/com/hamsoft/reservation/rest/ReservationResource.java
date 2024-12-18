package com.hamsoft.reservation.rest;

import com.hamsoft.reservation.entity.Reservation;
import com.hamsoft.reservation.inventory.Car;
import com.hamsoft.reservation.inventory.GraphQLInventoryClient;
import com.hamsoft.reservation.inventory.InventoryClient;
import com.hamsoft.reservation.rental.RentalClient;
import io.quarkus.hibernate.reactive.panache.PanacheEntityBase;
import io.quarkus.hibernate.reactive.panache.common.WithTransaction;
import io.quarkus.logging.Log;
import io.smallrye.graphql.client.GraphQLClient;
import io.smallrye.mutiny.Uni;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.SecurityContext;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.resteasy.reactive.RestQuery;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Path("reservation")
@Produces(MediaType.APPLICATION_JSON)
public class ReservationResource {


    final InventoryClient inventoryClient;
    final RentalClient rentalClient;
    final SecurityContext context;

    public ReservationResource(@RestClient RentalClient rentalClient,
                               @GraphQLClient("inventory") GraphQLInventoryClient inventoryClient,
                               SecurityContext context
    ) {
        this.inventoryClient = inventoryClient;
        this.rentalClient = rentalClient;
        this.context = context;
    }

    @GET
    @Path("availability")
    public Uni<Collection<Car>> availability(@RestQuery LocalDate startDate, @RestQuery LocalDate endDate) {
        Uni<Map<Long, Car>> carsUni = inventoryClient.allCars().map(cars -> cars.stream().collect(Collectors.toMap(car -> car.id, Function.identity())));
        Uni<List<Reservation>> reservationUni = Reservation.listAll();
        return Uni.combine().all().unis(carsUni, reservationUni).asTuple().chain(tuple -> {
            Map<Long, Car> carsById = tuple.getItem1();
            List<Reservation> reservations = tuple.getItem2();
            for (Reservation reservation : reservations) {
                if (reservation.isReserved()) {
                    carsById.remove(reservation.carId);
                }
            }
            return Uni.createFrom().item(carsById.values());
        });
    }

    @Consumes(MediaType.APPLICATION_JSON)
    @POST
    @WithTransaction
    public Uni<Reservation> make(Reservation reservation) {
        reservation.userId = context.getUserPrincipal() != null ? context.getUserPrincipal().getName() : "anonymous";
        return reservation.<Reservation>persist().onItem().call(persistedReservation -> {
            Log.info("Successfully reservation: " + persistedReservation);
            if (persistedReservation.startDay.equals(LocalDate.now()) && persistedReservation.userId != null) {
                return rentalClient.start(reservation.userId, reservation.id).onItem().invoke(
                        rental -> Log.info("Successfully started rental " + rental)
                ).replaceWith(persistedReservation);
            }
            return Uni.createFrom().item(persistedReservation);
        });

    }

    @GET
    @Path("all")
    public Uni<Collection<Reservation>> allReservations() {
        String userId = context.getUserPrincipal() != null ? context.getUserPrincipal().getName() : null;
        return PanacheEntityBase.<Reservation>listAll().onItem().transform(reservations -> reservations.stream()
                .filter(reservation -> userId == null || userId.equals(reservation.userId))
                .toList());
    }
}
