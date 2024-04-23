/*계정 생성하기(관리자계정으로 접속해서)*/
ALTER SESSION SET "_ORACLE_SCRIPT" = TRUE;

CREATE USER SEMI_LIK IDENTIFIED BY SEMILIK1234;

GRANT CONNECT, RESOURCE TO SEMI_LIK; 

ALTER USER SEMI_LIK
DEFAULT TABLESPACE USERS
QUOTA 20M ON USERS;  --테이블 만들 공간 20메가만 줌
--계정생성 끝!

-->계정 생성 후 접속 방법(새 데이터베이스) 추가하기(왼쪽 새 데이터 베이스 연결 플러그 버튼 눌러서)
----------------------------------------------------------------------------------------------
/*SPRING계정 접속해서 테이블 만들기*/
--SQL에서 "" : 쌍따옴표 내부에 작성된 글(모양) 그대로 인식해라 -> 대/소문자가 구분된다
-->""작성 권장!
--member == MEMBER
--"member" !="MEMBER"

--영어,숫자, 키보드에 있:1바이트 // 한글, 키보드에 없는 특수문자 : 3바이트

--CHAR(10)		: 고정 길이 문자열 10바이트 -> 최대 2000바이트
--VARCHAR2(10): 가변 길이 문자열 10바이트(남는 공간 반환됨) -> 최대 4000바이트 ->요즘 이거 안쓰고 밑에거 사용
--VARCHAR2는 UTF-8 이용

--NVARCHAR2(10) : 가변 길이 문자열 10글자( !=10바이트)(한글이든 영어든 숫자든 다 10글자 저장 가능) ->최대 4000바이트
--NVARCHAR2는 유니코드 이용
--CLOB : 가변 길이 문자열(최대 4GB)

/*MEMBER 테이블 생성하기*/
CREATE TABLE "MEMBER"(
	--쌍따옴표 써서, 소문자로 쓰면 인식안되고 대문자로만 써야 함!
	"MEMBER_NO" 			NUMBER CONSTRAINT "MEMBER_PK" PRIMARY KEY, --제약조건 이름 지정
	"MEMBER_EMAIL" 		NVARCHAR2(50) NOT NULL,
	"MEMBER_PW" 			NVARCHAR2(100) NOT NULL,--암호화 하면 더 길게 변함
	"MEMBER_NICKNAME" NVARCHAR2(10) NOT NULL, --아이디가 아닌 닉네임 노출시키기
	"MEMBER_TEL" 			CHAR(11) NOT NULL, --01012341234
	"MEMBER_ADDRESS" 	NVARCHAR2(150), --주소 검색 API 쓸거임
	"PROFILE_IMG" 		VARCHAR2(300), --프로필 이미지->그 파일이 어디있는지 파일 경로를 저장(영어로 돼있어서 VARCHAR)
	"ENROLL_DATE" 		DATE DEFAULT SYSDATE NOT NULL,--가입일
	"MEMBER_DEL_FL" 	CHAR(1) DEFAULT 'N'--회원 삭제 플래그(탈퇴 여부 기록)
										CHECK("MEMBER_DEL_FL" IN ('Y', 'N')),
	"AUTHORITY" 			NUMBER DEFAULT 1 --권한
										CHECK("AUTHORITY" IN (1,2)) --1번이 일반 회원, 2번이 관리자
);
--회원 번호 시퀀스 만들기
CREATE SEQUENCE SEQ_MEMBER_NO NOCACHE;
--sql도 깃허브에 올라가서 위의 계정 생성부분도 지우고 올리는 게 좋다
--샘플 회원 데이터 하나 삽입하기
INSERT INTO "MEMBER"
VALUES(SEQ_MEMBER_NO.NEXTVAL, 
				'member01@kh.or.kr',
				'$2a$10$vonrRgV/jcdE6q1H8YJ31e1Xl1CYlydO034MMBYdMvo/S7GaSS796',
				'샘플1',
				'01012341234',
				NULL,
				NULL,
				DEFAULT,
				DEFAULT,
				DEFAULT
);
COMMIT;

--로그인 하는 SQL 작성
--로그인 조건 : BCrypt 암호화 사용 중이어서
--					DB에서 비밀번호 비교 불가능!
--					->그래서 비밀번호를 SELECT문으로 조회한다!
--			이메일을 이용해서 이메일이 일치하고 탈퇴를 안 한 회원 조건만 추가하기(조건으로 비밀번호를 쓸 수는 없으니까!)
SELECT MEMBER_NO , MEMBER_EMAIL , MEMBER_NICKNAME , MEMBER_PW , MEMBER_TEL , MEMBER_ADDRESS ,
PROFILE_IMG , AUTHORITY , 
TO_CHAR(ENROLL_DATE,'YYYY"년" MM"월" DD"일" HH24"시" MI"분" SS"초"' ) ENROLL_DATE
--TO_CHAR : 지정한 패턴의 문자열로 바뀜
--회원번호가 PK여서 모든 것 조회 시 필요
--비밀번호 가져와서 비교해야 해서 비밀번호도 필요
FROM "MEMBER"
WHERE MEMBER_EMAIL =?
AND MEMBER_DEL_FL ='N';



SELECT * FROM "MEMBER";

--이메일 중복 검사(탈퇴 안 한 회원 중 같은 이메일이 있는 지 조회)
--같은 게 있는지 조회
SELECT COUNT(*) FROM "MEMBER" WHERE MEMBER_DEL_FL  = 'N'
AND MEMBER_EMAIL =;

DELETE FROM "MEMBER"
WHERE MEMBER_EMAIL ='ink0216@naver.com';
COMMIT;
SELECT * FROM "MEMBER";

--인증번호가 잘 안돼서 DB이용하는 인증 방법 시도!
/*이메일, 인증키 저장하는 테이블 생성하기*/
CREATE TABLE "TB_AUTH_KEY"(
	"KEY_NO" NUMBER PRIMARY KEY,
	"EMAIL" NVARCHAR2(50) NOT NULL,
	"AUTH_KEY" CHAR(6) NOT NULL,
	"CREATE_TIME" DATE DEFAULT SYSDATE NOT NULL  --인증번호가 만들어진 시간(이거 이용하면 JS 말고 DB에서도 컨트롤 가능)
);
COMMENT ON COLUMN "TB_AUTH_KEY"."KEY_NO" IS '인증키 구분 번호(시퀀스)';
COMMENT ON COLUMN "TB_AUTH_KEY"."EMAIL" IS '인증 이메일';
COMMENT ON COLUMN "TB_AUTH_KEY"."AUTH_KEY" IS '인증 번호';
COMMENT ON COLUMN "TB_AUTH_KEY"."CREATE_TIME" IS '인증 번호 생성 시간';

