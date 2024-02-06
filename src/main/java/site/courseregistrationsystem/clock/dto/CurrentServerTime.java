package site.courseregistrationsystem.clock.dto;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class CurrentServerTime {

	private final LocalDateTime currentServerTime;

}
