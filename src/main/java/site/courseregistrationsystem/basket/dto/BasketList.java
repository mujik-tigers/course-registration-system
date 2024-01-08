package site.courseregistrationsystem.basket.dto;

import java.util.List;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class BasketList {

	private final List<BasketDetail> baskets;

}
