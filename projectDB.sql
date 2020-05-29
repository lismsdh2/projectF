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
    teacher_id varchar(20) not null,			-- 강사ID
    class_desc varchar(1000),					-- 강의설명
    start_date date not null,					-- 강의 시작일
    end_date date not null,						-- 강의 종강일
    limitstudent int not null,					-- 수강신청 가능 인원
    foreign key(teacher_id) references user(user_id) ON UPDATE CASCADE ON DELETE CASCADE
);
alter table class auto_increment=1001;		-- 강의ID : 1001부터 시작

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
		foreign key(class_no) references class(class_no),
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
		foreign key(class_no) references class(class_no),
    constraint fk_task_no						-- task테이블의 task_no를 FK로 지정
		foreign key(task_no) references task(task_no),
    constraint fk_stu_id_submit					-- student테이블의 student_id를 FK로 지정
        foreign key(student_id) references user(user_id),
	constraint pk_taskno_stuid					-- PK 지정
		primary key(class_no,task_no, student_id),			
	constraint score_range						-- taskscore의 입력 범위 지정
		check (taskscore >= -1 and taskscore < 101)
);
select * from submission_task;

#샘플 입력
-- 강사
insert into user values ('123','홍길동','123','123@naver.com','0101234123','2020-01-12','강사');
insert into user values ('a123','이순신','123','123@naver.com','0101234123','2019-01-12','강사');
insert into user values ('b123','강감찬','123','123@naver.com','0101234123','2028-01-12','강사');
insert into user values ('c123','고길동','123','123@naver.com','0101234123','2020-01-01','강사');
insert into user values ('d123','김우진','123','123@naver.com','0101234123','2020-02-12','강사');
insert into user values ('e123','정동환','123','123@naver.com','0101234123','2019-06-12','강사');
insert into user values ('f123','강민호','123','123@naver.com','0101234123','2019-07-12','강사');
insert into user values ('gf123','임창정','123','123@naver.com','0101234123','2019-07-12','강사');
insert into user values ('h123','손창일','123','123@naver.com','0101234123','2019-08-12','강사');
insert into user values ('i123','심재현','123','123@naver.com','0101234123','2020-01-13','강사');
insert into user values ('j123','권지용','123','123@naver.com','0101234123','2020-01-12','강사');
insert into user values ('k123','임성빈','123','123@naver.com','0101234123','2020-01-12','강사');
insert into user values ('l123','최성호','123','123@naver.com','0101234123','2020-01-12','강사');
insert into user values ('m123','김한빈','123','123@naver.com','0101234123','2020-01-12','강사');
insert into user values ('n123','김현중','123','123@naver.com','0101234123','2020-01-12','강사');

-- 학생
insert into user values ('321','차우미','321','123@naver.com','0101234123','2020-01-12','학생');
insert into user values ('a321','엄지선','321','123@naver.com','0101234123','2019-01-12','학생');
insert into user values ('b321','김지은','321','123@naver.com','0101234123','2028-01-12','학생');
insert into user values ('c321','강정미','321','123@naver.com','0101234123','2020-01-01','학생');
insert into user values ('d321','주미진','321','123@naver.com','0101234123','2020-02-12','학생');
insert into user values ('e321','강은비','321','123@naver.com','0101234123','2019-06-12','학생');
insert into user values ('f321','안칠현','321','123@naver.com','0101234123','2019-07-12','학생');
insert into user values ('g321','김유환','321','123@naver.com','0101234123','2019-07-12','학생');
insert into user values ('h321','권율','321','123@naver.com','0101234123','2019-08-12','학생');
insert into user values ('i321','조규현','321','123@naver.com','0101234123','2020-01-13','학생');
insert into user values ('j321','백보람','321','123@naver.com','0101234123','2020-01-12','학생');
insert into user values ('k321','김현동','321','123@naver.com','0101234123','2020-01-12','학생');
insert into user values ('l321','김태우','321','123@naver.com','0101234123','2020-01-12','학생');
insert into user values ('m321','기은세','321','123@naver.com','0101234123','2020-01-12','학생');
insert into user values ('n321','이익준','321','123@naver.com','0101234123','2020-01-12','학생');


