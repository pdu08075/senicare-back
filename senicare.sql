-- MySQL Workbench Forward Engineering

SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION';

-- -----------------------------------------------------
-- Schema mydb
-- -----------------------------------------------------

-- -----------------------------------------------------
-- Schema mydb
-- -----------------------------------------------------
CREATE SCHEMA IF NOT EXISTS `mydb` DEFAULT CHARACTER SET utf8 ;
-- -----------------------------------------------------
-- Schema senicare
-- -----------------------------------------------------
USE `mydb` ;

-- -----------------------------------------------------
-- Table `mydb`.`tools`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `mydb`.`tools` (
  `tool_number` INT NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(255) NOT NULL,
  `purpose` TEXT NOT NULL,
  `count` INT NOT NULL,
  PRIMARY KEY (`tool_number`),
  UNIQUE INDEX `tool_number_UNIQUE` (`tool_number` ASC) VISIBLE)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `mydb`.`tel_auth_number`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `mydb`.`tel_auth_number` (
  `tel_number` VARCHAR(11) NOT NULL,
  `auth_number` VARCHAR(4) NOT NULL,
  PRIMARY KEY (`tel_number`),
  UNIQUE INDEX `tel_number_UNIQUE` (`tel_number` ASC) VISIBLE)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `mydb`.`nurses`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `mydb`.`nurses` (
  `user_id` VARCHAR(20) NOT NULL,
  `password` VARCHAR(255) NOT NULL,
  `name` VARCHAR(15) NOT NULL,
  `tel_number` VARCHAR(11) NOT NULL,
  `join_path` VARCHAR(5) NOT NULL,
  `sns_id` VARCHAR(255) NULL,
  PRIMARY KEY (`user_id`),
  UNIQUE INDEX `user_id_UNIQUE` (`user_id` ASC) VISIBLE,
  UNIQUE INDEX `tel_number_UNIQUE` (`tel_number` ASC) VISIBLE,
  CONSTRAINT `tel_auth`
    FOREIGN KEY (`tel_number`)
    REFERENCES `mydb`.`tel_auth_number` (`tel_number`)
    ON DELETE CASCADE
    ON UPDATE CASCADE)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `mydb`.`customers`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `mydb`.`customers` (
  `custom_number` INT NOT NULL AUTO_INCREMENT,
  `profile_image` TEXT NOT NULL,
  `name` VARCHAR(15) NOT NULL,
  `birth` VARCHAR(6) NOT NULL,
  `charger` VARCHAR(20) NOT NULL,
  `address` TEXT NOT NULL,
  `location` VARCHAR(60) NOT NULL,
  UNIQUE INDEX `custom_number_UNIQUE` (`custom_number` ASC) VISIBLE,
  PRIMARY KEY (`custom_number`),
  INDEX `customer_charger_idx` (`charger` ASC) VISIBLE,
  CONSTRAINT `customer_charger`
    FOREIGN KEY (`charger`)
    REFERENCES `mydb`.`nurses` (`user_id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `mydb`.`care_records`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `mydb`.`care_records` (
  `record_number` INT NOT NULL AUTO_INCREMENT,
  `record_date` DATE NOT NULL,
  `contents` TEXT NOT NULL,
  `used_tool` INT NULL COMMENT '관리에 사용한 용품',
  `count` INT NULL,
  `charger` VARCHAR(20) NOT NULL,
  `custom_number` INT NOT NULL,
  PRIMARY KEY (`record_number`),
  UNIQUE INDEX `record_number_UNIQUE` (`record_number` ASC) VISIBLE,
  INDEX `used_tools_idx` (`used_tool` ASC) VISIBLE,
  INDEX `record_charger_idx` (`charger` ASC) VISIBLE,
  INDEX `cared_customer_idx` (`custom_number` ASC) VISIBLE,
  CONSTRAINT `used_tools`
    FOREIGN KEY (`used_tool`)
    REFERENCES `mydb`.`tools` (`tool_number`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `record_charger`
    FOREIGN KEY (`charger`)
    REFERENCES `mydb`.`nurses` (`user_id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `cared_customer`
    FOREIGN KEY (`custom_number`)
    REFERENCES `mydb`.`customers` (`custom_number`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;

CREATE USER 'senicare'@'%' IDENTIFIED BY 'P!ssw0rd';

GRANT ALL privileges ON senicare.* TO 'senicare'@'%';