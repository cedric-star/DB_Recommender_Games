PRAGMA foreign_keys=OFF;
BEGIN TRANSACTION;
CREATE TABLE zzz_user (
  userid INTEGER PRIMARY KEY AUTOINCREMENT,
  name TEXT NOT NULL,
  password TEXT NOT NULL
);
INSERT INTO zzz_user VALUES(1,'alice','pass123');
INSERT INTO zzz_user VALUES(2,'bob','hunter2');
INSERT INTO zzz_user VALUES(3,'charlie','123456');
CREATE TABLE zzz_produkt (
  produktid INTEGER PRIMARY KEY AUTOINCREMENT,
  produktname TEXT NOT NULL
);
INSERT INTO zzz_produkt VALUES(1,'Minecraft');
INSERT INTO zzz_produkt VALUES(2,'The Witcher 3');
INSERT INTO zzz_produkt VALUES(3,'Stardew Valley');
CREATE TABLE bewertung (
  userid INTEGER,
  produktid INTEGER,
  bewertung INTEGER,
  PRIMARY KEY (userid, produktid)
);
INSERT INTO bewertung VALUES(1,1,5);
INSERT INTO bewertung VALUES(1,2,4);
INSERT INTO bewertung VALUES(2,1,3);
INSERT INTO bewertung VALUES(2,3,5);
INSERT INTO bewertung VALUES(3,2,2);
INSERT INTO bewertung VALUES(3,3,4);
INSERT INTO sqlite_sequence VALUES('zzz_user',3);
INSERT INTO sqlite_sequence VALUES('zzz_produkt',3);
COMMIT;
