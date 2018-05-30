SET FOREIGN_KEY_CHECKS=0;
TRUNCATE TABLE `account`;
TRUNCATE TABLE `transaction`;
SET FOREIGN_KEY_CHECKS=1;

INSERT INTO `account` (`id`, `user_id`, `full_name`, `creation_date`, `balance`, `address`, `email`) VALUES (10000, 'popo', 'Popo Man', '2017-06-10 16:22:00', 75000.52, 'Sarin 12, NYC', 'popoman@gmail.com');
INSERT INTO `account` (`id`, `user_id`, `full_name`, `creation_date`, `balance`, `address`, `email`) VALUES (10001, 'nisim', 'Nisim Garame', '2017-7-05 21:52:00',220.00, 'Hanegev 34/2, Tel-Aviv', 'nisimg@gmail.com');
INSERT INTO `account` (`id`, `user_id`, `full_name`, `creation_date`, `balance`, `address`, `email`) VALUES (10002, 'sami', 'Sami Vesusu', '2017-03-12 09:02:00',35154.59, 'Rotcshild 100, Tel-Aviv', 'sami22@gmail.com');
INSERT INTO `account` (`id`, `user_id`, `full_name`, `creation_date`, `balance`, `address`, `email`) VALUES (10003, 'bobybob', 'Bob Habanei', '2017-06-01 13:21:00',10760.25, 'Bugrashove 158, Tel-Aviv', 'bobhabanei@kenkenken.com');
INSERT INTO `account` (`id`, `user_id`, `full_name`, `creation_date`, `balance`, `address`, `email`) VALUES (10004, 'yuval', 'Yuval Hamebulbal', '2017-05-21 21:13:00',-395.11, 'Allenby 50, Tel-Aviv', 'oopshediditagain@gmail.com');
INSERT INTO `account` (`id`, `user_id`, `full_name`, `creation_date`, `balance`, `address`, `email`) VALUES (10005, 'habil', 'Habil Hagarbil', '2017-06-17 19:27:21',28000.76, 'Farkush 22, Tel-Aviv', 'garbilPower@gmail.com');
INSERT INTO `account` (`id`, `user_id`, `full_name`, `creation_date`, `balance`, `address`, `email`) VALUES (10006, 'utzli', 'Utzli Gutzli', '2017-05-08 14:32:00',5642.74, 'Hayarkon 270, Tel-Aviv', 'gutzli@whatup.com');
INSERT INTO `account` (`id`, `user_id`, `full_name`, `creation_date`, `balance`, `address`, `email`) VALUES (10007, 'hugo', 'Hugo Boss', '2017-04-09 23:07:00',5221.42, 'Habonim 17, Haifa', 'hugoboss@gmail.com');
INSERT INTO `account` (`id`, `user_id`, `full_name`, `creation_date`, `balance`, `address`, `email`) VALUES (10008, 'aber', 'Aber Crombie', '2017-02-16 13:25:00',-3000.23, 'Hahorsim 71, Haifa 12', 'abercombie@gmail.com');
INSERT INTO `account` (`id`, `user_id`, `full_name`, `creation_date`, `balance`, `address`, `email`) VALUES (10009, 'kopa', 'Kopa Kabana', '2017-03-23 22:12:15',46745.00, 'Panini 20, Yavne', 'kopahopa@gmail.com');
INSERT INTO `account` (`id`, `user_id`, `full_name`, `creation_date`, `balance`, `address`, `email`) VALUES (10010, 'snow', 'Snow White', '2017-04-27 22:39:00',46650.06, 'Dizingof 2, Tel-Aviv', 'dwarfs@gmail.com');
INSERT INTO `account` (`id`, `user_id`, `full_name`, `creation_date`, `balance`, `address`, `email`) VALUES (10011, 'rambo', 'Rambo Man', '2017-06-12 12:23:00', 0, 'Supyo 32, NYC', 'rambo@gmail.com');

INSERT INTO `transaction` (`id`, `amount`, `from_account_id`, `to_account_id`, `date`, `description`) VALUES ('10000', 200, 10000, 10001, '2017-09-10 16:22:00', 'For you');
INSERT INTO `transaction` (`id`, `amount`, `from_account_id`, `to_account_id`, `date`, `description`) VALUES ('10001', 500, 10000, 10002, '2017-08-2 16:22:00', 'Good luck!');
INSERT INTO `transaction` (`id`, `amount`, `from_account_id`, `to_account_id`, `date`, `description`) VALUES ('10002', 1200, 10002, 10009, '2017-08-28 16:22:00', 'Shut up and take my money');
INSERT INTO `transaction` (`id`, `amount`, `from_account_id`, `to_account_id`, `date`, `description`) VALUES ('10003', 458, 10008, 10003, '2017-04-12 16:22:00', 'Poo');
INSERT INTO `transaction` (`id`, `amount`, `from_account_id`, `to_account_id`, `date`, `description`) VALUES ('10004', 288.54, 10006, 10005, '2017-07-14 16:22:00', 'Moneyyyy$$');

#CREATE INDEX balance ON `account`(`balance`,`userid`);