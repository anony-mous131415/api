use AdX;
--drop table if exists UserInfo;
drop table if exists Roles;
drop table UserRoles;

--drop table if exists Advertiser;

-- Creating Dummy Advertisers For Testing
--create Table Advertiser (av_id int(10) unsigned NOT NULL AUTO_INCREMENT, av_advertiser_name varchar(512) DEFAULT NULL,av_licensee_id int(10) unsigned NOT NULL,  PRIMARY KEY (av_id));

--Insert Into Advertiser (av_id, av_advertiser_name, av_licensee_id ) values(7338 , 'Digikala',359), ( 7330 , 'Moneytap App',358), ( 7324 , 'Pharmeasy' ,357), ( 7318 , 'Go Life' ,356), ( 7343 , 'Total Trive (DT Test) ',355), ( 7333 , 'Test Advertiser' ,355), ( 7311 , 'Digibank',354), ( 7309 , 'Paytm Mall',353), ( 7308 , 'Adskom',352), ( 7335 , 'DCO Demo - Shawl Closet' ,351), ( 7334 , 'Netcore Marketing Team',351), ( 7317 , 'Netcore_Pepipost',351), ( 7306 , 'Netcore Pilot Advertiser',351), ( 7295 , 'Lifestyle' ,350), ( 7288 , 'Bluebird',349), ( 7279 , 'Ayopop',348), ( 7278 , 'FreshMenu' ,347), ( 7273 , 'Max Fashions App',346), ( 7319 , 'Test',345), ( 7298 , 'Australia tourism cpc dec17 ',345);


--CREATE TABLE user (id bigint auto_increment, username varchar(64) NOT NULL, password varchar(64) NOT NULL, is_active tinyint(1) NOT NULL DEFAULT '0', creation_time datetime NOT NULL DEFAULT CURRENT_TIMESTAMP, created_by varchar(64) NOT NULL, modfied_by varchar(64) NOT NULL, modified_time datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP, PRIMARY KEY (id));

--CREATE TABLE UserInfo (ui_id int(11) NOT NULL AUTO_INCREMENT,   ui_password varchar(64) NOT NULL,   ui_licensee_id int(11) unsigned DEFAULT NULL,   ui_login_name varchar(128) NOT NULL,   ui_is_active tinyint(1) NOT NULL DEFAULT '0',   ui_creation_time bigint(13) DEFAULT NULL,   ui_advertiser_id int(10) unsigned DEFAULT NULL,   ui_role enum('RW','RO','DEMO','ADMIN') NOT NULL DEFAULT 'RW' , ui_creation datetime NOT NULL DEFAULT CURRENT_TIMESTAMP, ui_created_by varchar(64) NOT NULL, ui_modfied_by varchar(64) NOT NULL, ui_modified_time datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP ,   PRIMARY KEY (ui_id),   UNIQUE KEY ui_login_name_UNIQUE (ui_login_name));

-- Alter Table

ALTER TABLE AdX.UserInfo ADD COLUMN `ui_create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP, ADD COLUMN `ui_created_by` varchar(64) NOT NULL,  ADD COLUMN `ui_modfied_by` varchar(64) NOT NULL, ADD COLUMN `ui_modified_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP;

-- password is password

INSERT INTO UserInfo (ui_id, ui_login_name, ui_password, ui_is_active, ui_created_by, ui_modfied_by, ui_role)  VALUES (1, 'SAdmin', '$2a$10$qXQo4z4oXKPEKyYO7bAQmOQ9PhIcHK4LOo/L1U9j/xkLEmseLWECK', '1','Admin' , 'Admin','RW');

INSERT INTO UserInfo (ui_id, ui_login_name, ui_password, ui_is_active, ui_created_by, ui_modfied_by,ui_role)  VALUES (2, 'Admin', '$2a$10$qXQo4z4oXKPEKyYO7bAQmOQ9PhIcHK4LOo/L1U9j/xkLEmseLWECK', '1','Admin' , 'Admin', 'RW');
INSERT INTO UserInfo (ui_id, ui_login_name, ui_password, ui_is_active, ui_created_by, ui_modfied_by, ui_role)  VALUES (3, 'Akhilesh', '$2a$10$Vst8Mp1RE3cJcKv22tC/Nej2yOleCxzPTwknVqLgDxFAt/gYL6cfG', '1','Admin' , 'Admin', 'RW');

