USE activitytracker;

CREATE TABLE users (
    id INT NOT NULL AUTO_INCREMENT,
    firebase_uid VARCHAR(30) NOT NULL UNIQUE,
    PRIMARY KEY (id)
);

CREATE TABLE groups_tb (
    id INT NOT NULL AUTO_INCREMENT,
    name VARCHAR(150) NOT NULL,
    parent_id INT,
    user_id INT NOT NULL,
    created_at TIMESTAMP(3) NOT NULL DEFAULT NOW(3),
    updated_at TIMESTAMP(3) NOT NULL DEFAULT NOW(3) ON UPDATE NOW(3),
    PRIMARY KEY (id),
    FOREIGN KEY (user_id) REFERENCES users(id)
);

CREATE TABLE activities (
    id INT NOT NULL AUTO_INCREMENT,
    happening TIMESTAMP(3) NOT NULL,
    group_id INT NOT NULL,
    created_at TIMESTAMP(3) NOT NULL DEFAULT NOW(3),
    updated_at TIMESTAMP(3) NOT NULL DEFAULT NOW(3) ON UPDATE NOW(3),
    is_deleted boolean DEFAULT false,
    PRIMARY KEY (id),
    FOREIGN KEY (group_id) REFERENCES groups_tb(id) ON DELETE CASCADE
);
