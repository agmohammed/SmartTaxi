package com.agmohammed.smarttaxi;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class VehicleActivity extends AppCompatActivity {

    Spinner sServiceType, sVehicleBrand, sVehicleModel, sProvincialCode, sAvailableSeats;

    EditText mVehicleNo;

    Button mSubmit;

    private DatabaseReference mDriverDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vehicle);

        sServiceType = (Spinner) findViewById(R.id.serviceType);
        sVehicleBrand = (Spinner) findViewById(R.id.vehicleBrand);
        sVehicleModel = (Spinner) findViewById(R.id.vehicleModel);
        sProvincialCode = (Spinner) findViewById(R.id.provincialCode);
        sAvailableSeats = (Spinner) findViewById(R.id.availableSeats);

        mVehicleNo = (EditText) findViewById(R.id.vehicleID);

        mSubmit = (Button) findViewById(R.id.btnSubmit);

        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        mDriverDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child("Drivers").child(userId).child("VehicleData");


        String[] serviceTypeItems = new String[]{"Select A Service Type", "Three Wheel", "Nano", "Mini", "Car", "VIP"};
        ArrayAdapter<String> adapter1 = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, serviceTypeItems);
        sServiceType.setAdapter(adapter1);

        String[] NormalVehicleBrands = new String[]{"Select A Vehicle Brand", "Toyota", "Honda", "Suzuki", "Nissan"};
        final ArrayAdapter<String> NormalVehicleBrandsAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, NormalVehicleBrands);
        //sVehicleBrand.setAdapter(NormalVehicleBrandsAdapter);

        String[] LuxuryVehicleBrands = new String[]{"Select A Vehicle Brand","BMW", "Benz", "Jaguar"};
        final ArrayAdapter<String> LuxuryVehicleBrandsAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, LuxuryVehicleBrands);
        //sVehicleBrand.setAdapter(LuxuryVehicleBrandsAdapter);

        String[] ThreeWheelBrandItems = new String[]{"Select A Vehicle Brand", "Bajaj", "TVS"};
        final ArrayAdapter<String> ThreewheelBrandAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, ThreeWheelBrandItems);

        String[] ThreeWheelModelItems = new String[]{"Select A Three Wheel Model", "2 Stroke", "4 Stroke"};
        final ArrayAdapter<String> ThreewheelModelAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, ThreeWheelModelItems);

        String[] Toyota = new String[]{"Select A Vehicle Model", "Corolla", "Avalon", "Prius", "Camry", "Yaris"};
        final ArrayAdapter<String> adapterToyota = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, Toyota);

        String[] Honda = new String[]{"Select A Vehicle Model", "Amaze", "Accord", "Avancier", "City", "Civic", "Vezel" };
        final ArrayAdapter<String> adapterHonda = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, Honda);

        String[] Suzuki = new String[]{"Select A Vehicle Model", "Maruti Suzuki Alto 800", "Suzuki Wagon R", "Suzuki Swift", "Omni", "Celerio"};
        final ArrayAdapter<String> adapterSuzuki = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, Suzuki);

        String[] Nissan = new String[]{"Select A Vehicle Model", "Altima", "Armada", "Leaf", "Kicks", "Maxima"};
        final ArrayAdapter<String> adapterNissan = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, Nissan);

        String[] BMW = new String[]{"Select A Vehicle Model", "BMW X1", "BMW X6", "BMW M3", "BMW 6", "BMW 5"};
        final ArrayAdapter<String> adapterBMW = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, BMW);

        String[] Benz = new String[]{"Select A Vehicle Model", "S-Class", "E-Class", "C-Class", "G-Class", "SLC"};
        final ArrayAdapter<String> adapterBenz = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, Benz);

        String[] Jaguar = new String[]{"Select A Vehicle Model", "XE", "E‑PACE", "XF", "SVO", "XJ"};
        final ArrayAdapter<String> adapterJaguar = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, Jaguar);

        String[] provincialCodeItems = new String[]{"Select A Provincial Code", "WP", "CP", "SP", "UP", "SG", "NW", "NP", "NC", "EP"};
        ArrayAdapter<String> adapter4 = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, provincialCodeItems);
        sProvincialCode.setAdapter(adapter4);

        String[] availableSeatsItems = new String[]{"How many seats available", "1", "2", "3", "4"};
        ArrayAdapter<String> adapter5 = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, availableSeatsItems);
        sAvailableSeats.setAdapter(adapter5);

        sServiceType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String spinnerValue = sServiceType.getSelectedItem().toString();
                if (spinnerValue.equals("Three Wheel")){
                    sVehicleBrand.setAdapter(ThreewheelBrandAdapter);
                    sVehicleModel.setAdapter(ThreewheelModelAdapter);
                }
                else if (spinnerValue.equals("Nano")){
                    sVehicleBrand.setAdapter(NormalVehicleBrandsAdapter);
                }
                else if (spinnerValue.equals("Mini")){
                    sVehicleBrand.setAdapter(NormalVehicleBrandsAdapter);
                }
                else if (spinnerValue.equals("Car")){
                    sVehicleBrand.setAdapter(NormalVehicleBrandsAdapter);
                }
                else if (spinnerValue.equals("VIP")){
                    sVehicleBrand.setAdapter(LuxuryVehicleBrandsAdapter);
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


        sVehicleBrand.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String spinnerValue = sVehicleBrand.getSelectedItem().toString();
                if (spinnerValue.equals("Toyota")){
                    sVehicleModel.setAdapter(adapterToyota);
                }
                else if (spinnerValue.equals("Honda")){
                    sVehicleModel.setAdapter(adapterHonda);
                }
                else if (spinnerValue.equals("Suzuki")){
                    sVehicleModel.setAdapter(adapterSuzuki);
                }
                else if (spinnerValue.equals("Nissan")){
                    sVehicleModel.setAdapter(adapterNissan);
                }
                else if (spinnerValue.equals("BMW")){
                    sVehicleModel.setAdapter(adapterBMW);
                }
                else if (spinnerValue.equals("Benz")){
                    sVehicleModel.setAdapter(adapterBenz);
                }
                else if (spinnerValue.equals("Jaguar")){
                    sVehicleModel.setAdapter(adapterJaguar);
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        mSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

                String serviceType = sServiceType.getSelectedItem().toString();
                String vehicleBrand = sVehicleBrand.getSelectedItem().toString();
                String vehicleModel = sVehicleModel.getSelectedItem().toString();
                String provincialCode = sProvincialCode.getSelectedItem().toString();
                String availableSeats = sAvailableSeats.getSelectedItem().toString();

                String VehicleNo = mVehicleNo.getText().toString();

                Map vehicleInfo = new HashMap();
                vehicleInfo.put("Service Type", serviceType);
                vehicleInfo.put("Vehicle Brand", vehicleBrand);
                vehicleInfo.put("Vehicle Model", vehicleModel);
                vehicleInfo.put("Vehicle Number", provincialCode + " " + VehicleNo);
                vehicleInfo.put("Available Seats", availableSeats);

                mDriverDatabase.updateChildren(vehicleInfo);
            }
        });

    }
}
