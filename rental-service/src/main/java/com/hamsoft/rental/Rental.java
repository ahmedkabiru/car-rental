package com.hamsoft.rental;

import io.quarkus.mongodb.panache.PanacheMongoEntity;
import io.quarkus.mongodb.panache.common.MongoEntity;
import org.bson.codecs.pojo.annotations.BsonProperty;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@MongoEntity(collection = "Rentals")
public class Rental extends PanacheMongoEntity {

    public String userId;

    public Long reservationId;

    public LocalDate startDate;

    public LocalDate endDate;

    @BsonProperty("rental-active")
    public boolean active;

    public static Optional<Rental> findByUserAndReservationIds(String userId, Long reservationId) {
        return find("userId = ?1 and  reservationId = ?2", userId, reservationId).firstResultOptional();
    }

    public static List<Rental> listActive() {
        return list("active", true);
    }

}
