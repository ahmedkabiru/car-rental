package com.hamsoft.inventory.database;

import com.hamsoft.inventory.model.Car;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicLong;

@ApplicationScoped
public class CarInventory {

    private List<Car> cars;

    public static final  AtomicLong id = new AtomicLong();

    @PostConstruct
    void  initialize(){
        cars = new CopyOnWriteArrayList<>();
        initializeData();
    }

    public  List<Car> getCars() {
        return cars;
    }

    private void initializeData() {
        Car mazda = new Car();
        mazda.id = id.incrementAndGet();
        mazda.manufacturer = "Mazda";
        mazda.model= "6";
        mazda.licensePlateNumber = "ABC123";
        cars.add(mazda);


        Car ford = new Car();
        ford.id = id.incrementAndGet();
        ford.manufacturer = "Ford";
        ford.model= "Mustang";
        ford.licensePlateNumber = "XYZ123";
        cars.add(ford);
    }
}
