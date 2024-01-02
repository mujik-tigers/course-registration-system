package site.courseregistrationsystem.enrollment.application;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import site.courseregistrationsystem.enrollment.infrastructure.EnrollmentRepository;
import site.courseregistrationsystem.exception.lecture.NonexistenceLectureException;
import site.courseregistrationsystem.exception.student.NonexistenceStudentException;
import site.courseregistrationsystem.lecture.Lecture;
import site.courseregistrationsystem.lecture.infrastructure.LectureRepository;
import site.courseregistrationsystem.student.Student;
import site.courseregistrationsystem.student.infrastructure.StudentRepository;

@Service
@RequiredArgsConstructor
public class EnrollmentService {

	private final EnrollmentRepository enrollmentRepository;
	private final StudentRepository studentRepository;
	private final LectureRepository lectureRepository;

	public Long enrollLecture(Long studentPk, Long lectureId) {
		Student student = studentRepository.findById(studentPk).orElseThrow(NonexistenceStudentException::new);
		Lecture lecture = lectureRepository.findById(lectureId).orElseThrow(NonexistenceLectureException::new);
		return null;
	}

}
