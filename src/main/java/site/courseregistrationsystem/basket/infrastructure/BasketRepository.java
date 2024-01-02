package site.courseregistrationsystem.basket.infrastructure;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import site.courseregistrationsystem.basket.Basket;
import site.courseregistrationsystem.student.Student;

public interface BasketRepository extends JpaRepository<Basket, Long> {

	List<Basket> findAllByStudent(Student student);

}
