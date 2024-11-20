package com.hamsoft.inventory;

import com.hamsoft.inventory.database.CarInventory;
import com.hamsoft.inventory.model.Car;
import jakarta.inject.Inject;
import org.eclipse.microprofile.graphql.*;

import java.util.List;
import java.util.Optional;

@GraphQLApi
public class GraphQLInventoryService {

    @Inject
    CarInventory carInventory;


    @Query
    @Description("List of Cars")
    public List<Car> cars() {
        return  carInventory.getCars();
    }

    @Mutation
    public Car createCar(Car car) {
        car.id = CarInventory.id.incrementAndGet();
        carInventory.getCars().add(car);
        return car;
    }

    @Mutation
    public boolean remove(String  licensePlateNumber) {
        List<Car> cars = carInventory.getCars();
        Optional<Car> carToRemove  = cars.stream().filter(car -> car.licensePlateNumber.equals(licensePlateNumber)).findAny();
        return carToRemove.map(car -> carInventory.getCars().remove(car)).orElse(false);
    }
}