-- 강좌
insert into class values (null, '이것이 자바다','홍길동','123',null,'2020-01-01','2020-06-30',50);
insert into class values (null, '이것이 국어다','홍길동','123',null,'2020-01-01','2020-06-30',40);
insert into class values (null, '이것이 영어다','이순신','a123',null,'2020-01-01','2020-06-30',30);
insert into class values (null, '이것이 수학다','이순신','a123',null,'2020-01-01','2020-06-30',20);
insert into class values (null, '이것이 DB다','강감찬','b123',null,'2020-01-01','2020-06-30',20);
insert into class values (null, '이것이 JSP다','고길동','c123',null,'2020-01-01','2020-06-30',20);
insert into class values (null, '이것이 사회다','고길동','c123',null,'2020-01-01','2020-06-30',50);
insert into class values (null, '이것이 지리다','정동환','e123',null,'2020-01-01','2020-06-30',40);
insert into class values (null, '이것이 물리다','정동환','e123',null,'2020-01-01','2020-06-30',30);
insert into class values (null, '이것이 화학이다','강민호','f123',null,'2020-01-01','2020-06-30',100);
insert into class values (null, '이것이 고수다','강민호','f123',null,'2020-01-01','2020-06-30',20);
insert into class values (null, '이것이 주식이다','강민호','g123',null,'2020-01-01','2020-06-30',15);
insert into class values (null, '이것이 기술이다','강민호','g123',null,'2020-01-01','2020-06-30',20);
insert into class values (null, '이것이 Web이다','손창일','h123',null,'2019-01-01','2020-06-30',30);
insert into class values (null, '이것이 자바다','손창일','h123',null,'2019-01-01','2019-06-30',40);
insert into class values (null, '이것이 국어다','심재현','i123',null,'2019-01-01','2019-06-30',15);
insert into class values (null, '이것이 영어다','심재현','i123',null,'2019-01-01','2019-06-30',50);
insert into class values (null, '이것이 수학다','권지용','j123',null,'2019-01-01','2019-06-30',50);
insert into class values (null, '이것이 DB다','권지용','j123',null,'2019-01-01','2019-06-30',60);
insert into class values (null, '이것이 JSP다','임성빈','k123',null,'2019-01-01','2019-06-30',70);
insert into class values (null, '이것이 사회다','임성빈','k123',null,'2019-01-01','2019-06-30',20);
insert into class values (null, '이것이 지리다','최성호','l123',null,'2019-01-01','2019-06-30',20);
insert into class values (null, '이것이 물리다','최성호','l123',null,'2019-01-01','2019-06-30',30);
insert into class values (null, '이것이 화학이다','김한빈','m123',null,'2019-01-01','2019-06-30',30);
insert into class values (null, '이것이 수학2다','김한빈','m123',null,'2019-01-01','2019-06-30',30);
insert into class values (null, '이것이 의학이다','김현중','n123',null,'2019-01-01','2019-06-30',30);
insert into class values (null, '이것이 자바다','김현중','n123',null,'2019-01-01','2019-06-30',30);

-- 과제
insert into task values (null, '과제1',null,'2020-01-01','2020-01-15',null,null,1001,100);
insert into task values (null, '과제2',null,'2020-01-01','2020-01-15',null,null,1001,100);
insert into task values (null, '과제3',null,'2020-01-01','2020-01-15',null,null,1001,100);
insert into task values (null, '과제4',null,'2020-01-01','2020-01-15',null,null,1001,80);
insert into task values (null, '과제5',null,'2020-02-01','2020-02-15',null,null,1001,70);
insert into task values (null, '과제6',null,'2020-02-01','2020-02-15',null,null,1001,60);
insert into task values (null, '과제7',null,'2020-02-01','2020-02-15',null,null,1001,50);
insert into task values (null, '과제8',null,'2020-02-01','2020-02-15',null,null,1001,50);
insert into task values (null, '과제9',null,'2020-02-01','2020-02-15',null,null,1001,50);
insert into task values (null, '과제10',null,'2020-05-01','2020-06-15',null,null,1001,80);
insert into task values (null, '과제11',null,'2020-05-01','2020-06-15',null,null,1001,90);
insert into task values (null, '과제12',null,'2020-05-01','2020-06-15',null,null,1001,100);
insert into task values (null, '과제13',null,'2020-05-01','2020-06-15',null,null,1001,60);
insert into task values (null, '과제1',null,'2020-01-01','2020-01-15',null,null,1002,80);
insert into task values (null, '과제2',null,'2020-01-01','2020-01-15',null,null,1002,90);
insert into task values (null, '과제3',null,'2020-01-01','2020-01-15',null,null,1002,50);
insert into task values (null, '과제4',null,'2020-01-01','2020-01-15',null,null,1002,60);
insert into task values (null, '과제5',null,'2020-01-01','2020-01-15',null,null,1002,70);
insert into task values (null, '과제6',null,'2020-01-01','2020-01-15',null,null,1002,100);







