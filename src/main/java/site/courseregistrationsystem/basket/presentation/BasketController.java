package site.courseregistrationsystem.basket.presentation;

import java.time.Year;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import site.courseregistrationsystem.basket.application.BasketService;
import site.courseregistrationsystem.basket.dto.BasketList;
import site.courseregistrationsystem.basket.dto.DeletedBasket;
import site.courseregistrationsystem.basket.dto.SelectedLecture;
import site.courseregistrationsystem.lecture.Semester;
import site.courseregistrationsystem.util.api.ApiResponse;
import site.courseregistrationsystem.util.api.ResponseMessage;
import site.courseregistrationsystem.util.resolver.Login;

@RestController
@RequiredArgsConstructor
public class BasketController {

	private final BasketService basketService;

	@PostMapping("/baskets/{lectureId}")
	public ApiResponse<SelectedLecture> addLectureToBasket(@Login Long studentPk, @PathVariable Long lectureId) {
		Long selectedLectureId = basketService.addLectureToBasket(Year.now(), Semester.getCurrentSemester(), studentPk, lectureId);
		SelectedLecture selectedLecture = new SelectedLecture(selectedLectureId);

		return ApiResponse.ok(ResponseMessage.BASKET_ADD_SUCCESS.getMessage(), selectedLecture);
	}

	@GetMapping("/baskets")
	public ApiResponse<BasketList> fetchBaskets(@Login Long studentPk) {
		BasketList basketList = basketService.fetchBaskets(studentPk);

		return ApiResponse.ok(ResponseMessage.BASKET_FETCH_SUCCESS.getMessage(), basketList);
	}

	@DeleteMapping("/baskets/{basketId}")
	public ApiResponse<DeletedBasket> deleteBasket(@Login Long studentPk, @PathVariable Long basketId) {
		Long deletedBasketId = basketService.deleteBasket(studentPk, basketId);
		DeletedBasket deletedBasket = new DeletedBasket(deletedBasketId);

		return ApiResponse.ok(ResponseMessage.BASKET_DELETE_SUCCESS.getMessage(), deletedBasket);
	}

}
