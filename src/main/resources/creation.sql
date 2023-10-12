CREATE TABLE `%prefix%balances` (
    `uuid` VARCHAR(36) NOT NULL,
    `shops` INT NOT NULL DEFAULT 0,
    `pay` INT NOT NULL DEFAULT 0,
    `votes` INT NOT NULL DEFAULT 0,
    `gems` INT NOT NULL DEFAULT 0,
    `others` INT NOT NULL DEFAULT 0,
    `date` DATE NOT NULL DEFAULT CURRENT_DATE(),
    PRIMARY KEY (`uuid`)
);