CREATE SEQUENCE SEQ_KEY_NO NOCACHE; --인증키 구분 번호 시퀀스

--인증하기 버튼 누르면 인증 이메일과 인증번호를 TB_AUTH_KEY 테이블에 넣어둘거다
SELECT * FROM "TB_AUTH_KEY";

SELECT COUNT(*) FROM "TB_AUTH_KEY"
WHERE EMAIL = #{가입하려는 이메일 입력값}
AND AUTH_KEY = #{위 이메일로 보낸 인증번호};


--------------------------------------------------------------------------
--파일 업로드 테스트용 테이블
CREATE TABLE "UPLOAD_FILE"(
	FILE_NO NUMBER PRIMARY KEY,
	FILE_PATH VARCHAR2(500) NOT NULL,
	FILE_ORIGINAL_NAME VARCHAR2(300) NOT NULL,
	FILE_RENAME VARCHAR2(100) NOT NULL,
	FILE_UPLOAD_DATE DATE DEFAULT SYSDATE,
	MEMBER_NO NUMBER REFERENCES "MEMBER" --FK 제약조건 설정
	--부모테이블의 PK역할하는 MEMBER_NO 참조(누가 올렸는지 기록)
);
COMMENT ON COLUMN "UPLOAD_FILE".FILE_NO IS '파일 번호(PK)';
COMMENT ON COLUMN "UPLOAD_FILE".FILE_PATH IS '클라이언트 요청 경로(webPath)';
COMMENT ON COLUMN "UPLOAD_FILE".FILE_ORIGINAL_NAME IS '파일 원본명';
COMMENT ON COLUMN "UPLOAD_FILE".FILE_RENAME IS '변경된 파일명'; 
--서버의 한 폴더에 같은이름,같은확장자 파일 2개이상이 업로드 돼서 여러 명이 같은 이름,확장자로 업로드 시 나중 파일로 덮어씌워져서 
--항상 이름이 바뀌어서 저장되도록 함
COMMENT ON COLUMN "UPLOAD_FILE".FILE_UPLOAD_DATE IS '업로드 날짜';
COMMENT ON COLUMN "UPLOAD_FILE".MEMBER_NO IS 'MEMBER 테이블의 PK(MEMBER_NO)참조';

--파일 번호에 사용할 시퀀스
CREATE SEQUENCE SEQ_FILE_NO NOCACHE;

SELECT * ;

--DB에는 Date타입인데 String타입으로 바꿔야 한다 TO_CHAR 이용해서 문자열로 바꾸기
--파일 목록 조회
SELECT FILE_NO , FILE_PATH , FILE_ORIGINAL_NAME , FILE_RENAME , 
	TO_CHAR(FILE_UPLOAD_DATE, 'YYYY-MM-DD HH24:MI:SS') FILE_UPLOAD_DATE , --컬럼 별칭(DTO에 담기게 하려고)
	MEMBER_NICKNAME 
FROM UPLOAD_FILE
JOIN "MEMBER" USING(MEMBER_NO)
ORDER BY FILE_NO DESC; --그럼 최근 게 가장 위에 나옴
---------------------------------------------------------------------------------------
COMMENT ON COLUMN "MEMBER"."MEMBER_NO" IS '회원 번호(PK)';

COMMENT ON COLUMN "MEMBER"."MEMBER_EMAIL" IS '회원 이메일(ID 역할)';

COMMENT ON COLUMN "MEMBER"."MEMBER_PW" IS '회원 비밀번호(암호화)';

COMMENT ON COLUMN "MEMBER"."MEMBER_NICKNAME" IS '회원 닉네임';

COMMENT ON COLUMN "MEMBER"."MEMBER_TEL" IS '회원 전화번호';

COMMENT ON COLUMN "MEMBER"."MEMBER_ADDRESS" IS '회원 주소';

COMMENT ON COLUMN "MEMBER"."PROFILE_IMG" IS '프로필 이미지';

COMMENT ON COLUMN "MEMBER"."ENROLL_DATE" IS '회원 가입일';

COMMENT ON COLUMN "MEMBER"."MEMBER_DEL_FL" IS '회원 탈퇴 여부(Y,N)';

COMMENT ON COLUMN "MEMBER"."AUTHORITY" IS '권한(1: 일반 // 2: 관리자)';



COMMENT ON COLUMN "UPLOAD_FILE"."FILE_NO" IS '파일 번호(PK)';

COMMENT ON COLUMN "UPLOAD_FILE"."FILE_PATH" IS '파일 요청 경로(웹 주소)';

COMMENT ON COLUMN "UPLOAD_FILE"."FILE_ORIGINAL_NAME" IS '파일 원본명';

COMMENT ON COLUMN "UPLOAD_FILE"."FILE_RENAME" IS '파일 변경명';

COMMENT ON COLUMN "UPLOAD_FILE"."FILE_UPLOAD_DATE" IS '업로드 날짜';

COMMENT ON COLUMN "UPLOAD_FILE"."MEMBER_NO" IS '업로드한 회원 번호';
/*게시판 테이블 생성*/
CREATE TABLE "BOARD" (
	"BOARD_NO"	NUMBER		NOT NULL,
	"BOARD_TITLE"	NVARCHAR2(100)		NOT NULL,
	"BOARD_CONTENT"	VARCHAR2(4000)		NOT NULL,
	"BOARD_WRITE_DATE"	DATE	DEFAULT SYSDATE	NOT NULL,
	"BOARD_UPDATE_DATE"	DATE		NULL,
	"READ_COUNT"	NUMBER	DEFAULT 0	NOT NULL,
	"BOARD_DEL_FL"	CHAR(1)	DEFAULT 'N'	NOT NULL,
	"BOARD_CODE"	NUMBER		NOT NULL,
	"MEMBER_NO"	NUMBER		NOT NULL
);

