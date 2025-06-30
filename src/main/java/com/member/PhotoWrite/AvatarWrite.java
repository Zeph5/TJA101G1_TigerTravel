package com.member.PhotoWrite;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class AvatarWrite {
	
	private static final String DB_URL = "jdbc:mysql://localhost:3306/tigertraveldb?serverTimezone=Asia/Taipei";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "123456";

	public static void main(String argv[]) {
		String folderPath = "src/main/resources/templates/member/member_avatar"; //測試用圖片已置於【專案錄徑】底下的【resources/DB_photos1】目錄內

		try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String sql = "UPDATE member SET avatar = ? WHERE member_id = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);

            for (int i = 1; i <= 10; i++) {
                File imageFile = new File(folderPath, i + ".jpg");

                if (!imageFile.exists()) {
                    System.out.println("找不到圖片：" + imageFile.getName());
                    continue;
                }

                try (FileInputStream fis = new FileInputStream(imageFile)) {
                    pstmt.setBinaryStream(1, fis, (int) imageFile.length());
                    pstmt.setInt(2, i);
                    int rowsUpdated = pstmt.executeUpdate();

                    if (rowsUpdated > 0) {
                        System.out.println("✅ 成功更新會員 " + i);
                    } else {
                        System.out.println("⚠️ 找不到 member_id=" + i);
                    }
                } catch (IOException e) {
                    System.out.println("❌ 圖片讀取錯誤：" + imageFile.getName());
                }
            }

            pstmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
