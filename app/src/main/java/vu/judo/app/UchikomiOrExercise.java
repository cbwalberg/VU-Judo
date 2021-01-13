package vu.judo.app;

public class UchikomiOrExercise {

    // Variables
    private String name;
    private double multiplier;

    // Constructor
    public UchikomiOrExercise(String name, double multiplier) {
        this.name = name;
        this.multiplier = multiplier;
    }

    // Getters
    public String getName() {
        return name;
    }

    public double getMultiplier() {
        return multiplier;
    }

    // Setters
    public void setName(String name) {
        this.name = name;
    }

    public void setMultiplier(double multiplier) {
        this.multiplier = multiplier;
    }

    // toString
    @Override
    public String toString() {
        return "Name: " + name + "\n Multiplier: " + multiplier;
    }
}