COMMENT ON COLUMN "BOARD"."BOARD_NO" IS '게시글 번호(PK)';

COMMENT ON COLUMN "BOARD"."BOARD_TITLE" IS '게시글 제목';

COMMENT ON COLUMN "BOARD"."BOARD_CONTENT" IS '게시글 내용';

COMMENT ON COLUMN "BOARD"."BOARD_WRITE_DATE" IS '게시글 작성일';

COMMENT ON COLUMN "BOARD"."BOARD_UPDATE_DATE" IS '게시글 마지막 수정일';

COMMENT ON COLUMN "BOARD"."READ_COUNT" IS '조회수';

COMMENT ON COLUMN "BOARD"."BOARD_DEL_FL" IS '게시글 삭제 여부(Y,N)';

COMMENT ON COLUMN "BOARD"."BOARD_CODE" IS '게시판 종류 코드 번호';

COMMENT ON COLUMN "BOARD"."MEMBER_NO" IS '작성한 회원 번호(FK)';

CREATE TABLE "BOARD_TYPE" (
	"BOARD_CODE"	NUMBER		NOT NULL,
	"BOARD_NAME"	NVARCHAR2(20)		NOT NULL
);

COMMENT ON COLUMN "BOARD_TYPE"."BOARD_CODE" IS '게시판 종류 코드 번호';

COMMENT ON COLUMN "BOARD_TYPE"."BOARD_NAME" IS '게시판명';

CREATE TABLE "BOARD_LIKE" (
	"MEMBER_NO"	NUMBER		NOT NULL,
	"BOARD_NO"	NUMBER		NOT NULL
);

COMMENT ON COLUMN "BOARD_LIKE"."MEMBER_NO" IS '회원 번호(PK)';

COMMENT ON COLUMN "BOARD_LIKE"."BOARD_NO" IS '게시글 번호(PK)';

CREATE TABLE "BOARD_IMG" (
	"IMG_NO"	NUMBER		NOT NULL,
	"IMG_PATH"	VARCHAR2(200)		NOT NULL,
	"IMG_ORIGINAL_NAME"	NVARCHAR2(50)		NOT NULL,
	"IMG_RENAME"	NVARCHAR2(50)		NOT NULL,
	"IMG_ORDER"	NUMBER		NULL,
	"BOARD_NO"	NUMBER		NOT NULL
);

COMMENT ON COLUMN "BOARD_IMG"."IMG_NO" IS '이미지 번호(PK)';

COMMENT ON COLUMN "BOARD_IMG"."IMG_PATH" IS '이미지 요청 경로';

COMMENT ON COLUMN "BOARD_IMG"."IMG_ORIGINAL_NAME" IS '이미지 원본명';

COMMENT ON COLUMN "BOARD_IMG"."IMG_RENAME" IS '이미지 변경명';

COMMENT ON COLUMN "BOARD_IMG"."IMG_ORDER" IS '이미지 순서';

COMMENT ON COLUMN "BOARD_IMG"."BOARD_NO" IS '게시글 번호(PK)';

CREATE TABLE "COMMENT" (
	"COMMENT_NO"	NUMBER		NOT NULL,
	"COMMENT_CONTENT"	VARCHAR2(4000)		NOT NULL,
	"COMMENT_WRITE_DATE"	DATE	DEFAULT SYSDATE	NOT NULL,
	"COMMENT_DEL_FL"	CHAR(1)	DEFAULT 'N'	NOT NULL,
	"BOARD_NO"	NUMBER		NOT NULL,
	"MEMBER_NO"	NUMBER		NOT NULL,
	"PARENT_COMMENT_NO"	NUMBER		NOT NULL
);

COMMENT ON COLUMN "COMMENT"."COMMENT_NO" IS '댓글 번호(PK)';

COMMENT ON COLUMN "COMMENT"."COMMENT_CONTENT" IS '댓글 내용';

COMMENT ON COLUMN "COMMENT"."COMMENT_WRITE_DATE" IS '댓글 작성일';

COMMENT ON COLUMN "COMMENT"."COMMENT_DEL_FL" IS '댓글 삭제 여부(Y,N)';

COMMENT ON COLUMN "COMMENT"."BOARD_NO" IS '게시글 번호(PK)';

COMMENT ON COLUMN "COMMENT"."MEMBER_NO" IS '회원 번호(PK)';

COMMENT ON COLUMN "COMMENT"."PARENT_COMMENT_NO" IS '부모 댓글 번호';
---------------------------------------------------------------
--PK
ALTER TABLE "MEMBER" ADD CONSTRAINT "PK_MEMBER" PRIMARY KEY (
	"MEMBER_NO"
);

ALTER TABLE "UPLOAD_FILE" ADD CONSTRAINT "PK_UPLOAD_FILE" PRIMARY KEY (
	"FILE_NO"
);

ALTER TABLE "BOARD" ADD CONSTRAINT "PK_BOARD" PRIMARY KEY (
	"BOARD_NO"
);

ALTER TABLE "BOARD_TYPE" ADD CONSTRAINT "PK_BOARD_TYPE" PRIMARY KEY (
	"BOARD_CODE"
);

ALTER TABLE "BOARD_LIKE" ADD CONSTRAINT "PK_BOARD_LIKE" PRIMARY KEY (
	"MEMBER_NO",
	"BOARD_NO"
);

ALTER TABLE "BOARD_IMG" ADD CONSTRAINT "PK_BOARD_IMG" PRIMARY KEY (
	"IMG_NO"
);

ALTER TABLE "COMMENT" ADD CONSTRAINT "PK_COMMENT" PRIMARY KEY (
	"COMMENT_NO"
);
---여기서부터 FK
ALTER TABLE "UPLOAD_FILE" ADD CONSTRAINT "FK_MEMBER_TO_UPLOAD_FILE_1" FOREIGN KEY (
	"MEMBER_NO"
)
REFERENCES "MEMBER" (
	"MEMBER_NO"
);

ALTER TABLE "BOARD" ADD CONSTRAINT "FK_BOARD_TYPE_TO_BOARD_1" FOREIGN KEY (
	"BOARD_CODE"
)
REFERENCES "BOARD_TYPE" (
	"BOARD_CODE"
);

