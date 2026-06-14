package com.example.project.controller;

import com.example.project.model.Subscription;
import com.example.project.model.User;
import com.example.project.model.UserSubscription;
import com.example.project.service.SubscriptionService;
import com.example.project.service.UserService;
import com.example.project.service.UserSubscriptionService;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class MainController implements Initializable {

    @FXML private TableView<UserSubscription> table;
    @FXML private TableColumn<UserSubscription, Integer> colId;
    @FXML private TableColumn<UserSubscription, String>  colUser;
    @FXML private TableColumn<UserSubscription, String>  colSubscription;
    @FXML private TableColumn<UserSubscription, String>  colPrice;
    @FXML private TableColumn<UserSubscription, String>  colDuration;
    @FXML private TableColumn<UserSubscription, String>  colDescription;

    @FXML private ComboBox<User>         userCombo;
    @FXML private ComboBox<Subscription> subscriptionCombo;

    private UserSubscriptionService userSubscriptionService;
    private UserService             userService;
    private SubscriptionService     subscriptionService;

    private ObservableList<UserSubscription> tableData;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        userSubscriptionService = new UserSubscriptionService();
        userService             = new UserService();
        subscriptionService     = new SubscriptionService();

        setupColumns();
        loadCombos();
        loadTable();

        table.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldVal, newVal) -> { if (newVal != null) fillForm(newVal); }
        );
    }

    private void setupColumns() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colUser.setCellValueFactory(cd ->
                new SimpleStringProperty(cd.getValue().getUser() != null
                        ? cd.getValue().getUser().getName() : ""));
        colSubscription.setCellValueFactory(cd ->
                new SimpleStringProperty(cd.getValue().getSubscription() != null
                        ? cd.getValue().getSubscription().getName() : ""));
        colPrice.setCellValueFactory(cd ->
                new SimpleStringProperty(cd.getValue().getSubscription() != null
                        ? cd.getValue().getSubscription().getPrice() + " руб." : ""));
        colDuration.setCellValueFactory(cd ->
                new SimpleStringProperty(cd.getValue().getSubscription() != null
                        ? cd.getValue().getSubscription().getDurationDays() + " дней" : ""));
        colDescription.setCellValueFactory(cd ->
                new SimpleStringProperty(cd.getValue().getSubscription() != null
                        ? (cd.getValue().getSubscription().getDescription() != null
                        ? cd.getValue().getSubscription().getDescription() : "") : ""));
    }

    // ── Загрузка комбобоксов ─────────────────────────────────────────────────
    private void loadCombos() {
        // Сохраняем текущие выбранные значения
        User currentUser = userCombo.getValue();
        Subscription currentSub = subscriptionCombo.getValue();

        List<User> users = userService.getAllUsers();
        ObservableList<User> userList = FXCollections.observableArrayList(users);
        userCombo.setItems(userList);

        userCombo.setCellFactory(lv -> new ListCell<>() {
            @Override protected void updateItem(User u, boolean empty) {
                super.updateItem(u, empty);
                setText(empty || u == null ? null : u.getName() + " (" + u.getEmail() + ")");
            }
        });
        userCombo.setConverter(new javafx.util.StringConverter<>() {
            @Override public String toString(User u) {
                return u == null ? "" : u.getName() + " (" + u.getEmail() + ")";
            }
            @Override public User fromString(String s) { return null; }
        });

        List<Subscription> subs = subscriptionService.getAllSubscriptions();
        ObservableList<Subscription> subList = FXCollections.observableArrayList(subs);
        subscriptionCombo.setItems(subList);

        subscriptionCombo.setCellFactory(lv -> new ListCell<>() {
            @Override protected void updateItem(Subscription s, boolean empty) {
                super.updateItem(s, empty);
                setText(empty || s == null ? null : s.getName() + " — " + s.getPrice() + " руб. / " + s.getDurationDays() + " дней");
            }
        });
        subscriptionCombo.setConverter(new javafx.util.StringConverter<>() {
            @Override public String toString(Subscription s) {
                return s == null ? "" : s.getName() + " — " + s.getPrice() + " руб. / " + s.getDurationDays() + " дней";
            }
            @Override public Subscription fromString(String s) { return null; }
        });

        // Восстанавливаем выбранные значения если были
        if (currentUser != null) {
            users.stream().filter(u -> u.getId().equals(currentUser.getId()))
                    .findFirst().ifPresent(userCombo::setValue);
        }
        if (currentSub != null) {
            subs.stream().filter(s -> s.getId().equals(currentSub.getId()))
                    .findFirst().ifPresent(subscriptionCombo::setValue);
        }
    }

    private void loadTable() {
        tableData = FXCollections.observableArrayList(userSubscriptionService.getAll());
        table.setItems(tableData);
    }

    // ── Заполнение формы — теперь ищем объект по ID в списке комбобокса ──────
    private void fillForm(UserSubscription us) {
        // Ищем пользователя в списке комбобокса по ID
        userCombo.getItems().stream()
                .filter(u -> u.getId().equals(us.getUser().getId()))
                .findFirst()
                .ifPresent(userCombo::setValue);

        // Ищем тариф в списке комбобокса по ID
        subscriptionCombo.getItems().stream()
                .filter(s -> s.getId().equals(us.getSubscription().getId()))
                .findFirst()
                .ifPresent(subscriptionCombo::setValue);
    }

    private void clearForm() {
        userCombo.setValue(null);
        subscriptionCombo.setValue(null);
        table.getSelectionModel().clearSelection();
    }

    // ── Добавить ─────────────────────────────────────────────────────────────
    @FXML
    private void onAddButtonClick() {
        User user = userCombo.getValue();
        Subscription sub = subscriptionCombo.getValue();

        if (user == null) { showError("Выберите пользователя"); return; }
        if (sub == null)  { showError("Выберите тариф"); return; }

        for (UserSubscription existing : tableData) {
            if (existing.getUser().getId().equals(user.getId()) &&
                    existing.getSubscription().getId().equals(sub.getId())) {
                showError("У пользователя «" + user.getName() + "» уже есть тариф «" + sub.getName() + "»");
                return;
            }
        }

        try {
            userSubscriptionService.add(new UserSubscription(user, sub));
            loadTable();
            clearForm();
            showInfo("Подписка успешно добавлена");
        } catch (Exception e) {
            showError("Не удалось добавить: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // ── Обновить тариф у выбранной записи ────────────────────────────────────
    @FXML
    private void onUpdateButtonClick() {
        UserSubscription selected = table.getSelectionModel().getSelectedItem();
        if (selected == null) { showError("Выберите строку в таблице для обновления"); return; }

        Subscription newSub = subscriptionCombo.getValue();
        if (newSub == null) { showError("Выберите новый тариф"); return; }

        try {
            selected.setSubscription(newSub);
            userSubscriptionService.update(selected);
            loadTable();
            clearForm();
            showInfo("Тариф успешно обновлён");
        } catch (Exception e) {
            showError("Не удалось обновить: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // ── Удалить ──────────────────────────────────────────────────────────────
    @FXML
    private void onDeleteButtonClick() {
        UserSubscription selected = table.getSelectionModel().getSelectedItem();
        if (selected == null) { showError("Выберите строку для удаления"); return; }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Подтверждение");
        confirm.setHeaderText(null);
        confirm.setContentText("Удалить тариф «" + selected.getSubscription().getName()
                + "» у пользователя «" + selected.getUser().getName() + "»?");
        if (confirm.showAndWait().orElse(ButtonType.CANCEL) != ButtonType.OK) return;

        try {
            userSubscriptionService.delete(selected);
            loadTable();
            clearForm();
            showInfo("Подписка удалена");
        } catch (Exception e) {
            showError("Не удалось удалить: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void onClearButtonClick() {
        clearForm();
    }

    // ── Создать нового пользователя ──────────────────────────────────────────
    @FXML
    private void onCreateUserButtonClick() {
        Dialog<User> dialog = new Dialog<>();
        dialog.setTitle("Новый пользователь");
        dialog.setHeaderText("Введите данные нового пользователя");

        ButtonType okBtn     = new ButtonType("Создать", ButtonBar.ButtonData.OK_DONE);
        ButtonType cancelBtn = new ButtonType("Отмена",  ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().addAll(okBtn, cancelBtn);

        TextField nameInput  = new TextField();
        nameInput.setPromptText("Имя");
        TextField emailInput = new TextField();
        emailInput.setPromptText("user@mail.ru");

        GridPane grid = new GridPane();
        grid.setHgap(10); grid.setVgap(10);
        grid.setPadding(new Insets(10));
        grid.add(new Label("Имя:"),   0, 0); grid.add(nameInput,  1, 0);
        grid.add(new Label("Email:"), 0, 1); grid.add(emailInput, 1, 1);
        dialog.getDialogPane().setContent(grid);

        javafx.scene.Node createBtn = dialog.getDialogPane().lookupButton(okBtn);
        createBtn.setDisable(true);
        nameInput.textProperty().addListener((obs, o, n) ->
                createBtn.setDisable(n.trim().isEmpty() || emailInput.getText().trim().isEmpty()));
        emailInput.textProperty().addListener((obs, o, n) ->
                createBtn.setDisable(n.trim().isEmpty() || nameInput.getText().trim().isEmpty()));

        dialog.setResultConverter(btn -> {
            if (btn != okBtn) return null;
            String name  = nameInput.getText().trim();
            String email = emailInput.getText().trim();
            for (User u : userService.getAllUsers()) {
                if (u.getEmail().equalsIgnoreCase(email)) {
                    showError("Пользователь с таким email уже существует: " + u.getName());
                    return null;
                }
            }
            User newUser = new User(name, email);
            userService.addUser(newUser);
            return newUser;
        });

        dialog.showAndWait().ifPresent(user -> {
            if (user != null) {
                loadCombos();
                userCombo.setValue(user);
                showInfo("Пользователь «" + user.getName() + "» создан");
            }
        });
    }

    private void showInfo(String msg) {
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setTitle("Успешно"); a.setHeaderText(null); a.setContentText(msg);
        a.showAndWait();
    }

    private void showError(String msg) {
        Alert a = new Alert(Alert.AlertType.ERROR);
        a.setTitle("Ошибка"); a.setHeaderText(null); a.setContentText(msg);
        a.showAndWait();
    }
}