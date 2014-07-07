package com.akoudri.healthrecord.data;

/**
 * Created by Ali Koudri on 12/04/14.
 */
public class Therapist {

    private int id;
    private String name;
    private String phoneNumber;
    private String cellPhoneNumber;
    private String email;
    private int branchId;

    public Therapist() {}

    public Therapist(String name, String phoneNumber, String cellPhoneNumber, String email, int branchId) {
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.cellPhoneNumber = cellPhoneNumber;
        this.email = email;
        this.branchId = branchId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName(){
        return name;
    }

    public void setName(String name){
        this.name = name;
    }

    public String getPhoneNumber(){
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber){
        this.phoneNumber = phoneNumber;
    }

    public int getBranchId() {
        return branchId;
    }

    public void setBranchId(int branchId) {
        this.branchId = branchId;
    }

    public String getCellPhoneNumber() {
        return cellPhoneNumber;
    }

    public void setCellPhoneNumber(String cellPhoneNumber) {
        this.cellPhoneNumber = cellPhoneNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean equalsTo(Therapist other)
    {
        boolean eqName = name.equals(other.getName());
        boolean eqPhone = (phoneNumber == null)?other.getPhoneNumber()==null:phoneNumber.equals(other.getPhoneNumber());
        boolean eqCell = (cellPhoneNumber == null)?other.getCellPhoneNumber()==null:cellPhoneNumber.equals(other.getCellPhoneNumber());
        boolean eqEmail = (email == null)?other.getEmail()==null:email.equals(other.getEmail());
        boolean eqBranch = (branchId == other.getBranchId());
        return eqName && eqPhone && eqCell && eqEmail && eqBranch;
    }
}
