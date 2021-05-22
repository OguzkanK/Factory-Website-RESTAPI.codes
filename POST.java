package com.fabrika.fabrikabackend;

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
        int userId = 0, orderId = 0;
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
            for(int i = 0; i < items.length; i++){
                String itemsQuery = "insert into `orderitems` (order_id, product_id, amount)" + " values (?, ?, ?)";
                PreparedStatement itemsPreparedStmt = con.prepareStatement(itemsQuery);
                itemsPreparedStmt.setInt(1, orderId);
                itemsPreparedStmt.setInt(2, Integer.parseInt(items[i][0]));
                itemsPreparedStmt.setInt(3, Integer.parseInt(items[i][1]));
                itemsPreparedStmt.execute();
            }
            con.close();
        } catch (Exception e) {
            System.out.println(e);
        }
        return redirect;
    }
}

