import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.Random;

public class TourismDataGenerator {
    private static final Random random = new Random();
    
    // 基础数据数组
    private static final String[] TOUR_IDS = {"T001", "T002", "T003", "T004", "T005", "T006", "T007", "T008", "T009", "T010"};
    private static final double[] PRICES = {1500.00, 2200.00, 1800.00, 1000.00, 1300.00, 2500.00, 1600.00, 1400.00, 2000.00, 2800.00};
    private static final String[] SERVICE_LEVELS = {"标准", "高级", "豪华"};
    private static final String[] SERVICE_LEVELS2 = {"标准", "中级", "高级"};
    private static final String[] INSURANCES = {"平安保险", "太平洋保险", "人保财险", "人寿保险"};
    private static final String[] GUIDE_IDS = {"G001", "G002", "G003", "G004", "G005", "G006", "G007", "G008", "G009", "G010"};

    public static void main(String[] args) throws ClassNotFoundException {
        String url = "jdbc:mysql://localhost:3306/tourism_db?useUnicode=true&characterEncoding=UTF-8";
        String user = "root";
        String password = "123456";
        Class.forName("com.mysql.cj.jdbc.Driver");
        try (Connection conn = DriverManager.getConnection(url, user, password)) {
            // 1. 生成基础数据
            generateBaseData(conn);
            
            // 2. 生成大量旅游信息
            generateTravelInfo(conn, 1000);
            
            // 3. 生成财务数据
            generateFinancialData(conn);
            
            System.out.println("所有数据生成完成！");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void generateBaseData(Connection conn) throws Exception {
        try (Statement stmt = conn.createStatement()) {
            // 员工表
            stmt.executeUpdate("INSERT INTO 员工 (导游号, 身份证号, 姓名, 导游资格等级) VALUES " +
                    "('G001', '110101199003072516', '张伟', '高级')," +
                    "('G002', '110101198504123456', '王芳', '中级')," +
                    "('G003', '110101199206154321', '李娜', '初级')," +
                    "('G004', '110101198809221234', '赵强', '高级')," +
                    "('G005', '110101199312111111', '孙丽', '中级')," +
                    "('G006', '110101199107081234', '刘明', '高级')," +
                    "('G007', '110101198808091234', '张红', '中级')," +
                    "('G008', '110101199505061234', '王浩', '初级')," +
                    "('G009', '110101199010101234', '李梅', '高级')," +
                    "('G010', '110101198712121234', '赵阳', '中级')");

            // 分公司表
            stmt.executeUpdate("INSERT INTO 分公司 (分公司ID, 名称, 办公地址, 经理ID) VALUES " +
                    "('B001', '北京分公司', '北京市朝阳区建国路88号', 'G001')," +
                    "('B002', '上海分公司', '上海市浦东新区世纪大道100号', 'G002')," +
                    "('B003', '广州分公司', '广州市天河区珠江新城华夏路23号', 'G003')," +
                    "('B004', '成都分公司', '成都市武侯区人民南路四段55号', 'G004')," +
                    "('B005', '武汉分公司', '武汉市江汉区解放大道688号', 'G005')," +
                    "('B006', '深圳分公司', '深圳市南山区科技园路33号', 'G006')," +
                    "('B007', '杭州分公司', '杭州市西湖区文三路478号', 'G007')," +
                    "('B008', '西安分公司', '西安市雁塔区高新路25号', 'G008')");

            // 更新员工分公司归属
            stmt.executeUpdate("UPDATE 员工 SET 分公司ID = 'B001' WHERE 导游号 IN ('G001', 'G004')");
            stmt.executeUpdate("UPDATE 员工 SET 分公司ID = 'B002' WHERE 导游号 IN ('G002', 'G005')");
            stmt.executeUpdate("UPDATE 员工 SET 分公司ID = 'B003' WHERE 导游号 IN ('G003', 'G006')");
            stmt.executeUpdate("UPDATE 员工 SET 分公司ID = 'B004' WHERE 导游号 IN ('G007')");
            stmt.executeUpdate("UPDATE 员工 SET 分公司ID = 'B005' WHERE 导游号 IN ('G008')");
            stmt.executeUpdate("UPDATE 员工 SET 分公司ID = 'B006' WHERE 导游号 IN ('G009')");
            stmt.executeUpdate("UPDATE 员工 SET 分公司ID = 'B007' WHERE 导游号 IN ('G010')");

            // 旅游线路
            stmt.executeUpdate("INSERT INTO 旅游线路 (线路ID, 地点, 景点, 时间段, 价格, 交通方式, 服务等级) VALUES " +
                    "('T001', '北京-天津', '故宫、长城、五大道', '3天2晚', 1500.00, '大巴', '标准')," +
                    "('T002', '上海-杭州', '外滩、西湖、灵隐寺', '4天3晚', 2200.00, '高铁', '高级')," +
                    "('T003', '成都-重庆', '宽窄巷子、磁器口、洪崖洞', '5天4晚', 1800.00, '飞机+大巴', '豪华')," +
                    "('T004', '广州-深圳', '白云山、世界之窗、欢乐谷', '2天1晚', 1000.00, '大巴', '标准')," +
                    "('T005', '武汉-长沙', '黄鹤楼、橘子洲头、岳麓山', '3天2晚', 1300.00, '高铁', '高级')," +
                    "('T006', '西安-洛阳', '兵马俑、华山、龙门石窟', '4天3晚', 2500.00, '高铁', '豪华')," +
                    "('T007', '厦门-泉州', '鼓浪屿、南普陀、开元寺', '3天2晚', 1600.00, '大巴', '标准')," +
                    "('T008', '青岛-威海', '栈桥、刘公岛、华夏城', '3天2晚', 1400.00, '大巴', '高级')," +
                    "('T009', '桂林-阳朔', '象鼻山、漓江、西街', '4天3晚', 2000.00, '飞机+大巴', '豪华')," +
                    "('T010', '昆明-大理', '石林、洱海、古城', '5天4晚', 2800.00, '高铁', '豪华')");

            // 客户表
            stmt.executeUpdate("INSERT INTO 客户 (客户ID, 身份证号, 姓名, 工作单位, 职业) VALUES " +
                    "('C001', '110101198001011234', '刘洋', '腾讯科技有限公司', '软件工程师')," +
                    "('C002', '110101198202022345', '陈静', '阿里巴巴集团', '产品经理')," +
                    "('C003', '110101199003033456', '周杰', '华为技术有限公司', '网络工程师')," +
                    "('C004', '110101198504044567', '吴婷', '工商银行', '银行职员')," +
                    "('C005', '110101198805055678', '黄磊', '京东商城', '市场经理')," +
                    "('C006', '110101199106066789', '张萍', '字节跳动', '数据分析师')," +
                    "('C007', '110101198707077890', '王健', '百度公司', '算法工程师')," +
                    "('C008', '110101199208088901', '李想', '美团点评', '运营经理')," +
                    "('C009', '110101198309099012', '赵琳', '网易公司', '游戏策划')," +
                    "('C010', '110101199410101123', '孙伟', '小米科技', '硬件工程师')," +
                    "('C011', '110101198611111234', '周明', '滴滴出行', '产品运营')," +
                    "('C012', '110101199712121345', '吴超', '快手科技', '视频编导')," +
                    "('C013', '110101198813131456', '郑华', '携程旅行', '客户经理')," +
                    "('C014', '110101199914141567', '刘芳', '拼多多', '商务专员')," +
                    "('C015', '110101198015151678', '王璐', '新浪微博', '内容运营')");

            System.out.println("基础数据生成完成！");
        }
    }

    private static void generateTravelInfo(Connection conn, int count) throws Exception {
        String insertTravelInfo = "INSERT INTO 旅游信息 (旅游ID, 客户ID, 线路ID, 旅游时间, 费用, 保险, 服务等级, 合同ID) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        String insertTravelGroup = "INSERT INTO 旅游团 (旅游团ID, 导游ID, 线路ID, 旅游时间) VALUES (?, ?, ?, ?)";
        String insertTravelGroupCustomer = "INSERT INTO 旅游团_客户 (旅游团ID, 客户ID) VALUES (?, ?)";
        String insertContract = "INSERT INTO 合同 (合同ID, 版本号, 线路ID, 导游ID, 服务等级, 保险信息, 费用约定, 旅游时间) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement psTravelInfo = conn.prepareStatement(insertTravelInfo);
             PreparedStatement psTravelGroup = conn.prepareStatement(insertTravelGroup);
             PreparedStatement psTravelGroupCustomer = conn.prepareStatement(insertTravelGroupCustomer);
             PreparedStatement psContract = conn.prepareStatement(insertContract)) {

            conn.setAutoCommit(false);
            LocalDate startDate = LocalDate.of(2024, 1, 1);

            for (int i = 1; i <= count; i++) {
                String travelId = String.format("TR%04d", i);
                String customerId = String.format("C%03d", random.nextInt(15) + 1);
                int tourIndex = random.nextInt(TOUR_IDS.length);
                String tourId = TOUR_IDS[tourIndex];
                String guideId = GUIDE_IDS[random.nextInt(GUIDE_IDS.length)];

                LocalDate travelDate = startDate.plusDays(random.nextInt(365));
                double price = PRICES[tourIndex]; // 使用对应线路的价格
                String insurance = INSURANCES[random.nextInt(INSURANCES.length)];
                String serviceLevel = SERVICE_LEVELS[random.nextInt(SERVICE_LEVELS.length)];
                String serviceLevel2 = SERVICE_LEVELS2[random.nextInt(SERVICE_LEVELS.length)];

                String contractId = String.format("CT%04d", i);
                String groupId = String.format("TG%04d", i);

                // 插入合同信息
                psContract.setString(1, contractId);
                psContract.setString(2, "v1." + random.nextInt(3));
                psContract.setString(3, tourId);
                psContract.setString(4, guideId);
                psContract.setString(5, serviceLevel2);
                psContract.setString(6, insurance);
                psContract.setDouble(7, price);
                psContract.setDate(8, java.sql.Date.valueOf(travelDate));
                psContract.addBatch();

                // 插入旅游信息
                psTravelInfo.setString(1, travelId);
                psTravelInfo.setString(2, customerId);
                psTravelInfo.setString(3, tourId);
                psTravelInfo.setDate(4, java.sql.Date.valueOf(travelDate));
                psTravelInfo.setDouble(5, price);
                psTravelInfo.setString(6, insurance);
                psTravelInfo.setString(7, serviceLevel);
                psTravelInfo.setString(8, contractId);
                psTravelInfo.addBatch();

                // 插入旅游团信息
                psTravelGroup.setString(1, groupId);
                psTravelGroup.setString(2, guideId);
                psTravelGroup.setString(3, tourId);
                psTravelGroup.setDate(4, java.sql.Date.valueOf(travelDate));
                psTravelGroup.addBatch();

                // 插入旅游团-客户关系
                psTravelGroupCustomer.setString(1, groupId);
                psTravelGroupCustomer.setString(2, customerId);
                psTravelGroupCustomer.addBatch();

                if (i % 500 == 0) {
                    try {
                        psContract.executeBatch();
                        psTravelInfo.executeBatch();
                        psTravelGroup.executeBatch();
                        psTravelGroupCustomer.executeBatch();
                        conn.commit();
                    } catch (Exception e) {
                        conn.rollback();
                        System.err.println("Error at batch " + i + ": " + e.getMessage());
                        throw e;
                    }
                }
            }

            try {
                psContract.executeBatch();
                psTravelInfo.executeBatch();
                psTravelGroup.executeBatch();
                psTravelGroupCustomer.executeBatch();
                conn.commit();
            } catch (Exception e) {
                conn.rollback();
                System.err.println("Error at final batch: " + e.getMessage());
                throw e;
            }

            conn.setAutoCommit(true);
            System.out.println("旅游信息数据生成完成！");
        }
    }

    private static void generateFinancialData(Connection conn) throws Exception {
        try (Statement stmt = conn.createStatement()) {
            // 财务账目表
            stmt.executeUpdate("INSERT INTO 财务账目 (账目ID, 类型, 统计月份, 关联ID, 金额) VALUES " +
                    // 线路收入
                    "('F101', '分类账', '2024-05-01', 'T001', 15000.00)," +
                    "('F102', '分类账', '2024-05-01', 'T002', 22000.00)," +
                    "('F103', '分类账', '2024-05-01', 'T003', 18000.00)," +
                    "('F104', '分类账', '2024-05-01', 'T004', 10000.00)," +
                    "('F105', '分类账', '2024-05-01', 'T005', 13000.00)," +
                    "('F106', '分类账', '2024-05-01', 'T006', 25000.00)," +
                    "('F107', '分类账', '2024-05-01', 'T007', 16000.00)," +
                    "('F108', '分类账', '2024-05-01', 'T008', 14000.00)," +
                    "('F109', '分类账', '2024-05-01', 'T009', 20000.00)," +
                    "('F110', '分类账', '2024-05-01', 'T010', 28000.00)," +

                    // 分公司总账
                    "('F201', '总账', '2024-05-01', 'B001', 45000.00)," +
                    "('F202', '总账', '2024-05-01', 'B002', 30000.00)," +
                    "('F203', '总账', '2024-05-01', 'B003', 35000.00)," +
                    "('F204', '总账', '2024-05-01', 'B004', 28000.00)," +
                    "('F205', '总账', '2024-05-01', 'B005', 22000.00)," +
                    "('F206', '总账', '2024-05-01', 'B006', 33000.00)," +
                    "('F207', '总账', '2024-05-01', 'B007', 27000.00)," +
                    "('F208', '总账', '2024-05-01', 'B008', 25000.00)," +

                    // 导游业绩
                    "('F301', '分类账', '2024-05-01', 'G001', 5000.00)," +
                    "('F302', '分类账', '2024-05-01', 'G002', 4500.00)," +
                    "('F303', '分类账', '2024-05-01', 'G003', 4000.00)," +
                    "('F304', '分类账', '2024-05-01', 'G004', 3500.00)," +
                    "('F305', '分类账', '2024-05-01', 'G005', 3000.00)," +
                    "('F306', '分类账', '2024-05-01', 'G006', 4800.00)," +
                    "('F307', '分类账', '2024-05-01', 'G007', 3800.00)," +
                    "('F308', '分类账', '2024-05-01', 'G008', 3200.00)," +
                    "('F309', '分类账', '2024-05-01', 'G009', 4200.00)," +
                    "('F310', '分类账', '2024-05-01', 'G010', 3600.00)," +

                    // 6月份数据
                    "('F401', '分类账', '2024-06-01', 'T001', 16000.00)," +
                    "('F402', '分类账', '2024-06-01', 'T002', 23000.00)," +
                    "('F403', '总账', '2024-06-01', 'B001', 48000.00)," +
                    "('F404', '总账', '2024-06-01', 'B002', 32000.00)," +
                    "('F405', '分类账', '2024-06-01', 'G001', 5500.00)," +
                    "('F406', '分类账', '2024-06-01', 'G002', 4800.00)");

            System.out.println("财务数据生成完成！");
        }
    }
}