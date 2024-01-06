package site.courseregistrationsystem.enrollment.application;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import site.courseregistrationsystem.enrollment.Enrollment;
import site.courseregistrationsystem.enrollment.dto.EnrolledLecture;
import site.courseregistrationsystem.enrollment.dto.EnrolledLectureDetail;
import site.courseregistrationsystem.enrollment.dto.EnrolledLectures;
import site.courseregistrationsystem.enrollment.infrastructure.EnrollmentRepository;
import site.courseregistrationsystem.exception.auth.UnauthorizedAccessException;
import site.courseregistrationsystem.exception.enrollment.CreditsLimitExceededException;
import site.courseregistrationsystem.exception.enrollment.DuplicateEnrollmentException;
import site.courseregistrationsystem.exception.enrollment.EnrollmentNotFoundException;
import site.courseregistrationsystem.exception.enrollment.LectureNotInCurrentSemesterException;
import site.courseregistrationsystem.exception.enrollment.ScheduleConflictException;
import site.courseregistrationsystem.exception.lecture.NonexistenceLectureException;
import site.courseregistrationsystem.exception.student.NonexistenceStudentException;
import site.courseregistrationsystem.lecture.Lecture;
import site.courseregistrationsystem.lecture.infrastructure.LectureRepository;
import site.courseregistrationsystem.student.Student;
import site.courseregistrationsystem.student.infrastructure.StudentRepository;

@Service
@Transactional
@RequiredArgsConstructor
public class EnrollmentService {

	private static final int MAX_CREDITS_PER_SEMESTER = 18;

	private final EnrollmentRepository enrollmentRepository;
	private final StudentRepository studentRepository;
	private final LectureRepository lectureRepository;

	public EnrolledLecture enrollLecture(Long studentPk, Long lectureId) {
		Student student = studentRepository.findById(studentPk).orElseThrow(NonexistenceStudentException::new);
		Lecture lecture = lectureRepository.findWithSchedule(lectureId).orElseThrow(NonexistenceLectureException::new);

		Enrollment savedEnrollment = enroll(student, lecture);

		return new EnrolledLecture(savedEnrollment.fetchLectureId());
	}

	public EnrolledLecture enrollLectureByNumber(Long studentPk, Integer lectureNumber) {
		Student student = studentRepository.findById(studentPk).orElseThrow(NonexistenceStudentException::new);
		Lecture lecture = lectureRepository.findByNumberWithSchedule(lectureNumber)
			.orElseThrow(NonexistenceLectureException::new);

		Enrollment savedEnrollment = enroll(student, lecture);

		return new EnrolledLecture(savedEnrollment.fetchLectureId());
	}

	private Enrollment enroll(Student student, Lecture lecture) {
		List<Enrollment> enrollments = enrollmentRepository.findAllBy(student.getId());

		checkLectureInCurrentSemester(lecture);
		checkCreditsLimit(enrollments);
		checkDuplicateSubject(enrollments, lecture);
		checkScheduleConflict(enrollments, lecture);

		Enrollment newEnrollment = Enrollment.builder()
			.student(student)
			.lecture(lecture)
			.build();

		return enrollmentRepository.save(newEnrollment);
	}

	private void checkLectureInCurrentSemester(Lecture lecture) {
		if (!lecture.isCurrentSemester()) {
			throw new LectureNotInCurrentSemesterException();
		}
	}

	private void checkCreditsLimit(List<Enrollment> enrollments) {
		int creditsForCurrentSemester = enrollments.stream()
			.mapToInt(Enrollment::fetchCredits)
			.sum();

		if (creditsForCurrentSemester >= MAX_CREDITS_PER_SEMESTER) {
			throw new CreditsLimitExceededException();
		}
	}

	private void checkDuplicateSubject(List<Enrollment> enrollments, Lecture lecture) {
		boolean isEnrollmentExist = enrollments.stream()
			.anyMatch(enrollment -> enrollment.fetchSubjectId().equals(lecture.fetchSubjectId()));

		if (isEnrollmentExist) {
			throw new DuplicateEnrollmentException();
		}
	}

	private void checkScheduleConflict(List<Enrollment> enrollments, Lecture lecture) {
		boolean hasEnrollmentConflict = enrollments.stream()
			.map(Enrollment::getLecture)
			.anyMatch(l -> l.hasScheduleConflict(lecture));

		if (hasEnrollmentConflict) {
			throw new ScheduleConflictException();
		}
	}

	public void cancel(Long studentPk, Long enrollmentId) {
		Enrollment enrollment = enrollmentRepository.findByIdWithStudent(enrollmentId)
			.orElseThrow(EnrollmentNotFoundException::new);

		if (!enrollment.fetchStudentPk().equals(studentPk)) {
			throw new UnauthorizedAccessException();
		}

		enrollmentRepository.deleteById(enrollmentId);
	}

	@Transactional(readOnly = true)
	public EnrolledLectures fetchAll(Long studentPk) {
		Student student = studentRepository.findById(studentPk).orElseThrow(NonexistenceStudentException::new);

		List<EnrolledLectureDetail> enrolledLectures = enrollmentRepository.findAllBy(student.getId()).stream()
			.map(EnrolledLectureDetail::new)
			.toList();

		return new EnrolledLectures(enrolledLectures);
	}

}