ALTER TABLE "BOARD" ADD CONSTRAINT "FK_MEMBER_TO_BOARD_1" FOREIGN KEY (
	"MEMBER_NO"
)
REFERENCES "MEMBER" (
	"MEMBER_NO"
);

ALTER TABLE "BOARD_LIKE" ADD CONSTRAINT "FK_MEMBER_TO_BOARD_LIKE_1" FOREIGN KEY (
	"MEMBER_NO"
)
REFERENCES "MEMBER" (
	"MEMBER_NO"
);

ALTER TABLE "BOARD_LIKE" ADD CONSTRAINT "FK_BOARD_TO_BOARD_LIKE_1" FOREIGN KEY (
	"BOARD_NO"
)
REFERENCES "BOARD" (
	"BOARD_NO"
);

ALTER TABLE "BOARD_IMG" ADD CONSTRAINT "FK_BOARD_TO_BOARD_IMG_1" FOREIGN KEY (
	"BOARD_NO"
)
REFERENCES "BOARD" (
	"BOARD_NO"
);

ALTER TABLE "COMMENT" ADD CONSTRAINT "FK_BOARD_TO_COMMENT_1" FOREIGN KEY (
	"BOARD_NO"
)
REFERENCES "BOARD" (
	"BOARD_NO"
);

ALTER TABLE "COMMENT" ADD CONSTRAINT "FK_MEMBER_TO_COMMENT_1" FOREIGN KEY (
	"MEMBER_NO"
)
REFERENCES "MEMBER" (
	"MEMBER_NO"
);

ALTER TABLE "COMMENT" ADD CONSTRAINT "FK_COMMENT_TO_COMMENT_1" FOREIGN KEY (
	"PARENT_COMMENT_NO"
)
REFERENCES "COMMENT" (
	"COMMENT_NO"
);
--CHECK 제약 조건(여기서는 ERDCLOUD에서 못 만듦 -> 여기서 함)
--게시글 삭제 여부
ALTER TABLE "BOARD" ADD
CONSTRAINT "BOARD_DEL_CHECK" 
CHECK("BOARD_DEL_FL" IN ('Y','N'));

--댓글 삭제 여부
ALTER TABLE "COMMENT" ADD --코멘트는 무조건 쌍따옴표로 써야 함(예약어가 존재해서)
CONSTRAINT "COMMENT_DEL_CHECK" 
CHECK("COMMENT_DEL_FL" IN ('Y','N'));
------------------------------------------------------------------
/*게시판 종류 추가(BOARD_TYPE)*/
/*게시판 번호 안겹치는게 좋으니까 시퀀스 이용!*/
CREATE SEQUENCE SEQ_BOARD_CODE NOCACHE;

INSERT INTO "BOARD_TYPE"
VALUES (SEQ_BOARD_CODE.NEXTVAL, '공지 게시판');
INSERT INTO "BOARD_TYPE"
VALUES (SEQ_BOARD_CODE.NEXTVAL, '정보 게시판');
INSERT INTO "BOARD_TYPE"
VALUES (SEQ_BOARD_CODE.NEXTVAL, '자유 게시판');
--DML 수행 후에 반드시 COMMIT해야한다!!!!
--DML 수행 여러 개 하면 트랜잭션에 다 임시로 저장돼있다가 롤백하면 다 지워지고, 커밋하면 DB에 들어감!(저장됨)
--TCL : COMMIT + ROLLBACK
COMMIT;

--읽어오는 인터셉터 클래스 만들기(스프링)

SELECT * FROM "BOARD_TYPE"
		ORDER BY BOARD_CODE;

	--인터셉터 이용해서 게시글 목록 조회할 건데
	--그러려면 목록 있어야 하니까 샘플 데이터 넣기
	/*게시판(BOARD) 테이블 샘플 데이터 삽입하기*/
	/*1000 ~5000 개 넣기*/
	/*PL(절차적 언어)/SQL 이용!*/
	/*SQL안에서 C언어 같은 코드 쓸 수 있다*/
	/*1부터 2000까지 알아서 1씩 증가하면서
	 * 내부의 INSERT 코드 수행*/
	
	/*게시글 번호 시퀀스 만들기*/
	CREATE SEQUENCE SEQ_BOARD_NO NOCACHE;
	SELECT * FROM "MEMBER";

	BEGIN
		FOR I IN 1..2000 LOOP
			INSERT INTO "BOARD" 
			VALUES (SEQ_BOARD_NO.NEXTVAL,
							SEQ_BOARD_NO.CURRVAL ||'번째 게시글',
						SEQ_BOARD_NO.CURRVAL ||'번째 게시글 내용입니다.',
						DEFAULT,DEFAULT,DEFAULT,DEFAULT,
						CEIL(DBMS_RANDOM.VALUE(0,3)),
						1
					);
		END LOOP;
		
	END; --이거는 ALT X로 실행해야 된다!!
	-- PL/SQL은 별도의 처리 안 하면 무조건 -1이 결과값으로 나온다
	
--DEFAULT 값이 지정 안돼있으면 NULL이 들어감
-- || : 연결 연산자
-- DBMS_RANDOM.VALUE(0,3) : 0.0 이상 3.0 미만의 난수 발생
-- CEIL : 올림
-- 난수를 다 1의자리까지 올림 -> 난수는 랜덤으로 나오지만, CEIL 처리된 난수는 1,2,3밖에 안나오게 된다
-- CEIL(DBMS_RANDOM.VALUE(0,3)) : 1,2,3 중 하나

	--게시판 종류별 샘플 데이터 삽입 확인
	SELECT COUNT(*) 
	FROM "BOARD"
	GROUP BY BOARD_CODE
	ORDER BY BOARD_CODE;
---------------------------------------------------
	--댓글 테이블 COMMENT
	--댓글 번호 시퀀스 생성
	CREATE SEQUENCE SEQ_COMMENT_NO NOCACHE;

	--게시글 목록 조회 할 때 
	-- 번호 제목[댓글 개수] 작성일 작성자
	--댓글 샘플 데이터 넣기
	BEGIN
		FOR I IN 1..2000 LOOP
			INSERT INTO "COMMENT" 
			VALUES(
				SEQ_COMMENT_NO.NEXTVAL,
				SEQ_COMMENT_NO.CURRVAL ||'번째 댓글 입니다.',
				DEFAULT,DEFAULT,
				CEIL(DBMS_RANDOM.VALUE(0,2000)),
				1,
				NULL
			);
		END LOOP;
	END;
