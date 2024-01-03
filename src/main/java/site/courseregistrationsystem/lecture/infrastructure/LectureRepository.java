package site.courseregistrationsystem.lecture.infrastructure;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import site.courseregistrationsystem.lecture.Lecture;

public interface LectureRepository extends JpaRepository<Lecture, Long>, LectureRepositoryCustom {

	@Query("SELECT l FROM Lecture l "
		+ "JOIN FETCH l.schedules "
		+ "WHERE l.id = :lectureId")
	Optional<Lecture> findWithSchedule(Long lectureId);

	@Query("SELECT l FROM Lecture l "
		+ "JOIN FETCH l.schedules "
		+ "WHERE l.lectureNumber = :lectureNumber")
	Optional<Lecture> findByNumberWithSchedule(Long lectureNumber);

}
