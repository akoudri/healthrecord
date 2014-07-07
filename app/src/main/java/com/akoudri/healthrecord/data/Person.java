package com.akoudri.healthrecord.data;

/**
 * Created by Ali Koudri on 01/04/14.
 */
public class Person {

    //fields

    private int id;
    private String name;
    private Gender gender;
    private String ssn; //Social Security Number
    private BloodType bloodType;
    private String birthdate;

    //Constructors
    public Person() {}

    public Person(String name, Gender gender, String ssn, BloodType bloodType, String birthdate)
    {
        this.name = name;
        this.gender = gender;
        this.ssn = ssn;
        this.bloodType = bloodType;
        this.birthdate = birthdate;
    }

    //Methods

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    public String getSsn() {
        return ssn;
    }

    public void setSsn(String ssn) {
        this.ssn = ssn;
    }

    public BloodType getBloodType() {
        return bloodType;
    }

    public void setBloodType(BloodType bloodType) {
        this.bloodType = bloodType;
    }

    public String getBirthdate() {
        return birthdate;
    }

    public void setBirthdate(String birthdate) {
        this.birthdate = birthdate;
    }

    public boolean equalsTo(Person other)
    {
        boolean eqName = name.equals(other.getName());
        boolean eqGender = (gender == other.getGender());
        boolean eqSsn = (ssn == null)?other.getSsn() == null:ssn.equals(other.getSsn());
        boolean eqBt = (bloodType == other.getBloodType());
        boolean eqBD = birthdate.equals(other.getBirthdate());
        return eqName && eqGender && eqSsn && eqBt && eqBD;
    }

}
