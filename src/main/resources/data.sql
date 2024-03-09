INSERT INTO `ccteam`.`member` (`id`, `first_name`, `last_name`, `email`, `password`, `phone`, `avatar_url`, `bike`, `active`, `verified`, `admin`, `otp`, `otp_date`, `registration_date`, `created_on`, `modified_on`) VALUES
(1, 'Bob', 'Admin', 'bob.admin@wanadoo.fr', '$2y$10$MuLwPiQkTlcKEbGX6ztzAOxGlqK7ddglgDXcYBRBFDwkM.AQy63EK', '+33 123456789', null, 'Honda CBR 600 RR 2007', 1, 1, 1, null, null, '2016-07-11 00:00:00', '2019-06-10 13:44:26', '2018-07-01 07:30:54'),
(2, 'Stéphane', 'Verger', 'steph.verger@orange.fr', null, '+72 777992834', null, 'Kawasaki ZX6R 636 2015', 0, 0, 0, null, null, '2016-06-30 00:00:00', '2019-02-11 21:11:24', NULL),
(3, 'Coralie', 'Archambault', 'coralie.ar@free.fr', null, '+56 856755465', null, 'Suzuki GSXR 750 2007', 0, 0, 0, null, null, '2019-01-11 21:44:00', '2019-02-11 21:07:37', NULL),
(4, 'Etienne', 'Moquin', 'etienne.moquin@gmail.com', null, '+34 583683774', null, 'Yamaha R1 2016', 0, 0, 0, null, null, '2019-01-31 23:27:00', '2019-02-11 21:06:59', NULL),
(5, 'Dylan', 'Gabriaux', 'dylangabriaux@orange.fr', null, '+03 381647281', null, 'BMW S1000RR', 0, 0, 0, null, null, '2017-05-16 00:00:00', '2019-02-11 21:08:00', NULL),
(6, 'André', 'De La Vergne', 'Andre.vergne@test.fr', null, '+77 373737377', null, 'Aprilia RSV4 2010', 0, 0, 0, null, null, '2019-02-11 22:12:00', '2019-02-11 21:12:34', NULL),
(7, 'Gilles', 'Arpin', 'gillearpin@test.ch', null, '+02 883773736', null, 'Honda 1000 CBR 2012', 0, 0, 0, null, null, '2019-02-11 22:13:00', '2019-02-11 21:13:47', NULL),
(8, 'Frédéric', 'Dupond', 'Fred.dupond@test.fr', null, '+03 383747278', null, 'Ducati 848 2012', 0, 0, 0, null, null, '2019-02-11 22:15:00', '2019-02-11 21:15:59', NULL),
(9, 'John', 'Doe', 'john.doe@mail.fr', null, '+33608080808', null, 'Honda CBR 600 RR 2010', 0, 0, 0, null, null, '2018-01-30 00:00:00', '2018-07-01 09:30:54', NULL),
(10, 'Jenna', 'Jonhnson', 'jenna.jonhnson@mail.com', null, NULL, null, 'Kawasaki ZX6R 636 2013', 0, 0, 0, null, null, '2018-02-19 13:56:42', '2018-07-01 09:37:12', NULL);

INSERT INTO `ccteam`.`news` (`id`, `title`, `catch_line`, `content`, `news_date`, `created_on`, `created_by`, `modified_on`, `modified_by`) VALUES
(1, 'Repas du club', 'Repas de club avec tartiflettre géante', 'Repas de club avec tartiflettre géante', '2018-05-30 23:17:12', '2018-06-01 11:50:41', 1, NULL, NULL),
(2, 'Réunion de dèbut d''année', 'Réunion de dèbut d''année pour oganiser les roulages', 'Réunion de dèbut d''année pour oganiser les roulages', '2018-05-30 23:31:44', '2018-06-01 00:35:07', 1, NULL, NULL),
(3, 'Réunion pour organisation foire au 2 roues', 'Réunion pour organisation foire au 2 roues qui aura lieu de 21 mars 2020', 'Réunion pour organisation foire au 2 roues qui aura lieu de 21 mars 2020', '2018-06-01 00:01:36', '2018-06-01 00:35:07', 1, '2018-06-01 02:14:44', 1),
(4, 'Annulation du roulage Alés fin d''année', 'Le roulage qui devait avoir lieu à Ales en fin d''année est annulé', 'Attention le roulage qui devait avoir lieu à Ales en fin d''année est annulé car le circuit est fermé suite au record du circuit battu par Yann', '2019-01-22 18:00:00', '2018-06-01 00:01:36', 1, NULL, NULL),
(5, 'Essai de la nouvelle R1 à Barcelone', 'Essai de la nouvelle R1 à Barcelone sous la pluie', 'Essai de la nouvelle R1 à Barcelone sous la pluie', '2019-11-22 16:08:00', '2019-06-01 16:08:00', 1, NULL, NULL),
(6, 'Soirée mousse chez Fred', 'Soirée mousse chez Fred avec DJ Fred et Arnold T', 'Soirée mousse chez Fred avec DJ Fred et Arnold T', '2019-12-02 19:24:16', '2019-06-01 16:08:00', 1, NULL, NULL);

