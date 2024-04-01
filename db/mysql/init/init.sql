CREATE TABLE IF NOT EXISTS game (
    gameId INT PRIMARY KEY,
    turn VARCHAR(5) NOT NULL
);

CREATE TABLE IF NOT EXISTS board (
    gameId INT,
    file CHAR(5) NOT NULL,
    `rank` CHAR(5) NOT NULL,
    type VARCHAR(10) NOT NULL,
    team VARCHAR(5) NOT NULL
);
