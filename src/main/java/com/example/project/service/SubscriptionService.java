package com.example.project.service;

import com.example.project.model.Subscription;
import com.example.project.repository.SubscriptionRepository;

import java.util.List;

public class SubscriptionService {

    private SubscriptionRepository repository = new SubscriptionRepository();

    public void addSubscription(Subscription subscription) {
        repository.save(subscription);
    }

    public void updateSubscription(Subscription subscription) {
        repository.update(subscription);
    }

    public void deleteSubscription(Subscription subscription) {
        repository.delete(subscription);
    }

    public List<Subscription> getAllSubscriptions() {
        return repository.findAll();
    }

    public Subscription getSubscriptionById(Integer id) {
        return repository.findById(id);
    }

    public void printAllSubscriptions() {
        List<Subscription> subscriptions = repository.findAll();

        if (subscriptions.isEmpty()) {
            System.out.println("Список подписок пуст");
            return;
        }

        System.out.println("\nСПИСОК ПОДПИСОК");

        for (Subscription s : subscriptions) {
            System.out.println("Название: " + s.getName() + ", Цена: " + s.getPrice() +
                    ", Длительность: " + s.getDurationDays() + " дней" );
        }
    }
}