package site.courseregistrationsystem.basket.infrastructure;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import site.courseregistrationsystem.basket.Basket;
import site.courseregistrationsystem.student.Student;

public interface BasketRepository extends JpaRepository<Basket, Long> {

	List<Basket> findAllByStudent(Student student);

	Optional<Basket> findByIdAndStudent(Long basketId, Student student);

	@Modifying
	@Query("delete from Basket b where b = :basket")
	void delete(@Param("basket") Basket basket);

}
