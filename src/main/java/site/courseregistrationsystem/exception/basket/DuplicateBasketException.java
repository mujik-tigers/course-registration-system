package site.courseregistrationsystem.exception.basket;

import site.courseregistrationsystem.exception.CustomException;
import site.courseregistrationsystem.exception.ErrorType;

public class DuplicateBasketException extends CustomException {

	public DuplicateBasketException() {
		super(ErrorType.BASKET_DUPLICATION);
	}

}
