package site.courseregistrationsystem.lecture.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class BasketStoringCount {

	private final int totalCapacity;
	private final int currentBasketStoringCount;

}
