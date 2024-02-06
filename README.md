# 수강 신청 시스템

#### 목표 : 부가적인 기능은 생략하고 수강 신청 기능에 집중하여 시스템 구현해보기  

<br/>

## Members


|<a href="https://github.com/ghkdgus29"><img src = "https://avatars.githubusercontent.com/u/91525492?v=4" width="120px;">|<a href="https://github.com/yeonise"><img src = "https://avatars.githubusercontent.com/u/105152276?v=4" width="120px;">|
|:---:|:---:|
|[Hyun](https://github.com/ghkdgus29)|[Fia](https://github.com/yeonise)|

<br/>

## ERD

<img width="929" alt="image" src="https://github.com/mujik-tigers/course-registration-system/assets/105152276/ef92875e-8ee8-4044-ae58-48346d94c66e"><br/>

<br/>

## Environment

<img src="https://img.shields.io/badge/Language-%23121011?style=for-the-badge"><img src="https://img.shields.io/badge/java-%23ED8B00?style=for-the-badge&logo=openjdk&logoColor=white"><img src="https://img.shields.io/badge/17-515151?style=for-the-badge">

<img src="https://img.shields.io/badge/Framework-%23121011?style=for-the-badge"><img src="https://img.shields.io/badge/springboot-6DB33F?style=for-the-badge&logo=springboot&logoColor=white"><img src="https://img.shields.io/badge/3.2.0-515151?style=for-the-badge">

<img src="https://img.shields.io/badge/Build-%23121011?style=for-the-badge"><img src="https://img.shields.io/badge/Gradle-02303A?style=for-the-badge&logo=Gradle&logoColor=white"><img src="https://img.shields.io/badge/8.5-515151?style=for-the-badge">

![Nginx](https://img.shields.io/badge/nginx-65B741?style=for-the-badge&logo=nginx&logoColor=white)
![MySQL](https://img.shields.io/badge/mysql-%2300f.svg?style=for-the-badge&logo=mysql&logoColor=white)
![redis](https://img.shields.io/badge/redis-B31312?style=for-the-badge&logo=redis&logoColor=white)
![Git](https://img.shields.io/badge/git-%23F05033.svg?style=for-the-badge&logo=git&logoColor=white)
![AWS](https://img.shields.io/badge/AWS-%23FF9900.svg?style=for-the-badge&logo=amazon-aws&logoColor=white)
![Apache Tomcat](https://img.shields.io/badge/apache%20tomcat-%23F8DC75.svg?style=for-the-badge&logo=apache-tomcat&logoColor=black)
![Hibernate](https://img.shields.io/badge/Hibernate-59666C?style=for-the-badge&logo=Hibernate&logoColor=white)
![GitHub](https://img.shields.io/badge/github-%23121011.svg?style=for-the-badge&logo=github&logoColor=white)

<br/>

## Demo

1. 학번과 비밀번호를 인증하여 로그인할 수 있습니다. 인증되지 않은 상태에서도 공지사항을 확인할 수 있습니다.
![공지사항-로그인](https://github.com/mujik-tigers/course-registration-system/assets/105152276/a0e06abc-7761-410d-bb76-6a82ea8561ed)

2. 세션은 60분동안 지속되며 연장할 수 있습니다. 로그인 후에도 공지사항을 확인할 수 있습니다.
![세션연장-일정안내](https://github.com/mujik-tigers/course-registration-system/assets/105152276/87057753-aa20-4023-84e3-cc6aa08a6b87)

3. 새로고침 버튼을 클릭하여 수강 바구니 인원을 조회할 수 있습니다.
![수강바구니인원조회](https://github.com/mujik-tigers/course-registration-system/assets/105152276/159ff64b-da88-4e66-91da-03985c7ecc18)

4. 개설학과, 이수구분, 과목명을 통해 개설 강의 목록을 조회할 수 있습니다.
![개설강의목록조회](https://github.com/mujik-tigers/course-registration-system/assets/105152276/dd851d69-2e0f-49af-8f22-b6368815e9d7)

5. 강의를 수강 바구니에 담거나 취소할 수 있습니다.
![수강바구니신청-취소](https://github.com/mujik-tigers/course-registration-system/assets/105152276/519d87fa-4fa7-445a-9640-ebfe15cce24e)

6. 수강 신청 페이지에서 수강 바구니를 통해 강의를 신청할 수 있습니다.
![수강신청-강의조회페이징](https://github.com/mujik-tigers/course-registration-system/assets/105152276/d2071c05-2eb2-4921-9d33-44813978d6e8)

7. 신청 버튼을 눌러 강의를 신청할 수 있습니다.
![수강신청](https://github.com/mujik-tigers/course-registration-system/assets/105152276/07cfc425-e1cd-4d93-a23c-203f521c71b1)

8. 강의 번호를 통해 검색 없이 빠른 수강 신청이 가능합니다. 취소 버튼을 눌러 수강 신청을 취소할 수 있습니다.
![빠른수강신청-취소](https://github.com/mujik-tigers/course-registration-system/assets/105152276/05809573-243e-4c8c-92d6-1abee718575f)

9. 로그아웃을 하면 메인으로 돌아갑니다.
![로그아웃](https://github.com/mujik-tigers/course-registration-system/assets/105152276/29f4d155-da9c-482f-92ed-230a4b582397)

<br/>

## 참고 링크

### [mujik-tigers 프로젝트 블로그](https://velog.io/@on-and-off/series/course-registration-system)
### [API 명세서](https://course-registration-system.site/docs/index.html)