COMMIT;
	--게시글 번호 최소값, 최대값 조회하기
SELECT MIN(BOARD_NO), MAX(BOARD_NO) FROM "BOARD";
--부모 댓글 번호 NULL 가능으로 만들기
ALTER TABLE "COMMENT"
MODIFY PARENT_COMMENT_NO NUMBER NULL; --숫자타입으로 할 거고 NULL허용할거다
--댓글 삽입 확인
SELECT COUNT(*) FROM "COMMENT"
GROUP BY BOARD_NO 
ORDER BY BOARD_NO;

--특정 게시판의 삭제되지 않은 게시글 목록 조회하는 SQL
--단, 최신 글이 제일 위에 존재해야하고
--몇 초/분/시간 전 또는 YYYY-MM-DD 형식으로 작성일 조회하겠다
-- + 댓글 개수
-- + 좋아요 개수
-- 번호 제목[댓글개수] 작성자닉네임 작성일 조회수 좋아요개수

--상관 서브쿼리 이용해서 댓글 개수 조회하기
--앞에서 조회한 BOARD_NO에 따라서 서브쿼리 수행
--서브쿼리는 원래 서브쿼리가 먼저 해석되고
--근데 상관 서브쿼리는 반대다!
--밖의 메인쿼리의 한 행이 해석*조회된 후
--1행 조회 결과를 이용해서 서브쿼리를 수행하는 형식 
-- (메인쿼리를 모두 조회할 때까지 반복)
--한 행 조회하는 서브쿼리를 2000번 반복한다 (행이 2000개여서)

--시간은 선택함수 이용 CASE문
SELECT BOARD_NO, BOARD_TITLE , MEMBER_NICKNAME,READ_COUNT,
(SELECT COUNT(*)
	FROM "COMMENT" C
	WHERE C.BOARD_NO = B.BOARD_NO) COMMENT_COUNT,
	(SELECT COUNT(*)
	FROM BOARD_LIKE L
	WHERE L.BOARD_NO=B.BOARD_NO) LIKE_COUNT,
	CASE --이 네 가지 중 하나를 선택해서 한 컬럼이 나온다
		WHEN SYSDATE-BOARD_WRITE_DATE < 1/24/60 
		THEN FLOOR((SYSDATE-BOARD_WRITE_DATE)*24*60*60)||'초 전'
		
		WHEN SYSDATE-BOARD_WRITE_DATE < 1/24 
		THEN FLOOR((SYSDATE-BOARD_WRITE_DATE)*24*60)||'분 전'
		
		WHEN SYSDATE-BOARD_WRITE_DATE < 1 
		THEN FLOOR((SYSDATE-BOARD_WRITE_DATE)*24)||'시간 전'
		
		ELSE TO_CHAR(BOARD_WRITE_DATE, 'YYYY-MM-DD') --아무 케이스에도 안 들어가는 경우
	END BOARD_WRITE_DATE --별칭
	
FROM "BOARD" B
JOIN "MEMBER" USING(MEMBER_NO)
WHERE BOARD_DEL_FL ='N'
AND BOARD_CODE=1
ORDER BY BOARD_NO DESC; --이걸 다 담을 DTO 만들기
--각각의 게시글에 따른 댓글 개수가 나옴

--특정 게시글의 댓글 개수 조회
SELECT COUNT(*)
FROM "COMMENT"
WHERE BOARD_NO = 30;

--현재 시간 -하루 전 조회 ->정수 == 일 단위
--날짜랑 날짜를 뺄 수 있다!
SELECT 
	(SYSDATE-TO_DATE('2024-04-10 12:14:30', 'YYYY-MM-DD HH24:MI:SS'))*60*60*24
FROM DUAL;

--지정된 게시판에서 삭제되지 않은 게시글 수를 조회하기
SELECT COUNT(*)
FROM "BOARD"
WHERE BOARD_DEL_FL='N'
AND BOARD_CODE=3;

--------------------------------------------------------------------
--게시판 이미지 테이블
/*BOARD_IMG 테이블 용 시퀀스 생성*/
CREATE SEQUENCE SEQ_IMG_NO NOCACHE;

/*BOARD_IMG 테이블에 샘플 데이터 삽입 5개 하기*/
INSERT INTO "BOARD_IMG"
VALUES(SEQ_IMG_NO.NEXTVAL,
	'/images/board/', --이미지 경로에 넣을 것을 써놯다			
	'원본1.jpg',
	'test1.gif',
	0,--이미지 순서
	1998--게시글 번호
);
INSERT INTO "BOARD_IMG"
VALUES(SEQ_IMG_NO.NEXTVAL,
	'/images/board/', --이미지 경로에 넣을 것을 써놯다			
	'원본2.jpg',
	'test2.gif',
	1,--이미지 순서
	1998--게시글 번호
);
INSERT INTO "BOARD_IMG"
VALUES(SEQ_IMG_NO.NEXTVAL,
	'/images/board/', --이미지 경로에 넣을 것을 써놯다			
	'원본3.jpg',
	'test3.jpg',
	2,--이미지 순서
	1998--게시글 번호
);
INSERT INTO "BOARD_IMG"
VALUES(SEQ_IMG_NO.NEXTVAL,
	'/images/board/', --이미지 경로에 넣을 것을 써놯다			
	'원본4.jpg',
	'test4.webp',
	3,--이미지 순서
	1998--게시글 번호
);
INSERT INTO "BOARD_IMG"
VALUES(SEQ_IMG_NO.NEXTVAL,
	'/images/board/', --이미지 경로에 넣을 것을 써놯다			
	'원본5.jpg',
	'test5.webp',
	4,--이미지 순서
	1998--게시글 번호
);
COMMIT;
---------------------------------------
/*게시글 상세 조회하는 SQL*/
--제목, 내용, 작성일, 수정일, 조회수, 좋아요수, 닉네임, 프로필이미지
SELECT BOARD_NO, BOARD_TITLE , BOARD_CONTENT ,BOARD_CODE ,READ_COUNT ,
	MEMBER_NO, MEMBER_NICKNAME, PROFILE_IMG,
	TO_CHAR(BOARD_WRITE_DATE,'YYYY"년" MM"월" DD"일" HH24:MI:SS') BOARD_WRITE_DATE,
	--HH는 12시 표기법, HH24는 24시 표기법
	TO_CHAR(BOARD_UPDATE_DATE,'YYYY"년" MM"월" DD"일" HH24:MI:SS') BOARD_UPDATE_DATE,
	--수정일
	(SELECT COUNT(*)FROM "BOARD_LIKE"
		WHERE BOARD_NO=1998) LIKE_COUNT,
		(SELECT IMG_PATH||IMG_RENAME
			FROM "BOARD_IMG" 
			WHERE BOARD_NO=1998
			AND IMG_ORDER=0) THUMBNAIL,
			(SELECT COUNT(*) FROM "BOARD_LIKE"
				WHERE MEMBER_NO =NULL
				AND BOARD_NO =1998) LIKE_CHECK --좋아요 눌렀는 지 확인할거다
		--썸네일 이미지 조회
			--IMG_ORDER 가 0인 게 썸네일 할거다
			--상세조회할 때 첨부된 이미지 전체랑 댓글도 전체 조회돼야 한다
			--SELECT 세 번 하면 됨
