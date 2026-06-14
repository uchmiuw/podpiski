package com.example.project.service;

import com.example.project.model.UserSubscription;
import com.example.project.repository.UserSubscriptionRepository;

import java.util.List;

public class UserSubscriptionService {

    private final UserSubscriptionRepository repository = new UserSubscriptionRepository();

    public void add(UserSubscription us) {
        repository.save(us);
    }

    public void update(UserSubscription us) {
        repository.update(us);
    }

    public List<UserSubscription> getAll() {
        return repository.findAll();
    }

    public void delete(UserSubscription us) {
        repository.delete(us);
    }
}