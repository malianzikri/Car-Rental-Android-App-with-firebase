package com.example.carrentalapp.ActivityPages;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.room.Room;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.carrentalapp.Database.BillingDao;
import com.example.carrentalapp.Database.BookingDao;
import com.example.carrentalapp.Database.CustomerDao;
import com.example.carrentalapp.Database.InsuranceDao;
import com.example.carrentalapp.Database.PaymentDao;
import com.example.carrentalapp.Database.Project_Database;
import com.example.carrentalapp.Database.VehicleDao;
import com.example.carrentalapp.Model.Billing;
import com.example.carrentalapp.Model.Booking;
import com.example.carrentalapp.Model.Customer;
import com.example.carrentalapp.Model.Insurance;
import com.example.carrentalapp.Model.Payment;
import com.example.carrentalapp.Model.Vehicle;
import com.example.carrentalapp.R;
import com.github.ybq.android.spinkit.style.Wave;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Calendar;
import java.util.Random;

public class BookingSummaryActivity extends AppCompatActivity {

    private Button back, book, payNow;

    //DRIVER DETAILS
    private TextView name, email, phoneNumber;

    //BOOKING SUMMARY
    private TextView vehicleName, rate, totalDays, _pickup, _return, insurance, insuranceRate, totalCost;

    //VEHICLE IMAGE
    private ImageView vehicleImage;

    //DATABASE TABLE
    private CustomerDao customerDao;
    private VehicleDao vehicleDao;
    private BookingDao bookingDao;
    private InsuranceDao insuranceDao;
    private BillingDao billingDao;
    private PaymentDao paymentDao;

    //BOOKING
    private Booking booking;
    //INSURANCE
    private Insurance chosenInsurance;
    //VEHICLE
    private Vehicle vehicle;

