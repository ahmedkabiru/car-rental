package com.hamsoft.inventory.graphql;

import com.hamsoft.inventory.database.CarInventory;
import com.hamsoft.inventory.model.Car;
import org.eclipse.microprofile.graphql.Description;
import org.eclipse.microprofile.graphql.GraphQLApi;
import org.eclipse.microprofile.graphql.Mutation;
import org.eclipse.microprofile.graphql.Query;

import java.util.List;
import java.util.Optional;

@GraphQLApi
public class GraphQLInventoryService {

    private final CarInventory carInventory;

    public GraphQLInventoryService(CarInventory carInventory) {
        this.carInventory = carInventory;
    }

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