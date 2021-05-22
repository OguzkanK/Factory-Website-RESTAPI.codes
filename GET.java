package com.fabrika.fabrikabackend;

import java.sql.*;

import org.json.JSONObject;
import org.json.JSONArray;
import org.springframework.web.bind.annotation.*;

import static java.lang.Class.forName;

@RestController
@CrossOrigin(origins = "http://localhost:5500")
@RequestMapping(method = RequestMethod.GET, path = "/GET")
public class GET { // This class is for testing on the localhost

    @RequestMapping(method = RequestMethod.GET, path = "/")
    public String pullData(){
        JSONObject musteriDetails;
        JSONArray entryList = new JSONArray();

        try{
            forName("com.mysql.jdbc.Driver");
            Connection con = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/fabrika-odev", "root","");
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM `customers`");
            while(rs.next()) {
                musteriDetails = new JSONObject();
                musteriDetails.put("id", rs.getInt(1));
                musteriDetails.put("name", rs.getString(2));
                musteriDetails.put("password", rs.getString(3));
                entryList.put(musteriDetails);
                }
            con.close();
        } catch (Exception e) {
            System.out.println(e);
        }
        return entryList.toString();
    }

    @RequestMapping(method = RequestMethod.GET, path = "/products/sellable")
    public String getsellableProducts(){
        JSONObject productDetails;
        JSONArray entryList = new JSONArray();

        try{
            forName("com.mysql.jdbc.Driver");
            Connection con = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/fabrika-odev", "root","");
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM `products`");
            while(rs.next()) {
                productDetails = new JSONObject();
                productDetails.put("id", rs.getInt(1));
                productDetails.put("name", rs.getString(2));
                productDetails.put("type", rs.getString(3));
                if(rs.getBoolean(4))
                    entryList.put(productDetails);
            }
            con.close();
        } catch (Exception e) {
            System.out.println(e);
        }
        return entryList.toString();
    }

    @RequestMapping(method = RequestMethod.GET, path = "/orders/myOrders")
    public String getMyOrders(@RequestParam int id){
        int orderId = 0, productId = 0;
        JSONObject productDetails;
        JSONArray entryList = new JSONArray();
        try{
            forName("com.mysql.jdbc.Driver");
            Connection con = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/fabrika-odev", "root","");
            Statement orderStmt = con.createStatement();
            ResultSet rs = orderStmt.executeQuery("SELECT * FROM `orders` WHERE customer_id = " + id);
            while(rs.next()) {
                productDetails = new JSONObject();

                orderId = rs.getInt(1);
                productDetails.put("orderDate", rs.getString(3));
                productDetails.put("deadlineDate", rs.getString(4));

                Statement orderDetailStmt = con.createStatement();
                ResultSet odRs = orderDetailStmt.executeQuery("SELECT * FROM `orderitems` WHERE order_id = " + orderId);
                while (odRs.next()){
                    productId = odRs.getInt(3);
                    productDetails.put("amount", odRs.getString(4));

                    Statement productDetailStmt = con.createStatement();
                    ResultSet prRs = productDetailStmt.executeQuery("SELECT * FROM `products` WHERE product_id = " + productId);
                    while (prRs.next()){
                        productDetails.put("name", prRs.getString(2));
                    }
                }
                entryList.put(productDetails);
            }
            con.close();
        } catch (Exception e) {
            System.out.println(e);
        }
        return entryList.toString();
    }

    @RequestMapping(method = RequestMethod.GET, path = "/orders/allOrders")
    public String getAllOrders(){
        int orderId = 0, customerId = 0, productId = 0;
        JSONObject orderDetails, productDetails;
        JSONArray entryList = new JSONArray();
        JSONArray completeOrder;
        try{
            forName("com.mysql.jdbc.Driver");
            Connection con = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/fabrika-odev", "root","");
            Statement orderStmt = con.createStatement();
            ResultSet rs = orderStmt.executeQuery("SELECT * FROM `orders`");
            while(rs.next()) {
                orderDetails = new JSONObject();
                completeOrder = new JSONArray();

                orderId = rs.getInt(1);
                customerId = rs.getInt(2);
                orderDetails.put("orderDate", rs.getString(3));
                orderDetails.put("deadlineDate", rs.getString(4));
                orderDetails.put("shipping", rs.getBoolean(5));

                Statement orderDetailStmt = con.createStatement();
                ResultSet odRs = orderDetailStmt.executeQuery("SELECT * FROM `orderitems` WHERE order_id = " + orderId);
                while (odRs.next()){
                    productDetails = new JSONObject();
                    productId = odRs.getInt(3);

                    Statement productDetailStmt = con.createStatement();
                    ResultSet prRs = productDetailStmt.executeQuery("SELECT * FROM `products` WHERE product_id = " + productId);
                    if(prRs.next()){
                        productDetails.put("amount", odRs.getString(4));
                        productDetails.put("productName", prRs.getString(2));
                        completeOrder.put(productDetails);
                    }
                }

                Statement cstmrDetailStmt = con.createStatement();
                ResultSet csRs = cstmrDetailStmt.executeQuery("SELECT * FROM `customers` WHERE customer_id = " + customerId);
                while (csRs.next()){
                    orderDetails.put("customerName", csRs.getString(2));
                }
                completeOrder.put(orderDetails);
                entryList.put(completeOrder);
            }
            con.close();
        } catch (Exception e) {
            System.out.println(e);
        }
        return entryList.toString();
    }
}
