package com.example.carrentalapp.ActivityPages;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.carrentalapp.Database.Project_Database;
import com.example.carrentalapp.Database.VehicleCategoryDao;
import com.example.carrentalapp.Database.VehicleDao;
import com.example.carrentalapp.Model.Vehicle;
import com.example.carrentalapp.R;
import com.google.firebase.FirebaseError;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.Random;

public class AddVehicleActivity extends AppCompatActivity {

    private EditText category;
    private EditText seats;
    private EditText price;
    private EditText mileage;
    private EditText manufacturer;
    private EditText model;
    private EditText year;
    private EditText imageURL;
    private CheckBox availability;

    private Button add;
    private Button reset;
    private Button vehicleCategory;
    private Button viewResult;
    private Button load;

    private ImageView vehicleImage;

    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_vehicle);

        initComponents();
        listenHandler();
    }

    private void initComponents(){
        category = findViewById(R.id.category);
        seats = findViewById(R.id.seats);
        price = findViewById(R.id.price);
        mileage = findViewById(R.id.mileage);
        manufacturer = findViewById(R.id.manufacturer);
        model = findViewById(R.id.model);
        year = findViewById(R.id.year);
        imageURL = findViewById(R.id.imageURL);
        availability = findViewById(R.id.availability);
        add = findViewById(R.id.add);
        reset = findViewById(R.id.reset);
        vehicleCategory = findViewById(R.id.vehicleCategory);
        viewResult = findViewById(R.id.viewResult);

        load = findViewById(R.id.load);
        vehicleImage = findViewById(R.id.viewVehicle);


    }

    private void listenHandler(){
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Vehicle vehicle = createVehicle();

                if(vehicle != null){

                    mDatabase = FirebaseDatabase.getInstance("https://car-rental-android-app-m-f727e-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference();
                    mDatabase.child("Vehicle").child(vehicle.getCategory()).child(String.valueOf(vehicle.getVehicleID())).setValue(vehicle);
                    mDatabase.child("VehicleCategory").child(category.getText().toString()).child("quantity").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot snapshot) {
                            try {
                                if (snapshot.getValue() != null) {
                                    try {
                                        mDatabase.child("VehicleCategory").child(category.getText().toString()).child("quantity").setValue(Integer.parseInt(snapshot.getValue().toString())+1);
                                      
                                        toast("Vehicle Added");
                                        Log.e("TAG", "" + snapshot.getValue()); // your name values you will get here
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
            }
        });

        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDatabase = FirebaseDatabase.getInstance("https://car-rental-android-app-m-f727e-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference();
                mDatabase.child("Vehicle").removeValue();
                mDatabase.child("VehicleCategory").removeValue();
                toast("RESET");
            }
        });

        vehicleCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent addVehicleCategoryPage = new Intent(AddVehicleActivity.this,AddVehicleCategoryActivity.class);
                startActivity(addVehicleCategoryPage);
            }
        });

        viewResult.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toast("View Result");
                Intent viewResult = new Intent(AddVehicleActivity.this,UserViewActivity.class);
                startActivity(viewResult);
            }
        });

        load.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!imageURL.getText().toString().equals("")){
                    Picasso.get().load(imageURL.getText().toString()).into(vehicleImage);
                }
            }
        });
    }

    private Vehicle createVehicle(){
        String _category = category.getText().toString();
        String _seats = seats.getText().toString();
        String _price = price.getText().toString();
        String _mileage = mileage.getText().toString();
        String _manufacturer = manufacturer.getText().toString();
        String _model = model.getText().toString();
        String _year = year.getText().toString();
        String _imageURL = imageURL.getText().toString();
        boolean _availability = availability.isChecked();

        boolean valid = isValid(_category,_seats,_price,_mileage,_manufacturer,_model,_year,_imageURL);

        int vehicleID = generateID(200,300);



        if(valid){
            Vehicle vehicle = new Vehicle(
                                    vehicleID,
                                    Double.valueOf(_price),
                                    Integer.valueOf(_seats),
                                    Integer.valueOf(_mileage),
                                    _manufacturer,
                                    _model,
                                    Integer.valueOf(_year),
                                    _category,
                                    _availability,
                                    _imageURL
                                );
            return vehicle;
        }

        return null;
    }

    private boolean isValid(String category, String seats, String price, String mileage, String manufacturer, String model, String year, String imageURL) {

         if(category.equals("")){
            toast("Category is blank");
            return false;
        }
        else if(seats.equals("")){
            toast("Seats is blank");
            return false;
        }
        else if(price.equals("")){
            toast("Price is blank");
            return false;
        }
        else if(mileage.equals("")){
            toast("Mileage is blank");
            return false;
        }else if(manufacturer.equals("")){
            toast("Manufacturer is blank");
            return false;
        }
        else if(model.equals("")){
            toast("Model is blank");
            return false;
        }
        else if(year.equals("")){
            toast("Year is blank");
            return false;
        }else if(imageURL.equals("")){
            toast("ImageURL is blank");
        }
        return true;
    }

    //DEBUGING
    private void toast(String txt){
        Toast toast = Toast.makeText(getApplicationContext(),txt,Toast.LENGTH_LONG);
        toast.show();
    }

    private int generateID(int start,int end){
        Random rnd = new Random();
        int id = 202000 + rnd.nextInt(65)+10;
        return id;
    }
}