INSERT INTO UserInfo (ui_id, ui_login_name, ui_password, ui_is_active, ui_created_by, ui_modfied_by, ui_role)  VALUES (4, 'maurya', '$2a$10$qXQo4z4oXKPEKyYO7bAQmOQ9PhIcHK4LOo/L1U9j/xkLEmseLWECK', '1','Admin' , 'Admin', 'RW');
INSERT INTO UserInfo (ui_id, ui_login_name, ui_password, ui_is_active, ui_created_by, ui_modfied_by, ui_role)  VALUES (5, 'demo', '$2a$10$Vst8Mp1RE3cJcKv22tC/Nej2yOleCxzPTwknVqLgDxFAt/gYL6cfG', '1','Admin' , 'Admin', 'RW');
INSERT INTO UserInfo (ui_id, ui_login_name, ui_password, ui_is_active, ui_created_by, ui_modfied_by, ui_role)  VALUES (6, 'akhileshmaurya494@gmail.com', '', '1','Admin' , 'Admin', 'RW');
INSERT INTO UserInfo (ui_id, ui_login_name, ui_password, ui_is_active, ui_created_by, ui_modfied_by, ui_role)  VALUES (7, 'maurya_akhilesh@ymail.com', '', '1','Admin' , 'Admin', 'RW');
INSERT INTO UserInfo (ui_id, ui_login_name, ui_password, ui_is_active, ui_created_by, ui_modfied_by, ui_role)  VALUES (8, 'akhileshmaurya494@outlook.com', '', '1','Admin' , 'Admin', 'RW');

create table Roles (ro_id bigint auto_increment, ro_description varchar(255), ro_name ENUM('SADMIN','ADMIN', 'RW','RO','DEMO') NOT NULL DEFAULT 'RO', primary key (ro_id));	

INSERT INTO Roles (ro_id, ro_description, ro_name) VALUES (1, 'Super Admin Role', 'SADMIN');
INSERT INTO Roles (ro_id, ro_description, ro_name) VALUES (2, 'Admin Role', 'ADMIN');
INSERT INTO Roles (ro_id, ro_description, ro_name) VALUES (3, 'Read And Write Role', 'RW');
INSERT INTO Roles (ro_id, ro_description, ro_name) VALUES (4, 'Read Role', 'RO');

INSERT INTO Roles (ro_id, ro_description, ro_name) VALUES (5, 'Demo Mock data Role', 'DEMO');


CREATE TABLE UserRoles (  `ur_id` bigint(20) NOT NULL AUTO_INCREMENT,   `ur_user_id` int(11) NOT NULL,   `ur_role_id` bigint( 20) NOT NULL,   `ur_licensee_id` int(11) NOT NULL,   `ur_adv_id` int(10) unsigned NOT NULL,   PRIMARY KEY (`ur_id`),   foreign key (ur_role_id) references Roles (ro_id),   foreign key (ur_user_id) references UserInfo (ui_id),   foreign key (ur_adv_id) references Advertiser (av_id));

INSERT INTO UserRoles (ur_user_id, ur_role_id, ur_licensee_id, ur_adv_id) VALUES (1, 1, 359, 7338), (1, 2, 355, 7343), (1, 4, 355, 7333);
INSERT INTO UserRoles (ur_user_id, ur_role_id, ur_licensee_id, ur_adv_id) VALUES (2, 1, 351, 7335), (2, 3, 351, 7334);
INSERT INTO UserRoles (ur_user_id, ur_role_id, ur_licensee_id, ur_adv_id) VALUES (3, 3, 346, 7273);
INSERT INTO UserRoles (ur_user_id, ur_role_id, ur_licensee_id, ur_adv_id) VALUES (4, 4, 355, 7343);
INSERT INTO UserRoles (ur_user_id, ur_role_id, ur_licensee_id, ur_adv_id) VALUES (5, 5, 359, 7338);
INSERT INTO UserRoles (ur_user_id, ur_role_id, ur_licensee_id, ur_adv_id) VALUES (6, 4, 355, 7333);
INSERT INTO UserRoles (ur_user_id, ur_role_id, ur_licensee_id, ur_adv_id) VALUES (7, 3, 351, 7335), (7, 4, 359, 7338), (7, 1, 355, 7343);

INSERT INTO UserRoles (ur_user_id, ur_role_id) VALUES (918, 1);



