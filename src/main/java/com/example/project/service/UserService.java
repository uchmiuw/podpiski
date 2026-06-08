package com.example.project.service;

import com.example.project.model.User;
import com.example.project.repository.UserRepository;

import java.util.List;

public class UserService {

    private UserRepository userRepository = new UserRepository();

    public void addUser(User user) {
        userRepository.save(user);
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public void printAllUsers() {
        List<User> users = getAllUsers();
        if (users.isEmpty()) {
            System.out.println("Список пользователей пуст");
            return;
        }

        System.out.println("\nСПИСОК ПОЛЬЗОВАТЕЛЕЙ");

        for (User user : users) {
            System.out.println("Имя: " + user.getName() + ", Email: " + user.getEmail());
        }
    }
}