INSERT INTO `ccteam`.`member` (`id`, `first_name`, `last_name`, `email`, `password`, `phone`, `verified`, `admin`, `otp`, `otp_date`, `registration_date`, `role`, `board_role`, `failed_login_attempts`, `locked_until`, `created_on`, `modified_on`) VALUES
(1, 'Bob', 'Admin', 'bob.admin@example.com', '$2y$10$MuLwPiQkTlcKEbGX6ztzAOxGlqK7ddglgDXcYBRBFDwkM.AQy63EK', '+33 123456789', 1, 1, null, null, '2016-07-11 00:00:00', 'ROLE_ADMIN', NULL, 0, NULL, '2019-06-10 13:44:26', '2019-07-01 07:30:54'),
(2, 'Stéphane', 'Verger', 'steph.verger@example.fr', '$2y$10$MuLwPiQkTlcKEbGX6ztzAOxGlqK7ddglgDXcYBRBFDwkM.AQy63EK', '+72 777992834', 0, 0, null, null, '2016-06-30 00:00:00', 'ROLE_MEMBER', NULL, 0, NULL, '2019-02-11 21:11:24', NULL),
(3, 'Coralie', 'Archambault', 'coralie.ar@example.fr', '$2y$10$MuLwPiQkTlcKEbGX6ztzAOxGlqK7ddglgDXcYBRBFDwkM.AQy63EK', '+56 856755465', 0, 0, null, null, '2019-01-11 21:44:00', 'ROLE_MEMBER', NULL, 0, NULL, '2019-02-11 21:07:37', NULL),
(4, 'Etienne', 'Moquin', 'etienne.moquin@example.com', '$2y$10$MuLwPiQkTlcKEbGX6ztzAOxGlqK7ddglgDXcYBRBFDwkM.AQy63EK', '+34 583683774', 0, 0, null, null, '2019-01-31 23:27:00', 'ROLE_MEMBER', NULL, 0, NULL, '2019-02-11 21:06:59', NULL),
(5, 'Dylan', 'Gabriaux', 'dylangabriaux@example.net', '$2y$10$MuLwPiQkTlcKEbGX6ztzAOxGlqK7ddglgDXcYBRBFDwkM.AQy63EK', '+03 381647281', 0, 0, null, null, '2017-05-16 00:00:00', 'ROLE_MEMBER', NULL, 0, NULL, '2019-02-11 21:08:00', NULL),
(6, 'André', 'De La Vergne', 'Andre.vergne@example.fr', '$2y$10$MuLwPiQkTlcKEbGX6ztzAOxGlqK7ddglgDXcYBRBFDwkM.AQy63EK', '+77 373737377', 0, 0, null, null, '2019-02-11 22:12:00', 'ROLE_MEMBER', NULL, 0, NULL, '2019-02-11 21:12:34', NULL),
(7, 'Gilles', 'Arpin', 'gillearpin@example.ch', '$2y$10$MuLwPiQkTlcKEbGX6ztzAOxGlqK7ddglgDXcYBRBFDwkM.AQy63EK', '+02 883773736', 0, 0, null, null, '2019-02-11 22:13:00', 'ROLE_MEMBER', NULL, 0, NULL, '2019-02-11 21:13:47', NULL),
(8, 'Frédéric', 'Dupond', 'Fred.dupond@example.fr', '$2y$10$MuLwPiQkTlcKEbGX6ztzAOxGlqK7ddglgDXcYBRBFDwkM.AQy63EK', '+03 383747278', 0, 0, null, null, '2019-02-11 22:15:00', 'ROLE_MEMBER', NULL, 0, NULL, '2019-02-11 21:15:59', NULL),
(9, 'John', 'Doe', 'john.doe@example.fr', '$2y$10$MuLwPiQkTlcKEbGX6ztzAOxGlqK7ddglgDXcYBRBFDwkM.AQy63EK', '+33608080808', 0, 0, null, null, '2018-01-30 00:00:00', 'ROLE_MEMBER', NULL, 0, NULL, '2018-07-01 09:30:54', NULL),
(10, 'Jenna', 'Jonhnson', 'jenna.jonhnson@example.com', '$2y$10$MuLwPiQkTlcKEbGX6ztzAOxGlqK7ddglgDXcYBRBFDwkM.AQy63EK', NULL, 0, 0, null, null, '2018-02-19 13:56:42', 'ROLE_MEMBER', NULL, 0, NULL, '2018-07-01 09:37:12', NULL);

