package com.shreyansh.calculator.models;

public class Operation {

    public String input;
    public String output;

    // Default constructor required for calls to
    // DataSnapshot.getValue(User.class)
    public Operation() {
    }

    public Operation(String input, String output) {
        this.input = input;
        this.output = output;
    }
}
