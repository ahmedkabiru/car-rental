package com.hamsoft.inventory.grpc;

import com.hamsoft.inventory.database.CarInventory;
import com.hamsoft.inventory.model.*;
import io.quarkus.grpc.GrpcService;
import io.quarkus.logging.Log;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;

import java.util.Objects;

@GrpcService
public class GrpcInventoryService implements InventoryService {


    final CarInventory carInventory;

    public GrpcInventoryService(CarInventory carInventory) {
        this.carInventory = carInventory;
    }

    @Override
    public Uni<CarResponse> add(InsertCarRequest request) {
        Car car = new Car();
        car.id = CarInventory.id.incrementAndGet();
        car.licensePlateNumber = request.getLicensePlateNumber();
        car.model = request.getModel();
        car.manufacturer = request.getManufacturer();
        carInventory.getCars().add(car);
        return Uni.createFrom().item(CarResponse.newBuilder()
                .setLicensePlateNumber(car.licensePlateNumber)
                .setModel(car.model)
                .setManufacturer(car.manufacturer)
                .setId(car.id)
                .build()
        );
    }


    @Override
    public Uni<CarResponse> remove(RemoveCarRequest request) {
        var carOptional = carInventory.getCars().stream().filter(car -> Objects.equals(car.licensePlateNumber, request.getLicensePlateNumber())).findFirst();
        if (carOptional.isPresent()) {
            var car = carOptional.get();
            carInventory.getCars().remove(car);
            return Uni.createFrom().item(CarResponse.newBuilder()
                    .setLicensePlateNumber(car.licensePlateNumber)
                    .setModel(car.model)
                    .setManufacturer(car.manufacturer)
                    .setId(car.id)
                    .build()
            );
        }
        return Uni.createFrom().nullItem();
    }

    @Override
    public Multi<CarResponse> addMulti(Multi<InsertCarRequest> requests) {
        return requests.map(request -> {
            var car = new Car();
            car.id = CarInventory.id.incrementAndGet();
            car.licensePlateNumber = request.getLicensePlateNumber();
            car.model = request.getModel();
            car.manufacturer = request.getManufacturer();
            return car;
        }).onItem().invoke(car -> {
            Log.info("Persisting car " + car.id);
            carInventory.getCars().add(car);
        }).map(car ->
                CarResponse.newBuilder()
                        .setLicensePlateNumber(car.licensePlateNumber)
                        .setModel(car.model)
                        .setManufacturer(car.manufacturer)
                        .setId(car.id)
                        .build());
    }
}
