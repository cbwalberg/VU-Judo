package vu.judo.app;

public class User {

    // Variables
    private String email;
    private String fname;
    private String lname;
    private double score;

    // Constructor
    public User(String email, String firstName, String lastName, double score) {
        this.email = email;
        this.fname = firstName;
        this.lname = lastName;
        this.score = score;
    }

    // Getters
    public String getEmail() {
        return email;
    }

    public String getFname() {
        return fname;
    }

    public String getLname() {
        return lname;
    }

    public double getScore() {
        return score;
    }

    // Setters
    public void setEmail(String email) {
        this.email = email;
    }

    public void setFname(String fname) {
        this.fname = fname;
    }

    public void setLname(String lname) {
        this.lname = lname;
    }

    public void setScore(double score) {
        this.score = score;
    }

    // toString
    @Override
    public String toString() {
        return "Email: " + email + "\nFirst Name: " + fname + "\nLast Name: " + lname + "\n Score: " + score;
    }
}
