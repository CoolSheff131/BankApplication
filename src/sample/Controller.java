package sample;


import aleg.Customer;
import aleg.Operation;
import aleg.Request;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import javafx.beans.property.SimpleFloatProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import static aleg.Request.getAwaitingRequests;


public class Controller {
    @FXML
    private Button loginBtn;
    @FXML
    private TextField login;
    @FXML
    private PasswordField passwordField;
    @FXML
    private TableView<Customer> customerTableView;
    @FXML
    private TableColumn<Customer, String> nameColumn,surnameColumn,patronymicColumn, loginColumn,passColumn;
    @FXML
    private TableColumn<Customer, Integer> passport_serColumn,passport_numColumn;

    @FXML
    private TableView<Operation> operationTableView;
    @FXML
    private TableColumn<Operation, String> statusColumn;
    @FXML
    private TableColumn<Operation, Integer> startAccColumn,endAccColumn,yearColumn,monthColumn,dayColumn,hourColumn,minuteColumn,idColumn;
    @FXML
    private TableColumn<Operation, Float> sumColumn;

    private String pas,log;
    private Gson gson = new Gson();
    ObservableList<Customer> customers = FXCollections.observableArrayList();
    ObservableList<Operation> operations = FXCollections.observableArrayList();

    @FXML
    private void initialize() {
        // Инициализация таблицы адресатов с двумя столбцами.
        nameColumn.setCellValueFactory(cellData ->  new SimpleStringProperty(cellData.getValue().getName()));
        surnameColumn.setCellValueFactory(cellData ->  new SimpleStringProperty(cellData.getValue().getSurname()));
        patronymicColumn.setCellValueFactory(cellData ->  new SimpleStringProperty(cellData.getValue().getPatronymic()));
        passport_serColumn.setCellValueFactory(cellData ->(new SimpleIntegerProperty(  cellData.getValue().getPassport_ser())).asObject());
        passport_numColumn.setCellValueFactory(cellData ->(new SimpleIntegerProperty( cellData.getValue().getPassport_num())).asObject());
        loginColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getLogin()));
        passColumn.setCellValueFactory(cellData -> new SimpleStringProperty( cellData.getValue().getPass()));

        statusColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getStatus()));
        startAccColumn.setCellValueFactory(cellData -> (new SimpleIntegerProperty( cellData.getValue().getStartAcc())).asObject());
        endAccColumn.setCellValueFactory(cellData ->  (new SimpleIntegerProperty( cellData.getValue().getEndAcc())).asObject());
        yearColumn.setCellValueFactory(cellData -> (new SimpleIntegerProperty( cellData.getValue().getYear())).asObject());
        monthColumn.setCellValueFactory(cellData ->  (new SimpleIntegerProperty( cellData.getValue().getMonth())).asObject());
        dayColumn.setCellValueFactory(cellData -> (new SimpleIntegerProperty( cellData.getValue().getDay())).asObject());
        hourColumn.setCellValueFactory(cellData -> (new SimpleIntegerProperty( cellData.getValue().getHour())).asObject());
        minuteColumn.setCellValueFactory(cellData -> (new SimpleIntegerProperty( cellData.getValue().getMinute())).asObject());
        idColumn.setCellValueFactory(cellData ->  (new SimpleIntegerProperty( cellData.getValue().getId())).asObject());
        sumColumn.setCellValueFactory(cellData -> (new SimpleFloatProperty(cellData.getValue().getSum())).asObject());

        customerTableView.setItems(customers);
        operationTableView.setItems(operations);
    }


    @FXML
    public void login(ActionEvent actionEvent) {
        System.out.println(login.getText() + " "  + passwordField.getText());
        log = login.getText();
        pas = passwordField.getText();
        try {
            String responce = Request.checkWorkerFromServer(log, pas);
            System.out.println(responce);
            if(responce.equals("true")) {
                try{
                    FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("main.fxml"));
                    Parent root1 = (Parent) fxmlLoader.load();
                    Stage stage = new Stage();
                    stage.setTitle("a");
                    stage.setScene(new Scene(root1));
                    stage.show();
                    ((Stage) loginBtn.getScene().getWindow()).close();
                }catch (Exception e){
                    e.printStackTrace();
                    System.out.println("AASAAS");
                }
            }else{

                //todo показать окно что неверный логин или пароль
            }

        } catch (IOException e) {

            //todo показать окно ошибка сервера
            e.printStackTrace();
        }

    }




    public void showTransfers(ActionEvent actionEvent) {
    }

    public void refresh(ActionEvent actionEvent) {
        try {
            String request = getAwaitingRequests("asd", "asd");
            System.out.println(request);
            if(request.equals("Access denied")) return;
            Type itemsListType = new TypeToken<List<Customer>>() {}.getType();
            ArrayList<Customer> arr = gson.fromJson(request, itemsListType);
            customers.clear();
            customers.addAll(arr);

        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            String request = Request.getAwaitingOperations("asd", "asd");
            System.out.println(request);
            if(request.equals("Access denied")) return;

            Type itemsListType = new TypeToken<List<Operation>>() {}.getType();
            ArrayList<Operation> arr = gson.fromJson(request, itemsListType);

            operations.clear();
            operations.addAll(arr);


        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    public void acceptCustomer(ActionEvent actionEvent) {
        Customer customer = customerTableView.getSelectionModel().getSelectedItem();
        if(customer != null){
            System.out.println("acc " + customer.getName());
            try {
                String response = Request.addUser("asd","asd",gson.toJson(customer));
                if(response.equals("success")){
                    customers.remove(customer);
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    public void denyCustomer(ActionEvent actionEvent) {
        Customer customer = customerTableView.getSelectionModel().getSelectedItem();
        if(customer != null){
            System.out.println("acc " + customer.getName());
            try {
                String response = Request.rejectRequest("asd","asd",gson.toJson(customer));
                if(response.equals("success")){
                    customers.remove(customer);
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    public void acceptOperation(ActionEvent actionEvent) {
        Operation operation = operationTableView.getSelectionModel().getSelectedItem();
        if(operation != null){
            try {
                String response = Request.acceptOperation("asd","asd",operation.getId());
                if(response.equals("success")){
                    operations.remove(operation);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            System.out.println("acc " + operation.getStatus());
        }
    }
    public void denyOperation(ActionEvent actionEvent) {
        Operation operation = operationTableView.getSelectionModel().getSelectedItem();
        if(operation != null){
            try {
                String response = Request.rejectOperation("asd","asd",operation.getId());
                if(response.equals("success")){
                    operations.remove(operation);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            System.out.println("acc " + operation.getStatus());
        }
    }
}
