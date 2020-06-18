#mysql워크벤치 연결
#connection name : 아무거나
#hostname : projectf.cqwdlyjz7pdl.ap-northeast-2.rds.amazonaws.com
#username : admin
#password : 12341234

#서버 시간
#select @@global.time_zone, @@session.time_zone;

#데이터베이스 생성
#drop database if exists projectdb;
#create database projectdb;
use projectdb;
set foreign_key_checks = 0; 	-- 연습용으로 자주 삭제를 위해 0으로 설정

#1. 기본 테이블
-- 회원테이블
drop table if exists user;
create table user (
	user_id varchar(20) not null,				-- 아이디
	user_name varchar(20) not null,				-- 이름
	user_password varchar(20) not null,			-- 비밀번호
	email varchar(50) not null unique,			-- 이메일
	phoneNum varchar(12) not null,				-- 핸드폰 번호
	registration_date date,						-- 등록일
    position enum('강사', '학생'),				-- 신분
	primary key(user_id)
);
-- 회원테이블 트리거
drop table if EXISTS backup_user;
create table backup_user(
	user_id 		varchar(20),
    user_name		varchar(20),
    user_password	VARCHAR(20),
    email			VARCHAR(50),
    phoneNum		VARCHAR(12),
    registration_date date,
    position		enum('강사','학생'),
    modtype			enum('수정','삭제'),
    moddate			datetime,
    moduser			varchar(20)
);
DROP TRIGGER IF EXISTS backup_user_delete_trg;
delimiter $$
create TRIGGER bakcup_user_delete_trg
 after delete
    on user
   for each row
begin
	insert into backup_user values (
		old.user_id,
        old.user_name,
        old.user_password,
        old.email,
        old.phoneNum,
        old.registration_date,
        old.position,
        '삭제',
        curdate(),
        current_user()
    );
end $$
delimiter ;

-- 학생 뷰 테이블
drop view if exists student;
create view student
as
	select user_id, user_name, user_password, email, phoneNum, registration_date
      from user
	 where position = '학생';
select * from student;

-- 강사 뷰 테이블
drop view if exists teacher;
create view teacher
as
	select user_id, user_name, user_password, email, phoneNum, registration_date
      from user
	 where position = '강사';
select * from teacher;

-- 강의테이블
drop table if exists class;
create table class(
	class_no int primary key auto_increment,	-- 강의번호
	class_name varchar(20) not null,			-- 강의명
    teacher_id varchar(20) not null,			-- 강사ID
    class_desc text,							-- 강의설명
    start_date date not null,					-- 강의 시작일
    end_date date not null,						-- 강의 종강일
    limitstudent int not null,					-- 수강신청 가능 인원
    foreign key(teacher_id) references user(user_id)
);
alter table class auto_increment=1001;		-- 강의ID : 1001부터 시작

-- 강의테이블 트리거
drop table if EXISTS backup_class;
create table backup_class(
	class_no 		int,
	class_name 		varchar(20),
    teacher_name 	varchar(20),
    teacher_id 		varchar(20),
    class_desc 		text,
    start_date 		date,
    end_date 		date,
    limitstudent 	int,
	modtype			enum('수정','삭제'),
    moddate			datetime,
    moduser			varchar(20)
);

drop TRIGGER IF EXISTS backup_class_delete_trg;
delimiter $$
create TRIGGER backup_class_delete_trg
 AFTER delete
    on backup_class
   for each row
begin
	insert into backup_class values (
		old.class_no,
        old.class_name,
        old.teacher_name,
        old.teacher_id,
        old.class_desc,
        old.start_date,
        old.end_date,
        old.limitstudent,
        '삭제',
        curdate(),
        current_user()
    );
end $$
delimiter ;

-- 과제테이블
drop table if exists task;
create table task(
	task_no int primary key auto_increment,		-- 과제번호
	task_name varchar(20),						-- 과제명
	task_desc text,								-- 과제 설명
	reg_date date,								-- 과제 등록일
	expire_date date,							-- 과제 종료일
	attachedfile longblob,						-- 과제 참고 파일
    attachedfile_name varchar(100),				-- 과제 참고 파일명
	class_no int,								-- 강의 no
	perfect_score int,							-- 만점 
	foreign key (class_no) references class(class_no) on delete cascade
    
);
alter table task auto_increment=10001;		-- 과제ID : 10001부터 시작

#2. 결합테이블
-- 수강신청테이블
-- student테이블의 student_id(FK)와 class 테이블의 class_no(FK)를 PK로 지정
drop table if exists request_class;
create table request_class(
	student_id varchar(20) not null,			-- 학생 ID
    class_no int not null,						-- 강의 no
    constraint fk_stu_id						-- student테이블의 student_id를 FK로 지정
        foreign key(student_id) references user(user_id),
	constraint fk_cls_id						-- class테이블의 class_no를 FK로 지정
		foreign key(class_no) references class(class_no) on delete cascade,
	constraint pk_stuid_clsid					-- PK로 지정
		primary key(student_id, class_no)
);
select * from request_class;

