INSERT INTO dummy.accounts VALUES
                          (1,'johndoe1234@gmail.com','$2a$10$puI2eoONtTnKED28QcmGnOlhA8CpVW2fvufnmpoD9XOXuwR3Dt.Ie','ADMIN'),
                          (2,'usernumber1@gmail.com','$2a$10$kES2XssVL/rpKC8SXs.2C.VYk4AOpQejdQLCNunNE5MGQklI0V1sW','USER'),
                          (3,'merchantnumber1@gmail.com','$2a$10$rA1fl5IRicN3Ew0NbcO2b.yQ.B54PfwexkCLbSbf/9ztvkpE6qBKO','MERCHANT'),
                          (4,'merchantnumber2@gmail.com','$2a$10$Lyjo04uZbqLv6io1Z4bsYe7/gdRsnRdbVXZdLH57tSybMjjzOvE5i','MERCHANT'),
                          (5,'usernumber2@gmail.com','$2a$10$7YHgtN.jrv5Cn1N3gadn6eaojfyMqegDMAql5frfpcEG3Y/3S03V.','USER');

INSERT INTO dummy.users VALUES
                        (1,'John','Doe',1),
                        (2,'User','NumberOne',2),
                        (3,'Merchant','NumberOne',3),
                        (4,'Merchant','NumberTwo',4),
                        (5,'User','NumberTwo',5);

INSERT INTO dummy.categories VALUES
                             (1,'Category 1 description','Category 1'),
                             (2,'Category 2 description','Category 2');

INSERT INTO dummy.addresses VALUES
                            (1,'New York','USA','5th Avenue','10128',3),
                            (2,'New York','USA','Different Avenue','10128',4),
                            (3,'Lisbon','Portugal','Different street','1234',2);

INSERT INTO dummy.merchants VALUES
                            (1,'merchantnumber1@gmail.com','Merchant 1',1),
                            (2,'merchantnumber2@gmail.com','Merchant 2',2);

INSERT INTO dummy.items VALUES
                        (1,'Item 1 description','Item 1',12,8,'ABC-12345-S-BL',1,1),
                        (2,'Item 2 description','Item 2',20,5,'ABC-12345-XL-BL',1,1),
                        (3,'Item 3 description','Item 3',13,20,'ABC-12345-M-BL',2,2),
                        (4,'Item 4 description','Item 4',15,20,'ABC-12345-L-BL',2,2);