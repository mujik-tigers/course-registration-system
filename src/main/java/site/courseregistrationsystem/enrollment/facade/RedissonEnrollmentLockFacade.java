package site.courseregistrationsystem.enrollment.facade;

import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import site.courseregistrationsystem.enrollment.Enrollment;
import site.courseregistrationsystem.enrollment.application.EnrollmentService;
import site.courseregistrationsystem.enrollment.dto.EnrolledLecture;
import site.courseregistrationsystem.exception.lecture.LectureNotFoundException;
import site.courseregistrationsystem.exception.student.StudentNotFoundException;
import site.courseregistrationsystem.lecture.Lecture;
import site.courseregistrationsystem.lecture.infrastructure.LectureRepository;
import site.courseregistrationsystem.student.Student;
import site.courseregistrationsystem.student.infrastructure.StudentRepository;

@Component
@RequiredArgsConstructor
public class RedissonEnrollmentLockFacade {

	private static final long lockWaitTime = 20;
	private static final long lockLeaseTime = 2;
	private static final TimeUnit lockTimeUnit = TimeUnit.SECONDS;

	private final RedissonClient redissonClient;
	private final StudentRepository studentRepository;
	private final LectureRepository lectureRepository;
	private final EnrollmentService enrollmentService;

	public EnrolledLecture enrollLecture(LocalDateTime now, Long studentPk, Long lectureId) {
		Student student = studentRepository.findById(studentPk).orElseThrow(StudentNotFoundException::new);
		Lecture lecture = lectureRepository.findByIdWithSubjectAndSchedule(lectureId).orElseThrow(LectureNotFoundException::new);

		return enrollWithLock(now, student, lecture);
	}

	public EnrolledLecture enrollLectureByNumber(LocalDateTime now, Long studentPk, Integer lectureNumber) {
		Student student = studentRepository.findById(studentPk).orElseThrow(StudentNotFoundException::new);
		Lecture lecture = lectureRepository.findByNumberWithSubjectAndSchedule(lectureNumber).orElseThrow(LectureNotFoundException::new);

		return enrollWithLock(now, student, lecture);
	}

	private EnrolledLecture enrollWithLock(LocalDateTime now, Student student, Lecture lecture) {
		RLock lock = redissonClient.getLock(lecture.getId().toString());

		try {
			boolean available = lock.tryLock(lockWaitTime, lockLeaseTime, lockTimeUnit);

			if (!available) {
				throw new RuntimeException("수강 신청을 위한 Lock 획득에 실패했습니다");
			}

			Enrollment savedEnrollment = enrollmentService.enroll(now, student, lecture);

			return new EnrolledLecture(savedEnrollment.fetchLectureId());
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		} finally {
			lock.unlock();
		}
	}

}
