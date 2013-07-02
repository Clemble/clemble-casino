
    create table GAME_CONSTRUCTION (
        CONSTRUCTION_ID bigint generated by default as identity,
        REQUEST varchar(4096),
        RESPONSES varchar(4096),
        SESSION_ID bigint,
        state varchar(255),
        primary key (CONSTRUCTION_ID)
    );

    create table GAME_SESSION (
        SESSION_ID bigint generated by default as identity,
        NUM_MADE_MOVES integer,
        SESSION_STATE integer,
        GAME_STATE varchar(4096),
        GAME_NAME varchar(255),
        SPECIFICATION_NAME varchar(255),
        primary key (SESSION_ID)
    );

    create table GAME_SESSION_MOVES (
        SESSION_ID bigint not null,
        GAME_MOVE varchar(512),
        MOVE_ID integer,
        MOVE_TIME bigint
    );

    create table GAME_SESSION_PLAYERS (
        SESSION_ID bigint not null,
        players bigint,
        PLAYERS_ORDER integer not null,
        primary key (SESSION_ID, PLAYERS_ORDER)
    );

    create table GAME_SPECIFICATION (
        GAME_NAME varchar(255) not null,
        SPECIFICATION_NAME varchar(255) not null,
        PRICE integer,
        CURRENCY varchar(255),
        GIVE_UP varchar(255),
        MOVE_TIME_LIMIT integer,
        MOVE_TIME_PUNISHMENT varchar(255),
        PLAYER_NUMBER varchar(255),
        PRIVACY_RULE varchar(255),
        TOTAL_TIME_LIMIT integer,
        TOTAL_TIME_PUNISHMENT varchar(255),
        primary key (GAME_NAME, SPECIFICATION_NAME)
    );

    create table GAME_TABLE (
        TABLE_ID bigint generated by default as identity,
        SESSION_ID bigint,
        GAME_NAME varchar(255),
        SPECIFICATION_NAME varchar(255),
        primary key (TABLE_ID)
    );

    create table GAME_TABLE_PLAYERS (
        TABLE_ID bigint not null,
        players bigint,
        PLAYERS_ORDER integer not null,
        primary key (TABLE_ID, PLAYERS_ORDER)
    );

    alter table GAME_SESSION 
        add constraint FK8E8AE729E7296D7F 
        foreign key (GAME_NAME, SPECIFICATION_NAME) 
        references GAME_SPECIFICATION;

    alter table GAME_SESSION_MOVES 
        add constraint FKDDDFCE0C575A4F22 
        foreign key (SESSION_ID) 
        references GAME_SESSION;

    alter table GAME_SESSION_PLAYERS 
        add constraint FK7D99A27C575A4F22 
        foreign key (SESSION_ID) 
        references GAME_SESSION;

    alter table GAME_TABLE 
        add constraint FK8D61AD21575A4F22 
        foreign key (SESSION_ID) 
        references GAME_SESSION;

    alter table GAME_TABLE 
        add constraint FK8D61AD21E7296D7F 
        foreign key (GAME_NAME, SPECIFICATION_NAME) 
        references GAME_SPECIFICATION;

    alter table GAME_TABLE_PLAYERS 
        add constraint FK9226F07474164E22 
        foreign key (TABLE_ID) 
        references GAME_TABLE;
