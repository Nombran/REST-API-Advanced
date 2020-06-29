INSERT INTO tag (id, name)
VALUES (1,'tag one'),(2,'tag two'),(3,'tag three'),(4,'fourth tag'),(5,'fifth tag'),(6,'tag six');

INSERT INTO certificate (name, description, price, creation_date, modification_date, duration)
VALUES ('certificate one', 'description', 12.5, '2020-06-09 00:00', null, 5),
       ('certificate two', 'some text', 8.5, '2018-07-19 12:30', null, 9),
       ('certificate three', 'third row', 2.5, '2021-09-17 10:10', null, 18),
       ('certificate four', 'fourth row', 3.5, '2021-09-17 10:10', null, 17);

INSERT INTO certificate_tag (certificate_id, tag_id)
VALUES
       (1,1),
       (1,2),
       (1,3),
       (2,3),
       (2,4),
       (3,5);
