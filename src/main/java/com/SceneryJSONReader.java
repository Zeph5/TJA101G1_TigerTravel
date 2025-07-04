package com;

import javax.json.*;
import java.io.FileInputStream;
import java.io.InputStream;
import java.sql.*;
import java.math.BigDecimal;

// 將 https://data.gov.tw/dataset/7777 網站下載的景點JSON檔寫入MySQL資料庫
public class SceneryJSONReader {

	public static void main(String[] args) {
        String jdbcURL = "jdbc:mysql://localhost:3306/TigerTravelDB";
        String dbUser = "root";
        String dbPassword = "123456";

        String sql = "INSERT INTO scenery (sce_name, sce_intro, sce_total_score, score_sce_total_score, sce_address, sce_longitude, sce_latitude) VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (
        	InputStream fis = SceneryJSONReader.class.getResourceAsStream("scenic_spot_C_f.json");
            JsonReader jsonReader = Json.createReader(fis);
            Connection conn = DriverManager.getConnection(jdbcURL, dbUser, dbPassword);
            PreparedStatement stmt = conn.prepareStatement(sql);
        ) {
        	JsonObject root = jsonReader.readObject(); 
        	JsonObject xmlHead = root.getJsonObject("XML_Head");
        	JsonObject infos = xmlHead.getJsonObject("Infos");
        	JsonArray sceneryArray = infos.getJsonArray("Info");

            for (JsonValue value : sceneryArray) {
                JsonObject obj = value.asJsonObject();

                String name = obj.getString("Name", "");
                String intro = obj.getString("Description", "");
                String address = obj.getString("Add", "");
                BigDecimal longitude = obj.containsKey("Px") ? obj.getJsonNumber("Px").bigDecimalValue() : null;
                BigDecimal latitude = obj.containsKey("Py") ? obj.getJsonNumber("Py").bigDecimalValue() : null;

                if (name.isEmpty() || intro.isEmpty() || address.isEmpty() || longitude == null || latitude == null) {
                    continue;
                }

                stmt.setString(1, name);
                stmt.setString(2, intro);
                stmt.setInt(3, 0);
                stmt.setInt(4, 0);
                stmt.setString(5, address);
                stmt.setBigDecimal(6, longitude);
                stmt.setBigDecimal(7, latitude);

                try {
                    stmt.executeUpdate();
                } catch (SQLIntegrityConstraintViolationException dup) {
                    System.out.println("Duplicate entry skipped: " + name);
                }
            }

            System.out.println("Filtered scenery data inserted successfully!");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
