/*계정 생성하기(관리자계정으로 접속해서)*/
ALTER SESSION SET "_ORACLE_SCRIPT" = TRUE;

CREATE USER SPRING_LIK IDENTIFIED BY SPRING1234;

GRANT CONNECT, RESOURCE TO SPRING_LIK; 

ALTER USER SPRING_LIK
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
AND MEMBER_EMAIL =










