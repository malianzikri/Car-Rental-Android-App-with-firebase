package com.example.carrentalapp.FragmentPages;


import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.carrentalapp.ActivityPages.LoginActivity;
import com.example.carrentalapp.ActivityPages.VehicleInfoActivity;
import com.example.carrentalapp.Adapter.VehicleAdapter;
import com.example.carrentalapp.Database.Project_Database;
import com.example.carrentalapp.Database.VehicleDao;
import com.example.carrentalapp.Model.Vehicle;
import com.example.carrentalapp.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.Serializable;
import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class VehicleFragment extends Fragment implements VehicleAdapter.onVehicleListener{
    
    private String selectVehicleCategory;
    private VehicleDao vehicleDao;

    private RecyclerView recyclerView;
    private ArrayList<Vehicle> list;
    private VehicleAdapter adapter;

    public VehicleFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_vehicle, container, false);
        initComponents(view);
        
        return view;
    }
    private DatabaseReference mDatabase;
    private void initComponents(View view) {
        selectVehicleCategory = getArguments().getString("CATEGORY");
        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mDatabase = FirebaseDatabase.getInstance("https://car-rental-android-app-m-f727e-default-rtdb.asia-southeast1.firebasedatabase.app").getReference();
        final ArrayList<Vehicle> vehicles=new ArrayList<>();
        mDatabase.child("Vehicle").child(selectVehicleCategory.toLowerCase()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                System.out.println("masuk sini dak sih");
                for(DataSnapshot singleSnapshot : dataSnapshot.getChildren()){
                    Vehicle vehicle = singleSnapshot.getValue(Vehicle.class);
                    vehicles.add(vehicle);
                }
                list =vehicles ;
                adapter = new VehicleAdapter(getContext(), list,VehicleFragment.this);
                recyclerView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
//        list = (ArrayList<Vehicle>)vehicleDao.getCategoryVehicle(selectVehicleCategory);
////        list =vehicles ;
//        adapter = new VehicleAdapter(getContext(), list,this);
//        recyclerView.setAdapter(adapter);



    }

    @Override
    public void onClick(int position) {
        Intent vehicleInfoPage = new Intent(getActivity(), VehicleInfoActivity.class);
        vehicleInfoPage.putExtra("VEHICLE",list.get(position));
        startActivity(vehicleInfoPage);
    }


    //DEBUGING
    private void toast(String txt){
        Toast toast = Toast.makeText(getContext(),txt,Toast.LENGTH_SHORT);
        toast.show();
    }
}
