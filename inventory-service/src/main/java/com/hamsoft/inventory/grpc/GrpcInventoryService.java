package com.hamsoft.inventory.grpc;

import com.hamsoft.inventory.database.CarRepository;
import com.hamsoft.inventory.model.Car;
import com.hamsoft.inventory.model.CarResponse;
import com.hamsoft.inventory.model.InsertCarRequest;
import com.hamsoft.inventory.model.InventoryService;
import com.hamsoft.inventory.model.RemoveCarRequest;
import io.quarkus.grpc.GrpcService;
import io.quarkus.logging.Log;
import io.quarkus.narayana.jta.QuarkusTransaction;
import io.smallrye.common.annotation.Blocking;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import jakarta.transaction.Transactional;

@GrpcService
public class GrpcInventoryService implements InventoryService {


    final CarRepository carRepository;

    public GrpcInventoryService(CarRepository carRepository) {
        this.carRepository = carRepository;
    }

    @Override
    @Transactional
    @Blocking
    public Uni<CarResponse> add(InsertCarRequest request) {
        Car car = new Car();
        car.licensePlateNumber = request.getLicensePlateNumber();
        car.model = request.getModel();
        car.manufacturer = request.getManufacturer();
        carRepository.persist(car);
        return Uni.createFrom().item(CarResponse.newBuilder()
                .setLicensePlateNumber(car.licensePlateNumber)
                .setModel(car.model)
                .setManufacturer(car.manufacturer)
                .setId(car.id)
                .build()
        );
    }


    @Override
    @Transactional
    @Blocking
    public Uni<CarResponse> remove(RemoveCarRequest request) {
        var carOptional = carRepository.findByLicensePlateNumber(request.getLicensePlateNumber());
        if (carOptional.isPresent()) {
            var car = carOptional.get();
            carRepository.delete(car);
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

    @Blocking
    @Override
    public Multi<CarResponse> addMulti(Multi<InsertCarRequest> requests) {
        return requests.map(request -> {
            var car = new Car();
            car.licensePlateNumber = request.getLicensePlateNumber();
            car.model = request.getModel();
            car.manufacturer = request.getManufacturer();
            return car;
        }).onItem().invoke(car -> {
            Log.info("Persisting car " + car.id);
            QuarkusTransaction.run(() -> carRepository.persist(car));

        }).map(car ->
                CarResponse.newBuilder()
                        .setLicensePlateNumber(car.licensePlateNumber)
                        .setModel(car.model)
                        .setManufacturer(car.manufacturer)
                        .setId(car.id)
                        .build());
    }
}
