-- 员工 (暂不加对分公司的外键)
CREATE TABLE 员工 (
                      导游号 VARCHAR(10) PRIMARY KEY,
                      身份证号 VARCHAR(18) UNIQUE NOT NULL,
                      姓名 VARCHAR(50) NOT NULL,
                      导游资格等级 ENUM('初级', '中级', '高级') NOT NULL,
                      分公司ID VARCHAR(10)
);

-- 分公司
CREATE TABLE 分公司 (
                        分公司ID VARCHAR(10) PRIMARY KEY,
                        名称 VARCHAR(50) NOT NULL,
                        办公地址 VARCHAR(100),
                        经理ID VARCHAR(10),
                        FOREIGN KEY (经理ID) REFERENCES 员工(导游号)
);

-- 后续添加员工表对外键的引用
ALTER TABLE 员工
    ADD CONSTRAINT fk_分公司ID FOREIGN KEY (分公司ID) REFERENCES 分公司(分公司ID);

-- 旅游线路
CREATE TABLE 旅游线路 (
                          线路ID VARCHAR(10) PRIMARY KEY,
                          地点 TEXT,
                          景点 TEXT,
                          时间段 VARCHAR(50),
                          价格 DECIMAL(10,2),
                          交通方式 VARCHAR(50),
                          服务等级 ENUM('标准', '高级', '豪华')
);

-- 客户
CREATE TABLE 客户 (
                      客户ID VARCHAR(10) PRIMARY KEY,
                      身份证号 VARCHAR(18) UNIQUE NOT NULL,
                      姓名 VARCHAR(50) NOT NULL,
                      工作单位 VARCHAR(100),
                      职业 VARCHAR(50)
);

-- 合同
CREATE TABLE 合同 (
                      合同ID VARCHAR(10) PRIMARY KEY,
                      版本号 VARCHAR(10),
                      线路ID VARCHAR(10),
                      导游ID VARCHAR(10),
                      服务等级 ENUM('标准', '中级', '高级') NOT NULL,
                      保险信息 VARCHAR(50),
                      费用约定 DECIMAL(10,2),
                      旅游时间 DATE,
                      FOREIGN KEY (线路ID) REFERENCES 旅游线路(线路ID),
                      FOREIGN KEY (导游ID) REFERENCES 员工(导游号)
);

-- 旅游信息
CREATE TABLE 旅游信息 (
                          旅游ID VARCHAR(10) PRIMARY KEY,
                          客户ID VARCHAR(10),
                          线路ID VARCHAR(10),
                          旅游时间 DATE,
                          费用 DECIMAL(10,2),
                          保险 VARCHAR(50),
                          服务等级 ENUM('标准', '高级', '豪华'),
                          合同ID VARCHAR(10),
                          FOREIGN KEY (客户ID) REFERENCES 客户(客户ID),
                          FOREIGN KEY (线路ID) REFERENCES 旅游线路(线路ID),
                          FOREIGN KEY (合同ID) REFERENCES 合同(合同ID)
);

-- 旅游团
CREATE TABLE 旅游团 (
                        旅游团ID VARCHAR(10) PRIMARY KEY,
                        导游ID VARCHAR(10),
                        线路ID VARCHAR(10),
                        旅游时间 DATE,
                        FOREIGN KEY (导游ID) REFERENCES 员工(导游号),
                        FOREIGN KEY (线路ID) REFERENCES 旅游线路(线路ID)
);

-- 旅游团_客户
CREATE TABLE 旅游团_客户 (
                             旅游团ID VARCHAR(10),
                             客户ID VARCHAR(10),
                             PRIMARY KEY (旅游团ID, 客户ID),
                             FOREIGN KEY (旅游团ID) REFERENCES 旅游团(旅游团ID),
                             FOREIGN KEY (客户ID) REFERENCES 客户(客户ID)
);

-- 财务账目
CREATE TABLE 财务账目 (
                          账目ID VARCHAR(10) PRIMARY KEY,
                          类型 ENUM('分类账', '总账') NOT NULL,
                          统计月份 DATE NOT NULL,
                          关联ID VARCHAR(10),
                          金额 DECIMAL(10,2)
);

CREATE TABLE 导游用户 (
                          导游ID VARCHAR(10) PRIMARY KEY,
                          密码哈希 VARCHAR(60) NOT NULL,
                          FOREIGN KEY (导游ID) REFERENCES 员工(导游号)
);