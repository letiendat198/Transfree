package com.transfree.views.components;

import javafx.collections.FXCollections;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;

import java.util.ArrayList;
import java.util.List;

public class DeviceComboBox extends ComboBox<Device> {
    List<Device> targetList = new ArrayList<>();
    public DeviceComboBox(){
        this.setItems(FXCollections.observableList(targetList));
        this.setCellFactory(param -> new DeviceCell());
        this.setButtonCell(new DeviceCell());

        Device scanDevice = new Device();
        scanDevice.setNotTarget(true);
        scanDevice.setName("Scanning for devices...");
        this.addDevice(scanDevice);

        Device manualDevice = new Device();
        manualDevice.setNotTarget(true);
        manualDevice.setName("Add a device manually");
        this.addDevice(manualDevice);

        this.getSelectionModel().select(scanDevice);
    }

    public void addDevice(Device device){
        targetList.add(device);
    }

    private class DeviceCell extends ListCell<Device>{
        @Override
        protected void updateItem(Device device, boolean empty) {
            super.updateItem(device, empty);
            if (!empty){
                if(!device.isNotTarget()){
                    setGraphic(new DeviceBox(device));
                }
                else{
                    Label infoLabel = new Label(device.getName());
                    infoLabel.getStyleClass().add("bold");
                    setGraphic(infoLabel);
                }
            }
            else{
                setGraphic(null);
            }
        }
    }
}
