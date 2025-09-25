Spring Boot + React 실습 프로젝트
- 2025.09.24 스프링부트 시작 -

이 프로젝트는 React와 Spring Boot를 연동하여 사용자 회원가입 및 로그인 기능을 구현하고, 이후 파이썬 시각화까지 이루어질 실습 저장소입니다.


# 📝 프로젝트 문서 및 상세 정리 (Notion)
# 개발 왕초보 이기에 정리, 코드 작성, 파일 관리, 미흡 주의!


[![Notion](https://img.shields.io/badge/Notion-Python_Study-000000?style=flat&logo=notion&logoColor=white)](https://www.notion.so/Web-project-26a140ad7c83800287bed9ea0cb03eec?source=copy_link)

[**🧑‍💻 개인 학습 및 성장 기록 노션 링크**](https://www.notion.so/A-journey-to-find-myself-25f140ad7c83802b8ed6c129c5e16d37?source=copy_link)

[![Instagram](https://img.shields.io/badge/Instagram-Daily_Quest-E4405F?style=flat&logo=instagram&logoColor=white)](https://www.instagram.com/pomodoro._.life/?next=%2Flucete_w.b%2F)




🛠️ 기술 스택 상세 정리

**1. 백엔드 (Backend)**

코어 환경	: Java Development Kit (JDK)	17
프레임워크	: Spring Boot	3.5.6 (Starter Parent)
웹	: Spring Boot Starter Web	3.x 버전 기반
데이터 처리	: Spring Data JPA	3.x 버전 기반
보안	: Spring Boot Starter Security	3.x 버전 기반
유효성 검사	: Spring Boot Starter Validation	3.x 버전 기반
편의 기능	: Lombok	의존성 추가됨
개발 도구	: Spring Boot Devtools	runtime


**2. 데이터베이스 (Database)**

데이터베이스	: MySQL	8.0.41
연결 드라이버	: MySQL Connector/J	runtime


**3. 프론트엔드 (Frontend)**

프레임워크	React	-
번들러	Vite	-
상태 관리	Context API	-
통신 라이브러리	Axios	-


**4. 선택된 의존성 목록**

1	Lombok	: 편의 기능	반복되는 코드를 줄여주는 Java 애너테이션 라이브러리입니다. (@Getter, @Setter 등)
2	Spring Web	: 웹/API	RESTful 애플리케이션 구축 및 Spring MVC 지원, 기본적으로 Apache Tomcat을 사용합니다.
3	Spring Data JPA	: 데이터 처리	Java Persistence API (JPA)를 사용하여 SQL 데이터베이스에 데이터를 영속화(저장/관리)하는 것을 돕습니다.
4	Validation	: 유효성 검사	Hibernate Validator를 사용하여 Bean의 유효성 검사를 수행합니다.
5	Spring Security	: 보안	Spring 애플리케이션을 위한 고도로 사용자 정의 가능한 인증 및 접근 제어 프레임워크입니다.
6	MySQL Driver	: 데이터베이스	MySQL 데이터베이스와 통신하기 위한 JDBC 드라이버입니다.
7	Spring Boot DevTools	: 개발 도구	개발 시 빠른 애플리케이션 재시작, LiveReload 및 개발 환경 구성을 지원합니다.
