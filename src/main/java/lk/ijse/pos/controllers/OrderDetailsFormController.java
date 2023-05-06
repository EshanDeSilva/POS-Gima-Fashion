package lk.ijse.pos.controllers;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.controls.JFXTreeTableView;
import com.jfoenix.controls.RecursiveTreeItem;
import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableArray;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.cell.TreeItemPropertyValueFactory;
import javafx.scene.layout.Background;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import lk.ijse.pos.dto.OrderDetailsDto;
import lk.ijse.pos.dto.OrderDto;
import lk.ijse.pos.dto.PaymentDto;
import lk.ijse.pos.dto.tm.EmployerTm;
import lk.ijse.pos.dto.tm.OrderDetailsTm;
import lk.ijse.pos.dto.tm.OrderTm;
import lk.ijse.pos.dto.tm.SupplierTm;
import lk.ijse.pos.model.EmployerModel;
import lk.ijse.pos.model.ItemModel;
import lk.ijse.pos.model.OrderDetailsModel;
import lk.ijse.pos.model.OrderModel;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

public class OrderDetailsFormController {
    public JFXTreeTableView<OrderTm> tblOrderDetails;
    public TreeTableColumn colItemCode;
    public TreeTableColumn colDescription;
    public TreeTableColumn colQty;
    public TreeTableColumn colUnitPrice;
    public TreeTableColumn colDate;
    public TreeTableColumn colDiscount;
    public TreeTableColumn colType;
    public TreeTableColumn colSize;
    public TreeTableColumn colAmount;
    public JFXTreeTableView<OrderDetailsTm> tblOrder;
    public TreeTableColumn colOrderId;
    public TreeTableColumn colDate1;
    public TreeTableColumn colTotal1;
    public TreeTableColumn colCustName1;
    public TreeTableColumn colContact1;
    public TreeTableColumn colEmail1;
    public TreeTableColumn colEmployer1;
    public TreeTableColumn colArrears1;
    public JFXTextField txtSearch;
    public BorderPane orderDetailsPane;

    public void initialize(){

        colOrderId.setCellValueFactory(new TreeItemPropertyValueFactory<>("orderId"));
        colDate1.setCellValueFactory(new TreeItemPropertyValueFactory<>("date"));
        colTotal1.setCellValueFactory(new TreeItemPropertyValueFactory<>("total"));
        colCustName1.setCellValueFactory(new TreeItemPropertyValueFactory<>("customerName"));
        colContact1.setCellValueFactory(new TreeItemPropertyValueFactory<>("contact"));
        colEmail1.setCellValueFactory(new TreeItemPropertyValueFactory<>("email"));
        colEmployer1.setCellValueFactory(new TreeItemPropertyValueFactory<>("employer"));
        colArrears1.setCellValueFactory(new TreeItemPropertyValueFactory<>("arrears"));

        colItemCode.setCellValueFactory(new TreeItemPropertyValueFactory<>("itemCode"));
        colDescription.setCellValueFactory(new TreeItemPropertyValueFactory<>("description"));
        colQty.setCellValueFactory(new TreeItemPropertyValueFactory<>("qty"));
        colUnitPrice.setCellValueFactory(new TreeItemPropertyValueFactory<>("unitPrice"));
        colDate.setCellValueFactory(new TreeItemPropertyValueFactory<>("date"));
        colDiscount.setCellValueFactory(new TreeItemPropertyValueFactory<>("discount"));
        colType.setCellValueFactory(new TreeItemPropertyValueFactory<>("type"));
        colSize.setCellValueFactory(new TreeItemPropertyValueFactory<>("size"));
        colAmount.setCellValueFactory(new TreeItemPropertyValueFactory<>("amount"));

        loadOrders();

        txtSearch.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observableValue, String oldValue, String newValue) {
                tblOrder.setPredicate(new Predicate<TreeItem<OrderDetailsTm>>() {
                    @Override
                    public boolean test(TreeItem<OrderDetailsTm> treeItem) {
                        boolean flag = treeItem.getValue().getOrderId().contains(newValue) ||
                                treeItem.getValue().getDate().contains(newValue) ||
                                treeItem.getValue().getCustomerName().contains(newValue) ||
                                treeItem.getValue().getEmployer().contains(newValue);
                        return flag;
                    }
                });
            }
        });
    }

    private void loadOrders() {
        try {
            List<OrderDto> list = OrderModel.getAll();
            ObservableList<OrderDetailsTm> tmList = FXCollections.observableArrayList();
            for (OrderDto dto:list) {
                double payAmounts = 0;
                for (PaymentDto payment:dto.getPaymentDto()) {
                    payAmounts += (payment.getCash()-(payment.getBalance()>0?payment.getBalance():0));
                }
                tmList.add(new OrderDetailsTm(
                        dto.getOrderId(),
                        dto.getDate(),
                        dto.getTotal(),
                        dto.getCustomerName(),
                        dto.getCustomerContact(),
                        dto.getCustomerEmail(),
                        EmployerModel.findName(dto.getEmployerId()),
                        dto.getTotal()-payAmounts
                ));
            }
            TreeItem<OrderDetailsTm> treeItem = new RecursiveTreeItem<>(tmList, RecursiveTreeObject::getChildren);
            tblOrder.setRoot(treeItem);
            tblOrder.setShowRoot(false);
        }catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }

        tblOrder.getSelectionModel().selectedItemProperty().addListener((observableValue, oldValue, newValue) ->{
            if (newValue!=null){
                loadOrderDetails(newValue);
            }
        } );
    }

    private void loadOrderDetails(TreeItem<OrderDetailsTm> newValue) {
        try {
            List<OrderDetailsDto> list = OrderDetailsModel.getAll(newValue.getValue().getOrderId());
            ObservableList<OrderTm> tmList = FXCollections.observableArrayList();
            for (int i = 0; i < list.size(); i++) {
                tmList.add(new OrderTm(
                        list.get(i).getItemCode(),
                        ItemModel.find(list.get(i).getItemCode()).getDescription(),
                        list.get(i).getOrderQty(),
                        list.get(i).getUnitPrice(),
                        newValue.getValue().getDate(),
                        list.get(i).getDiscRate(),
                        ItemModel.find(list.get(i).getItemCode()).getCategoryDto().getType(),
                        ItemModel.find(list.get(i).getItemCode()).getCategoryDto().getSize(),
                        ((list.get(i).getUnitPrice()-((list.get(i).getUnitPrice()/100)*list.get(i).getDiscRate()))*list.get(i).getOrderQty()),
                        new JFXButton("Delete")
                ));
            }
            TreeItem<OrderTm> treeItem = new RecursiveTreeItem<>(tmList, RecursiveTreeObject::getChildren);
            tblOrderDetails.setRoot(treeItem);
            tblOrderDetails.setShowRoot(false);
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }

    public void refreshButtonOnAction(ActionEvent event) {
        txtSearch.clear();
        tblOrderDetails.setRoot(null);
        loadOrders();
    }

    public void backButtonOnAction(ActionEvent event) throws IOException {
        Stage stage = (Stage) orderDetailsPane.getScene().getWindow();
        stage.hide();
        stage.setMaximized(false);
        stage.setScene(new Scene(FXMLLoader.load(getClass().getResource("../../../../view/DashBoardForm.fxml"))));
        stage.setMaximized(true);
        stage.setTitle("Gima Fashion");
        stage.centerOnScreen();
        stage.show();
    }
}
