package site.courseregistrationsystem.basket.presentation;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import site.courseregistrationsystem.basket.application.BasketService;
import site.courseregistrationsystem.basket.dto.SelectedLecture;
import site.courseregistrationsystem.util.api.ApiResponse;
import site.courseregistrationsystem.util.api.ResponseMessage;
import site.courseregistrationsystem.util.resolver.Login;

@RestController
@RequiredArgsConstructor
public class BasketController {

	private final BasketService basketService;

	@PostMapping("/baskets/{lectureId}")
	public ApiResponse<SelectedLecture> addLectureToBasket(@Login Long studentPk, @PathVariable Long lectureId) {
		Long selectedLectureId = basketService.addLectureToBasket(studentPk, lectureId);
		SelectedLecture selectedLecture = new SelectedLecture(selectedLectureId);

		return ApiResponse.ok(ResponseMessage.BASKET_ADD_SUCCESS.getMessage(), selectedLecture);
	}

}
