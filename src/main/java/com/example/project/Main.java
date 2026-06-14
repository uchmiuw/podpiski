package com.example.project;

import com.example.project.model.Subscription;
import com.example.project.model.User;
import com.example.project.service.SubscriptionService;
import com.example.project.service.UserService;
import com.example.project.util.HibernateUtil;

public class Main {

    public static void main(String[] args) {

        UserService userService = new UserService();

        SubscriptionService subscriptionService = new SubscriptionService();

        System.out.println("Данные в БД");

        if (userService.getAllUsers().isEmpty()) {

            System.out.println("\nБаза данных пуста. Добавляем данные...");

            User user1 = new User("Алексей", "alex@mail.ru");

            User user2 = new User("Мария", "maria@mail.ru");

            User user3 = new User("Дмитрий", "dmitry@mail.ru");

            userService.addUser(user1);
            userService.addUser(user2);
            userService.addUser(user3);

            Subscription sub1 = new Subscription("Мини", 299.00, 30, "Базовый тариф");
            Subscription sub2 = new Subscription("Стандарт", 399.00, 30, "Стандартный тариф");
            Subscription sub3 = new Subscription("Премиум", 699.00, 30, "Премиум тариф");

            subscriptionService.addSubscription(sub1);
            subscriptionService.addSubscription(sub2);
            subscriptionService.addSubscription(sub3);

            System.out.println("Данные сохранены.");
        }

        userService.printAllUsers();
        subscriptionService.printAllSubscriptions();
        HibernateUtil.shutdown();
    }
}