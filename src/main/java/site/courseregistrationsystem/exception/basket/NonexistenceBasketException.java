package site.courseregistrationsystem.exception.basket;

import site.courseregistrationsystem.exception.CustomException;
import site.courseregistrationsystem.exception.ErrorType;

public class NonexistenceBasketException extends CustomException {

	public NonexistenceBasketException() {
		super(ErrorType.NONEXISTENT_BASKET);
	}

}