    private ProgressBar paidLoading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking_summary);

        initComponents();


    }



    private void initComponents() {
        back = findViewById(R.id.back);
        book = findViewById(R.id.book);
        payNow = findViewById(R.id.payNow);

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

        //VEHICLE IMAGE
        vehicleImage = findViewById(R.id.vehicleImage);

        //DATABASE TABLE
        customerDao = Room.databaseBuilder(getApplicationContext(), Project_Database.class, "car_rental_db").allowMainThreadQueries()
                    .build()
                    .customerDao();
        vehicleDao = Room.databaseBuilder(getApplicationContext(), Project_Database.class, "car_rental_db").allowMainThreadQueries()
                    .build()
                    .vehicleDao();
        bookingDao = Room.databaseBuilder(getApplicationContext(), Project_Database.class, "car_rental_db").allowMainThreadQueries()
                    .build()
                    .bookingDao();
        insuranceDao = Room.databaseBuilder(getApplicationContext(), Project_Database.class, "car_rental_db").allowMainThreadQueries()
                    .build()
                    .insuranceDao();
        billingDao  = Room.databaseBuilder(getApplicationContext(), Project_Database.class, "car_rental_db").allowMainThreadQueries()
                    .build()
                    .billingDao();
        paymentDao = Room.databaseBuilder(getApplicationContext(), Project_Database.class, "car_rental_db").allowMainThreadQueries()
                    .build()
                    .paymentDao();
        paidLoading = findViewById(R.id.paidLoading);
        paidLoading.setVisibility(View.INVISIBLE);
        //GET BOOKING OBJECT WHICH WAS PASSED FROM PREVIOUS PAGE
        booking = (Booking) getIntent().getSerializableExtra("BOOKING");

        mDatabase = FirebaseDatabase.getInstance("https://car-rental-android-app-m-f727e-default-rtdb.asia-southeast1.firebasedatabase.app").getReference();
        mDatabase.child("Insurance").child(booking.getInsuranceID()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                try {
                    if (snapshot.getValue() != null) {
                        try {
                            chosenInsurance = snapshot.getValue(Insurance.class);
                            System.out.println("sini cy");
                            System.out.println(booking.toString());
                            System.out.println(booking.getVehicleID());
                            mDatabase.child("Vehicle").child(booking.getVehicleCategory().toLowerCase()).child(String.valueOf(booking.getVehicleID())).addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot snapshot) {
                                    try {
                                        if (snapshot.getValue() != null) {
                                            try {
                                                vehicle = snapshot.getValue(Vehicle.class);

                                                Wave wave = new Wave();
                                                paidLoading.setIndeterminateDrawable(wave);

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




    }

    private void listenHandler() {
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        book.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(!book.isEnabled()){
                    toast("Payment must be done");
                    return;
                }
                generateBilling_Payment();
                Intent bookingCompletePage = new Intent(BookingSummaryActivity.this,BookingCompleteActivity.class);
                bookingCompletePage.putExtra("BOOKING",booking);
                startActivity(bookingCompletePage);
            }
        });

        payNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                paidLoading.setVisibility(View.VISIBLE);

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        paidLoading.setVisibility(View.INVISIBLE);
                        payNow.setText("Paid");
                        payNow.setEnabled(false);
                        book.setEnabled(true);
                    }
                },7000);
            }
        });
    }

    private void generateBilling_Payment() {

        //GENERATE PAYMENT ID
        int paymentID = generateID(600,699);


        //GENRATE BILLING ID
        int billingID = generateID(500,599);


        Calendar currentDate = Calendar.getInstance();

        Payment payment = new Payment(paymentID,"Credit",calculateTotalCost(),0);
        Billing billing = new Billing(billingID,"Paid",currentDate,0,paymentID);
        booking.setBillingID(billingID);
        booking.setBookingStatus("Waiting for approval");

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        mDatabase = FirebaseDatabase.getInstance("https://car-rental-android-app-m-f727e-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference();
        mDatabase.child("Booking").child(user.getUid()).child(String.valueOf(booking.getBookingID())).setValue(booking);
        mDatabase.child("Payment").child(user.getUid()).child(String.valueOf(payment.getPaymentID())).setValue(payment);
        mDatabase.child("Billing").child(user.getUid()).child(String.valueOf(billing.getBillingID())).setValue(billing);
        mDatabase.child("Vehicle").child(booking.getVehicleCategory().toLowerCase()).child(String.valueOf(booking.getVehicleID())).child("availability").setValue(false);

    }

    private DatabaseReference mDatabase;
    private void displayCustomerInformation() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        //DISPLAY DRIVER INFO
        name.setText(user.getDisplayName());
        email.setText(user.getEmail());
        phoneNumber.setText(user.getPhoneNumber());
    }

    private void displaySummary(){

        vehicleName.setText(vehicle.fullTitle());
        rate.setText("$"+vehicle.getPrice()+"/Day");
        totalDays.setText(getDayDifference(booking.getPickupDate(),booking.getReturnDate())+" Days");
        _pickup.setText(booking.getPickupTime());
        _return.setText(booking.getReturnTime());

        insurance.setText(chosenInsurance.getCoverageType());
        insuranceRate.setText("$"+chosenInsurance.getCost());

        Picasso.get().load(vehicle.getVehicleImageURL()).into(vehicleImage);
    }

    private void displayTotalCost(){
        double cost = calculateTotalCost();
        totalCost.setText("$"+cost);
    }


    private long getDayDifference(String  start, String end){
        LocalDate localDate1 = LocalDate.parse(start);
        LocalDate localDate2 = LocalDate.parse(end);
        long noOfDaysDifference = ChronoUnit.DAYS.between(localDate1, localDate2);
        return noOfDaysDifference;
    }

    private double calculateTotalCost(){

        long _days = getDayDifference(booking.getPickupDate(),booking.getReturnDate());
        double _vehicleRate = vehicle.getPrice();
        double _insuranceRate = chosenInsurance.getCost();

        return (_days*_vehicleRate) + _insuranceRate;
    }

    private int generateID(int start, int end){
        Random rnd = new Random();
        int bound = end%100;
        int id = rnd.nextInt(bound)+start;
        return id;
    }

    //DEBUGING
    private void toast(String txt){
        Toast toast = Toast.makeText(getApplicationContext(),txt,Toast.LENGTH_SHORT);
        toast.show();
    }

}
