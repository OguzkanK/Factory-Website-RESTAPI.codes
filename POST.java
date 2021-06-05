package com.fabrika.fabrikabackend;

import org.json.JSONObject;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;
import java.sql.*;
import java.time.LocalDate;

import static java.lang.Class.forName;

@RestController
@CrossOrigin(origins = "http://localhost:5500")
@RequestMapping(method = RequestMethod.POST, path = "/POST")
public class POST { // This class is for testing on the localhost

    @RequestMapping(method = RequestMethod.POST, path = "/musteri")
    public RedirectView musteriLogin(@RequestBody String jsonInput){
        RedirectView profileRedirect = new RedirectView();
        profileRedirect.setUrl("http://localhost:5500/index.html");
        try{
            int customerId = 0;
            String[] array = jsonInput.split("&");
            String name = array[0].split("=")[1],
                    password = array[1].split("=")[1],
                    passwordCheck = "";

            forName("com.mysql.jdbc.Driver");
            Connection con = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/fabrika-odev", "root","");
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM `customers` WHERE name = \"" + name + "\"");
            while(rs.next()) {
                customerId = rs.getInt(1);
                passwordCheck = rs.getString(3);
            }
            con.close();
            if(password.equals(passwordCheck)) {
                profileRedirect.setUrl("http://localhost:5500/musteriProfile.html?name=" + name + "&id=" + customerId);
                return profileRedirect;
            }
        } catch (Exception e) {
            System.out.println(e);
        }
        return profileRedirect;
    }

    @RequestMapping(method = RequestMethod.POST, path = "/personel")
    public RedirectView personelLogin(@RequestBody String jsonInput){
        RedirectView profileRedirect = new RedirectView();
        profileRedirect.setUrl("http://localhost:5500/index.html");
        try{
            int userId = 0;
            String[] array = jsonInput.split("&");
            String name = array[0].split("=")[1],
                    password = array[1].split("=")[1],
                    passwordCheck = "";

            forName("com.mysql.jdbc.Driver");
            Connection con = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/fabrika-odev", "root","");
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM `users` WHERE name = \"" + name + "\"");
            while(rs.next()) {
                userId = rs.getInt(1);
                passwordCheck = rs.getString(3);
            }
            con.close();
            if(password.equals(passwordCheck)) {
                profileRedirect.setUrl("http://localhost:5500/personelProfile.html?name=" + name + "&id=" + userId);
                return profileRedirect;
            }
        } catch (Exception e) {
            System.out.println(e);
        }
        return profileRedirect;
    }

    @RequestMapping(method = RequestMethod.POST, path = "/orders")
    public RedirectView addOrder(@RequestBody String input){
        RedirectView redirect = new RedirectView();
        redirect.setUrl("http://localhost:5500/index.html");
        int userId, orderId = 0;
        String[] itemInput = input.split("%3B");
        String[][] items = new String[itemInput.length][2];
        LocalDate orderDate = LocalDate.now(),
        deadlineDate = LocalDate.now().plusMonths(1).plusDays(10);

        userId = Integer.parseInt(itemInput[itemInput.length - 1].split("=")[1]);

        for(int i = 0; i < itemInput.length - 1; i++){
            String[] itemDetails = new String[2];
            // %2C = , | %3B = ;
            if(i == 0)
                itemDetails[0] = itemInput[i].split("%2C")[0].split("=")[1];
            else
                itemDetails[0] = itemInput[i].split("%2C")[0];
            itemDetails[1] = itemInput[i].split("%2C")[1].split("%")[0];
            items[i] = itemDetails;
        }
        RedirectView profileRedirect = new RedirectView();
        profileRedirect.setUrl("http://localhost:5500/index.html");
        try{
            forName("com.mysql.jdbc.Driver"); // SQL connection
            Connection con = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/fabrika-odev", "root","");
            String ordersQuery = "insert into `orders` (customer_id, order_date, deadline, shipped)" + " values (?, ?, ?, ?)";
            PreparedStatement ordersPreparedStmt = con.prepareStatement(ordersQuery, Statement.RETURN_GENERATED_KEYS);
            ordersPreparedStmt.setInt (1,userId);
            ordersPreparedStmt.setDate (2, Date.valueOf(orderDate));
            ordersPreparedStmt.setDate   (3, Date.valueOf(deadlineDate));
            ordersPreparedStmt.setBoolean   (4, false);
            ordersPreparedStmt.execute();

            ResultSet generatedKey = ordersPreparedStmt.getGeneratedKeys();
            if(generatedKey.next())
                orderId = generatedKey.getInt(1);
            for (String[] item : items) {
                String itemsQuery = "insert into `orderitems` (order_id, product_id, amount)" + " values (?, ?, ?)";
                PreparedStatement itemsPreparedStmt = con.prepareStatement(itemsQuery);
                itemsPreparedStmt.setInt(1, orderId);
                itemsPreparedStmt.setInt(2, Integer.parseInt(item[0]));
                itemsPreparedStmt.setInt(3, Integer.parseInt(item[1]));
                itemsPreparedStmt.execute();
            }
            con.close();
        } catch (Exception e) {
            System.out.println(e);
        }
        return redirect;
    }

    @RequestMapping(method = RequestMethod.POST, path = "/changeSalability")
    public void changeSalability(@RequestBody String jsonInput){
        try{
            String[] input = jsonInput.split(",");

            String name = input[0].split(":")[1];
            name = name.substring(1);

            String salability = input[1].split(":")[1];
            salability = salability.substring(1, salability.indexOf("}"));

            int salable_int = 0;

            if(salability.equals("true"))
                salable_int = 1;

            forName("com.mysql.jdbc.Driver");
            Connection con = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/fabrika-odev", "root","");
            String query = "UPDATE `products` SET `is_salable` = "+ salable_int +" WHERE product_name = \"" + name + "\"";
            PreparedStatement stmt = con.prepareStatement(query);
            stmt.executeUpdate();
            con.close();
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    @RequestMapping(method = RequestMethod.POST, path = "/transferWork")
    public void transferWork(@RequestBody String jsonInput){
        try{
            System.out.println(jsonInput);
            int checkId = 0;
            String[] input = jsonInput.split(",");

            String wcId = input[0].split(":")[1];
            wcId = wcId.substring(1);

            String productId = input[1].split(":")[1];
            productId = productId.substring(1);

            String orderId = input[2].split(":")[1];
            orderId = orderId.substring(1, orderId.indexOf("}"));

            forName("com.mysql.jdbc.Driver");
            Connection con = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/fabrika-odev", "root","");

            Statement idStmt = con.createStatement();
            ResultSet idRs = idStmt.executeQuery("SELECT * FROM `workcenterplans` WHERE order_id = " + Integer.parseInt(orderId) + " AND product_id = " + Integer.parseInt(productId));
            if(idRs.next()) {
                checkId = idRs.getInt(2);
            }

            String query = "UPDATE `workcenterplans` SET work_center_id = " + Integer.parseInt(wcId) + " WHERE order_id = " + Integer.parseInt(orderId) + " AND product_id = " + Integer.parseInt(productId);
            PreparedStatement stmt = con.prepareStatement(query);
            stmt.executeUpdate();

            Statement checkStmt = con.createStatement();
            ResultSet rs = checkStmt.executeQuery("SELECT * FROM `workcenterplans` WHERE work_center_id = " + checkId);
            if(!rs.next()) {
                String availabilityQuery = "UPDATE `workcenters` SET active = " + 0 + " WHERE work_center_id = " + checkId;
                PreparedStatement availabilityStmt = con.prepareStatement(availabilityQuery);
                availabilityStmt.executeUpdate();
            }

            con.close();
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    @RequestMapping(method = RequestMethod.POST, path = "/addOperationToPlan")
    public void addOperationToPlan(@RequestBody String jsonInput){
        int productId = 0, workcenterTime = 0, workcenterId = 0;
        boolean found = false;
        String productType;
        try{
            String[] input = jsonInput.split("},");

            for(int i = 0; i < input.length; i++) {
                String[] infos = new String[2];
                infos = input[i].split(",");
                infos[0] = infos[0].split(":")[1];
                infos[0] = infos[0].substring(1);
                infos[1] = infos[1].split(":")[1];
                infos[1] = infos[1].substring(1);
                infos[2] = infos[2].split(":")[1];
                infos[2] = infos[2].substring(1);
                if(i == input.length - 1)
                    infos[2] = infos[2].substring(0, infos[2].length() - 1);

                forName("com.mysql.jdbc.Driver");
                Connection con = DriverManager.getConnection(
                        "jdbc:mysql://localhost:3306/fabrika-odev", "root","");

                String shippedQuery = "UPDATE `orders` SET `shipped` = 1 WHERE order_id = " + infos[2];
                PreparedStatement shippedStmt = con.prepareStatement(shippedQuery);
                shippedStmt.executeUpdate();

                Statement stmt = con.createStatement();
                ResultSet rs = stmt.executeQuery("SELECT * FROM `products` WHERE product_name = \"" + infos[0] + "\"");
                if(rs.next()) {
                    productId = rs.getInt(1);
                    productType = rs.getString(3);

                    Statement opStmt = con.createStatement();
                    ResultSet opRs = opStmt.executeQuery("SELECT * FROM `operations` WHERE operation_type = \"" + productType + "\"");
                    if(opRs.next()) {
                        found = false;

                        Statement wcLength = con.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,
                                ResultSet.CONCUR_UPDATABLE);
                        ResultSet lengthRs = wcLength.executeQuery("SELECT * FROM `workcenteroperation` WHERE operation_id = " + opRs.getInt(1));
                        lengthRs.last();
                        int[] workcenters = new int[lengthRs.getRow()];
                        int[] selectedTimes = new int[lengthRs.getRow()];

                        Statement wcopDetailStmt = con.createStatement();
                        ResultSet wcopRs = wcopDetailStmt.executeQuery("SELECT * FROM `workcenteroperation` WHERE operation_id = " + opRs.getInt(1));
                        int j = 0;
                        while(wcopRs.next()){
                            Statement wcDetailStmt = con.createStatement();
                            ResultSet wcRs = wcDetailStmt.executeQuery("SELECT * FROM `workcenters` WHERE work_center_id = " + wcopRs.getInt(2));
                            if(wcRs.next()) {
                                workcenterId = wcRs.getInt(1);
                                workcenterTime = wcopRs.getInt(4);
                                if(wcRs.getBoolean(3) == false){
                                    found = true;
                                    break;
                                } else
                                    if (workcenters[j] == 0) {
                                        workcenters[j] = workcenterId;
                                        selectedTimes[j] = workcenterTime;
                                        j++;
                                    }
                            }
                        }
                        if(!found){
                            int shortest = -1;
                            for(int k = 0; k < workcenters.length; k++){
                                int totalTime = 0;
                                Statement compareStmt = con.createStatement();
                                ResultSet compRs = compareStmt.executeQuery("SELECT * FROM `workcenterplans` WHERE work_center_id = " + workcenters[k]);
                                while (compRs.next()) {
                                    totalTime += compRs.getInt(5);
                                }
                                if(shortest == -1) {
                                    shortest = totalTime;
                                    workcenterId = workcenters[k];
                                    workcenterTime = selectedTimes[k];
                                }
                                else if(shortest > totalTime) {
                                    shortest = totalTime;
                                    workcenterId = workcenters[k];
                                    workcenterTime = selectedTimes[k];
                                }
                            }
                        }

                        String updateQuery = "UPDATE `workcenters` SET `active` = 1 WHERE work_center_id = " + workcenterId;
                        PreparedStatement updateStmt = con.prepareStatement(updateQuery);
                        updateStmt.executeUpdate();

                        String insertQuery = "INSERT INTO `workcenterplans` (`work_center_id`, `order_id`, `product_id`, `amount`, `time`)" + " values (?, ?, ?, ?, ?)";
                        PreparedStatement insertPreparedStmt = con.prepareStatement(insertQuery, Statement.RETURN_GENERATED_KEYS);
                        insertPreparedStmt.setInt(1, workcenterId);
                        insertPreparedStmt.setInt(2, Integer.parseInt(infos[2]));
                        insertPreparedStmt.setInt(3, productId);
                        insertPreparedStmt.setInt(4, Integer.parseInt(infos[1]));
                        insertPreparedStmt.setInt(5, workcenterTime * Integer.parseInt(infos[1]));
                        insertPreparedStmt.execute();
                    }
                }
                con.close();
            }
        } catch (Exception e) {
            System.out.println(e);
        }
    }
}

