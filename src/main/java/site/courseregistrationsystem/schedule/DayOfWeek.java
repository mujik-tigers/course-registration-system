package site.courseregistrationsystem.schedule;

import lombok.Getter;

@Getter
public enum DayOfWeek {

	MON("월"),
	TUE("화"),
	WED("수"),
	THU("목"),
	FRI("금"),
	SAT("토");

	private final String description;

	DayOfWeek(String description) {
		this.description = description;
	}

}
