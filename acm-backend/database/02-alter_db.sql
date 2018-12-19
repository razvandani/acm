SET NAMES 'utf8';

CREATE TABLE IF NOT EXISTS `role` (
  `role_id` INT NOT NULL AUTO_INCREMENT,
  `role_name` VARCHAR(45) NOT NULL,
  `role_desc` VARCHAR(45) NOT NULL,
  `is_predefined_role` BIT NOT NULL DEFAULT 0,
  `is_deleted` BIT NOT NULL DEFAULT 0,
  PRIMARY KEY (`role_id`),
  UNIQUE KEY role_roleName_unique (`role_name`))
ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

CREATE TABLE IF NOT EXISTS `user` (
  `user_id` bigint(20) NOT NULL AUTO_INCREMENT,
  `user_name` varchar(200) NOT NULL,
  `email` varchar(100) NOT NULL,
  `first_name` varchar(100) NOT NULL,
  `last_name` varchar(100) NOT NULL,
  `password` varchar(80) DEFAULT NULL,
  `created_dttm` timestamp NULL DEFAULT NULL,
  `is_active` tinyint(1) NOT NULL,
  `is_locked` tinyint(1) DEFAULT NULL,
  `role_id` int(11) NOT NULL,
  phone_number varchar(20) NOT NULL,
  PRIMARY KEY (`user_id`),
  UNIQUE KEY `user_userName_unique` (`user_name`),
  UNIQUE KEY `user_email_unique` (`email`),
  KEY `fk_user_role` (`role_id`),
  CONSTRAINT `fk_user_role` FOREIGN KEY (`role_id`) REFERENCES `role` (`role_id`) ON DELETE CASCADE ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE IF NOT EXISTS `county` (
  `id` int NOT NULL AUTO_INCREMENT PRIMARY KEY,
  `name` varchar(100) NOT NULL,
  unique key county_unique (name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

INSERT IGNORE INTO role (role_id, role_name, role_desc, is_predefined_role) VALUES(1, 'Super user', 'Super user', 1);
INSERT IGNORE INTO role (role_id, role_name, role_desc, is_predefined_role) VALUES(2, 'Agent', 'Agent', 1);
INSERT IGNORE INTO role (role_id, role_name, role_desc, is_predefined_role) VALUES(3, 'Moderator', 'Moderator', 1);

insert ignore into user(user_name, email, first_name, last_name, password, is_active, is_locked,
role_id, phone_number)
values ('super.user@pixelenergy.com', 'super.user@pixelenergy.com', 'Super', 'User',
'fa6ebc77818e918d48cce57c1065c73be466833e794e22b5d6604b6c00e071d40f97063057bd6962',
1, 0, 1, '111111');

insert ignore into user(user_name, email, first_name, last_name, password, is_active, is_locked,
role_id, phone_number)
values ('agent.user@pixelenergy.com', 'agent.user@pixelenergy.com', 'Super', 'User',
'fa6ebc77818e918d48cce57c1065c73be466833e794e22b5d6604b6c00e071d40f97063057bd6962',
1, 0, 2, '111111');

insert ignore into user(user_name, email, first_name, last_name, password, is_active, is_locked,
role_id, phone_number)
values ('moderator.user@pixelenergy.com', 'moderator.user@pixelenergy.com', 'Super', 'User',
'fa6ebc77818e918d48cce57c1065c73be466833e794e22b5d6604b6c00e071d40f97063057bd6962',
1, 0, 3, '111111');

insert ignore into county(name) values ('Alba');
insert ignore into county(name) values ('Arad');
insert ignore into county(name) values ('Argeş');
insert ignore into county(name) values ('Bacău');
insert ignore into county(name) values ('Bihor');
insert ignore into county(name) values ('Bistriţa-Năsăud');
insert ignore into county(name) values ('Botoşani');
insert ignore into county(name) values ('Brăila');
insert ignore into county(name) values ('Braşov');
insert ignore into county(name) values ('Buzău');
insert ignore into county(name) values ('Călăraşi');
insert ignore into county(name) values ('Caraş-Severin');
insert ignore into county(name) values ('Cluj');
insert ignore into county(name) values ('Constanţa');
insert ignore into county(name) values ('Covasna');
insert ignore into county(name) values ('Dâmboviţa');
insert ignore into county(name) values ('Dolj');
insert ignore into county(name) values ('Galaţi');
insert ignore into county(name) values ('Giurgiu');
insert ignore into county(name) values ('Gorj');
insert ignore into county(name) values ('Harghita');
insert ignore into county(name) values ('Hunedoara');
insert ignore into county(name) values ('Ialomiţa');
insert ignore into county(name) values ('Iaşi');
insert ignore into county(name) values ('Ilfov');
insert ignore into county(name) values ('Maramureş');
insert ignore into county(name) values ('Mehedinţi');
insert ignore into county(name) values ('Mureş');
insert ignore into county(name) values ('Neamţ');
insert ignore into county(name) values ('Olt');
insert ignore into county(name) values ('Prahova');
insert ignore into county(name) values ('Sălaj');
insert ignore into county(name) values ('Satu Mare');
insert ignore into county(name) values ('Sibiu');
insert ignore into county(name) values ('Suceava');
insert ignore into county(name) values ('Teleorman');
insert ignore into county(name) values ('Timiş');
insert ignore into county(name) values ('Tulcea');
insert ignore into county(name) values ('Vâlcea');
insert ignore into county(name) values ('Vaslui');
insert ignore into county(name) values ('Vrancea');
insert ignore into county(name) values ('Bucureşti');

CREATE TABLE IF NOT EXISTS `permission` (
  permission_id int(11) NOT NULL AUTO_INCREMENT,
  permission_name varchar(100) NOT NULL,
  permission_code varchar(45) NOT NULL,
  permission_desc varchar(45) DEFAULT NULL,
  is_deleted bit(1) NOT NULL DEFAULT b'0',
  PRIMARY KEY (permission_id),
  UNIQUE KEY `permission_permissionName_idx` (`permission_name`),
  UNIQUE KEY `permission_permissionCode_idx` (`permission_code`)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8;


-- rolePermission table
CREATE TABLE IF NOT EXISTS role_permission (
  role_perm_id INT NOT NULL AUTO_INCREMENT,
  role_id INT NOT NULL,
  permission_id INT NOT NULL,
  PRIMARY KEY (role_perm_id),
  UNIQUE KEY rolePermission_unique (role_id, permission_id),
  INDEX `fk_rolePermission_role_idx` (`role_id` ASC),
  INDEX `fk_rolePermission_permission_idx` (`permission_id` ASC),
  CONSTRAINT `fk_rolePermission_role`
    FOREIGN KEY (`role_id`)
    REFERENCES role (role_id)
    ON DELETE CASCADE
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_rolePermission_permission_idx`
    FOREIGN KEY (permission_id)
    REFERENCES permission (permission_id)
    ON DELETE CASCADE
    ON UPDATE NO ACTION)
ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

CREATE TABLE IF NOT EXISTS permission_rest_method (
  permission_rest_method_id int(11) NOT NULL AUTO_INCREMENT,
  permission_id int(11) NOT NULL,
  rest_request_path varchar(200) DEFAULT NULL,
  rest_request_method varchar(20) DEFAULT NULL,
  PRIMARY KEY (`permission_rest_method_id`),
  UNIQUE KEY `PERMISSION_REST_METHOD_UNIQUE` (`rest_request_path`,`rest_request_method`,`permission_id`),
  KEY `fk_RN_PERMISSION_REST_METHOD_PERIMISSION` (`permission_id`),
  CONSTRAINT `fk_RN_PERMISSION_REST_METHOD_PERIMISSION` FOREIGN KEY (`permission_id`) REFERENCES `permission` (`permission_id`) ON DELETE CASCADE ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=68 DEFAULT CHARSET=utf8;

INSERT IGNORE INTO permission (permission_name, permission_code, permission_desc, is_deleted)
VALUES ('Manage customer', 'MCU', 'Manage customer', 0);

INSERT IGNORE INTO role_permission (role_id, permission_id)
VALUES (1, (SELECT permission_id FROM permission WHERE permission_code='MCU'));

INSERT IGNORE INTO role_permission (role_id, permission_id)
VALUES (2, (SELECT permission_id FROM permission WHERE permission_code='MCU'));

INSERT IGNORE INTO permission_rest_method (permission_id, rest_request_path, rest_request_method)
VALUES ((SELECT permission_id FROM permission WHERE permission_code='MCU'), '/customerservice/customer', 'POST');

INSERT IGNORE INTO permission_rest_method (permission_id, rest_request_path, rest_request_method)
VALUES ((SELECT permission_id FROM permission WHERE permission_code='MCU'), '/customerservice/customer', 'PUT');

INSERT IGNORE INTO permission_rest_method (permission_id, rest_request_path, rest_request_method)
VALUES ((SELECT permission_id FROM permission WHERE permission_code='MCU'), '/customerservice/customer', 'GET');

INSERT IGNORE INTO permission_rest_method (permission_id, rest_request_path, rest_request_method)
VALUES ((SELECT permission_id FROM permission WHERE permission_code='MCU'), '/customerservice/customer/find', 'POST');

INSERT IGNORE INTO permission_rest_method (permission_id, rest_request_path, rest_request_method)
VALUES ((SELECT permission_id FROM permission WHERE permission_code='MCU'), '/customerservice/county/find', 'POST');

INSERT IGNORE INTO permission_rest_method (permission_id, rest_request_path, rest_request_method)
VALUES ((SELECT permission_id FROM permission WHERE permission_code='MCU'), '/customerservice/commission/calculate', 'POST');

INSERT IGNORE INTO permission (permission_name, permission_code, permission_desc, is_deleted)
VALUES ('Manage customer moderator', 'MCUM', 'Manage customer moderator', 0);

INSERT IGNORE INTO permission_rest_method (permission_id, rest_request_path, rest_request_method)
VALUES ((SELECT permission_id FROM permission WHERE permission_code='MCUM'), '/customerservice/customer', 'PUT');

INSERT IGNORE INTO permission_rest_method (permission_id, rest_request_path, rest_request_method)
VALUES ((SELECT permission_id FROM permission WHERE permission_code='MCUM'), '/customerservice/customer', 'GET');

INSERT IGNORE INTO permission_rest_method (permission_id, rest_request_path, rest_request_method)
VALUES ((SELECT permission_id FROM permission WHERE permission_code='MCUM'), '/customerservice/customer/find', 'POST');

INSERT IGNORE INTO permission_rest_method (permission_id, rest_request_path, rest_request_method)
VALUES ((SELECT permission_id FROM permission WHERE permission_code='MCUM'), '/customerservice/county/find', 'POST');

INSERT IGNORE INTO role_permission (role_id, permission_id)
VALUES (3, (SELECT permission_id FROM permission WHERE permission_code='MCUM'));

INSERT IGNORE INTO permission (permission_name, permission_code, permission_desc, is_deleted)
VALUES ('Customer Export', 'CE', 'Customer Export', 0);

INSERT IGNORE INTO role_permission (role_id, permission_id)
VALUES (1, (SELECT permission_id FROM permission WHERE permission_code='CE'));

INSERT IGNORE INTO permission_rest_method (permission_id, rest_request_path, rest_request_method)
VALUES ((SELECT permission_id FROM permission WHERE permission_code='CE'), '/customerservice/customer/export', 'POST');


CREATE TABLE IF NOT EXISTS `customer` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  contract_number VARCHAR(10) NOT NULL,
  `first_name` varchar(100) NOT NULL,
  `last_name` varchar(100) NOT NULL,
  product_type INT NOT NULL, -- 1 = Electric energy, 2 = Natural Gas
  commission_type INT NOT NULL, -- 1 = Fix abonament 6 luni, 2 = E-go citire lunara, 3 = Flex citire lunara, 4 = Flux gaz
  commission_subcategory INT NOT NULL, -- 1 = B1, 2 = B2, 3 = B3, 4 = B4
  `county_id` int(11) NOT NULL,
  `location` varchar(100) NOT NULL,
  `phone_number` varchar(20) NOT NULL,
  `contract_date` date NOT NULL,
  `is_active` bit(1) NOT NULL,
  `agent_id` bigint(20) NOT NULL,
  agent_name varchar(100) NOT NULL,
  street VARCHAR(100) NOT NULL,
  street_number VARCHAR(10) NOT NULL,
  start_delivery_date date NOT NULL,
  commission DECIMAL(12, 2) NULL,
  status INT NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY (contract_number),
  KEY `fk_customer_county` (`county_id`),
  KEY `fk_customer_agent` (`agent_id`),
  CONSTRAINT `fk_customer_agent` FOREIGN KEY (`agent_id`) REFERENCES `user` (`user_id`) ON DELETE CASCADE ON UPDATE NO ACTION,
  CONSTRAINT `fk_customer_county` FOREIGN KEY (`county_id`) REFERENCES `county` (`id`) ON DELETE CASCADE ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE IF NOT EXISTS `commission_type` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(45) NOT NULL,
  `commission_value` decimal(12,2) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `name_UNIQUE` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE IF NOT EXISTS `agent_commission` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `agent_id` bigint NOT NULL,
  `commission_type` int(11) NOT NULL,
  `commission_subcategory` int(11) NULL, -- this is only spplicable for Gas, 1 = B1, 2 = B2, 3 = B3, 4 = B4
  `commission_subcategory_start` int(11)  NULL, -- this is applicable for Electric current, for ex 5KW - 25KW
  `commission_subcategory_end` int(11) NULL, -- this is applicable for Electric current, for ex 5KW - 25KW
  `commission_value` decimal(10,0) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `agent_commission_gas` (`agent_id`, commission_type, commission_subcategory),
  KEY `agent_commission_electric` (`agent_id`, commission_type, commission_subcategory_start, commission_subcategory_end)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


call AddColumn('customer', 'flat', 'VARCHAR(10) NULL');
call AddColumn('customer', 'stair_number', 'VARCHAR(10) NULL');
call AddColumn('customer', 'apartment_number', 'VARCHAR(10) NULL');

call AddColumn('user', 'reset_password_token', 'VARCHAR(50) NULL');

call RenameColumn('customer', 'contract_type', 'commission_type', 'INT NOT NULL');
call AddColumn('customer', 'status', 'INT NOT NULL DEFAULT 1');
update customer set status = 4 where status = 2 and id != 0;

call ChangeColumn('customer', 'is_active', 'BIT NULL');
call ChangeColumn('agent_commission', 'commission_type', 'INT NULL');