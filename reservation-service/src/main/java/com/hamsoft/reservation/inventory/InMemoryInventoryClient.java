package com.hamsoft.reservation.inventory;

import jakarta.inject.Singleton;

import java.util.List;

@Singleton
public class InMemoryInventoryClient  implements InventoryClient {

    @Override
    public List<Car> allCars() {
        return List.of(
                new Car(1L, "ABC-123","Toyota","Corolla"),
                new Car(2L, "ABC-456","Honda","Jazz"),
                new Car(3L, "XYZ-123","Audi","Clio"),
                new Car(4L, "XYZ-456","Ford","Focus")
        );
    }
}
