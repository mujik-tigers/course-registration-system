package site.courseregistrationsystem.basket.infrastructure;

import org.springframework.data.jpa.repository.JpaRepository;

import site.courseregistrationsystem.basket.Basket;
import site.courseregistrationsystem.lecture.Lecture;
import site.courseregistrationsystem.student.Student;

public interface BasketRepository extends JpaRepository<Basket, Long> {

	boolean existsByStudentAndLecture(Student student, Lecture lecture);

}
