package com.hamsoft.inventory.database;

import com.hamsoft.inventory.model.Car;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.Optional;

@ApplicationScoped
public class CarRepository implements PanacheRepository<Car> {

    public Optional<Car> findByLicensePlateNumber(String licensePlateNumber) {
        return find("licensePlateNumber", licensePlateNumber).firstResultOptional();
    }
}