INSERT INTO `ccteam`.`track` (`id`, `name`, `distance`, `lap_record`, `website`, `latitude`, `longitude`) VALUES
(1, 'Bresse', 3000, 84330, 'https://www.circuitdebresse.com', 46.551431, 5.328642),
(2, 'Dijon-Prenois', 3800, 79427, 'https://www.circuit-dijon-prenois.com', 47.3643282, 4.8977331),
(3, 'Magny-Cours', 4410, 96950, 'https://www.circuitmagnycours.com', 46.8615409, 3.1612033),
(4, 'Bourbonnais', 2300, 66800, 'https://circuitdubourbonnais.com', 46.5364023, 3.4311176),
(5, 'Vaison', 2000, 58590, 'http://vaisonpiste.com', 46.762535, 4.4403653),
(6, 'Lédenon', 3150, 81996, 'https://www.ledenon.com', 43.9236779, 4.5043975),
(7, 'Le Mans', 4190, 91185, 'https://www.lemans.org', 47.9560052, 0.2056818),
(8, 'Carole', 2055, 59462, 'https://www.circuit-carole.com', 48.9787026, 2.5203949),
(9, 'La Ferté-Gaucher', 3600, 107245, 'https://www.circuitslfg.fr', 48.7579086, 3.2811883),
(10, 'Alès', 2500, 74679, 'http://www.pole-mecanique.fr', 44.1421587, 4.0682965);

INSERT INTO `ccteam`.`event` (`id`, `title`, `description`, `start_date`, `end_date`, `track_id`, `organizer`, `price`, `created_on`, `created_by`, `modified_on`, `modified_by`) VALUES
(1, 'Roulage Dijon', 'Roulage à Dijon-Prenois avec ActivBike', '2018-07-12 00:00:00', '2018-07-12 00:00:00', 2, 'ActivBike', 189, '2018-06-01 09:35:07', 1, NULL, NULL),
(2, 'Roulage Vaison piste', 'Vaison avec Goret team comme d''habitude', '2018-08-02 00:00:00', '2018-08-02 00:00:00', 5, 'ActivBike', 90, '2018-02-08 14:30:29', 1, NULL, NULL),
(3, 'Journée du club à Bresse', 'Journée du Club sur le circuit de Bresse. Tous les participants se verront rembourser 70€ (par chéque) pris sur le compte du club.', '2018-08-28 00:00:00', '2018-08-28 00:00:00', 1, 'Team Blatz', 125, '2018-04-20 17:14:27', 1, NULL, NULL),
(4, 'Week-end à Alès', '', '2019-10-25 00:00:00', '2019-10-26 00:00:00', 1, 'ActivBike', 256, '2019-04-20 11:01:47', 1, NULL, NULL),
(5, 'Roulage Magny-cours', '2 jours à Magny-cours avec TP55', '2019-08-28 00:00:00', '2019-08-29 00:00:00', 1, 'Team Performance 55', 340, '2020-05-13 22:43:26', 1, NULL, NULL),
(6, 'Roulage Bresse', 'Premier roulage de l''année à Bresse avec ActivBike', '2020-05-09 00:00:00', '2019-05-09 00:00:00', 1, 'ActivBike', 140, '2020-05-22 13:20:45', 1, NULL, NULL),
(7, 'Roulage La Ferté Gaucher', 'Roulage libre à Bresse avec Moto France Racing', '2019-04-21 00:00:00', '2019-04-21 00:00:00', 9, 'Moto France Racing', 90, '2019-03-01 12:00:00', 1, NULL, NULL),
(8, 'Roulage Carole', 'Roulage libre à Carole avec CAM Racing Team', '2019-04-22 00:00:00', '2019-04-22 00:00:00', 8, 'CAM Racing Team', 109, '2019-03-01 12:00:00', 1, NULL, NULL),
(9, 'Roulage Dijon-Prenois', 'Roulage libre à Dijon avec ActivBike', '2019-05-08 00:00:00', '2019-05-08 00:00:00', 2, 'ActivBike', 195, '2019-03-01 12:00:00', 1, NULL, NULL),
(10, 'Roulage Bourbonnais', 'Roulage libre au Bourbonnais avec le moto club Fleur de Lys', '2019-06-15 00:00:00', '2019-06-15 00:00:00', 4, 'Moto club Fleur de Lys', 90, '2019-03-01 12:00:00', 1, NULL, NULL),
(11, 'Roulage Magny-cours', 'Roulage libre à Magny-cours avec Box23', '2019-06-16 00:00:00', '2019-06-16 00:00:00', 3, 'Box23', 205, '2019-03-01 12:00:00', 1, NULL, NULL),
(12, 'Roulage Bresse', 'Journée du Club sur le circuit de Bresse. Tous les participants se verront rembourser 70€ (par chéque) pris sur le compte du club.', '2019-07-06 00:00:00', '2019-07-06 00:00:00', 1, 'ActivBike', 140, '2019-03-01 12:00:00', 1, NULL, NULL),
(13, 'Roulage Le Mans', 'Roulage libre au Mans avec H2S', '2019-07-22 00:00:00', '2019-07-23 00:00:00', 7, 'H2S', 340, '2019-03-01 12:00:00', 1, NULL, NULL),
(14, 'Roulage Alès', 'Roulage libre à Alès avec ActivBike', '2019-10-27 00:00:00', '2019-10-28 00:00:00', 10, 'ActivBike', 254, '2019-03-01 12:00:00', 1, NULL, NULL);

