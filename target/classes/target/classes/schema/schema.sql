CREATE DATABASE IF NOT EXISTS gimafashion;

USE gimafashion;

CREATE TABLE IF NOT EXISTS user(
                                   username VARCHAR(45) PRIMARY KEY,
                                   password VARCHAR(100) NOT NULL
);
CREATE TABLE IF NOT EXISTS employer(
                                       id VARCHAR(15) PRIMARY KEY,
                                       name VARCHAR(45) NOT NULL,
                                       nic VARCHAR(15) UNIQUE NOT NULL,
                                       dob DATE NOT NULL,
                                       address VARCHAR(45),
                                       bankAccountNo VARCHAR(45) NOT NULL,
                                       bankBranch VARCHAR(45) NOT NULL,
                                       contactNo VARCHAR(45) NOT NULL
);
CREATE TABLE IF NOT EXISTS orders(
                                     orderId VARCHAR(15) PRIMARY KEY,
                                     date DATE,
                                     total DECIMAL(17,2),
                                     employerId VARCHAR(15),
                                     customerName VARCHAR(45),
                                     customerEmail VARCHAR(100),
                                     customerContact VARCHAR(45),
                                     customerAddress VARCHAR(100),
                                     CONSTRAINT FOREIGN KEY orders(employerId) REFERENCES employer(id)
                                         ON DELETE CASCADE ON UPDATE CASCADE
);
CREATE TABLE IF NOT EXISTS payment(
                                      paymentId VARCHAR(15) PRIMARY KEY,
                                      cash DECIMAL(17,2) NOT NULL,
                                      balance DECIMAL(17,2) NOT NULL,
                                      date DATE,
                                      orderId VARCHAR(15),
                                      CONSTRAINT FOREIGN KEY payment(orderId) REFERENCES orders(orderId)
                                          ON DELETE CASCADE ON UPDATE CASCADE
);
CREATE TABLE IF NOT EXISTS category(
                                       categoryId VARCHAR(15) PRIMARY KEY,
                                       size VARCHAR(15) NOT NULL,
                                       gender VARCHAR(15) NOT NULL
);
CREATE TABLE IF NOT EXISTS item(
                                   itemCode VARCHAR(15) PRIMARY KEY,
                                   description VARCHAR(45) NOT NULL,
                                   qtyOnHand INT NOT NULL,
                                   sellingPrice DECIMAL(17,2) NOT NULL,
                                   buyingPrice DECIMAL(17,2) NOT NULL,
                                   categoryId VARCHAR(15),
                                   CONSTRAINT FOREIGN KEY item(categoryId) REFERENCES category(categoryId)
                                       ON DELETE CASCADE ON UPDATE CASCADE
);
CREATE TABLE IF NOT EXISTS orderDetails(
                                           orderId VARCHAR(15),
                                           itemCode VARCHAR(15),
                                           orderQty INT NOT NULL,
                                           unitPrice DECIMAL(17,2),
                                           categoryId VARCHAR(15),
                                           PRIMARY KEY (orderId,itemCode),
                                           CONSTRAINT FOREIGN KEY orderDetails(orderId) REFERENCES orders(orderId)
                                               ON DELETE CASCADE ON UPDATE CASCADE,
                                           CONSTRAINT FOREIGN KEY orderDetails(itemCode) REFERENCES item(itemCode)
                                               ON DELETE CASCADE ON UPDATE CASCADE
);
CREATE TABLE IF NOT EXISTS supplier(
                                       supplierId VARCHAR(15) PRIMARY KEY,
                                       title VARCHAR(15) NOT NULL,
                                       supplierName VARCHAR(45) NOT NULL,
                                       company VARCHAR(100) NOT NULL,
                                       contact VARCHAR(45) NOT NULL
);
CREATE TABLE IF NOT EXISTS supplierInvoice(
                                              invoiceId VARCHAR(15) UNIQUE NOT NULL,
                                              supplierId VARCHAR(15),
                                              itemCode VARCHAR(15),
                                              date DATE NOT NULL,
                                              qty VARCHAR(15),
                                              PRIMARY KEY (supplierId,itemCode),
                                              CONSTRAINT FOREIGN KEY supplierInvoice(supplierId) REFERENCES supplier(supplierId)
                                                  ON DELETE CASCADE ON UPDATE CASCADE,
                                              CONSTRAINT FOREIGN KEY supplierInvoice(itemCode) REFERENCES item(itemCode)
                                                  ON DELETE CASCADE ON UPDATE CASCADE
);