-- 과제제출테이블
-- task테이블의 task_no(FK)와 student테이블의 student_id(FK)를 PK로 지정
drop table if exists submission_task;
create table submission_task(
	class_no int not null,						-- 강의 no
    task_no int not null,						-- 과제 no
    student_id varchar(20) not null,			-- 학생 ID
    tasksubmit varchar(1) default 'N',			-- 제출여부(기본값 N)
    tasksubmit_date datetime,					-- 제출날짜
    taskscore int default -1,					-- 과제점수(기본값 -1), 선생님이 점수매기기전에는 -1로 설정(프로그램에서는 0으로 변경)
    taskquestion text,							-- 질문사항
    taskanswer text,							-- 답변
    taskfile longblob,							-- 제출파일
    taskfile_name varchar(100),					-- 제출파일명
    -- 제약조건
    constraint fk_classno_no					-- task테이블의 task_no를 FK로 지정
		foreign key(class_no) references class(class_no) on delete cascade,
    constraint fk_task_no						-- task테이블의 task_no를 FK로 지정
		foreign key(task_no) references task(task_no) on delete cascade,
    constraint fk_stu_id_submit					-- student테이블의 student_id를 FK로 지정
        foreign key(student_id) references user(user_id),
	constraint pk_taskno_stuid					-- PK 지정
		primary key(class_no,task_no, student_id),			
	constraint score_range						-- taskscore의 입력 범위 지정
		check (taskscore >= -1)
);
drop trigger if exists cnt_reuquest_class;
delimiter $$
create trigger cnt_reuquest_class
before insert
    on request_class
   for each row
begin
	declare current_cnt int;				-- 현재인원
    declare max_cnt int;					-- 최대인원
    
	select count(*) into current_cnt		-- 현재인원 조회
      from request_class
	 where class_no = new.class_no;
     
     select limitstudent into max_cnt		-- 최대인원 조회
      from class
	 where class_no = new.class_no;
     if(current_cnt >= max_cnt) then
		SIGNAL sqlstate '45000';
     end if;
end $$
delimiter ;

desc class;
desc request_class;
-- 제약조건 검색
select * from information_schema.table_constraints where table_name = 'submission_task';

-- 조회용 뷰테이블
-- 클래스의 학생 목록 뷰
drop view if EXISTS class_student;
create or replace view class_student as 
		select c.class_no, c.student_id, s.user_name
		  from student s
		  join request_class c
		    on s.user_id = c.student_id
		 order by c.class_no;

-- 사용 예 (1001 클래스의 학생목록)
select * from class_student where class_no=1001;

 -- 클래스의 과제 제출현황 뷰
drop view if EXISTS class_task_status;
create or replace view class_task_status as
    SELECT 
		t.class_no,
		t.task_no,
		t.task_name,
		cs.student_id,
		cs.user_name as 'student_name',
		st.tasksubmit,
		st.tasksubmit_date,
		st.taskscore,	
		st.taskquestion,
		st.taskanswer,
		st.taskfile,
		st.taskfile_name,
		t.perfect_score,
        t.expire_Date,
        t.attachedfile,
        t.attachedfile_name,
        t.task_desc
	FROM class_student cs
	JOIN task t
      ON t.class_no = cs.class_no
	LEFT JOIN submission_task st
      ON (t.task_no = st.task_no
	 AND cs.student_id = st.student_id)
ORDER BY cs.user_name;

-- 사용 예 ) 10028과제의 과제 제출 현황
select * from class_task_status where task_no = 10028;

-- 사용 예 (1001 클래스의 학생목록)
select * from class_student where class_no=1001;

-- 과제제출현황 뷰
drop view if EXISTS class_submission_status;
create view class_submission_status as
SELECT 
	cts.*, c.class_name, c.start_date, c.end_date
  from class_task_status cts
  join class c
    on cts.class_no = c.class_no;
    
#샘플 입력
-- 강사
insert into user values ('123','홍길동','123','123@naver.com','0101234123','2020-01-12','강사');
insert into user values ('a123','이순신','123','a123@naver.com','0101234123','2019-01-12','강사');
insert into user values ('b123','강감찬','123','b123@naver.com','0101234123','2028-01-12','강사');
insert into user values ('c123','고길동','123','c123@naver.com','0101234123','2020-01-01','강사');
insert into user values ('d123','김우진','123','d123@naver.com','0101234123','2020-02-12','강사');
insert into user values ('e123','정동환','123','e123@naver.com','0101234123','2019-06-12','강사');
insert into user values ('f123','강민호','123','f123@naver.com','0101234123','2019-07-12','강사');
insert into user values ('g123','임창정','123','g123@naver.com','0101234123','2019-07-12','강사');
insert into user values ('h123','손창일','123','h123@naver.com','0101234123','2019-08-12','강사');
insert into user values ('i123','심재현','123','i123@naver.com','0101234123','2020-01-13','강사');
insert into user values ('j123','권지용','123','j123@naver.com','0101234123','2020-01-12','강사');
insert into user values ('k123','임성빈','123','k123@naver.com','0101234123','2020-01-12','강사');
insert into user values ('l123','최성호','123','l123@naver.com','0101234123','2020-01-12','강사');
insert into user values ('m123','김한빈','123','m123@naver.com','0101234123','2020-01-12','강사');
insert into user values ('n123','김현중','123','n123@naver.com','0101234123','2020-01-12','강사');

