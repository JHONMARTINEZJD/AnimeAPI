
-- ============================
-- Datos de ejemplo para anime_api
-- ============================

-- Tabla: studios
INSERT INTO studios (country, founded_year, name) VALUES
('Japan', 1985, 'Studio Ghibli'),
('Japan', 2000, 'Ufotable'),
('South Korea', 2010, 'Studio Mir'),
('USA', 2015, 'Powerhouse Animation'),
('Japan', 1999, 'Bones');

-- Tabla: animes
INSERT INTO animes (description, episodes, rating, release_date, status, title, studio_id) VALUES
('A boy finds a magical creature.', 24, 8.7, '2001-07-20', 'FINISHED', 'Spirited Boy', 1),
('Demon slayers fight in ancient Japan.', 26, 9.1, '2019-04-06', 'ONGOING', 'Demon Fire', 2),
('A girl enters a dream world.', 13, 7.9, '2010-10-01', 'FINISHED', 'Dream Maze', 3),
('Gods battle in a modern city.', 12, 8.3, '2022-05-05', 'ONGOING', 'Urban Divinity', 4),
('Adventurers explore a fantasy world.', 50, 8.5, '2016-01-10', 'FINISHED', 'Chronicles of Ether', 5);

-- Tabla: genres
INSERT INTO genres (name) VALUES
('Action'),
('Adventure'),
('Fantasy'),
('Slice of Life'),
('Romance');

-- Tabla: anime_genres
INSERT INTO anime_genres (anime_id, genre_id) VALUES
(1, 3),
(2, 1),
(2, 2),
(3, 4),
(5, 2);

-- Tabla: characters
INSERT INTO characters (description, name, anime_id) VALUES
('A brave young warrior.', 'Kai', 2),
('A mysterious girl from another world.', 'Luna', 3),
('A wise old man.', 'Sensei', 5),
('The main hero with a tragic past.', 'Ryu', 1),
('Comic relief sidekick.', 'Moki', 4);

-- Tabla: voice_actors
INSERT INTO voice_actors (birthday, country, name) VALUES
('1980-03-15', 'Japan', 'Kana Hanazawa'),
('1975-11-30', 'USA', 'Troy Baker'),
('1990-07-09', 'Canada', 'Erica Lindbeck'),
('1988-04-20', 'Japan', 'Yuki Kaji'),
('1995-06-12', 'South Korea', 'Kim So-yeon');

-- Tabla: character_voice_actors
INSERT INTO character_voice_actors (character_id, voice_actor_id) VALUES
(1, 4),
(2, 1),
(3, 2),
(4, 3),
(5, 5);

-- Tabla: users
INSERT INTO users (created_at, email, password, username) VALUES
(NOW(), 'alice@example.com', 'hashed1', 'alice'),
(NOW(), 'bob@example.com', 'hashed2', 'bob'),
(NOW(), 'chris@example.com', 'hashed3', 'chris'),
(NOW(), 'dana@example.com', 'hashed4', 'dana'),
(NOW(), 'erik@example.com', 'hashed5', 'erik');

-- Tabla: user_roles
INSERT INTO user_roles (user_id, role_id) VALUES
(1, 1),
(2, 1),
(3, 1),
(4, 1),
(5, 1);

-- Tabla: favorites
INSERT INTO favorites (added_at, anime_id, user_id) VALUES
(NOW(), 2, 1),
(NOW(), 1, 2),
(NOW(), 3, 3),
(NOW(), 4, 4),
(NOW(), 5, 5);

-- Tabla: reviews
INSERT INTO reviews (comment, created_at, score, anime_id, user_id) VALUES
('Amazing animation and story!', NOW(), 9, 1, 1),
('Very emotional.', NOW(), 8, 2, 2),
('Could be better.', NOW(), 6, 3, 3),
('Loved the characters.', NOW(), 10, 4, 4),
('Solid action scenes.', NOW(), 8, 5, 5);

-- Tabla: watchlists
INSERT INTO watchlists (status, anime_id, user_id) VALUES
('WATCHING', 1, 1),
('PLANNED', 2, 2),
('DROPPED', 3, 3),
('COMPLETED', 4, 4),
('WATCHING', 5, 5);
