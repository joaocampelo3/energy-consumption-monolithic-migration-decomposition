INSERT INTO dummy.accounts (email, password, role) VALUES
                                                       ('admin_email@gmail.com','$2a$10$CoZ5c8.S3Iht/V3SRCOnP.dH.trp/rvmjtloGXlmDcdCDrNP51Qg2','ADMIN'),
                                                       ('johndoe1234@gmail.com','$2a$10$J7jrwtYh2UAoOjNgsrZTEOKCqnn3UdT5Prj7cL08bDLT3pJOvVdYe','USER'),
                                                       ('merchant@gmail.com','$2a$10$MdZ6GNLCdfkrhmHFdLHO0eXFn5z6Omd2rsZBTgINB44mfUim18w2u','MERCHANT');

INSERT INTO dummy.users(user_firstname, user_lastname, account_id) VALUES
                                                                       ('Admin','OfEverything',1),
                                                                       ('John','Doe',2),
                                                                       ('Merchant','Dummy',3);
INSERT INTO dummy.categories(category_description, category_name) VALUES
                             ('Category 1 description','Category 1'),
                             ('Category 2 description','Category 2');

INSERT INTO dummy.addresses(city, country, street, zip_code, user_id) VALUES
                            ('New York','USA','5th Avenue','10128',3),
                            ('Lisbon','Portugal','Different street','1234',2);

INSERT INTO dummy.merchants(merchant_email, merchant_name, address_id) VALUES
                            ('merchant@gmail.com','Merchant Dummy',1);

INSERT INTO dummy.items(item_description, item_name, item_price, item_stock_quantity, item_sku, item_category, item_merchant) VALUES
                        ('Item 1 description','Item 1',12,8,'ABC-12345-S-BL',1,1),
                        ('Item 2 description','Item 2',20,5,'ABC-12345-XL-BL',1,1);