-- 학생
insert into user values ('321', '차우미', '321', '123@naver.com', '01012341234', '2020-01-12', '학생');
insert into user values ('a321', '엄지선', '321', 'a321@naver.com', '01012341234', '2019-01-12', '학생');
insert into user values ('b321', '김지은', '321', 'b321@naver.com', '01012341234', '2010-01-12', '학생');
insert into user values ('c321', '강정미', '321', 'c321@naver.com', '01012341234', '2020-01-01', '학생');
insert into user values ('d321', '주미진', '321', 'd321@naver.com', '01012341234', '2020-02-12', '학생');
insert into user values ('e321', '강은비', '321', 'e321@naver.com', '01012341234', '2019-06-12', '학생');
insert into user values ('f321', '안칠현', '321', 'f321@naver.com', '01012341234', '2019-07-12', '학생');
insert into user values ('g321', '김유환', '321', 'g321@naver.com', '01012341234', '2019-07-12', '학생');
insert into user values ('h321', '권율', '321', 'h321@naver.com', '01012341234', '2019-08-12', '학생');
insert into user values ('i321', '조규현', '321', 'i321@naver.com', '01012341234', '2020-01-13', '학생');
insert into user values ('j321', '백보람', '321', 'j321@naver.com', '01012341234', '2020-01-12', '학생');
insert into user values ('k321', '김현동', '321', 'k321@naver.com', '01012341234', '2020-01-12', '학생');
insert into user values ('l321', '김태우', '321', 'l321@naver.com', '01012341234', '2020-01-12', '학생');
insert into user values ('m321', '기은세', '321', 'm321@naver.com', '01012341234', '2020-01-12', '학생');
insert into user values ('n321', '이익준', '321', 'n321@naver.com', '01012341234', '2020-01-12', '학생');
insert into user values ('o321',  '가나다',  '321', 'o321@naver.com',  '01012341234',  '2019-01-01', '학생');
insert into user values ('p321',  '나다가',  '321', 'p321@naver.com',  '01012341234',  '2019-01-01', '학생');
insert into user values ('q321',  '다가나',  '321', 'q321@naver.com',  '01012341234',  '2019-01-01', '학생');
insert into user values ('r321',  '가나다',  '321', 'r321@naver.com',  '01012341234',  '2019-01-01', '학생');
insert into user values ('s321',  '나다가',  '321', 's321@naver.com',  '01012341234',  '2019-01-01', '학생');
insert into user values ('t321',  '다가나',  '321', 't321@naver.com',  '01012341234',  '2019-01-01', '학생');
insert into user values ('u321',  '가나다',  '321', 'u321@naver.com',  '01012341234',  '2019-01-01', '학생');
insert into user values ('v321',  '나다가',  '321', 'v321@naver.com',  '01012341234',  '2019-01-01', '학생');
insert into user values ('w321',  '다가나',  '321', 'w321@naver.com',  '01012341234',  '2019-01-01', '학생');
insert into user values ('x321',  '가나다',  '321', 'x321@naver.com',  '01012341234',  '2019-01-01', '학생');
insert into user values ('y321',  '나다가',  '321', 'y321@naver.com',  '01012341234',  '2019-01-01', '학생');
insert into user values ('z321',  '다가나',  '321', 'z321@naver.com',  '01012341234',  '2019-01-01', '학생');
insert into user values ('aa321',  '가나다',  '321', 'aa321@naver.com',  '01012341234',  '2019-01-01', '학생');
insert into user values ('bb321',  '나다가',  '321', 'bb321@naver.com',  '01012341234',  '2019-01-01', '학생');
insert into user values ('cc321',  '다가나',  '321', 'cc321@naver.com',  '01012341234',  '2019-01-01', '학생');
insert into user values ('dd321',  '가나다',  '321', 'dd321@naver.com',  '01012341234',  '2019-01-01', '학생');
insert into user values ('ee321',  '나다가',  '321', 'ee321@naver.com',  '01012341234',  '2019-01-01', '학생');
insert into user values ('ff321',  '다가나',  '321', 'ff321@naver.com',  '01012341234',  '2019-01-01', '학생');
insert into user values ('gg321',  '가나다',  '321', 'gg321@naver.com',  '01012341234',  '2019-01-01', '학생');
insert into user values ('hh321',  '나다가',  '321', 'hh321@naver.com',  '01012341234',  '2019-01-01', '학생');
insert into user values ('ii321',  '다가나',  '321', 'ii321@naver.com',  '01012341234',  '2019-01-01', '학생');
insert into user values ('jj321',  '가나다',  '321', 'jj321@naver.com',  '01012341234',  '2019-01-01', '학생');
insert into user values ('kk321',  '나다가',  '321', 'kk321@naver.com',  '01012341234',  '2019-01-01', '학생');
insert into user values ('ll321',  '다가나',  '321', 'll321@naver.com',  '01012341234',  '2019-01-01', '학생');
insert into user values ('mm321',  '가나다',  '321', 'mm321@naver.com',  '01012341234',  '2019-01-01', '학생');
insert into user values ('nn321',  '나다가',  '321', 'nn321@naver.com',  '01012341234',  '2019-01-01', '학생');
insert into user values ('oo321',  '다가나',  '321', 'oo321@naver.com',  '01012341234',  '2019-01-01', '학생');
insert into user values ('pp321',  '가나다',  '321', 'pp321@naver.com',  '01012341234',  '2019-01-01', '학생');
insert into user values ('qq321',  '나다가',  '321', 'qq321@naver.com',  '01012341234',  '2019-01-01', '학생');
insert into user values ('rr321',  '다가나',  '321', 'rr321@naver.com',  '01012341234',  '2019-01-01', '학생');
insert into user values ('ss321',  '가나다',  '321', 'ss321@naver.com',  '01012341234',  '2019-01-01', '학생');
insert into user values ('tt321',  '나다가',  '321', 'tt321@naver.com',  '01012341234',  '2019-01-01', '학생');
insert into user values ('uu321',  '다가나',  '321', 'uu321@naver.com',  '01012341234',  '2019-01-01', '학생');
insert into user values ('vv321',  '가나다',  '321', 'vv321@naver.com',  '01012341234',  '2019-01-01', '학생');
insert into user values ('ww321',  '나다가',  '321', 'ww321@naver.com',  '01012341234',  '2019-01-01', '학생');
insert into user values ('xx321',  '다가나',  '321', 'xx321@naver.com',  '01012341234',  '2019-01-01', '학생');
insert into user values ('yy321',  '가나다',  '321', 'yy321@naver.com',  '01012341234',  '2019-01-01', '학생');
insert into user values ('zz321',  '나다가',  '321', 'zz321@naver.com',  '01012341234',  '2019-01-01', '학생');
insert into user values ('aaa321',  '다가나',  '321', 'aaa321@naver.com',  '01012341234',  '2019-01-01', '학생');
insert into user values ('bbb321',  '가나다',  '321', 'bbb321@naver.com',  '01012341234',  '2019-01-01', '학생');
insert into user values ('ccc321',  '나다가',  '321', 'ccc321@naver.com',  '01012341234',  '2019-01-01', '학생');
insert into user values ('ddd321',  '다가나',  '321', 'ddd321@naver.com',  '01012341234',  '2019-01-01', '학생');
insert into user values ('eee321',  '가나다',  '321', 'eee321@naver.com',  '01012341234',  '2019-01-01', '학생');
insert into user values ('fff321',  '나다가',  '321', 'fff321@naver.com',  '01012341234',  '2019-01-01', '학생');
insert into user values ('ggg321',  '다가나',  '321', 'ggg321@naver.com',  '01012341234',  '2019-01-01', '학생');
insert into user values ('hhh321',  '가나다',  '321', 'hhh321@naver.com',  '01012341234',  '2019-01-01', '학생');
insert into user values ('iii321',  '나다가',  '321', 'iii321@naver.com',  '01012341234',  '2019-01-01', '학생');
insert into user values ('jjj321',  '다가나',  '321', 'jjj321@naver.com',  '01012341234',  '2019-01-01', '학생');
insert into user values ('kkk321',  '가나다',  '321', 'kkk321@naver.com',  '01012341234',  '2019-01-01', '학생');
insert into user values ('lll321',  '나다가',  '321', 'lll321@naver.com',  '01012341234',  '2019-01-01', '학생');
insert into user values ('mmm321',  '다가나',  '321', 'mmm321@naver.com',  '01012341234',  '2019-01-01', '학생');
insert into user values ('nnn321',  '가나다',  '321', 'nnn321@naver.com',  '01012341234',  '2019-01-01', '학생');
insert into user values ('ooo321',  '나다가',  '321', 'ooo321@naver.com',  '01012341234',  '2019-01-01', '학생');
insert into user values ('ppp321',  '다가나',  '321', 'ppp321@naver.com',  '01012341234',  '2019-01-01', '학생');
insert into user values ('qqq321',  '가나다',  '321', 'qqq321@naver.com',  '01012341234',  '2019-01-01', '학생');
insert into user values ('rrr321',  '나다가',  '321', 'rrr321@naver.com',  '01012341234',  '2019-01-01', '학생');
insert into user values ('sss321',  '다가나',  '321', 'sss321@naver.com',  '01012341234',  '2019-01-01', '학생');
insert into user values ('ttt321',  '가나다',  '321', 'ttt321@naver.com',  '01012341234',  '2019-01-01', '학생');
insert into user values ('uuu321',  '나다가',  '321', 'uuu321@naver.com',  '01012341234',  '2019-01-01', '학생');
insert into user values ('vvv321',  '다가나',  '321', 'vvv321@naver.com',  '01012341234',  '2019-01-01', '학생');
insert into user values ('xxx321',  '가나다',  '321', 'xxx321@naver.com',  '01012341234',  '2019-01-01', '학생');
insert into user values ('www321',  '나다가',  '321', 'www321@naver.com',  '01012341234',  '2019-01-01', '학생');
insert into user values ('yyy321',  '다가나',  '321', 'yyy321@naver.com',  '01012341234',  '2019-01-01', '학생');
insert into user values ('zzz321',  '가나다',  '321', 'zzz321@naver.com',  '01012341234',  '2019-01-01', '학생');

