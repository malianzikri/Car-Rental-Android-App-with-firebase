package com.example.carrentalapp.FragmentPages;


import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.example.carrentalapp.Adapter.VehicleAdapter;
import com.example.carrentalapp.Adapter.VehicleCategoryAdapter;
import com.example.carrentalapp.Database.Project_Database;
import com.example.carrentalapp.Database.VehicleCategoryDao;
import com.example.carrentalapp.FragmentPages.VehicleFragment;
import com.example.carrentalapp.Model.Vehicle;
import com.example.carrentalapp.Model.VehicleCategory;
import com.example.carrentalapp.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class VehicleCategoryFragment extends Fragment implements VehicleCategoryAdapter.onCategoryListener {

    private VehicleCategoryDao vehicleCategoryDao;

    private RecyclerView recyclerView;
    private VehicleCategoryAdapter adapter;

    private Button home;

    private ArrayList<VehicleCategory> list;

    public VehicleCategoryFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        
        View view = inflater.inflate(R.layout.fragment_vehicle_category, container, false);
        
        initComponents(view);
        listenHandler();

        return view;
    }
    private DatabaseReference mDatabase;
    private void initComponents(View view) {

        vehicleCategoryDao = Room.databaseBuilder(getContext(), Project_Database.class, "car_rental_db").allowMainThreadQueries()
                .build()
                .vehicleCategoryDao();

        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mDatabase = FirebaseDatabase.getInstance("https://car-rental-android-app-m-f727e-default-rtdb.asia-southeast1.firebasedatabase.app").getReference();
        final ArrayList<VehicleCategory> vehicles=new ArrayList<>();
        mDatabase.child("VehicleCategory").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                System.out.println("masuk sini dak sih");
                for(DataSnapshot singleSnapshot : dataSnapshot.getChildren()){
                    VehicleCategory vehicle = singleSnapshot.getValue(VehicleCategory.class);
                    vehicles.add(vehicle);
                }
                list =vehicles ;
                adapter = new VehicleCategoryAdapter(getContext(), list,VehicleCategoryFragment.this);
                recyclerView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
//        list = (ArrayList<VehicleCategory>) vehicleCategoryDao.getAllCategory();
//        adapter = new VehicleCategoryAdapter(getContext(), list,this);
//        recyclerView.setAdapter(adapter);
    }

    private void listenHandler() {

//        home.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent homePage = new Intent(HomePageActivity.this, LoginActivity.class);
//                startActivity(homePage);
//            }
//        });


    }

    //DEBUGING
    private void toast(String txt) {
        Toast toast = Toast.makeText(getContext(), txt, Toast.LENGTH_SHORT);
        toast.show();
    }

    @Override
    public void onCategoryClick(int position) {
        toast(list.get(position).getCategory());

        String selectedCategory = list.get(position).getCategory();

        Bundle bundle=new Bundle();
        bundle.putString("CATEGORY", String.valueOf(selectedCategory));

        Fragment viewVehicle = new VehicleFragment();
        viewVehicle.setArguments(bundle);
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.framelayout, viewVehicle);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();

    }

    @Override
    public void onSelectClick(int position) {
        toast(list.get(position).getCategory() + " Select");
    }

}
