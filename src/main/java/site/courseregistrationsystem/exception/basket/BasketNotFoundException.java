package site.courseregistrationsystem.exception.basket;

import site.courseregistrationsystem.exception.CustomException;
import site.courseregistrationsystem.exception.ErrorType;

public class BasketNotFoundException extends CustomException {

	public BasketNotFoundException() {
		super(ErrorType.BASKET_NONEXISTENT);
	}

}
