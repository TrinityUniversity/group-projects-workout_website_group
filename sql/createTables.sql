CREATE TABLE users (
	user_id SERIAL PRIMARY KEY, 
	username varchar(20) NOT NULL, 
    favorites INTEGER[],
	password varchar(200) NOT NULL
);

CREATE TABLE workouts (
    id SERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    VIDEO_URL VARCHAR(255) NOT NULL,
    sweat_level INTEGER(3) NOT NULL,
    intensity INTEGER(3) NOT NULL,
    work_length INTEGER(3) NOT NULL,
    workout_type varchar(3) NOT NULL,
    likes INTEGER NOT NULL,
    views INTEGER not null,
);