INSERT INTO `ccteam`.`bike` (`id`, `manufacturer`, `model_name`, `engine_size`, `year`, `member_id`) VALUES
(1, 'Honda', 'CBR 600 RR', 600, 2007, 1),
(2, 'Kawasaki', 'ZX6R 636', 636, 2015, 2),
(3, 'Suzuki', 'GSXR 750', 750, 2007, 3),
(4, 'Yamaha', 'R1', 1000, 2016, 4),
(5, 'BMW', 'S1000RR', 1000, 2015, 5),
(6, 'Aprilia', 'RSV4', 1000, 2010, 6),
(7, 'Honda', '1000 CBR', 1000, 2012, 7),
(8, 'Ducati', '848', 848, 2012, 8),
(9, 'Honda', 'CBR 600 RR', 600, 2010, 9),
(10, 'Kawasaki', 'ZX6R 636', 636, 2013, 10),
(11, 'Kawasaki', 'ZX10RR', 1000, 2017, 1);

INSERT INTO `ccteam`.`organizer` (`id`, `name`) VALUES
(1, 'Just2race'),
(2, 'Tony roulage'),
(3, 'Ambiance Paddock'),
(4, 'Team Action Vitesse'),
(5, 'Activbike'),
(6, 'Team Blats Organisation'),
(7, 'Up Racing'),
(8, 'Circuit de bresse'),
(9, 'Born to run'),
(10, 'Team Performance 55'),
(11, 'Moto France Racing'),
(12, 'Ultimate cup'),
(13, 'Cam Racing Team'),
(14, 'MC Fleur de Lys'),
(15, 'Box23'),
(16, 'H2S'),
(17, 'Goret Team'),
(18, 'Moto Team Racing'),
(19, 'Team 18 sapeur pompiers'),
(20, 'Acces Piste Moto'),
(21, 'Valdam\'s Racing'),
(22, 'Team roulage 25'),
(23, 'Riding sensations'),
(24, 'Moto ain');

INSERT INTO `ccteam`.`news` (`id`, `title`, `catch_line`, `content`, `news_date`, `created_on`, `created_by`, `modified_on`, `modified_by`) VALUES
(1, 'Repas du club', 'Repas de club avec tartiflettre géante', 'Repas de club avec tartiflettre géante', '2018-05-30 23:17:12', '2018-06-01 11:50:41', 1, NULL, NULL),
(2, 'Réunion de dèbut d''année', 'Réunion de dèbut d''année pour oganiser les roulages', 'Réunion de dèbut d''année pour oganiser les roulages', '2018-05-30 23:31:44', '2018-06-01 00:35:07', 1, NULL, NULL),
(3, 'Réunion pour organisation foire au 2 roues', 'Réunion pour organisation foire au 2 roues qui aura lieu de 21 mars 2020', 'Réunion pour organisation foire au 2 roues qui aura lieu de 21 mars 2020', '2018-06-01 00:01:36', '2018-06-01 00:35:07', 1, '2018-06-01 02:14:44', 1),
(4, 'Annulation du roulage Alés fin d''année', 'Le roulage qui devait avoir lieu à Ales en fin d''année est annulé', 'Attention le roulage qui devait avoir lieu à Ales en fin d''année est annulé car le circuit est fermé suite au record du circuit battu par Yann', '2019-01-22 18:00:00', '2018-06-01 00:01:36', 1, NULL, NULL),
(5, 'Essai de la nouvelle R1 à Barcelone', 'Essai de la nouvelle R1 à Barcelone sous la pluie', 'Essai de la nouvelle R1 à Barcelone sous la pluie', '2019-11-22 16:08:00', '2019-06-01 16:08:00', 1, NULL, NULL),
(6, 'Soirée mousse chez Fred', 'Soirée mousse chez Fred avec DJ Fred et Arnold T', 'Soirée mousse chez Fred avec DJ Fred et Arnold T', '2019-12-02 19:24:16', '2019-06-01 16:08:00', 1, NULL, NULL);

