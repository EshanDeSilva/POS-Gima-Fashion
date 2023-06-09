package lk.ijse.pos.model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import lk.ijse.pos.dto.SupplierDto;
import lk.ijse.pos.dto.tm.SupplierTm;
import lk.ijse.pos.dto.tm.SuppliesTm;
import lk.ijse.pos.util.CrudUtil;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class SupplierModel {

    public static Boolean save(SupplierDto dto) throws SQLException, ClassNotFoundException {
        return CrudUtil.execute("INSERT INTO supplier VALUES (?,?,?,?,?)",
                dto.getSupplierId(),
                dto.getTitle(),
                dto.getName(),
                dto.getCompany(),
                dto.getContact());
    }

    public static String getId() throws SQLException, ClassNotFoundException {
        ResultSet resultSet = CrudUtil.execute("SELECT * FROM supplier ORDER BY supplierId DESC LIMIT 1");
        if (resultSet.next()){
            String lastId = resultSet.getString(1).split("[-]")[1];
            return String.format("SUP-%04d",Integer.valueOf(lastId)+1);
        }
        return "SUP-0001";
    }

    public static List<SupplierDto> findAll() throws SQLException, ClassNotFoundException {
        ResultSet resultSet = CrudUtil.execute("SELECT * FROM supplier");
        List<SupplierDto> list = new ArrayList<>();
        while (resultSet.next()){
            list.add(new SupplierDto(
                    resultSet.getString(1),
                    resultSet.getString(2),
                    resultSet.getString(3),
                    resultSet.getString(4),
                    resultSet.getString(5)
            ));
        }
        return list;
    }

    public static Boolean delete(String supplierId) throws SQLException, ClassNotFoundException {
        return CrudUtil.execute("DELETE FROM supplier WHERE supplierId=?",supplierId);
    }

    public static Boolean update(SupplierDto dto) throws SQLException, ClassNotFoundException {
        return CrudUtil.execute("UPDATE supplier SET title=?,supplierName=?,company=?,contact=? WHERE supplierId=?",
                dto.getTitle(),dto.getName(),dto.getCompany(),dto.getContact(),dto.getSupplierId());
    }

    public static SupplierDto find(String id) throws SQLException, ClassNotFoundException {
        ResultSet resultSet = CrudUtil.execute("SELECT * FROM supplier WHERE supplierId=?", id);
        while (resultSet.next()){
            return new SupplierDto(resultSet.getString(1),
                    resultSet.getString(2),
                    resultSet.getString(3),
                    resultSet.getString(4),
                    resultSet.getString(5)
                    );
        }
        return null;
    }

    public static SupplierDto findByName(String name) throws SQLException, ClassNotFoundException {
        ResultSet resultSet = CrudUtil.execute("SELECT * FROM supplier WHERE supplierName=?", name);
        while (resultSet.next()){
            return new SupplierDto(resultSet.getString(1),
                    resultSet.getString(2),
                    resultSet.getString(3),
                    resultSet.getString(4),
                    resultSet.getString(5)
            );
        }
        return null;
    }

    public static ObservableList<SuppliesTm> getItems(String id) throws SQLException, ClassNotFoundException {
        ResultSet resultSet = CrudUtil.execute("SELECT * FROM item WHERE supplierId=?", id);
        ObservableList<SuppliesTm> list = FXCollections.observableArrayList();
        while (resultSet.next()){
            list.add(new SuppliesTm(
                    resultSet.getString(1),
                    resultSet.getString(3),
                    resultSet.getInt(4)
            ));
        }
        return list;
    }
}
