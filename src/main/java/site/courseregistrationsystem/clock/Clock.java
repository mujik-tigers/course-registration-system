package site.courseregistrationsystem.clock;

import static site.courseregistrationsystem.util.ProjectConstant.*;

import java.time.Year;

import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import site.courseregistrationsystem.lecture.Semester;

@RedisHash("currentClock")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Clock {

	@Id
	private String current = CLOCK_ID;

	private int year;
	private String semester;

	@Builder
	private Clock(Year year, Semester semester) {
		this.year = year.getValue();
		this.semester = semester.name();
	}

}
