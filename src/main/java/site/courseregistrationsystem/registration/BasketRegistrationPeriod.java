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

@RedisHash("basketRegistrationPeriod")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class BasketRegistrationPeriod {

	@Id
	private String targetGrade = Grade.COMMON.name();

	private LocalDateTime startTime;
	private LocalDateTime endTime;
	private int year;
	private String semester;

	public boolean isWithinTimeRange(LocalDateTime now) {
		return startTime.compareTo(now) <= 0 && now.compareTo(endTime) <= 0;
	}

	@Builder
	private BasketRegistrationPeriod(LocalDateTime startTime, LocalDateTime endTime, Semester semester) {
		this.startTime = startTime;
		this.endTime = endTime;
		this.year = startTime.getYear();
		this.semester = semester.name();
	}

}
