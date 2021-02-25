package org.example;

import java.time.LocalDate;
import java.util.Objects;

public class Person {
    long id;
    String login;
    String password;
    LocalDate birthday;

    public Person(long id, String login, String password, LocalDate birthday) {
        this.id = id;
        this.login = login;
        this.password = password;
        this.birthday = birthday;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public LocalDate getBirthday() {
        return birthday;
    }

    public void setBirthday(LocalDate birthday) {
        this.birthday = birthday;
    }

    @Override
    public String toString() {
        return "Person{" +
                "id=" + id +
                ", login='" + login + '\'' +
                ", password='" + password + '\'' +
                ", birthday=" + birthday +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Person person = (Person) o;
        return id == person.id &&
                login.equals(person.login) &&
                password.equals(person.password) &&
                birthday.equals(person.birthday);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, login, password, birthday);
    }
}
