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
	email varchar(50) not null,					-- 이메일
	phoneNum varchar(12) not null,				-- 핸드폰 번호
	registration_date date,						-- 등록일
    position enum('강사', '학생'),				-- 신분
	primary key(user_id)
);
-- 샘플
insert into user values ('abcabc','홍길동','12341234','abc@abc.com', '01012341234', '2020-01-01', '학생');
insert into user values ('defdef','이순신','12341234','def@abc.com', '01012341234', '2020-11-01', '학생');
insert into user values ('321','ABC','321','teacher12@abc.com', '01012341234', '2020-03-01','학생');
insert into user values ('teacher11','가나다','12341234','teacher11@abc.com', '01012341234', '2020-02-01','강사');
insert into user values ('teacher12','ABC','12341234','teacher12@abc.com', '01012341234', '2020-03-01','강사');
insert into user values ('123','ABC','123','teacher12@abc.com', '01012341234', '2020-03-01','강사');
desc user;
select * from user;

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
    teacher_name varchar(20) not null,			-- 강사명(추후 join으로 대체)
    teacher_id varchar(20) not null,				-- 강사ID
    class_desc varchar(1000) not null,			-- 강의설명
    start_date date not null,					-- 강의 시작일
    end_date date not null,						-- 강의 종강일
    limitstudent int not null,					-- 수강신청 가능 인원
    foreign key(teacher_id) references user(user_id) ON UPDATE CASCADE ON DELETE CASCADE
);
alter table class auto_increment=1001;		-- 강의ID : 1001부터 시작

insert into class values (null, '이것이 자바다', 'ABC', '123', '자바 기초부터 고급까지', '2020-01-01','2020-06-30', 20);
insert into class values (null, '이것이 MySQL이다', '가나다', '123', 'MySQL 기초부터 고급까지', '2020-02-01','2020-06-30', 20);
insert into class values (null, '이것이 국어다', '가나다', 'teacher12', '국어 기초부터 고급까지', '2020-02-01','2020-06-30', 20);
insert into class values (null, '이것이 수학이다', '가나다', 'teacher11', '수학 기초부터 고급까지', '2020-02-01','2020-06-30', 20);
insert into class values (null, '이것이 영어다', '가나다', '123', '영어 기초부터 고급까지', '2020-02-01','2020-06-30', 20);
select * from class;

-- 과제테이블
drop table if exists task;
create table task(
	task_no int primary key auto_increment,		-- 과제번호
	task_name varchar(20),						-- 과제명
	task_desc varchar(200),						-- 과제 설명
	reg_date date,								-- 과제 등록일
	expire_date date,							-- 과제 종료일
	attachedfile longblob,						-- 과제 참고 파일
    attachedfile_name varchar(100),				-- 과제 참고 파일명
	class_no int,								-- 강의 no
	perfect_score int,							-- 만점 
	foreign key (class_no) references class(class_no)
);
alter table task auto_increment=10001;		-- 과제ID : 10001부터 시작

insert into task values (null, '반복문과 조건문', '첨부된 파일 확인', '2020-03-01', '2020-03-14', null, null, 1001, 100);
insert into task values (null, '반복문과 조건문2', '첨부된 파일 확인', '2020-03-01', '2020-03-14', null, null, 1001, 100);
insert into task values (null, '반복문과 조건문3', '첨부된 파일 확인', '2020-03-01', '2020-03-14', null, null, 1001, 100);
insert into task values (null, '객체지향', '첨부된 파일 확인', '2020-03-01', '2020-03-14', null, null, 1001, 100);
insert into task values (null, 'select문', '첨부된 파일 확인', '2020-03-11', '2020-03-15', null, null,1001, 100);
select * from task;

#2. 결합테이블
-- 수강신청테이블
-- student테이블의 student_id(FK)와 class 테이블의 class_no(FK)를 PK로 지정
drop table if exists request_class;
create table request_class(
	student_id varchar(20) not null,			-- 학생 ID
    class_no int not null,						-- 강의 no
    constraint fk_stu_id						-- student테이블의 student_id를 FK로 지정
        foreign key(student_id) references student(user_id),
	constraint fk_cls_id						-- class테이블의 class_no를 FK로 지정
		foreign key(class_no) references class(class_no),
	constraint pk_stuid_clsid					-- PK로 지정
		primary key(student_id, class_no)
);

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
		foreign key(class_no) references class(class_no),
    constraint fk_task_no						-- task테이블의 task_no를 FK로 지정
		foreign key(task_no) references task(task_no),
    constraint fk_stu_id_submit					-- student테이블의 student_id를 FK로 지정
        foreign key(student_id) references student(user_id),
	constraint pk_taskno_stuid					-- PK 지정
		primary key(class_no,task_no, student_id),			
	constraint score_range						-- taskscore의 입력 범위 지정
		check (taskscore >= -1 and taskscore < 101)
);