-- 강좌
insert into class values (null, '이것이 자바다','홍길동','123',null, '2020-01-05', '2020-08-30',40);
insert into class values (null, '이것이 국어다','홍길동','123',null, '2020-01-05', '2020-08-30',30);
insert into class values (null, '이것이 영어다','이순신','a123',null, '2020-01-05', '2020-08-30',20);
insert into class values (null, '이것이 수학다','이순신','a123',null, '2020-01-05', '2020-08-30',20);
insert into class values (null, '이것이 DB다','강감찬','b123',null, '2020-01-05', '2020-08-30',20);
insert into class values (null, '이것이 JSP다','고길동','c123',null, '2020-01-05', '2020-08-30',50);
insert into class values (null, '이것이 사회다','고길동','c123',null, '2020-01-05', '2020-08-30',40);
insert into class values (null, '이것이 지리다','정동환','e123',null, '2020-07-01', '2020-12-30',30);
insert into class values (null, '이것이 물리다','정동환','e123',null, '2020-07-01', '2020-12-30',100);
insert into class values (null, '이것이 화학이다','강민호','f123',null, '2020-07-01', '2020-12-30',20);
insert into class values (null, '이것이 고수다','강민호','f123',null, '2020-07-01', '2020-12-30',15);
insert into class values (null, '이것이 주식이다','강민호','g123',null, '2020-07-01', '2020-12-30',20);
insert into class values (null, '이것이 기술이다','강민호','g123',null, '2019-07-01', '2020-12-30',30);
insert into class values (null, '이것이 Web이다','손창일','h123',null, '2019-01-05', '2019-06-30',40);
insert into class values (null, '이것이 자바다','손창일','h123',null, '2019-01-05', '2019-06-30',15);
insert into class values (null, '이것이 국어다','심재현','i123',null, '2019-01-05', '2019-06-30',50);
insert into class values (null, '이것이 영어다','심재현','i123',null, '2019-01-05', '2019-06-30',50);
insert into class values (null, '이것이 수학다','권지용','j123',null, '2019-01-05', '2019-06-30',60);
insert into class values (null, '이것이 DB다','권지용','j123',null, '2019-01-05', '2019-06-30',70);
insert into class values (null, '이것이 JSP다','임성빈','k123',null, '2019-01-05', '2019-06-30',20);
insert into class values (null, '이것이 사회다','임성빈','k123',null, '2019-01-05', '2019-12-30',20);
insert into class values (null, '이것이 지리다','최성호','l123',null, '2019-01-05', '2019-12-30',30);
insert into class values (null, '이것이 물리다','최성호','l123',null, '2019-01-05', '2019-12-30',30);
insert into class values (null, '이것이 화학이다','김한빈','m123',null, '2019-01-05', '2019-12-30',30);
insert into class values (null, '이것이 수학2다','김한빈','m123',null, '2019-01-05', '2019-12-30',30);
insert into class values (null, '이것이 의학이다','김현중','n123',null, '2019-01-05', '2019-12-30',30);
insert into class values (null, '이것이 자바다','김현중','n123',null, '2019-01-05', '2019-12-30',30);
insert into class values (null, '이것이 자바다','홍길동','123',null, '2020-10-01', '2021-03-30',40);
insert into class values (null, '이것이 국어다','홍길동','123',null, '2020-10-01', '2021-03-30',30);
insert into class values (null, '이것이 영어다','이순신','a123',null, '2020-10-01', '2021-03-30',20);
insert into class values (null, '이것이 수학다','이순신','a123',null, '2020-10-01', '2021-03-30',20);
insert into class values (null, '이것이 DB다','강감찬','b123',null, '2020-10-01', '2021-03-30',20);
insert into class values (null, '이것이 JSP다','고길동','c123',null, '2020-10-01', '2021-03-30',50);
insert into class values (null, '이것이 사회다','고길동','c123',null, '2020-10-01', '2021-03-30',40);
-- 강좌 설명 추가
update class
   set class_desc = "면세범위 초과 물품, 위장 반입, 원산지 조작 등 세관에서 벌어지는 불법적 행위를 빈틈없이 적발할 수 있는 기술이 개발됐다.

