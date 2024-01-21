package site.courseregistrationsystem.registration;

import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import site.courseregistrationsystem.lecture.Semester;
import site.courseregistrationsystem.student.Grade;

@RedisHash("enrollmentRegistrationPeriod")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class EnrollmentRegistrationPeriod {

	@Id
	private String targetGrade;
	private LocalDateTime startTime;
	private LocalDateTime endTime;
	private int year;
	private String semester;

	@Builder
	private EnrollmentRegistrationPeriod(Grade targetGrade, LocalDateTime startTime, LocalDateTime endTime, Semester semester) {
		this.targetGrade = targetGrade.name();
		this.startTime = startTime;
		this.endTime = endTime;
		this.year = startTime.getYear();
		this.semester = semester.name();
	}

	public boolean isWithinTimeRange(LocalDateTime now) {
		return startTime.compareTo(now) <= 0 && now.compareTo(endTime) <= 0;
	}

}
