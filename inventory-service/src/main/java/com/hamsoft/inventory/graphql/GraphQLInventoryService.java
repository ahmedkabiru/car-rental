package com.hamsoft.inventory.graphql;

import com.hamsoft.inventory.database.CarRepository;
import com.hamsoft.inventory.model.Car;
import jakarta.transaction.Transactional;
import org.eclipse.microprofile.graphql.Description;
import org.eclipse.microprofile.graphql.GraphQLApi;
import org.eclipse.microprofile.graphql.Mutation;
import org.eclipse.microprofile.graphql.Query;

import java.util.List;
import java.util.Optional;

@GraphQLApi
public class GraphQLInventoryService {

    private final CarRepository carRepository;

    public GraphQLInventoryService(CarRepository carRepository) {
        this.carRepository = carRepository;
    }

    @Query
    @Description("List of Cars")
    public List<Car> cars() {
        return carRepository.listAll();
    }

    @Transactional
    @Mutation
    public Car createCar(Car car) {
        carRepository.persist(car);
        return car;
    }

    @Mutation
    public boolean remove(String  licensePlateNumber) {
        Optional<Car> car = carRepository.findByLicensePlateNumber(licensePlateNumber);
        if (car.isPresent()) {
            carRepository.delete(car.get());
            return true;
        } else {
            return false;
        }
    }
}