INSERT INTO `ccteam`.`track` (`id`, `distance`, `lap_record`, `latitude`, `longitude`, `name`, `website`, `country_code`, `lap_record_info`) VALUES
(1, 3000, 83630, 46.55143100, 5.32864200, 'Bresse', 'https://www.circuitdebresse.com', 'FR', 'Corentin Perolari, Yamaha R1, 2022'),
(2, 3800, 79475, 47.36432820, 4.89773310, 'Dijon-Prenois', 'https://www.circuit-dijon-prenois.com', 'FR', 'Dominique Schmitter, Yamaha R1M, 2019'),
(3, 4411, 94930, 46.86154090, 3.16120330, 'Magny-Cours', 'https://www.circuitmagnycours.com', 'FR', 'Toprak Razgatlioglu, BMW M1000 RR, 2025'),
(4, 2300, 64400, 46.53640230, 3.43111760, 'Bourbonnais', 'https://circuitdubourbonnais.com', 'FR', 'Guillaume Raymond, Yamaha R6, 2020'),
(5, 2000, 58110, 46.76253500, 4.44036530, 'Vaison', 'http://vaisonpiste.com', 'FR', 'Diego Poncet, Kawasaki 636, 2022'),
(6, 3150, 81412, 43.92367790, 4.50439750, 'Lédenon', 'https://www.ledenon.com', 'FR', 'Kenny Foray, BMW M1000RR, 2026'),
(7, 4190, 89288, 47.95600520, 0.20568180, 'Le Mans', 'https://www.lemans.org', 'FR', 'Marc Márquez, Ducati Desmosedici GP26, 2026'),
(8, 2055, 58485, 48.97870260, 2.52039490, 'Carole', 'https://www.circuit-carole.com', 'FR', 'Lucas Mahias, Yamaha R9, 2025'),
(9, 3600, 107245, 48.75790860, 3.28118830, 'La Ferté-Gaucher', 'https://www.circuitslfg.fr', 'FR', 'TBD'),
(10, 2500, 72293, 44.14215870, 4.06829650, 'Alès', 'http://www.pole-mecanique.fr', 'FR', 'Johann Zarco, Ducati Panigale V4S, 2023'),
(11, 1476, 51680, 47.22308507, 4.55575485, 'Pouilly-en-Auxois', 'https://circuit-auxois-sud.fr', 'FR', 'Julien Ballais, Triumph 765 RS, 2021'),
(12, 3750, 99760, 48.32100355, 6.07927493, 'Mirecourt', 'https://www.circuitdemirecourt.fr', 'FR', 'TBD'),
(13, 4226, 90031, 43.96371950, 12.68609206, 'Misano', 'https://www.misanocircuit.com', 'IT', 'Francesco Bagnaia, Ducati Desmosedici GP24, 2024'),
(14, 4653, 97226, 37.23170730, -8.62997651, 'Portimão', 'https://autodromodoalgarve.com', 'PT', 'Marc Márquez, Honda RC213V, 2023'),
(15, 4657, 98190, 41.56996242, 2.25820663, 'Barcelone', 'https://www.circuitcat.com/fr', 'ES', 'Aleix Espargaró, Aprilia RS-GP24, 2024');

