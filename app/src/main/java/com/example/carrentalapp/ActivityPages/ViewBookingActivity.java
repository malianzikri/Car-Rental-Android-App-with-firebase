package com.example.carrentalapp.ActivityPages;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.carrentalapp.Database.BookingDao;
import com.example.carrentalapp.Database.CustomerDao;
import com.example.carrentalapp.Database.InsuranceDao;
import com.example.carrentalapp.Database.Project_Database;
import com.example.carrentalapp.Database.VehicleDao;
import com.example.carrentalapp.Model.Booking;
import com.example.carrentalapp.Model.Customer;
import com.example.carrentalapp.Model.Insurance;
import com.example.carrentalapp.Model.Vehicle;
import com.example.carrentalapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.time.temporal.ChronoUnit;
import java.util.Calendar;

public class ViewBookingActivity extends AppCompatActivity {

    private Button back, returnCar;

    //DRIVER DETAILS
    private TextView name, email, phoneNumber;

    //BOOKING SUMMARY
    private TextView bookingID, vehicleName, rate, totalDays, _pickup, _return, insurance, insuranceRate, totalCost;

    //DATABASE TABLE
    private BookingDao bookingDao;
    private CustomerDao customerDao;
    private VehicleDao vehicleDao;
    private InsuranceDao insuranceDao;

    //BOOKING
    private Booking booking;
    //INSURANCE
    private Insurance chosenInsurance;
    //VEHICLE
    private Vehicle vehicle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_booking);

        initComponents();

    }
    private DatabaseReference mDatabase;

    private void initComponents() {
        back = findViewById(R.id.back);

        //DRIVER DETAILS
        name = findViewById(R.id.name);
        email = findViewById(R.id.email);
        phoneNumber = findViewById(R.id.phoneNumber);

        //BOOKING SUMMARY
        vehicleName = findViewById(R.id.vehicleName);
        rate = findViewById(R.id.rate);
        totalDays = findViewById(R.id.totalDays);
        _pickup = findViewById(R.id.pickup);
        _return = findViewById(R.id.dropoff);

        //INSURANCE TYPE
        insurance = findViewById(R.id.insurance);
        insuranceRate = findViewById(R.id.insuranceRate);

        //TOTAL COST
        totalCost = findViewById(R.id.totalCost);

        //DATABASE TABLE
        bookingDao = Room.databaseBuilder(getApplicationContext(), Project_Database.class, "car_rental_db").allowMainThreadQueries()
                    .build()
                    .bookingDao();
        customerDao = Room.databaseBuilder(getApplicationContext(), Project_Database.class, "car_rental_db").allowMainThreadQueries()
                .build()
                .customerDao();
        vehicleDao = Room.databaseBuilder(getApplicationContext(), Project_Database.class, "car_rental_db").allowMainThreadQueries()
                .build()
                .vehicleDao();
        insuranceDao = Room.databaseBuilder(getApplicationContext(), Project_Database.class, "car_rental_db").allowMainThreadQueries()
                .build()
                .insuranceDao();

        //GET BOOKING OBJECT WHICH WAS PASSED FROM PREVIOUS PAGE
        int _bookingID = Integer.valueOf(getIntent().getStringExtra("BOOKINGID"));
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance("https://car-rental-android-app-m-f727e-default-rtdb.asia-southeast1.firebasedatabase.app").getReference();
        bookingID = findViewById(R.id.bookingID);
        mDatabase.child("Booking").child(user.getUid()).child(String.valueOf(_bookingID)).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                try {
                    if (snapshot.getValue() != null) {
                        try {
                            booking = snapshot.getValue(Booking.class);

                            mDatabase.child("Vehicle").child(booking.getVehicleCategory().toLowerCase()).child(String.valueOf(booking.getVehicleID())).addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot snapshot) {
                                    try {
                                        if (snapshot.getValue() != null) {
                                            try {
                                                vehicle = snapshot.getValue(Vehicle.class);

                                                mDatabase.child("Insurance").child(booking.getInsuranceID()).addValueEventListener(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(DataSnapshot snapshot) {
                                                        try {
                                                            if (snapshot.getValue() != null) {
                                                                try {
                                                                    chosenInsurance = snapshot.getValue(Insurance.class);

                                                                    listenHandler();
                                                                    displayCustomerInformation();
                                                                    displaySummary();
                                                                    displayTotalCost();
                                                                } catch (Exception e) {
                                                                    e.printStackTrace();
                                                                }
                                                            } else {
                                                                Log.e("TAG", " it's null.");
                                                            }
                                                        } catch (Exception e) {
                                                            e.printStackTrace();
                                                        }
                                                    }

                                                    @Override
                                                    public void onCancelled(@NonNull DatabaseError databaseError) {
                                                        Log.e("onCancelled", " cancelled");
                                                    }
                                                });
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                        } else {
                                            Log.e("TAG", " it's null.");
                                        }
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {
                                    Log.e("onCancelled", " cancelled");
                                }
                            });
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else {
                        Log.e("TAG", " it's null.");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("onCancelled", " cancelled");
            }
        });



    }

    private void listenHandler() {
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void displayCustomerInformation() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        //DISPLAY DRIVER INFO
        name.setText(user.getDisplayName());
        email.setText(user.getEmail());
        phoneNumber.setText(user.getPhoneNumber());

        bookingID.setText("BookingID: " + booking.getBookingID());
    }

    private void displaySummary(){

        vehicleName.setText(vehicle.fullTitle());
        rate.setText("$"+vehicle.getPrice()+"/Day");
        totalDays.setText(getDayDifference(booking.getPickupDate(),booking.getReturnDate())+" Days");
        _pickup.setText(booking.getPickupTime());
        _return.setText(booking.getReturnTime());

        insurance.setText(chosenInsurance.getCoverageType());
        insuranceRate.setText("$"+chosenInsurance.getCost());
    }

    private void displayTotalCost(){
        double cost = calculateTotalCost();
        totalCost.setText("$"+cost);
    }


    private long getDayDifference(Calendar start, Calendar end){
        return ChronoUnit.DAYS.between(start.toInstant(), end.toInstant())+2;
    }

    private double calculateTotalCost(){
        long _days = getDayDifference(booking.getPickupDate(),booking.getReturnDate());
        double _vehicleRate = vehicle.getPrice();
        double _insuranceRate = chosenInsurance.getCost();

        return (_days*_vehicleRate) + _insuranceRate;
    }


    public void onBackPressed(){
        super.onBackPressed();
        Intent homepage = new Intent(getApplicationContext(), UserViewActivity.class);
        homepage.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); // Will clear out your activity history stack till now
        startActivity(homepage);
    }
}