FROM "BOARD"
JOIN "MEMBER" USING(MEMBER_NO)
WHERE BOARD_DEL_FL ='N'
AND BOARD_CODE=1
AND BOARD_NO =1998;
--삭제가 안된 몇 번 게시판의 몇 번 글
--상관 쿼리로도 할 수 있따
-------------------------------------------------
/*상세조회되는 게시글의 첨부된 모든 이미지 조회하기*/
SELECT * FROM "BOARD_IMG"
WHERE BOARD_NO =1998
ORDER BY IMG_ORDER; --이미지 순서에 따라서
--이 결과를 저장할 DTO 만들기

/*상세조회되는 게시글의 모든 댓글 조회*/
/*계층형 쿼리
 * START WITH
 * 계층형에서 NULL인 애들이 1레벨이야
 * CONNECT BY PRIOR 
 * COMMENT_NO랑 PARENT_COMMENT_NO가 같은 것끼리 연결해줄거야
 * 정렬은 같은 레벨끼리 정렬하는데 COMMENT_NO 오름차순으로 정렬할거야
 * */

SELECT LEVEL, C.* 
FROM
		(SELECT COMMENT_NO, COMMENT_CONTENT,
		  TO_CHAR(COMMENT_WRITE_DATE, 'YYYY"년" MM"월" DD"일" HH24"시" MI"분" SS"초"') COMMENT_WRITE_DATE,
		    BOARD_NO, MEMBER_NO, MEMBER_NICKNAME, PROFILE_IMG, PARENT_COMMENT_NO, COMMENT_DEL_FL
		FROM "COMMENT"
		JOIN MEMBER USING(MEMBER_NO)
		WHERE BOARD_NO = 1998) C --서브쿼리의 결과가 테이블 됨
WHERE COMMENT_DEL_FL = 'N'
	OR 0 != (SELECT COUNT(*) FROM "COMMENT" SUB
					WHERE SUB.PARENT_COMMENT_NO = C.COMMENT_NO
					AND COMMENT_DEL_FL = 'N')
	START WITH PARENT_COMMENT_NO IS NULL
	CONNECT BY PRIOR COMMENT_NO = PARENT_COMMENT_NO
	ORDER SIBLINGS BY COMMENT_NO;
--이 결과를 저장할 DTO 만들기

--상세조회하면서 SELECT 3회 할거다
--DTO없는 이미지랑, 댓글에 대한 DTO 만들고 결과 다 가져와서 화면에 꾸미면 된다!!

-----------------------------------------------
/*좋아요 테이블(BOARD_LIKE) 샘플 데이터 추가*/
INSERT INTO "BOARD_LIKE" 
VALUES (1,1998); --1번 회원이 1998번 글에 좋아요를 클릭함 을 의미!!!->좋아요 취소 시 이 행을 DELETE하면 됨
COMMIT;

SELECT * FROM "BOARD_LIKE";