기초과학연구원(IBS) 수리 및 계산과학 연구단 데이터 사이언스 그룹 차미영 CI(KAIST 전산학부 교수) 연구팀은 세계관세기구(WCO)와의 협업을 통해 스마트 관세 행정을 위한 알고리즘 개발을 마쳤다고 29일 밝혔다.

데이터 사이언스 그룹은 지난해 9월부터 WCO의 바꾸다(BACUDA) 프로젝트에 참여해 알고리즘 개발을 주도해왔다. 바꾸다 프로젝트는 한국정부가 WCO에 공여하는 세관협력기금으로 설립, 운영되고 있다.

IBS가 WCO, 대만 국립성공대(NKCU)와 함께 개발한 알고리즘 '데이트'는 불법적 행위 발생 가능성이 높으면서도 세수 확보에 도움이 되는 물품을 우선적으로 선별해 세관원에게 알린다.
";

-- 과제
insert into task values (null, '과제 1',null, '2020-01-15', '2020-01-20',null,null,1001,100);
insert into task values (null, '과제 2',null, '2020-01-20', '2020-01-25',null,null,1001,100);
insert into task values (null, '과제 3',null, '2020-01-25', '2020-01-30',null,null,1001,60);
insert into task values (null, '과제 4',null, '2020-01-30', '2020-02-05',null,null,1001,80);
insert into task values (null, '과제 5',null, '2020-02-05', '2020-02-10',null,null,1001,90);
insert into task values (null, '과제 6',null, '2020-02-10', '2020-02-15',null,null,1001,100);
insert into task values (null, '과제 7',null, '2020-02-15', '2020-02-20',null,null,1001,100);
insert into task values (null, '과제 8',null, '2020-02-20', '2020-02-25',null,null,1001,100);
insert into task values (null, '과제 9',null, '2020-02-25', '2020-03-05',null,null,1001,100);
insert into task values (null, '과제 10',null, '2020-03-05', '2020-03-10',null,null,1001,80);
insert into task values (null, '과제 11',null, '2020-03-10', '2020-03-15',null,null,1001,50);
insert into task values (null, '과제 12',null, '2020-03-15', '2020-03-20',null,null,1001,100);
insert into task values (null, '과제 13',null, '2020-03-20', '2020-03-25',null,null,1001,100);
insert into task values (null, '과제 14',null, '2020-03-25', '2020-03-30',null,null,1001,60);
insert into task values (null, '과제 15',null, '2020-03-30', '2020-04-05',null,null,1001,100);
insert into task values (null, '과제 16',null, '2020-04-05', '2020-04-10',null,null,1001,100);
insert into task values (null, '과제 17',null, '2020-04-10', '2020-04-15',null,null,1001,70);
insert into task values (null, '과제 18',null, '2020-04-15', '2020-04-20',null,null,1001,100);
insert into task values (null, '과제 19',null, '2020-04-20', '2020-04-25',null,null,1001,100);
insert into task values (null, '과제 20',null, '2020-04-25', '2020-04-30',null,null,1001,60);
insert into task values (null, '과제 21',null, '2020-04-30', '2020-05-05',null,null,1001,100);
insert into task values (null, '과제 22',null, '2020-05-05', '2020-05-10',null,null,1001,100);
insert into task values (null, '과제 23',null, '2020-05-10', '2020-05-15',null,null,1001,90);
insert into task values (null, '과제 24',null, '2020-05-15', '2020-05-20',null,null,1001,100);
insert into task values (null, '과제 25',null, '2020-05-20', '2020-05-25',null,null,1001,80);
insert into task values (null, '과제 26',null, '2020-05-25', '2020-05-30',null,null,1001,100);
insert into task values (null, '과제 27',null, '2020-05-30', '2020-06-05',null,null,1001,70);
insert into task values (null, '과제 28',null, '2020-06-05', '2020-06-10',null,null,1001,100);
insert into task values (null, '과제 29',null, '2020-06-10', '2020-06-15',null,null,1001,60);
insert into task values (null, '과제 30',null, '2020-06-15', '2020-06-20',null,null,1001,100);
insert into task values (null, '과제 1',null, '2020-01-15', '2020-01-20',null,null,1005,100);
insert into task values (null, '과제 2',null, '2020-02-05', '2020-02-10',null,null,1005,90);
insert into task values (null, '과제 3',null, '2020-03-05', '2020-03-10',null,null,1005,80);
insert into task values (null, '과제 4',null, '2020-04-05', '2020-04-10',null,null,1005,100);
insert into task values (null, '과제 5',null, '2020-05-05', '2020-05-10',null,null,1005,100);
insert into task values (null, '과제 1',null, '2019-01-15', '2019-01-20',null,null,1014,100);
insert into task values (null, '과제 2',null, '2019-02-05', '2019-02-10',null,null,1014,90);
insert into task values (null, '과제 3',null, '2019-03-05', '2019-03-10',null,null,1014,80);
insert into task values (null, '과제 4',null, '2019-04-05', '2019-04-10',null,null,1014,100);
insert into task values (null, '과제 5',null, '2019-05-05', '2019-05-10',null,null,1014,100);
insert into task values (null, '과제 6',null, '2019-06-05', '2019-06-10',null,null,1014,100);
insert into task values (null, '과제 1',null, '2019-01-15', '2019-01-20',null,null,1016,100);
insert into task values (null, '과제 2',null, '2019-02-05', '2019-02-10',null,null,1016,90);
insert into task values (null, '과제 3',null, '2019-03-05', '2019-03-10',null,null,1016,80);
insert into task values (null, '과제 4',null, '2019-04-05', '2019-04-10',null,null,1016,100);
insert into task values (null, '과제 5',null, '2019-05-05', '2019-05-10',null,null,1016,100);
insert into task values (null, '과제 6',null, '2019-06-05', '2019-06-10',null,null,1016,100);
insert into task values (null, '과제 1',null, '2019-01-15', '2019-01-20',null,null,1020,100);
insert into task values (null, '과제 2',null, '2019-02-05', '2019-02-10',null,null,1020,90);
insert into task values (null, '과제 3',null, '2019-03-05', '2019-03-10',null,null,1020,80);
insert into task values (null, '과제 4',null, '2019-04-05', '2019-04-10',null,null,1020,100);
insert into task values (null, '과제 5',null, '2019-05-05', '2019-05-10',null,null,1020,100);
insert into task values (null, '과제 6',null, '2019-06-05', '2019-06-10',null,null,1020,100);
insert into task values (null, '과제 1',null, '2020-07-15', '2020-07-20',null,null,1008,100);
insert into task values (null, '과제 2',null, '2020-08-05', '2020-08-10',null,null,1008,90);
insert into task values (null, '과제 3',null, '2020-09-05', '2020-09-10',null,null,1008,80);
insert into task values (null, '과제 4',null, '2020-10-05', '2020-10-10',null,null,1008,100);
insert into task values (null, '과제 5',null, '2020-11-05', '2020-11-10',null,null,1008,100);
insert into task values (null, '과제 6',null, '2020-12-05', '2020-12-10',null,null,1008,100);
insert into task values (null, '과제 1',null, '2020-01-15', '2020-01-20',null,null,1032,100);
insert into task values (null, '과제 2',null, '2020-02-05', '2020-02-10',null,null,1032,90);
insert into task values (null, '과제 3',null, '2020-03-05', '2020-03-10',null,null,1032,80);
insert into task values (null, '과제 4',null, '2020-04-05', '2020-04-10',null,null,1032,100);
insert into task values (null, '과제 5',null, '2020-05-05', '2020-05-10',null,null,1032,100);
insert into task values (null, '과제 6',null, '2020-06-05', '2020-06-10',null,null,1032,100);
update task
   set task_desc = "면세범위 초과 물품, 위장 반입, 원산지 조작 등 세관에서 벌어지는 불법적 행위를 빈틈없이 적발할 수 있는 기술이 개발됐다.

