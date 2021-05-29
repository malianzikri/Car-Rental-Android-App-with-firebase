package com.example.carrentalapp.FragmentPages;


import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.carrentalapp.ActivityPages.ViewBookingActivity;
import com.example.carrentalapp.Adapter.BookingAdapter;
import com.example.carrentalapp.Adapter.VehicleCategoryAdapter;
import com.example.carrentalapp.Database.BookingDao;
import com.example.carrentalapp.Database.Project_Database;
import com.example.carrentalapp.Model.Booking;
import com.example.carrentalapp.Model.Vehicle;
import com.example.carrentalapp.Model.VehicleCategory;
import com.example.carrentalapp.R;
import com.example.carrentalapp.Session.Session;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class BookingFragment extends Fragment implements BookingAdapter.onBookingListener{

    private RecyclerView recyclerView;
    private ArrayList<Booking> bookings;
    private BookingAdapter bookingAdapter;

    private BookingDao bookingDao;

    private String customerID;

    public BookingFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_booking, container, false);
        initComponents(view);
        return view;
    }
    private DatabaseReference mDatabase;
    private void initComponents(View view) {
        bookingDao = Room.databaseBuilder(getContext(), Project_Database.class, "car_rental_db").allowMainThreadQueries()
                    .build()
                    .bookingDao();

        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        customerID =Session.read(getContext(),"customerID","-1");

        mDatabase = FirebaseDatabase.getInstance("https://car-rental-android-app-m-f727e-default-rtdb.asia-southeast1.firebasedatabase.app").getReference();
        final ArrayList<Booking> books=new ArrayList<>();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        mDatabase.child("Booking").child(user.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                System.out.println("masuk sini dak sih");
                for(DataSnapshot singleSnapshot : dataSnapshot.getChildren()){
                    Booking booking = singleSnapshot.getValue(Booking.class);
                    
//                    booking.setAdministratorID(Integer.parseInt(singleSnapshot.child("administratorID").getValue().toString()));
//                    booking.setBillingID(Integer.parseInt(singleSnapshot.child("billingID").getValue().toString()));
//                    booking.setBookingID(Integer.parseInt(singleSnapshot.child("bookingID").getValue().toString()));
//                    booking.setBookingStatus(singleSnapshot.child("bookingStatus").getValue().toString());
//                    booking.setCustomerID(singleSnapshot.child("customerID").getValue().toString());
//                    booking.setInsuranceID(singleSnapshot.child("insuranceID").getValue().toString());
//                    booking.setVehicleCategory(singleSnapshot.child("vehicleCategory").getValue().toString());
//                    booking.setVehicleID(Integer.parseInt(singleSnapshot.child("vehicleID").getValue().toString()));
                    books.add(booking);
                }
                bookings =books ;
                bookingAdapter = new BookingAdapter(getContext(),bookings,BookingFragment.this);
                recyclerView.setAdapter(bookingAdapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    @Override
    public void onClick(int position) {
        int bookingID = bookings.get(position).getBookingID();
        Intent viewBooking = new Intent(getContext(), ViewBookingActivity.class);
        viewBooking.putExtra("BOOKINGID",""+bookingID);
        startActivity(viewBooking);
    }

    //DEBUGING
    private void toast(String txt){
        Toast toast = Toast.makeText(getContext(),txt,Toast.LENGTH_SHORT);
        toast.show();
    }
}
