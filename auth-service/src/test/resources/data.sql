
drop table if exists user;
drop table if exists role;
drop table if exists user_roles;
drop table if exists user_advertisers;

drop table if exists Advertiser;

-- Creating Dummy Advertisers For Testing
create Table Advertiser (av_id int(10) unsigned NOT NULL AUTO_INCREMENT, av_advertiser_name varchar(512) DEFAULT NULL,av_licensee_id int(10) unsigned NOT NULL);

Insert Into Advertiser (av_id, av_advertiser_name, av_licensee_id ) values(7338 , 'Digikala',359), ( 7330 , 'Moneytap App',358), ( 7324 , 'Pharmeasy' ,357), ( 7318 , 'Go Life' ,356), ( 7343 , 'Total Trive (DT Test) ',355), ( 7333 , 'Test Advertiser' ,355), ( 7311 , 'Digibank',354), ( 7309 , 'Paytm Mall',353), ( 7308 , 'Adskom',352), ( 7335 , 'DCO Demo - Shawl Closet' ,351), ( 7334 , 'Netcore Marketing Team',351), ( 7317 , 'Netcore_Pepipost',351), ( 7306 , 'Netcore Pilot Advertiser',351), ( 7295 , 'Lifestyle' ,350), ( 7288 , 'Bluebird',349), ( 7279 , 'Ayopop',348), ( 7278 , 'FreshMenu' ,347), ( 7273 , 'Max Fashions App',346), ( 7319 , 'Test',345), ( 7298 , 'Australia tourism cpc dec17 ',345);


CREATE TABLE user (id int(11) NOT NULL AUTO_INCREMENT, username varchar(64) NOT NULL, password varchar(64) NOT NULL, is_active tinyint(1) NOT NULL DEFAULT '0', creation_time datetime NOT NULL DEFAULT CURRENT_TIMESTAMP, created_by varchar(64) NOT NULL, modfied_by varchar(64) NOT NULL, modified_time datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP, PRIMARY KEY (id));

-- password is password
INSERT INTO user (id, username, password, is_active, created_by, modfied_by)  VALUES (1, 'SAdmin', '$2a$10$qXQo4z4oXKPEKyYO7bAQmOQ9PhIcHK4LOo/L1U9j/xkLEmseLWECK', '1','Admin' , 'Admin');

INSERT INTO user (id, username, password, is_active, created_by, modfied_by)  VALUES (2, 'Admin', '$2a$10$qXQo4z4oXKPEKyYO7bAQmOQ9PhIcHK4LOo/L1U9j/xkLEmseLWECK', '1','Admin' , 'Admin');
INSERT INTO user (id, username, password, is_active, created_by, modfied_by)  VALUES (3, 'Akhilesh', '$2a$10$Vst8Mp1RE3cJcKv22tC/Nej2yOleCxzPTwknVqLgDxFAt/gYL6cfG', '1','Admin' , 'Admin');

INSERT INTO user (id, username, password, is_active, created_by, modfied_by)  VALUES (4, 'maurya', '$2a$10$qXQo4z4oXKPEKyYO7bAQmOQ9PhIcHK4LOo/L1U9j/xkLEmseLWECK', '1','Admin' , 'Admin');
INSERT INTO user (id, username, password, is_active, created_by, modfied_by)  VALUES (5, 'demo', '$2a$10$Vst8Mp1RE3cJcKv22tC/Nej2yOleCxzPTwknVqLgDxFAt/gYL6cfG', '1','Admin' , 'Admin');


create table role (id bigint auto_increment, description varchar(255), name ENUM('SADMIN','ADMIN', 'RW','RO','DEMO') NOT NULL DEFAULT 'RO', primary key (id));	

INSERT INTO role (id, description, name) VALUES (1, 'Super Admin Role', 'SADMIN');
INSERT INTO role (id, description, name) VALUES (2, 'Admin Role', 'ADMIN');
INSERT INTO role (id, description, name) VALUES (3, 'Read And Write Role', 'RW');
INSERT INTO role (id, description, name) VALUES (4, 'Read Role', 'RO');

INSERT INTO role (id, description, name) VALUES (5, 'Demo Mock data Role', 'DEMO');

create table user_roles (user_id bigint not null, role_id bigint not null, primary key (user_id, role_id));

alter table user_roles add constraint fk_role_id foreign key (role_id) references role (id);
alter table user_roles add constraint fk_user_id foreign key (user_id) references user (id);

INSERT INTO user_roles (user_id, role_id) VALUES (1, 1);
INSERT INTO user_roles (user_id, role_id) VALUES (2, 2);
INSERT INTO user_roles (user_id, role_id) VALUES (3, 3);

INSERT INTO user_roles (user_id, role_id) VALUES (4, 4);
INSERT INTO user_roles (user_id, role_id) VALUES (5, 5);


create table user_advertisers (user_id bigint not null, adv_id int not null, primary key (user_id, adv_id));
alter table user_advertisers add constraint fk_adv_id foreign key (adv_id) references Advertiser (av_id);
alter table user_advertisers add constraint fk_user_adv_id foreign key (user_id) references user (id);


INSERT INTO user_advertisers (user_id, adv_id) VALUES (1, 7338);
INSERT INTO user_advertisers (user_id, adv_id) VALUES (1, 7343);
INSERT INTO user_advertisers (user_id, adv_id) VALUES (1, 7333);
INSERT INTO user_advertisers (user_id, adv_id) VALUES (2, 7335);
INSERT INTO user_advertisers (user_id, adv_id) VALUES (2, 7334);
INSERT INTO user_advertisers (user_id, adv_id) VALUES (3, 7273);
INSERT INTO user_advertisers (user_id, adv_id) VALUES (4, 7343);
INSERT INTO user_advertisers (user_id, adv_id) VALUES (5, 7338);