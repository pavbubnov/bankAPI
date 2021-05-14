package com.bubnov.entity;

import java.util.List;
import java.util.Objects;

public class Account {

    private int id;
    private String nameOfPerson;

    public Account(int id, String nameOfPerson) {
        this.id = id;
        this.nameOfPerson = nameOfPerson;
    }

    public Account() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNameOfPerson() {
        return nameOfPerson;
    }

    public void setNameOfPerson(String nameOfPerson) {
        this.nameOfPerson = nameOfPerson;
    }


    @Override
    public String toString() {
        return "Account{" +
                "id=" + id +
                ", nameOfPerson='" + nameOfPerson + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Account account = (Account) o;
        return id == account.id && nameOfPerson.equals(account.nameOfPerson);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, nameOfPerson);
    }
}
