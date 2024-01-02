package site.courseregistrationsystem.enrollment.application;

import java.time.Year;
import java.util.List;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import site.courseregistrationsystem.enrollment.Enrollment;
import site.courseregistrationsystem.enrollment.infrastructure.EnrollmentRepository;
import site.courseregistrationsystem.exception.enrollment.DuplicateEnrollmentException;
import site.courseregistrationsystem.exception.enrollment.EnrollmentLimitExceededException;
import site.courseregistrationsystem.exception.enrollment.ScheduleConflictException;
import site.courseregistrationsystem.exception.lecture.NonexistenceLectureException;
import site.courseregistrationsystem.exception.student.NonexistenceStudentException;
import site.courseregistrationsystem.lecture.Lecture;
import site.courseregistrationsystem.lecture.Semester;
import site.courseregistrationsystem.lecture.infrastructure.LectureRepository;
import site.courseregistrationsystem.student.Student;
import site.courseregistrationsystem.student.infrastructure.StudentRepository;

@Service
@RequiredArgsConstructor
public class EnrollmentService {

	private static final int MAX_CREDITS_PER_SEMESTER = 18;

	private final EnrollmentRepository enrollmentRepository;
	private final StudentRepository studentRepository;
	private final LectureRepository lectureRepository;

	public Long enrollLecture(Long studentPk, Long lectureId) {
		Student student = studentRepository.findById(studentPk).orElseThrow(NonexistenceStudentException::new);
		Lecture lecture = lectureRepository.findWithSchedule(lectureId).orElseThrow(NonexistenceLectureException::new);

		Year openingYear = lecture.getOpeningYear();
		Semester semester = lecture.getSemester();

		List<Enrollment> enrollments = enrollmentRepository.findAllInCurrentSemester(studentPk, openingYear, semester);

		int creditsForCurrentSemester = enrollments.stream()
			.mapToInt(Enrollment::fetchCredits)
			.sum();

		if (creditsForCurrentSemester >= MAX_CREDITS_PER_SEMESTER) {
			throw new EnrollmentLimitExceededException();
		}

		boolean isEnrollmentExist = enrollments.stream()
			.anyMatch(enrollment -> enrollment.fetchSubjectId().equals(lecture.fetchSubjectId()));

		if (isEnrollmentExist) {
			throw new DuplicateEnrollmentException();
		}

		boolean hasEnrollmentConflict = enrollments.stream()
			.map(Enrollment::getLecture)
			.anyMatch(l -> l.hasScheduleConflict(lecture));

		if (hasEnrollmentConflict) {
			throw new ScheduleConflictException();
		}

		Enrollment newEnrollment = Enrollment.builder()
			.student(student)
			.lecture(lecture)
			.build();

		Enrollment savedEnrollment = enrollmentRepository.save(newEnrollment);

		return savedEnrollment.fetchLectureId();
	}

}
