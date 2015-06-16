package com.akoudri.healthrecord.app;

import com.akoudri.healthrecord.data.Person;

/**
 * Created by Ali Koudri on 16/06/15.
 */
public class PersonManager {

    private static PersonManager manager = null;
    private Person person = null;

    private PersonManager() {}

    public static PersonManager getInstance()
    {
        if (manager == null)
        {
            manager = new PersonManager();
        }
        return manager;
    }

    public Person getPerson()
    {
        return person;
    }

    public void setPerson(Person person)
    {
        this.person = person;
    }
}
