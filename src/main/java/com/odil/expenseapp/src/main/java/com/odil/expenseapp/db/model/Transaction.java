package com.odil.expenseapp.model;

import java.time.LocalDate;

public class Transaction {
    private Integer id;
    private LocalDate date;
    private String description;
    private String category;
    private String type; // INCOME or EXPENSE
    private double amount;

    public Transaction() {}

    public Transaction(Integer id, LocalDate date, String description, String category, String type, double amount) {
        this.id = id;
        this.date = date;
        this.description = description;
        this.category = category;
        this.type = type;
        this.amount = amount;
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }
}