INSERT INTO `ccteam`.`event` (`id`, `title`, `description`, `start_date`, `end_date`, `track_id`, `organizer_id`, `price`, `created_on`, `created_by`, `modified_on`, `modified_by`) VALUES
(1, 'Roulage Dijon', 'Roulage à Dijon-Prenois avec ActivBike', '2018-07-12 00:00:00', '2018-07-12 00:00:00', 2, 5, 189, '2018-06-01 09:35:07', 1, NULL, NULL),
(2, 'Roulage Vaison piste', 'Vaison avec Goret team comme d''habitude', '2018-08-02 00:00:00', '2018-08-02 00:00:00', 5, 5, 90, '2018-02-08 14:30:29', 1, NULL, NULL),
(3, 'Journée du club à Bresse', 'Journée du Club sur le circuit de Bresse. Tous les participants se verront rembourser 70€ (par chéque) pris sur le compte du club.', '2018-08-28 00:00:00', '2018-08-28 00:00:00', 1, 7, 125, '2018-04-20 17:14:27', 1, NULL, NULL),
(4, 'Week-end à Alès', '', '2019-10-25 00:00:00', '2019-10-26 00:00:00', 1, 5, 256, '2019-04-20 11:01:47', 1, NULL, NULL),
(5, 'Roulage Magny-cours', '2 jours à Magny-cours avec TP55', '2019-08-28 00:00:00', '2019-08-29 00:00:00', 1, 10, 340, '2020-05-13 22:43:26', 1, NULL, NULL),
(6, 'Roulage Bresse', 'Premier roulage de l''année à Bresse avec ActivBike', '2020-05-09 00:00:00', '2019-05-09 00:00:00', 1, 5, 140, '2020-05-22 13:20:45', 1, NULL, NULL),
(7, 'Roulage La Ferté Gaucher', 'Roulage libre à Bresse avec Moto France Racing', '2019-04-21 00:00:00', '2019-04-21 00:00:00', 9, 11, 90, '2019-03-01 12:00:00', 1, NULL, NULL),
(8, 'Roulage Carole', 'Roulage libre à Carole avec CAM Racing Team', '2019-04-22 00:00:00', '2019-04-22 00:00:00', 8, 13, 109, '2019-03-01 12:00:00', 1, NULL, NULL),
(9, 'Roulage Dijon-Prenois', 'Roulage libre à Dijon avec ActivBike', '2019-05-08 00:00:00', '2019-05-08 00:00:00', 2, 5, 195, '2019-03-01 12:00:00', 1, NULL, NULL),
(10, 'Roulage Bourbonnais', 'Roulage libre au Bourbonnais avec le moto club Fleur de Lys', '2019-06-15 00:00:00', '2019-06-15 00:00:00', 4, 14, 90, '2019-03-01 12:00:00', 1, NULL, NULL),
(11, 'Roulage Magny-cours', 'Roulage libre à Magny-cours avec Box23', '2019-06-16 00:00:00', '2019-06-16 00:00:00', 3, 15, 205, '2019-03-01 12:00:00', 1, NULL, NULL),
(12, 'Roulage Bresse', 'Journée du Club sur le circuit de Bresse. Tous les participants se verront rembourser 70€ (par chéque) pris sur le compte du club.', '2019-07-06 00:00:00', '2019-07-06 00:00:00', 1, 5, 140, '2019-03-01 12:00:00', 1, NULL, NULL),
(13, 'Roulage Le Mans', 'Roulage libre au Mans avec H2S', '2019-07-22 00:00:00', '2019-07-23 00:00:00', 7, 16, 340, '2019-03-01 12:00:00', 1, NULL, NULL),
(14, 'Roulage Alès', 'Roulage libre à Alès avec ActivBike', '2019-10-27 00:00:00', '2019-10-28 00:00:00', 10, 5, 254, '2019-03-01 12:00:00', 1, NULL, NULL);

INSERT INTO `ccteam`.`event_member` (`id`, `event_id`, `member_id`, `bike_id`, `created_on`) VALUES
(1, 3, 2, NULL, '2018-06-01 09:35:07'),
(2, 1, 1, 1, '2018-02-08 14:30:29'),
(3, 3, 1, 2, '2018-04-20 17:14:27'),
(4, 2, 1, NULL, '2017-11-18 10:42:55');

INSERT INTO `ccteam`.`liked_news` (`id`, `news_id`, `member_id`, `created_on`) VALUES
(1, 1, 2, '2018-06-01 09:35:07'),
(2, 1, 1, '2018-02-08 14:30:29'),
(3, 3, 1, '2018-04-20 17:14:27'),
(4, 4, 8, '2017-11-18 10:42:55');