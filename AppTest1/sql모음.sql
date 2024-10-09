CREATE TABLE TB_TODO(

TODO_NO NUMBER PRIMARY KEY,

TODO_TITLE VARCHAR2(100) NOT NULL,

TODO_CONTENT VARCHAR2(300) NOT NULL

);
CREATE SEQUENCE SEQ_TODONO;

-- 샘플 데이터 삽입

INSERT INTO TB_TODO VALUES(SEQ_TODONO.NEXTVAL, '청소하기', '내 방 청소해야해');

INSERT INTO TB_TODO VALUES(SEQ_TODONO.NEXTVAL, '코딩하기', '스프링은 어려워~');

INSERT INTO TB_TODO VALUES(SEQ_TODONO.NEXTVAL, '운동하기', '오늘은 하체 하는 날');

COMMIT;
SELECT EXTRACT(YEAR FROM TO_DATE('2023-07-06'))
FROM DUAL;