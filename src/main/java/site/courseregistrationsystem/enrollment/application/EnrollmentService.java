package site.courseregistrationsystem.enrollment.application;

import static site.courseregistrationsystem.util.ProjectConstant.*;

import java.time.LocalDateTime;
import java.time.Year;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import site.courseregistrationsystem.enrollment.Enrollment;
import site.courseregistrationsystem.enrollment.dto.EnrolledLecture;
import site.courseregistrationsystem.enrollment.dto.EnrolledLectureDetail;
import site.courseregistrationsystem.enrollment.dto.EnrolledLectures;
import site.courseregistrationsystem.enrollment.dto.EnrollmentCapacity;
import site.courseregistrationsystem.enrollment.infrastructure.EnrollmentRepository;
import site.courseregistrationsystem.exception.credit.CreditLimitExceededException;
import site.courseregistrationsystem.exception.enrollment.DuplicateEnrollmentException;
import site.courseregistrationsystem.exception.enrollment.EnrollmentNotFoundException;
import site.courseregistrationsystem.exception.enrollment.LectureApplicantsLimitExceededException;
import site.courseregistrationsystem.exception.enrollment.LectureNotInCurrentSemesterException;
import site.courseregistrationsystem.exception.lecture.LectureNotFoundException;
import site.courseregistrationsystem.exception.schedule.ScheduleConflictException;
import site.courseregistrationsystem.exception.student.StudentNotFoundException;
import site.courseregistrationsystem.lecture.Lecture;
import site.courseregistrationsystem.lecture.Semester;
import site.courseregistrationsystem.lecture.infrastructure.LectureRepository;
import site.courseregistrationsystem.registration.application.EnrollmentRegistrationPeriodService;
import site.courseregistrationsystem.registration.dto.RegistrationDate;
import site.courseregistrationsystem.student.Student;
import site.courseregistrationsystem.student.infrastructure.StudentRepository;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class EnrollmentService {

	private final EnrollmentRegistrationPeriodService enrollmentRegistrationPeriodService;

	private final EnrollmentRepository enrollmentRepository;
	private final StudentRepository studentRepository;
	private final LectureRepository lectureRepository;

	@Transactional
	public EnrolledLecture enrollLecture(LocalDateTime now, Long studentPk, Long lectureId) {
		Student student = studentRepository.findById(studentPk).orElseThrow(StudentNotFoundException::new);
		Lecture lecture = lectureRepository.findWithSchedule(lectureId).orElseThrow(LectureNotFoundException::new);

		Enrollment savedEnrollment = enroll(now, student, lecture);

		return new EnrolledLecture(savedEnrollment.fetchLectureId());
	}

	@Transactional
	public EnrolledLecture enrollLectureByNumber(LocalDateTime now, Long studentPk, Integer lectureNumber) {
		Student student = studentRepository.findById(studentPk).orElseThrow(StudentNotFoundException::new);
		Lecture lecture = lectureRepository.findByNumberWithSchedule(lectureNumber)
			.orElseThrow(LectureNotFoundException::new);

		Enrollment savedEnrollment = enroll(now, student, lecture);

		return new EnrolledLecture(savedEnrollment.fetchLectureId());
	}

	private Enrollment enroll(LocalDateTime now, Student student, Lecture lecture) {
		List<Enrollment> enrollments = enrollmentRepository.findAllBy(student.getId());
		RegistrationDate registrationDate = enrollmentRegistrationPeriodService.validateEnrollmentRegistrationPeriod(now, student.getGrade());

		checkLectureInCurrentSemester(registrationDate.getYear(), registrationDate.getSemester(), lecture);
		checkLectureApplicantsLimit(lecture);
		checkCreditsLimit(enrollments, lecture.fetchCredits());
		checkDuplicateSubject(enrollments, lecture);
		checkScheduleConflict(enrollments, lecture);

		Enrollment newEnrollment = Enrollment.builder()
			.student(student)
			.lecture(lecture)
			.build();

		return enrollmentRepository.save(newEnrollment);
	}

	private void checkLectureInCurrentSemester(Year year, Semester semester, Lecture lecture) {
		if (!lecture.hasSameSemester(year, semester)) {
			throw new LectureNotInCurrentSemesterException();
		}
	}

	private void checkLectureApplicantsLimit(Lecture lecture) {
		int applicants = enrollmentRepository.countByLecture(lecture);

		if (lecture.getTotalCapacity() < applicants + 1) {
			throw new LectureApplicantsLimitExceededException();
		}
	}

	private void checkCreditsLimit(List<Enrollment> enrollments, int creditsToAdd) {
		int creditsForCurrentSemester = enrollments.stream()
			.mapToInt(Enrollment::fetchCredits)
			.sum();

		if (creditsForCurrentSemester + creditsToAdd > DEFAULT_CREDIT_LIMIT) {
			throw new CreditLimitExceededException();
		}
	}

	private void checkDuplicateSubject(List<Enrollment> enrollments, Lecture lecture) {
		boolean isEnrollmentExist = enrollments.stream()
			.map(Enrollment::getLecture)
			.map(Lecture::getSubject)
			.anyMatch(subject -> subject.equals(lecture.getSubject()));

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

	@Transactional
	public void cancel(Long studentPk, Long enrollmentId) {
		studentRepository.findById(studentPk).orElseThrow(StudentNotFoundException::new);

		int deleted = enrollmentRepository.deleteByIdAndStudent(enrollmentId, studentPk);

		if (deleted == 0) {
			throw new EnrollmentNotFoundException();
		}
	}

	public EnrolledLectures fetchAll(Long studentPk) {
		Student student = studentRepository.findById(studentPk).orElseThrow(StudentNotFoundException::new);

		List<EnrolledLectureDetail> enrolledLectures = enrollmentRepository.findAllBy(student.getId()).stream()
			.map(EnrolledLectureDetail::new)
			.toList();

		return new EnrolledLectures(enrolledLectures);
	}

	public EnrollmentCapacity fetchCountBy(Year year, Semester semester, Long lectureId) {
		Lecture lecture = lectureRepository.findWithSchedule(lectureId).orElseThrow(LectureNotFoundException::new);

		checkLectureInCurrentSemester(year, semester, lecture);

		int currentEnrollmentCount = enrollmentRepository.countByLecture(lecture);

		return new EnrollmentCapacity(lecture.getTotalCapacity(), currentEnrollmentCount);
	}

}
