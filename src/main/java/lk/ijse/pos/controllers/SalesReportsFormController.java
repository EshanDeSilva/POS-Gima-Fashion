package lk.ijse.pos.controllers;

import com.jfoenix.controls.JFXComboBox;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import lk.ijse.pos.db.DBConnection;
import lk.ijse.pos.model.OrderDetailsModel;
import lk.ijse.pos.model.OrderModel;
import lk.ijse.pos.model.SalesReturnModel;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.design.JRDesignQuery;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.xml.JRXmlLoader;
import net.sf.jasperreports.view.JasperViewer;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.YearMonth;

public class SalesReportsFormController {
    public JFXComboBox cmbDuration;
    public Label lblIncome;
    public Label lblSalesCount;
    public LineChart chart;
    public BorderPane salesReportPane;
    public Label lblSales;

    public void initialize(){
        ObservableList<String> list = FXCollections.observableArrayList("Today","This Month","This Year");
        cmbDuration.setItems(list);

        cmbDuration.setValue("Today");
        loadToday();
        cmbDuration.setOnAction(event -> {
            switch (cmbDuration.getValue().toString()){
                case "This Month" : loadThisMonth();break;
                case "This Year" : loadThisYear();break;
                case "Today" : loadToday();
            }
        });
        chart.setTitle("Sales Chart");
    }

    private void loadThisYear() {
        chart.getData().clear();
        XYChart.Series series = new XYChart.Series();
        series.setName("Sales");
        //chart.setTitle("Annual Chart "+ Year.now());
        try {
            series.getData().add(new XYChart.Data("Jan", OrderDetailsModel.getMonthlySalesCount("january")));
            series.getData().add(new XYChart.Data("Feb", OrderDetailsModel.getMonthlySalesCount("february")));
            series.getData().add(new XYChart.Data("Mar", OrderDetailsModel.getMonthlySalesCount("march")));
            series.getData().add(new XYChart.Data("Apr", OrderDetailsModel.getMonthlySalesCount("april")));
            series.getData().add(new XYChart.Data("May", OrderDetailsModel.getMonthlySalesCount("may")));
            series.getData().add(new XYChart.Data("Jun", OrderDetailsModel.getMonthlySalesCount("june")));
            series.getData().add(new XYChart.Data("Jul", OrderDetailsModel.getMonthlySalesCount("july")));
            series.getData().add(new XYChart.Data("Aug", OrderDetailsModel.getMonthlySalesCount("august")));
            series.getData().add(new XYChart.Data("Sep", OrderDetailsModel.getMonthlySalesCount("september")));
            series.getData().add(new XYChart.Data("Oct", OrderDetailsModel.getMonthlySalesCount("october")));
            series.getData().add(new XYChart.Data("Nov", OrderDetailsModel.getMonthlySalesCount("november")));
            series.getData().add(new XYChart.Data("Dec", OrderDetailsModel.getMonthlySalesCount("december")));

            lblIncome.setText(String.format("%.2f",OrderDetailsModel.getAnnualIncome()));
            lblSalesCount.setText(String.valueOf(OrderDetailsModel.getAnnualSalesCount()));
            lblSales.setText(String.format("%.2f",OrderModel.getAnnualSalesTotal()));
        }catch (SQLException | ClassNotFoundException e){
            e.printStackTrace();
        }
        chart.getData().add(series);
    }

    private void loadThisMonth() {
        chart.getData().clear();
        XYChart.Series series = new XYChart.Series();
        series.setName("Sales");
        //chart.setTitle("Monthly Chart "+ Month.of(YearMonth.now().getMonthValue()));
        try {
            for (int i = 1; i <= YearMonth.now().lengthOfMonth(); i++) {
                series.getData().add(new XYChart.Data(i+"", OrderDetailsModel.getDailySalesCount(i)));
            }
            lblIncome.setText(String.format("%.2f",OrderDetailsModel.getMonthlyIncome()));
            lblSalesCount.setText(String.valueOf(OrderDetailsModel.getMonthlySalesCount()));
            lblSales.setText(String.format("%.2f",OrderModel.getMonthlySalesTotal()));
        }catch (SQLException | ClassNotFoundException e){
            e.printStackTrace();
        }
        chart.getData().add(series);
    }

