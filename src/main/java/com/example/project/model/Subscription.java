package com.example.project.model;

import jakarta.persistence.*;

@Entity
@Table(name = "subscriptions")
public class Subscription {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private String name;

    private Double price;

    @Column(name = "duration_days")
    private Integer durationDays;

    private String description;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    public Subscription() {
    }

    public Subscription(String name, Double price, Integer durationDays, String description, User user) {
        this.name = name;
        this.price = price;
        this.durationDays = durationDays;
        this.description = description;
        this.user = user;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public Double getPrice() {
        return price;
    }

    public Integer getDurationDays() {
        return durationDays;
    }

    public String getDescription() {
        return description;
    }

    public User getUser() {
        return user;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public void setDurationDays(Integer durationDays) {
        this.durationDays = durationDays;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setUser(User user) {
        this.user = user;
    }
}