package com.hamsoft.client;

import com.hamsoft.inventory.model.InsertCarRequest;
import com.hamsoft.inventory.model.InventoryService;
import com.hamsoft.inventory.model.RemoveCarRequest;
import io.quarkus.grpc.GrpcClient;
import io.quarkus.runtime.QuarkusApplication;
import io.quarkus.runtime.annotations.QuarkusMain;

@QuarkusMain
public class InventoryCommand implements QuarkusApplication {

    @GrpcClient("inventory")
    InventoryService inventoryService;

    private static final String USAGE = "Usage: Inventory <add>|<remove>" +
            "<license plate number> <manufacturer> <model>";

    @Override
    public int run(String... args) throws Exception {
        String action = args.length > 0 ? args[0] : null;
        if ("add".equals(action) && args.length >= 4) {
            add(args[1], args[2], args[3]);
            return 0;
        } else if ("remove".equals(action) && args.length >= 2) {
            remove(args[1]);
            return 0;
        }
        System.err.println(USAGE);
        return 1;
    }

    public void add(String licensePlateNumber, String manufacturer, String model) {
        inventoryService.add(InsertCarRequest.newBuilder()
                .setLicensePlateNumber(licensePlateNumber)
                .setManufacturer(manufacturer)
                .setModel(model)
                .build()).onItem().invoke(carResponse -> {
            System.out.println("Insert a nee Car" + carResponse.toString());
        }).await().indefinitely();
    }

    public void remove(String licensePlateNumber) {
        inventoryService.remove(RemoveCarRequest.newBuilder()
                        .setLicensePlateNumber(licensePlateNumber)
                        .build())
                .onItem().invoke(carResponse -> {
                    System.out.println("Remove Car" + carResponse.toString());
                }).await().indefinitely();
    }
}
