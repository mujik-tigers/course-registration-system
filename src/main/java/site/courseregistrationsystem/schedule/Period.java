package site.courseregistrationsystem.schedule;

import lombok.Getter;

@Getter
public enum Period {

	ONE(1),
	TWO(2),
	THREE(3),
	FOUR(4),
	FIVE(5),
	SIX(6),
	SEVEN(7),
	EIGHT(8),
	NINE(9);

	private final int periodNumber;

	Period(int periodNumber) {
		this.periodNumber = periodNumber;
	}

}
