package com.example.carrentalapp.ActivityPages;

import android.app.DatePickerDialog;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.room.Room;

import com.example.carrentalapp.Database.CustomerDao;
import com.example.carrentalapp.Database.Project_Database;
import com.example.carrentalapp.Model.*;
import com.example.carrentalapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Random;


public class RegistrationActivity extends AppCompatActivity{

    private Button register;
    private TextView login;

    private TextView expiryDate;
    private TextView dob;
    private CustomerDao customerDao;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        initComponents();
        mAuth = FirebaseAuth.getInstance();
        clickListenHandler();

    }

    //Initialize all the components in Register Page
    private void initComponents(){

        //Register Button
        register = findViewById(R.id.register);
        //Login Button
        login = findViewById(R.id.login);
        //Expiry Button
        expiryDate = findViewById(R.id.expiryDate);
        //Date of Birth Button
        dob = findViewById(R.id.dob);

        //Get the Customer Room (table)
        customerDao = Room.databaseBuilder(getApplicationContext(), Project_Database.class, "car_rental_db").allowMainThreadQueries()
                .build()
                .customerDao();

    }

    //This method handles all the click events
    private void clickListenHandler(){

        //Expiry Date Listener
        expiryDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openCalendar(expiryDate);
            }
        });

        //Date of Birth Listener
        dob.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openCalendar(dob);
            }
        });

        //Login Listener
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent registerPage = new Intent(RegistrationActivity.this,LoginActivity.class);
                startActivity(registerPage);
            }
        });

        //Register Listener
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Customer customer = createCustomerObject();

                if(customerDao != null) {
                    //If customer object is null -> Incomplete form
                    //If customer object not null -> Complete form
                    if(customer != null) {
                        mAuth.createUserWithEmailAndPassword(customer.getEmail(), customer.getPassword())
                                .addOnCompleteListener(RegistrationActivity.this, new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        if (task.isSuccessful()) {
                                            // Sign in success, update UI with the signed-in user's information
                                            mDatabase = FirebaseDatabase.getInstance("https://car-rental-android-app-m-f727e-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference();

                                            FirebaseUser user = task.getResult().getUser();
                                            final String uid= user.getUid();
                                            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                                    .setDisplayName(customer.getFullName())
                                                    .build();

                                            user.updateProfile(profileUpdates)
                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            if (task.isSuccessful()) {
                                                                mDatabase.child("users").child(uid).child("detail_user").setValue(customer);

                                                                toast("Registration Successful");
                                                                finish();
                                                            }
                                                        }
                                                    });

                                        } else {
                                            // If sign in fails, display a message to the user.
                                            Toast.makeText(RegistrationActivity.this, task.getException().getMessage(),
                                                    Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });

                    }
                }
            }
        });
    }

    //Opening a Calendar Dialog
    private void openCalendar(final TextView dateFieldButton) {
        DatePickerDialog datePickerDialog = new DatePickerDialog(this);

        datePickerDialog.setOnDateSetListener(new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                String date = year + "-" + month + "-" + dayOfMonth;
                dateFieldButton.setText(date);
            }
        });

        datePickerDialog.show();
    }

    //Create the customer object from the form
    private Customer createCustomerObject(){

        String firstName = ((EditText)findViewById(R.id.firstName)).getText().toString();
        String middleName = ((EditText)findViewById(R.id.middleName)).getText().toString();
        String lastName = ((EditText)findViewById(R.id.lastName)).getText().toString();

        String email = ((EditText)findViewById(R.id.email)).getText().toString();

        String driverLicense = ((EditText)findViewById(R.id.license)).getText().toString();
        String expiry = expiryDate.getText().toString();
        String dateOfBirth = dob.getText().toString();

        String phoneNumber = ((EditText)findViewById(R.id.phoneNumber)).getText().toString();

        String street = ((EditText)findViewById(R.id.street)).getText().toString();
        String city = ((EditText)findViewById(R.id.city)).getText().toString();
        String postalCode = ((EditText)findViewById(R.id.postalCode)).getText().toString();

        String password = ((EditText)findViewById(R.id.password)).getText().toString();
        String confirm_password = ((EditText)findViewById(R.id.confirmPassword)).getText().toString();

        boolean success = fieldRequiredCheck(firstName,lastName,email,driverLicense,expiry,dateOfBirth,phoneNumber,street,city,postalCode);

        int id = generateID();

        while(customerDao.exist(id)){
            id = generateID();
        }
        //This is to check if all fields are entered
        if(success){

            //Password and Confirm Password Check
            if(password.length() > 0 && password.equals(confirm_password)) {

                return new Customer(id,firstName, middleName, lastName,
                        email, driverLicense, expiry,
                        dateOfBirth, phoneNumber, street,
                        city, postalCode, password
                );
            }else{
                toast("Password do not match");
            }
        }else{
            toast("Incomplete Form");
        }

        return null;
    }

    private boolean fieldRequiredCheck(String firstName,String lastName, String email, String driverLicense, String expiry, String dateOfBirth, String phoneNumber, String street, String city, String postalCode) {
        return  !firstName.equals("") && !lastName.equals("") &&
                !email.equals("") && !driverLicense.equals("") && !expiry.equals("") &&
                !dateOfBirth.equals("") && !phoneNumber.equals("") && !street.equals("") &&
                !city.equals("") && !postalCode.equals("");
    }


    //DEBUGING
    private void toast(String txt){
        Toast toast = Toast.makeText(getApplicationContext(),txt,Toast.LENGTH_SHORT);
        toast.show();
    }

    private int generateID(){
        Random rnd = new Random();
        int id = 202000 + rnd.nextInt(65)+10;
        return id;
    }





}
