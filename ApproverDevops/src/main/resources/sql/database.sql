CREATE DATABASE IF NOT EXISTS `approver` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT ENCRYPTION='N';
USE `approver`;
CREATE TABLE IF NOT EXISTS `issue` (
  `ID` int(11) NOT NULL AUTO_INCREMENT,
  `Title` varchar(255) NOT NULL,
  `State` varchar(45) NOT NULL,
  `Md5` varchar(45) NOT NULL,
  `Date` varchar(20) NOT NULL,
  `Project` varchar(45) NOT NULL,
  `Project_url` varchar(45) NOT NULL,
  `Numissue` varchar(45) DEFAULT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB AUTO_INCREMENT=117 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;