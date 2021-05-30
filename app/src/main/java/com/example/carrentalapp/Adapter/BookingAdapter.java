package com.example.carrentalapp.Adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import com.example.carrentalapp.Database.CustomerDao;
import com.example.carrentalapp.Database.Project_Database;
import com.example.carrentalapp.Database.VehicleDao;
import com.example.carrentalapp.Model.Booking;
import com.example.carrentalapp.Model.Customer;
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

import java.util.ArrayList;

public class BookingAdapter extends RecyclerView.Adapter<BookingAdapter.BookingHolder>{

    private Context context;
    private ArrayList<Booking> bookings;
    private onBookingListener onBookingListener;

    private VehicleDao vehicleDao;
    private CustomerDao customerDao;

    public BookingAdapter(Context context, ArrayList<Booking> bookings, BookingAdapter.onBookingListener onBookingListener) {
        this.context = context;
        this.bookings = bookings;
        this.onBookingListener = onBookingListener;

        vehicleDao = Room.databaseBuilder(context, Project_Database.class, "car_rental_db").allowMainThreadQueries()
                    .build()
                    .vehicleDao();
        customerDao = Room.databaseBuilder(context, Project_Database.class, "car_rental_db").allowMainThreadQueries()
                    .build()
                    .customerDao();
    }

    @NonNull
    @Override
    public BookingHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.booking_card,null);
        return new BookingHolder(view,onBookingListener);
    }
    private DatabaseReference mDatabase;
    @Override
    public void onBindViewHolder(@NonNull final BookingHolder bookingHolder, int position) {
        final Booking _booking = bookings.get(position);
        mDatabase = FirebaseDatabase.getInstance("https://car-rental-android-app-m-f727e-default-rtdb.asia-southeast1.firebasedatabase.app").getReference();

        mDatabase.child("Vehicle").child(_booking.getVehicleCategory()).child(String.valueOf(_booking.getVehicleID())).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                try {
                    if (snapshot.getValue() != null) {
                        try {
                            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                            Vehicle _vehicle = snapshot.getValue(Vehicle.class);
                            bookingHolder.vehicleName.setText(_vehicle.fullTitle());
                            bookingHolder.bookingID.setText(_booking.getBookingID()+"");
                            bookingHolder.customerName.setText(user.getEmail());
                            bookingHolder.pickupDate.setText(_booking.getPickupTime());
                            bookingHolder.returnDate.setText(_booking.getReturnTime());
                            bookingHolder.bookingStatus.setText(_booking.getBookingStatus());
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

    @Override
    public int getItemCount() { return bookings.size(); }

    class BookingHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        TextView vehicleName, bookingID, customerName,
                 pickupDate, returnDate, bookingStatus;
        onBookingListener onBookingListener;

        public BookingHolder(@NonNull View itemView, onBookingListener onBookingListener) {
            super(itemView);

            vehicleName = itemView.findViewById(R.id.vehicleName);
            bookingID = itemView.findViewById(R.id.bookingID);
            customerName = itemView.findViewById(R.id.customerName);
            pickupDate = itemView.findViewById(R.id.pickupDate);
            returnDate = itemView.findViewById(R.id.returnDate);
            bookingStatus = itemView.findViewById(R.id.bookingStatus);

            this.onBookingListener = onBookingListener;
            itemView.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {
            onBookingListener.onClick(getAdapterPosition());
        }
    }

    public interface onBookingListener{
        void onClick(int position);
    }
}
