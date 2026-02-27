CREATE TABLE User (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL,
    password VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE
);

CREATE TABLE Workshop (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(100) NOT NULL,
    description TEXT,
    date DATE NOT NULL
);

CREATE TABLE Registration (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    workshop_id BIGINT NOT NULL,
    registration_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES User(id),
    FOREIGN KEY (workshop_id) REFERENCES Workshop(id)
);

CREATE TABLE ReservationModel (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    type VARCHAR(255) NOT NULL
);

CREATE TABLE Photo (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    filename VARCHAR(255) NOT NULL,
    artiste_id BIGINT,
    FOREIGN KEY (artiste_id) REFERENCES ArtisteModel(id)
);

CREATE TABLE EquipeContent (
    id BIGINT PRIMARY KEY,
    presentationText TEXT,
    bannerImageUrl VARCHAR(255)
);

CREATE TABLE ProgrammationModel (
    id BIGINT AUTO_INCREMENT PRIMARY KEY
);

CREATE TABLE PartenaireContent (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    titre VARCHAR(255),
    texte TEXT
);

CREATE TABLE Partenaire (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nom VARCHAR(255),
    siteWeb VARCHAR(255),
    description TEXT,
    logo VARCHAR(255),
    typePartenaire VARCHAR(255)
);

CREATE TABLE MurPeint (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    artiste_id BIGINT,
    nom VARCHAR(255),
    latitude DOUBLE,
    longitude DOUBLE,
    description TEXT,
    photoUrl VARCHAR(255),
    FOREIGN KEY (artiste_id) REFERENCES ArtisteModel(id)
);

CREATE TABLE EquipeModel (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    fullName VARCHAR(255) NOT NULL
);

CREATE TABLE CardPresentation (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(255),
    description TEXT,
    image VARCHAR(255),
    link VARCHAR(255)
);

CREATE TABLE ArtisteModel (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255),
    discipline VARCHAR(255)
);