--좋아요 눌렀는 지 여부 확인 (1 : 누름 // 2: 안누름)
SELECT COUNT(*) FROM "BOARD_LIKE"
WHERE MEMBER_NO =1
AND BOARD_NO =1998;

/*여러 행을 한번에 INSERT하는 방법!
 * -> INSERT + SUBQUERY 이용!!!
 * INSERT ALL 하면 한 테이블에 여러 개 넣을 수 있다 근데 이번엔 사용 X
 * INSERT VALUES 하면 한 행씩만 삽입 가능
 * 
 * BOARD_IMG 컬럼 6개
 * UNION(합집합) 이용하면 합친 것을 한 번에 INSERT할 수 있다
 *  -> 근데 문제 발생
 * 		IMG_NO를 삽입하기 위해서 SEQ_IMG_NO.NEXTVAL를 삽입해야하는데
 * 		서브쿼리 안에는 시퀀스를 못 넣는다!!
 * INSERT INTO "BOARD_IMG"
(
SELECT SEQ_IMG_NO.NEXTVAL, '경로1', '원본1', '변경1', 1, 1984 FROM DUAL
UNION
SELECT SEQ_IMG_NO.NEXTVAL, '경로2', '원본2', '변경2', 2, 1984 FROM DUAL

); 이렇게 하면 오류난다
 * [해결방법]
 * 시퀀스로 번호 생성하는 부분을 별도의 함수로 분리한 후 호출하면 문제 없음
 * 
 * 함수 : TO_CHAR()처럼 뒤에 ()붙는 것
 * */
INSERT INTO "BOARD_IMG"
(
SELECT NEXT_IMG_NO(), '경로1', '원본1', '변경1', 1, 1984 FROM DUAL
UNION
SELECT NEXT_IMG_NO(), '경로2', '원본2', '변경2', 2, 1984 FROM DUAL
UNION
SELECT NEXT_IMG_NO(), '경로2', '원본2', '변경2', 2, 1984 FROM DUAL
);
SELECT * FROM BOARD_IMG;
ROLLBACK;
--컬럼명이 BOARD_IMG랑 SELECT문의 컬럼명이랑 달라도 자료형만 같으면 잘 삽입된다!!
--SEQ_IMG_NO 시퀀스의 다음 값을 반환하는 함수 생성하기


CREATE OR REPLACE FUNCTION NEXT_IMG_NO

--반환형 숫자가 반환될거다
RETURN NUMBER 

--사용할 변수를 미리 선언
IS IMG_NO NUMBER;

BEGIN
	SELECT SEQ_IMG_NO.NEXTVAL --10번 생성 시
	INTO IMG_NO --생성된 10번을 여기다 넣겠다
	FROM DUAL;

	RETURN IMG_NO; --이 값을 리턴하겠다
END;
;
SELECT NEXT_IMG_NO() FROM DUAL;





SELECT SEQ_IMG_NO.NEXTVAL FROM DUAL;

SELECT * FROM "BOARD"
WHERE BOARD_DEL_FL ='Y';


INSERT INTO "COMMENT"	VALUES(
			SEQ_COMMENT_NO.NEXTVAL,
			'부모 댓글 1',
			DEFAULT, DEFAULT,
			2000,
			9,
			NULL
);


INSERT INTO "COMMENT"	VALUES(
			SEQ_COMMENT_NO.NEXTVAL,
			'부모 댓글 2',
			DEFAULT, DEFAULT,
			2000,
			9,
			NULL
);

INSERT INTO "COMMENT"	VALUES(
			SEQ_COMMENT_NO.NEXTVAL,
			'부모 댓글 3',
			DEFAULT, DEFAULT,
			2000,
			9,
			NULL
);
COMMIT;
SELECT COMMENT_NO ,PARENT_COMMENT_NO ,COMMENT_CONTENT 
FROM "COMMENT" WHERE BOARD_NO=2000;
--------------------
SELECT LEVEL,COMMENT_NO ,PARENT_COMMENT_NO ,COMMENT_CONTENT 
FROM "COMMENT" WHERE BOARD_NO=2000
 --LEVEL : 계층형 쿼리에서만 쓸 수 있음!
/*계층형 쿼리 댓글도 계층형으로 나오게 하기*/
START WITH PARENT_COMMENT_NO IS NULL --PARENT_NO가 NULL인 행이 최상위 부모(LV.1)이다
CONNECT BY PRIOR COMMENT_NO=PARENT_COMMENT_NO--부모의 COMMENT_NO와 같은 PARENT_COMMENT_NO를 가진 행을 자식으로 연결
ORDER SIBLINGS BY COMMENT_NO; --형제(같은 레벨 요소들)간의 정렬 순서를 COMMENT_NO 오름차순
--계층형쿼리 완성!


--부모 댓글 1의 자식 댓글 만들기
INSERT INTO "COMMENT"	VALUES(
			SEQ_COMMENT_NO.NEXTVAL,
			'부모 1의 자식 1 ',
			DEFAULT, DEFAULT,
			2000,
			9,
			2003
);
--부모 댓글 2의 자식 댓글 만들기
INSERT INTO "COMMENT"	VALUES(
			SEQ_COMMENT_NO.NEXTVAL,
			'부모 2의 자식 1 ',
			DEFAULT, DEFAULT,
			2000,
			9,
			2001
);

--부모 댓글 2의 자식1의 자식 댓글 만들기
INSERT INTO "COMMENT"	VALUES(
			SEQ_COMMENT_NO.NEXTVAL,
			'부모 2의 자식 1의 자식 1 ',
			DEFAULT, DEFAULT,
			2000,
			9,
			2006
); 



--부모 댓글 1의 자식 댓글 만들기
INSERT INTO "COMMENT"	VALUES(
			SEQ_COMMENT_NO.NEXTVAL,
			'부모 1의 자식 2 ',
			DEFAULT, DEFAULT,
			2000,
			9,
			2003
);
COMMIT;
-------------------------------------------------------------------------------------------------
--240419시험
DROP TABLE TB_USER;
--시퀀스도 없애기
COMMIT; 

-- 사용자 테이블

CREATE TABLE TB_USER(

USER_NO NUMBER PRIMARY KEY,

USER_ID VARCHAR2(50) UNIQUE NOT NULL,

USER_NAME VARCHAR2(50) NOT NULL,

USER_AGE NUMBER NOT NULL

);
SELECT * FROM TB_BOARD;

-- 사용자 시퀀스

CREATE SEQUENCE SEQ_UNO;

-- 샘플 데이터

INSERT INTO TB_USER VALUES(SEQ_UNO.NEXTVAL, 'gd_hong', '홍길동', 20);

INSERT INTO TB_USER VALUES(SEQ_UNO.NEXTVAL, 'sh_han', '한소희', 28);

INSERT INTO TB_USER VALUES(SEQ_UNO.NEXTVAL, 'jm_park', '지민', 27);

INSERT INTO TB_USER VALUES(SEQ_UNO.NEXTVAL, 'jm123', '지민', 25);

-- 게시판 테이블

CREATE TABLE TB_BOARD(

BOARD_NO NUMBER PRIMARY KEY,

BOARD_TITLE VARCHAR2(50) NOT NULL,

BOARD_CONTENT VARCHAR2(2000) NOT NULL,

BOARD_DATE DATE DEFAULT SYSDATE,

BOARD_READCOUNT NUMBER DEFAULT 0,

USER_NO NUMBER REFERENCES TB_USER

);

-- 게시판 시퀀스

CREATE SEQUENCE SEQ_BNO;

-- 샘플 데이터

INSERT INTO TB_BOARD VALUES(SEQ_BNO.NEXTVAL, '처음입니다', '만나서 반가워요', SYSDATE, DEFAULT, 1);

INSERT INTO TB_BOARD VALUES(SEQ_BNO.NEXTVAL, '신입입니다', '잘 부탁드립니다!', SYSDATE, DEFAULT, 2);

INSERT INTO TB_BOARD VALUES(SEQ_BNO.NEXTVAL, '날씨가 좋네요', '즐거운 한 주 보내세요', SYSDATE, DEFAULT, 3);

INSERT INTO TB_BOARD VALUES(SEQ_BNO.NEXTVAL, '저도 처음이에요', '좋은 추억 쌓아요', SYSDATE, DEFAULT, 4);

INSERT INTO TB_BOARD VALUES(SEQ_BNO.NEXTVAL, '오늘 처음인 분이 많네요', '다들 환영합니다', SYSDATE, DEFAULT, 3);

COMMIT;

-------------------------------------------------------------------------------------------------
--게시글 검색하던 SQL



-- 게시글 검색

SELECT BOARD_NO, BOARD_TITLE, MEMBER_NICKNAME, READ_COUNT,
	(SELECT
	COUNT(*)
	FROM "COMMENT" C
	WHERE C.BOARD_NO = B.BOARD_NO  
	AND COMMENT_DEL_FL = 'N') COMMENT_COUNT,

	(SELECT COUNT(*)
	FROM "BOARD_LIKE" L
	WHERE L.BOARD_NO = B.BOARD_NO) LIKE_COUNT,
	 
	 CASE
		 WHEN SYSDATE - BOARD_WRITE_DATE < 1 / 24 / 60 
		 THEN FLOOR((SYSDATE - BOARD_WRITE_DATE) * 24 * 60 * 60)  || '초 전'
		 
		 WHEN SYSDATE - BOARD_WRITE_DATE < 1 / 24 
		 THEN FLOOR((SYSDATE - BOARD_WRITE_DATE)* 24 * 60) || '분 전'
		 
		 WHEN SYSDATE - BOARD_WRITE_DATE < 1
		 THEN FLOOR((SYSDATE - BOARD_WRITE_DATE) * 24) || '시간 전'
		 
		 ELSE TO_CHAR(BOARD_WRITE_DATE, 'YYYY-MM-DD')
	 	
	 END BOARD_WRITE_DATE

FROM "BOARD" B
JOIN "MEMBER" USING(MEMBER_NO)
WHERE BOARD_DEL_FL = 'N'
AND BOARD_CODE = 1

-- 제목에 '10' 이 포함된 게시글 조회
-- AND BOARD_TITLE LIKE '%10%'

-- 내용에 '10' 이 포함된 게시글 조회
-- AND BOARD_CONTENT LIKE '%10%'

-- 제목 또는 내용에 '10' 이 포함된 게시글 조회
AND (BOARD_TITLE LIKE '%10%' 
     OR  BOARD_CONTENT LIKE '%10%')

--작성자 닉네임에 '샘플'이 포함된 게시글 조회
AND MEMBER_NICKNAME LIKE '%샘플%'
ORDER BY BOARD_NO DESC;
--상황에 따라 SQL이 달라지게 하는 동적 SQL 사용
--목록조회에서 한 코드를 재활용하기




























































--------------------------------------------------------------------
/*책 관리 프로젝트(연습용)*/
CREATE TABLE "BOOK" (
	"BOOK_NO"	NUMBER		NOT NULL,
	"BOOK_TITLE"	NVARCHAR2(50)		NOT NULL,
	"BOOK_WRITER"	NVARCHAR2(20)		NOT NULL,
	"BOOK_PRICE"	NUMBER		NOT NULL,
	"REG_DATE"	DATE	DEFAULT SYSDATE	NOT NULL
);

COMMENT ON COLUMN "BOOK"."BOOK_NO" IS '책 번호';

COMMENT ON COLUMN "BOOK"."BOOK_TITLE" IS '책 제목';

COMMENT ON COLUMN "BOOK"."BOOK_WRITER" IS '글쓴이';

COMMENT ON COLUMN "BOOK"."BOOK_PRICE" IS '책 가격';

COMMENT ON COLUMN "BOOK"."REG_DATE" IS '등록일';

ALTER TABLE "BOOK" ADD CONSTRAINT "PK_BOOK" PRIMARY KEY (
	"BOOK_NO"
);
--도서 번호 시퀀스 만들기
CREATE SEQUENCE SEQ_BOOK_NO NOCACHE;

--UPLOAD_FILE FK 제약조건 삭제하고 다시 설정하기 (ON DELETE SET NULL로)
ALTER TABLE UPLOAD_FILE 
DROP CONSTRAINT SYS_C007575;
ALTER TABLE UPLOAD_FILE 
ADD CONSTRAINT "FK_SETNULL" 
FOREIGN KEY(MEMBER_NO)
REFERENCES "MEMBER"(MEMBER_NO)
ON DELETE SET NULL;
/*ALTER TABLE EMP01
ADD CONSTRAINT EMP01_DEPTNO_FK
FOREIGN KEY(DEPTNO) REFERENCES DEPT(DEPTNO); 
[출처] [Oracle] 제약 조건 추가하기 (ADD ALTER, ADD MODIFY)|작성자 개구리*/
--------------------------------------------------
--20240409시험
-- TB_USER 테이블 생성 및 SEQ_UNO 시퀀스 생성

CREATE TABLE TB_USER(

USER_NO NUMBER PRIMARY KEY,

USER_ID VARCHAR2(50) UNIQUE NOT NULL,

USER_NAME VARCHAR2(50) NOT NULL,

USER_AGE NUMBER NOT NULL

);
SELECT * FROM TB_USER;

CREATE SEQUENCE SEQ_UNO;
DROP SEQUENCE SEQ_UNO;

-- 샘플 데이터 삽입

INSERT INTO TB_USER VALUES(SEQ_UNO.NEXTVAL, 'gd_hong', '홍길동', 20);

INSERT INTO TB_USER VALUES(SEQ_UNO.NEXTVAL, 'sh_han', '한소희', 28);

INSERT INTO TB_USER VALUES(SEQ_UNO.NEXTVAL, 'jm_park', '지민', 27);

INSERT INTO TB_USER VALUES(SEQ_UNO.NEXTVAL, 'jm123', '지민', 25);

COMMIT;

------------------------------------------------
--두 번째 시험(동일날짜)
-- DB 구성

--[테이블]

CREATE TABLE CUSTOMER(

CUSTOMER_NO NUMBER PRIMARY KEY,

CUSTOMER_NAME VARCHAR2(60) NOT NULL,

CUSTOMER_TEL VARCHAR2(30) NOT NULL,

CUSTOMER_ADDRESS VARCHAR2(200) NOT NULL

);

--[시퀀스]

CREATE SEQUENCE SEQ_CUSTOMER_NO NOCACHE;
SELECT * FROM CUSTOMER;














