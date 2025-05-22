import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class TourismDataAnalyzer {
    
    public static void main(String[] args) {
        String url = "jdbc:mysql://localhost:3306/tourism_db?useUnicode=true&characterEncoding=UTF-8";
        String user = "root";
        String password = "123456";
        
        try (Connection conn = DriverManager.getConnection(url, user, password)) {
            // 1. 按旅游线路统计收入
            List<String> tourIncome = analyzeTourIncome(conn);
            System.out.println("\n按旅游线路统计收入:");
            tourIncome.forEach(System.out::println);
            
            // 2. 按分公司统计收入
            List<String> branchIncome = analyzeBranchIncome(conn);
            System.out.println("\n按分公司统计收入:");
            branchIncome.forEach(System.out::println);
            
            // 3. 按导游统计业绩
            List<String> guidePerformance = analyzeGuidePerformance(conn);
            System.out.println("\n按导游统计业绩:");
            guidePerformance.forEach(System.out::println);
            
            // 4. 按月统计总收入
            List<String> monthlyIncome = analyzeMonthlyIncome(conn);
            System.out.println("\n按月统计总收入:");
            monthlyIncome.forEach(System.out::println);
            
            // 5. 生成总账报表
            generateGeneralLedger(conn);
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    // 按旅游线路统计收入
    private static List<String> analyzeTourIncome(Connection conn) throws Exception {
        List<String> results = new ArrayList<>();
        String sql = "SELECT t.线路ID, l.地点, l.景点, SUM(t.费用) AS 总收入, COUNT(*) AS 旅游次数 " +
                     "FROM 旅游信息 t JOIN 旅游线路 l ON t.线路ID = l.线路ID " +
                     "GROUP BY t.线路ID, l.地点, l.景点 " +
                     "ORDER BY 总收入 DESC";
        
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                String line = String.format("线路ID: %s, 地点: %s, 景点: %s, 总收入: %.2f, 旅游次数: %d",
                        rs.getString("线路ID"),
                        rs.getString("地点"),
                        rs.getString("景点"),
                        rs.getDouble("总收入"),
                        rs.getInt("旅游次数"));
                results.add(line);
            }
        }
        return results;
    }
    
    // 按分公司统计收入
    private static List<String> analyzeBranchIncome(Connection conn) throws Exception {
        List<String> results = new ArrayList<>();

        String sql = "SELECT b.分公司ID, b.名称, b.办公地址, SUM(ti.费用) AS 总收入 " +
                "FROM 旅游信息 ti " +
                "JOIN 合同 c ON ti.合同ID = c.合同ID " +  // 通过合同关联导游
                "JOIN 员工 e ON c.导游ID = e.导游号 " +  // 导游信息
                "JOIN 分公司 b ON e.分公司ID = b.分公司ID " +  // 分公司信息
                "GROUP BY b.分公司ID, b.名称, b.办公地址 " +
                "ORDER BY 总收入 DESC";

        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                String line = String.format("分公司ID: %s, 名称: %s, 办公地址: %s, 总收入: %.2f",
                        rs.getString("分公司ID"),
                        rs.getString("名称"),
                        rs.getString("办公地址"),
                        rs.getDouble("总收入"));
                results.add(line);
            }

            // 如果没有结果，添加提示信息
            if (results.isEmpty()) {
                results.add("没有找到分公司收入数据，请检查数据关联关系");
            }
        }
        return results;
    }

    //导游业绩统计
    private static List<String> analyzeGuidePerformance(Connection conn) throws Exception {
        List<String> results = new ArrayList<>();

        // 修正后的SQL查询
        String sql = "SELECT e.导游号, e.姓名, e.导游资格等级, " +
                "COUNT(DISTINCT ti.旅游ID) AS 带队次数, " +
                "SUM(ti.费用) AS 总业绩 " +
                "FROM 旅游信息 ti " +
                "JOIN 合同 c ON ti.合同ID = c.合同ID " +
                "JOIN 员工 e ON c.导游ID = e.导游号 " + 
                "GROUP BY e.导游号, e.姓名, e.导游资格等级 " +
                "ORDER BY 总业绩 DESC";

        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            if (!rs.isBeforeFirst()) {
                results.add("没有找到导游业绩数据，请检查数据完整性");
                return results;
            }

            while (rs.next()) {
                String line = String.format("导游号: %s, 姓名: %s, 等级: %s, 带队次数: %d, 总业绩: %.2f",
                        rs.getString("导游号"),
                        rs.getString("姓名"),
                        rs.getString("导游资格等级"),
                        rs.getInt("带队次数"),
                        rs.getDouble("总业绩"));
                results.add(line);
            }
        }
        return results;
    }
    
    // 按月统计总收入
    private static List<String> analyzeMonthlyIncome(Connection conn) throws Exception {
        List<String> results = new ArrayList<>();
        String sql = "SELECT DATE_FORMAT(旅游时间, '%Y-%m') AS 月份, SUM(费用) AS 月收入 " +
                     "FROM 旅游信息 " +
                     "GROUP BY DATE_FORMAT(旅游时间, '%Y-%m') " +
                     "ORDER BY 月份";
        
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                String line = String.format("月份: %s, 月收入: %.2f",
                        rs.getString("月份"),
                        rs.getDouble("月收入"));
                results.add(line);
            }
        }
        return results;
    }
    
    // 生成总账报表
    private static void generateGeneralLedger(Connection conn) throws Exception {
        String currentMonth = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM"));
        
        // 1. 删除当月已有的总账数据
        String deleteSql = "DELETE FROM 财务账目 WHERE 类型 = '总账' AND 统计月份 = '" + currentMonth + "-01'";
        try (Statement stmt = conn.createStatement()) {
            stmt.executeUpdate(deleteSql);
        }
        
        // 2. 按分公司统计当月收入并插入总账
        String branchIncomeSql = "SELECT b.分公司ID, SUM(t.费用) AS 总收入 " +
                                "FROM 旅游信息 t " +
                                "JOIN 旅游团 tg ON t.旅游ID = tg.旅游团ID " +
                                "JOIN 员工 e ON tg.导游ID = e.导游号 " +
                                "JOIN 分公司 b ON e.分公司ID = b.分公司ID " +
                                "WHERE DATE_FORMAT(t.旅游时间, '%Y-%m') = '" + currentMonth + "' " +
                                "GROUP BY b.分公司ID";
        
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(branchIncomeSql)) {
            while (rs.next()) {
                String branchId = rs.getString("分公司ID");
                double income = rs.getDouble("总收入");
                
                String insertSql = "INSERT INTO 财务账目 (账目ID, 类型, 统计月份, 关联ID, 金额) VALUES " +
                                   "('GL" + branchId + "', '总账', '" + currentMonth + "-01', '" + branchId + "', " + income + ")";
                stmt.executeUpdate(insertSql);
            }
        }
        
        // 3. 计算公司总收入并插入总账
        String totalIncomeSql = "SELECT SUM(费用) AS 总收入 FROM 旅游信息 " +
                               "WHERE DATE_FORMAT(旅游时间, '%Y-%m') = '" + currentMonth + "'";
        
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(totalIncomeSql)) {
            if (rs.next()) {
                double totalIncome = rs.getDouble("总收入");
                String insertSql = "INSERT INTO 财务账目 (账目ID, 类型, 统计月份, 关联ID, 金额) VALUES " +
                                  "('GLTOTAL', '总账', '" + currentMonth + "-01', 'TOTAL', " + totalIncome + ")";
                stmt.executeUpdate(insertSql);
            }
        }
        
        System.out.println("\n已生成" + currentMonth + "月份的总账报表");
    }
}