기초과학연구원(IBS) 수리 및 계산과학 연구단 데이터 사이언스 그룹 차미영 CI(KAIST 전산학부 교수) 연구팀은 세계관세기구(WCO)와의 협업을 통해 스마트 관세 행정을 위한 알고리즘 개발을 마쳤다고 29일 밝혔다.

데이터 사이언스 그룹은 지난해 9월부터 WCO의 바꾸다(BACUDA) 프로젝트에 참여해 알고리즘 개발을 주도해왔다. 바꾸다 프로젝트는 한국정부가 WCO에 공여하는 세관협력기금으로 설립, 운영되고 있다.

IBS가 WCO, 대만 국립성공대(NKCU)와 함께 개발한 알고리즘 '데이트'는 불법적 행위 발생 가능성이 높으면서도 세수 확보에 도움이 되는 물품을 우선적으로 선별해 세관원에게 알린다.
";

-- 수강신청
insert into request_class values ( 'o321',1001);
insert into request_class values ( 'p321',1001);
insert into request_class values ( 'q321',1001);
insert into request_class values ( 'r321',1001);
insert into request_class values ( 's321',1001);
insert into request_class values ( 't321',1001);
insert into request_class values ( 'u321',1001);
insert into request_class values ( 'v321',1001);
insert into request_class values ( 'w321',1001);
insert into request_class values ( 'x321',1001);
insert into request_class values ( 'y321',1001);
insert into request_class values ( 'z321',1001);
insert into request_class values ( 'aa321',1001);
insert into request_class values ( 'bb321',1001);
insert into request_class values ( 'cc321',1001);
insert into request_class values ( 'dd321',1001);
insert into request_class values ( 'ee321',1001);
insert into request_class values ( 'ff321',1001);
insert into request_class values ( 'gg321',1001);
insert into request_class values ( 'hh321',1001);
insert into request_class values ( 'ii321',1001);
insert into request_class values ( 'jj321',1001);
insert into request_class values ( 'kk321',1001);
insert into request_class values ( 'll321',1001);
insert into request_class values ( 'mm321',1001);
insert into request_class values ( 'nn321',1001);
insert into request_class values ( 'oo321',1001);
insert into request_class values ( 'pp321',1005);
insert into request_class values ( 'qq321',1006);
insert into request_class values ( 'rr321',1007);
insert into request_class values ( 'ss321',1008);
insert into request_class values ( 'tt321',1009);
insert into request_class values ( 'uu321',1010);
insert into request_class values ( 'vv321',1011);
insert into request_class values ( 'ww321',1012);
insert into request_class values ( 'xx321',1013);
insert into request_class values ( 'yy321',1014);
insert into request_class values ( 'zz321',1015);
insert into request_class values ( 'aaa321',1016);
insert into request_class values ( 'bbb321',1016);
insert into request_class values ( 'ccc321',1016);
insert into request_class values ( 'ddd321',1016);
insert into request_class values ( 'eee321',1016);
insert into request_class values ( 'fff321',1016);
insert into request_class values ( 'ggg321',1016);
insert into request_class values ( 'hhh321',1016);
insert into request_class values ( 'iii321',1016);
insert into request_class values ( 'jjj321',1016);
insert into request_class values ( 'kkk321',1016);
insert into request_class values ( 'lll321',1016);
insert into request_class values ( 'mmm321',1016);
insert into request_class values ( 'nnn321',1005);
insert into request_class values ( 'ooo321',1005);
insert into request_class values ( 'ppp321',1005);
insert into request_class values ( 'qqq321',1005);
insert into request_class values ( 'rrr321',1005);
insert into request_class values ( 'sss321',1005);
insert into request_class values ( 'ttt321',1014);
insert into request_class values ( 'uuu321',1014);
insert into request_class values ( 'vvv321',1014);
insert into request_class values ( 'xxx321',1014);
insert into request_class values ( 'www321',1014);
insert into request_class values ( 'yyy321',1014);
insert into request_class values ( 'zzz321',1014);
insert into request_class values ( 'a321',1001);
insert into request_class values ( 'b321',1001);
insert into request_class values ( 'c321',1001);
insert into request_class values ( 'd321',1001);
insert into request_class values ( 'e321',1001);
insert into request_class values ( 'f321',1001);
insert into request_class values ( 'g321',1001);
insert into request_class values ( 'h321',1001);
insert into request_class values ( 'i321',1001);
insert into request_class values ( 'j321',1001);
insert into request_class values ( 'k321',1001);
insert into request_class values ( 'l321',1001);
insert into request_class values ( 'a321',1002);
insert into request_class values ( 'b321',1002);
insert into request_class values ( 'c321',1002);
insert into request_class values ( 'd321',1002);
insert into request_class values ( 'e321',1002);
insert into request_class values ( 'f321',1002);
insert into request_class values ( 'g321',1002);
insert into request_class values ( 'h321',1002);
insert into request_class values ( 'i321',1002);
insert into request_class values ( 'j321',1002);
insert into request_class values ( 'k321',1002);
insert into request_class values ( 'l321',1002);
insert into request_class values ( 'a321',1003);
insert into request_class values ( 'b321',1003);
insert into request_class values ( 'c321',1003);
insert into request_class values ( 'd321',1003);
insert into request_class values ( 'e321',1003);
insert into request_class values ( 'f321',1003);
insert into request_class values ( 'g321',1003);
insert into request_class values ( 'h321',1003);
insert into request_class values ( 'i321',1003);
insert into request_class values ( 'j321',1003);
insert into request_class values ( 'k321',1003);
insert into request_class values ( 'l321',1003);
insert into request_class values ( 'a321',1005);
insert into request_class values ( 'b321',1005);
insert into request_class values ( 'c321',1005);
insert into request_class values ( 'd321',1005);
insert into request_class values ( 'e321',1005);
insert into request_class values ( 'f321',1005);
insert into request_class values ( 'g321',1005);
insert into request_class values ( 'h321',1005);
insert into request_class values ( 'i321',1005);
insert into request_class values ( 'j321',1005);
insert into request_class values ( 'k321',1005);
insert into request_class values ( 'l321',1005);
insert into request_class values ( 'a321',1032);
insert into request_class values ( 'b321',1032);
insert into request_class values ( 'c321',1032);
insert into request_class values ( 'd321',1032);
insert into request_class values ( 'e321',1032);
insert into request_class values ( 'f321',1032);
insert into request_class values ( 'g321',1032);
insert into request_class values ( 'h321',1032);
insert into request_class values ( 'i321',1032);
insert into request_class values ( 'j321',1032);
insert into request_class values ( 'k321',1032);
insert into request_class values ( 'l321',1032);
insert into request_class values ( '321', 1001);
insert into request_class values ( '321', 1002);
insert into request_class values ( '321', 1003);
insert into request_class values ( '321', 1004);
insert into request_class values ( '321', 1005);
insert into request_class values ( '321', 1006);
insert into request_class values ( '321', 1007);
insert into request_class values ( '321', 1008);
insert into request_class values ( '321', 1009);
insert into request_class values ( '321', 1010);
insert into request_class values ( '321', 1011);
insert into request_class values ( '321', 1012);
insert into request_class values ( '321', 1013);
insert into request_class values ( '321', 1014);
insert into request_class values ( '321', 1015);
insert into request_class values ( '321', 1016);
insert into request_class values ( '321', 1017);
insert into request_class values ( '321', 1018);
insert into request_class values ( '321', 1019);
insert into request_class values ( '321', 1020);
insert into request_class values ( '321', 1021);
insert into request_class values ( '321', 1022);
insert into request_class values ( '321', 1023);
insert into request_class values ( '321', 1024);
insert into request_class values ( '321', 1025);
insert into request_class values ( '321', 1026);
insert into request_class values ( '321', 1027);
insert into request_class values ( '321', 1028);
insert into request_class values ( '321', 1029);
insert into request_class values ( '321', 1030);
insert into request_class values ( '321', 1031);
insert into request_class values ( '321', 1032);