INSERT INTO `ccteam`.`gallery` (`id`, `title`, `description`, `created_on`, `modified_on`) VALUES
(1, 'Divers', 'Gallerie par défaut', '2020-05-29 23:18:52', NULL),
(2, 'Alès 2018', 'Roulage à Alès avec ActivBike en octobre 2018', '2020-06-02 09:25:17', NULL),
(3, 'Lédenon 2018', 'Roulage à Alès avec ActivBike en octobre 2018', '2020-06-02 09:25:17', NULL);

INSERT INTO `ccteam`.`photo` (`id`, `title`, `description`, `link`, `created_on`, `modified_on`) VALUES
(1, 'Lorenzo', 'Lorenzo qui célèbre sa victoire', 'http://photos.example.com/wp-content/uploads/2018/06/IMG_1575.jpg', '2018-09-12 08:33:19', NULL),
(2, 'Marc Màrquez', 'Marc Màrquez dans le 1er virage', 'http://photos.example.com/wp-content/uploads/2018/06/IMG_1548.jpg', '2018-02-08 13:30:29', NULL),
(3, 'Johann Zarco', 'Johann Zarco', 'http://photos.example.com/wp-content/uploads/2018/06/IMG_1464.jpg', '2018-04-20 15:14:27', NULL),
(4, 'Enea Bastianini', 'Enea Bastianini', 'http://photos.example.com/wp-content/uploads/2018/06/IMG_1274.jpg', '2018-11-21 13:09:04', NULL),
(5, 'Jorge Lorenzo', 'Jorge Lorenzo', 'http://photos.example.com/wp-content/uploads/2018/06/IMG_1576', '2019-01-16 21:49:01', NULL),
(6, 'Show aérien', 'Show aérien', 'http://photos.example.com/wp-content/uploads/2018/06/IMG_1417.jpg', '2019-01-16 21:50:52', NULL),
(7, 'Marc Márquez', 'Marc Márquez', 'http://photos.example.com/wp-content/uploads/2018/06/IMG_1478.jpg', '2019-01-16 21:51:19', NULL),
(8, 'Oliveira', 'Oliveira', 'http://photos.example.com/wp-content/uploads/2018/06/IMG_1293', '2019-01-16 21:50:31', NULL),
(9, 'Départ MotoGP', 'Départ MotoGP', 'http://photos.example.com/wp-content/uploads/2018/06/IMG_1441', '2019-01-16 21:49:31', NULL),
(10, 'Cal Crutchlow', 'Cal', 'http://photos.example.com/wp-content/uploads/2018/06/IMG_1400.jpg', '2019-02-11 21:05:16', NULL);

INSERT INTO `ccteam`.`event_member` (`id`, `event_id`, `member_id`, `created_on`) VALUES
(1, 3, 2, '2018-06-01 09:35:07'),
(2, 1, 1, '2018-02-08 14:30:29'),
(3, 3, 1, '2018-04-20 17:14:27'),
(4, 2, 1, '2017-11-18 10:42:55');

INSERT INTO `ccteam`.`liked_news` (`id`, `news_id`, `member_id`, `created_on`) VALUES
(1, 1, 2, '2018-06-01 09:35:07'),
(2, 1, 1, '2018-02-08 14:30:29'),
(3, 3, 1, '2018-04-20 17:14:27'),
(4, 4, 8, '2017-11-18 10:42:55');