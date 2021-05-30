package com.example.carrentalapp.Model;

import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.ForeignKey;

import com.google.firebase.database.Exclude;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Calendar;

@Entity(primaryKeys = {"bookingID","customerID"})
public class Booking implements Serializable {


    public String getVehicleCategory() {
        return VehicleCategory;
    }

    public void setVehicleCategory(String vehicleCategory) {
        VehicleCategory = vehicleCategory;
    }

    private  String VehicleCategory;
    private int bookingID;

    public String pickupDate;
    public String returnDate;

    private String bookingStatus;

    @ForeignKey(entity = Customer.class,
                parentColumns = "parentClassColumn",
                childColumns = "childClassColumn",
                onDelete = ForeignKey.CASCADE)
    @NonNull private String customerID;

    @ForeignKey(entity = Administrator.class,
                parentColumns = "parentClassColumn",
                childColumns = "childClassColumn",
                onDelete = ForeignKey.SET_NULL)
    private int administratorID;

    @ForeignKey(entity = Billing.class,
                parentColumns = "parentClassColumn",
                childColumns = "childClassColumn",
                onDelete = ForeignKey.CASCADE)
    private int billingID;



    private long vehicleID;

    @ForeignKey(entity = Insurance.class,
            parentColumns = "parentClassColumn",
            childColumns = "childClassColumn",
            onDelete = ForeignKey.CASCADE)
    private String insuranceID;

    public Booking() {

    }
    public Booking(int bookingID, String pickupDate, String returnDate, String bookingStatus, String customerID, int administratorID, int billingID, long vehicleID, String insuranceID,String VehicleCategory) {
        this.bookingID = bookingID;
        this.pickupDate = pickupDate;
        this.returnDate = returnDate;
        this.bookingStatus = bookingStatus;
        this.customerID = customerID;
        this.administratorID = administratorID;
        this.billingID = billingID;
        this.vehicleID = vehicleID;
        this.VehicleCategory = VehicleCategory;
        this.insuranceID = insuranceID;
    }

    public String toString(){
        SimpleDateFormat format = new SimpleDateFormat("MMMM, d yyyy hh:mm a");
        return  "\n" +
                "BookingID:         " + bookingID + "\n" +
                "Pickup Date:       " + pickupDate + "\n" +
                "Return Date:       " + returnDate + "\n" +
                "Status:            " + bookingStatus + "\n" +
                "CustomerID:        " + customerID + "\n" +
                "AdministratorID:   " + administratorID + "\n" +
                "VehicleCategory:   " + VehicleCategory + "\n" +
                "BillingID:         " + billingID + "\n";
    }

    public long getVehicleID() {
        return vehicleID;
    }

    public void setVehicleID(long vehicleID) {
        this.vehicleID = vehicleID;
    }

    public int getBookingID() {
        return bookingID;
    }

    public void setBookingID(int bookingID) {
        this.bookingID = bookingID;
    }

    public String getPickupDate() {
        return pickupDate;
    }

    public void setPickupDate(String pickupDate) {
        this.pickupDate = pickupDate;
    }

    public String getReturnDate() {
        return returnDate;
    }

    public void setReturnDate(String returnDate) {
        this.returnDate = returnDate;
    }

    public String getBookingStatus() {
        return bookingStatus;
    }

    public void setBookingStatus(String bookingStatus) {
        this.bookingStatus = bookingStatus;
    }

    public String getCustomerID() {
        return customerID;
    }

    public void setCustomerID(String customerID) {
        this.customerID = customerID;
    }

    public int getAdministratorID() {
        return administratorID;
    }

    public void setAdministratorID(int administratorID) {
        this.administratorID = administratorID;
    }

    public int getBillingID() {
        return billingID;
    }

    public void setBillingID(int billingID) {
        this.billingID = billingID;
    }

    public String getInsuranceID() {
        return insuranceID;
    }

    public void setInsuranceID(String insuranceID) {
        this.insuranceID = insuranceID;
    }

    public String getPickupTime(){
        return pickupDate;
    }

    public String getReturnTime(){
        return returnDate;
    }
}
