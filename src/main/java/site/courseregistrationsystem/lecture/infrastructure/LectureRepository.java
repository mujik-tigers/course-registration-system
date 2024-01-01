package site.courseregistrationsystem.lecture.infrastructure;

import org.springframework.data.jpa.repository.JpaRepository;

import site.courseregistrationsystem.lecture.Lecture;

public interface LectureRepository extends JpaRepository<Lecture, Long>, LectureRepositoryCustom {

}