    private void loadToday() {
        loadThisMonth();
        try {
            lblIncome.setText(String.format("%.2f",OrderDetailsModel.getDailyIncome()));
            lblSalesCount.setText(String.valueOf(OrderDetailsModel.getDailySalesCount()));
            lblSales.setText(String.format("%.2f",OrderModel.getDailySalesTotal()));
        }catch (SQLException | ClassNotFoundException e){
            e.printStackTrace();
        }
    }

    public void backButtonOnAction(ActionEvent event) throws IOException {
        Stage stage = (Stage) salesReportPane.getScene().getWindow();
        stage.hide();
        stage.setMaximized(false);
        stage.setScene(new Scene(FXMLLoader.load(getClass().getResource("../../../../view/DashBoardForm.fxml"))));
        stage.setMaximized(true);
        stage.setTitle("Gima Fashion");
        stage.centerOnScreen();
        stage.show();
    }

    public void dailyButtonOnAction(ActionEvent event) {
        try {
            JasperDesign jDesign = JRXmlLoader.load("src/main/resources/jasper/MyReports/Report.jrxml");
            JRDesignQuery query = new JRDesignQuery();
            query.setText("SELECT orderId,date,total FROM orders WHERE DAY(date)=DAY(CURDATE())");
            jDesign.setQuery(query);

            JasperReport compileReport = JasperCompileManager.compileReport(jDesign);
            java.util.Map params = new java.util.HashMap();
            params.put( "Total", String.format("%.2f", OrderModel.getDailySalesTotal()) );
            JasperPrint jasperPrint = JasperFillManager.fillReport(compileReport,params , DBConnection.getInstance().getConnection());
            JasperViewer.viewReport(jasperPrint, false);
        } catch (JRException | SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void annualButtonOnAction(ActionEvent event) {
        try {
            JasperDesign jDesign = JRXmlLoader.load("src/main/resources/jasper/MyReports/Report.jrxml");
            JRDesignQuery query = new JRDesignQuery();
            query.setText("SELECT orderId,date,total FROM orders WHERE YEAR(date)=YEAR(CURDATE())");
            jDesign.setQuery(query);

            JasperReport compileReport = JasperCompileManager.compileReport(jDesign);
            java.util.Map params = new java.util.HashMap();
            params.put( "Total", String.format("%.2f", OrderModel.getMonthlySalesTotal()) );
            JasperPrint jasperPrint = JasperFillManager.fillReport(compileReport,params , DBConnection.getInstance().getConnection());
            JasperViewer.viewReport(jasperPrint, false);
        } catch (JRException | SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void monthlyButtonOnAction(ActionEvent event) {
        try {
            JasperDesign jDesign = JRXmlLoader.load("src/main/resources/jasper/MyReports/Report.jrxml");
            JRDesignQuery query = new JRDesignQuery();
            query.setText("SELECT orderId,date,total FROM orders WHERE MONTH(date)=MONTH(CURDATE())");
            jDesign.setQuery(query);

            JasperReport compileReport = JasperCompileManager.compileReport(jDesign);
            java.util.Map params = new java.util.HashMap();
            params.put( "Total", String.format("%.2f", OrderModel.getAnnualSalesTotal()) );
            JasperPrint jasperPrint = JasperFillManager.fillReport(compileReport,params , DBConnection.getInstance().getConnection());
            JasperViewer.viewReport(jasperPrint, false);
        } catch (JRException | SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void dailyReturnsButtonOnAction(ActionEvent event) {
        try {
            JasperDesign jDesign = JRXmlLoader.load("src/main/resources/jasper/MyReports/DailyReturns.jrxml");
            JRDesignQuery query = new JRDesignQuery();
            query.setText("SELECT * FROM salesReturn INNER JOIN salesReturnDetails ON salesReturn.returnId = salesReturnDetails.returnId INNER JOIN item ON salesReturnDetails.itemCode=item.itemCode WHERE salesReturn.date = '"+LocalDate.now()+"'");
            jDesign.setQuery(query);

            JasperReport compileReport = JasperCompileManager.compileReport(jDesign);
            java.util.Map params = new java.util.HashMap();
            params.put( "Total", String.format("%.2f", SalesReturnModel.getDailyReturnTotal()) );
            JasperPrint jasperPrint = JasperFillManager.fillReport(compileReport,params , DBConnection.getInstance().getConnection());
            JasperViewer.viewReport(jasperPrint, false);
        } catch (JRException | SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
