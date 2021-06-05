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
        int orderId, productId;
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
        int orderId, customerId, productId;
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
                orderDetails.put("orderId", orderId);
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

    @RequestMapping(method = RequestMethod.GET, path = "/orders/allOperations")
    public String getAllOperations(){
        JSONObject wcDetail, operations;
        JSONArray completeList, entryList = new JSONArray();
        try{
            forName("com.mysql.jdbc.Driver");
            Connection con = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/fabrika-odev", "root","");
            Statement wcStmt = con.createStatement();
            ResultSet rs = wcStmt.executeQuery("SELECT * FROM `workcenters`");
            while(rs.next()) {
                completeList = new JSONArray();
                wcDetail = new JSONObject();
                wcDetail.put("wcName", rs.getString(2));
                wcDetail.put("wcId", rs.getInt(1));
                if(!rs.getBoolean(3)) {
                    wcDetail.put("activity", false);
                }
                else {
                    wcDetail.put("activity", true);

                    Statement planStmt = con.createStatement();
                    ResultSet planRs = planStmt.executeQuery("SELECT * FROM `workcenterplans` WHERE work_center_id = " + rs.getInt(1));
                    while(planRs.next()){
                        operations = new JSONObject();
                        operations.put("amount", planRs.getInt(5));
                        operations.put("time", planRs.getInt(6));
                        operations.put("orderID", planRs.getInt(3));
                        Statement productStmt = con.createStatement();
                        ResultSet productRs = productStmt.executeQuery("SELECT * FROM `products` WHERE product_id = " + planRs.getInt(4));
                        if(productRs.next()){
                            operations.put("productId", productRs.getString(1));
                            operations.put("productName", productRs.getString(2));
                            Statement operationStmt = con.createStatement();
                            ResultSet operationRs = operationStmt.executeQuery("SELECT * FROM `operations` WHERE operation_type = \"" + productRs.getString(3) + "\"");
                            if(operationRs.next()){
                                String alternativeWcs = "";
                                operations.put("operationName", operationRs.getString(2));
                                Statement wcIdsStmt = con.createStatement();
                                ResultSet wcIdsRs = wcIdsStmt.executeQuery("SELECT * FROM `workcenteroperation` WHERE operation_id = " + operationRs.getInt(1));
                                while(wcIdsRs.next()){
                                    alternativeWcs += wcIdsRs.getInt(2) + " ";
                                }
                                operations.put("alternativeWCs", alternativeWcs);
                            }
                        }
                        completeList.put(operations);
                    }
                }
                completeList.put(wcDetail);
                entryList.put(completeList);
            }
            con.close();
        } catch (Exception e) {
            System.out.println(e);
        }
        return entryList.toString();
    }

    @RequestMapping(method = RequestMethod.GET, path = "/orders/allRelations")
    public String getAllRelations(){
        int productId;
        JSONObject productDetail, subproductDetails, operationDetails;
        JSONArray entryList = new JSONArray();
        JSONArray completeOrder;
        try{
            forName("com.mysql.jdbc.Driver");
            Connection con = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/fabrika-odev", "root","");
            Statement productStmt = con.createStatement();
            ResultSet rs = productStmt.executeQuery("SELECT * FROM `products`");
            while(rs.next()) {
                completeOrder = new JSONArray();
                productDetail = new JSONObject();

                productId = rs.getInt(1);
                productDetail.put("productName", rs.getString(2));
                productDetail.put("productType", rs.getString(3));
                productDetail.put("salable", rs.getBoolean(4));

                completeOrder.put(productDetail);

                Statement opStmt = con.createStatement();
                ResultSet opRs = opStmt.executeQuery("SELECT * FROM `operations` WHERE operation_type = \"" + rs.getString(3) + "\"");
                if(opRs.next()) {

                    Statement wcopDetailStmt = con.createStatement();
                    ResultSet wcopRs = wcopDetailStmt.executeQuery("SELECT * FROM `workcenteroperation` WHERE operation_id = " + opRs.getInt(1));
                    while(wcopRs.next()){
                        operationDetails = new JSONObject();
                        operationDetails.put("operationName", opRs.getString(2));
                        Statement wcDetailStmt = con.createStatement();
                        ResultSet wcRs = wcDetailStmt.executeQuery("SELECT * FROM `workcenters` WHERE work_center_id = " + wcopRs.getInt(2));
                        if(wcRs.next()) {
                            operationDetails.put("workcenterName", wcRs.getString(2));
                            operationDetails.put("workTime", wcopRs.getInt(4));
                        }
                        completeOrder.put(operationDetails);
                    }
                }

                Statement spStmt = con.createStatement();
                ResultSet spRs = spStmt.executeQuery("SELECT * FROM `subproducttree` WHERE product_id = " + productId);
                while(spRs.next()) {
                    subproductDetails = new JSONObject();

                    subproductDetails.put("subProductName", spRs.getString(2));
                    subproductDetails.put("subProductAmount", spRs.getInt(3));

                    completeOrder.put(subproductDetails);
                }
                entryList.put(completeOrder);
            }
            con.close();
        } catch (Exception e) {
            System.out.println(e);
        }
        return entryList.toString();
    }
}
