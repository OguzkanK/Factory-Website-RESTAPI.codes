//package com.fabrika.fabrikabackend;
//
//import org.json.JSONArray;
//import org.springframework.web.bind.annotation.*;
//
//import java.io.File;
//import java.io.FileWriter;
//import java.sql.*;
//import static java.lang.Class.forName;
//
//@RestController
//@CrossOrigin(origins = "http://localhost:5500")
//public class TestPut { // This class is for testing on the localhost
//
//    @PutMapping(path = "/update/{targetId}")
//
//    public String insertMYSQL(@RequestBody String jsonInput, @PathVariable int targetId){
//        try{
//            JSONArray dataArray = new JSONArray(jsonInput);
//            String currentName = "",
//                    newName = (String) dataArray.getJSONObject(0).get("name"),
//                    newPath = "D:/Apps/intellij Projects/drawingRestapi/images/"+ dataArray.getJSONObject(0).get("name") +".txt";
//            int newScore = (int) dataArray.getJSONObject(0).get("score");
//
//            forName("com.mysql.jdbc.Driver"); // SQL connection
//            Connection con = DriverManager.getConnection(
//                    "jdbc:mysql://localhost:3306/drawings", "root","");
//
//            Statement stmt = con.createStatement();
//            ResultSet rs = stmt.executeQuery("SELECT * FROM `drawing-info` WHERE `id` = " + targetId);
//            while(rs.next()) {
//                currentName = rs.getString(2);
//            }
//            File currentFile = new File("D:/Apps/intellij Projects/drawingRestapi/images/"+ currentName +".txt");
//            if(currentFile.exists())
//                currentFile.delete();
//            FileWriter newFile = new FileWriter(newPath);
//            newFile.write((String) dataArray.getJSONObject(0).get("data"));
//            newFile.flush();
//
//            String query = "UPDATE `drawing-info` SET `name` = '" + newName + "', `score` = '" + newScore + "', `link` = '" + newPath + "' WHERE `id` = " + targetId;
//            PreparedStatement preparedStmt = con.prepareStatement(query);
//            preparedStmt.execute();
//            con.close();
//        } catch (Exception e) {
//            System.out.println(e);
//        }
//        return "done";
//    }
//}
//
