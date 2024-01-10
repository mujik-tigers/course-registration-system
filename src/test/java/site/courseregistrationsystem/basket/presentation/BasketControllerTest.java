package site.courseregistrationsystem.basket.presentation;

import static org.mockito.BDDMockito.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.*;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.restdocs.payload.JsonFieldType;

import jakarta.servlet.http.Cookie;
import site.courseregistrationsystem.RestDocsSupport;
import site.courseregistrationsystem.basket.Basket;
import site.courseregistrationsystem.basket.dto.BasketDetail;
import site.courseregistrationsystem.basket.dto.BasketList;
import site.courseregistrationsystem.department.Department;
import site.courseregistrationsystem.exception.basket.DuplicateBasketException;
import site.courseregistrationsystem.exception.basket.NonexistenceBasketException;
import site.courseregistrationsystem.exception.credit.CreditLimitExceededException;
import site.courseregistrationsystem.exception.schedule.ScheduleConflictException;
import site.courseregistrationsystem.lecture.Lecture;
import site.courseregistrationsystem.professor.Professor;
import site.courseregistrationsystem.schedule.DayOfWeek;
import site.courseregistrationsystem.schedule.Period;
import site.courseregistrationsystem.schedule.Schedule;
import site.courseregistrationsystem.student.Grade;
import site.courseregistrationsystem.subject.Subject;
import site.courseregistrationsystem.subject.SubjectDivision;

class BasketControllerTest extends RestDocsSupport {

	@Test
	@DisplayName("수강 바구니 담기 : 성공")
	void addLectureToBasket() throws Exception {
		// given
		String COOKIE_NAME = "SESSIONID";
		String COOKIE_VALUE = "03166dc4-2c82-4e55-85f5-f47919f367a6";
		Cookie sessionCookie = new Cookie(COOKIE_NAME, COOKIE_VALUE);

		Long LECTURE_ID = 1L;

		given(basketService.addLectureToBasket(anyLong(), anyLong()))
			.willReturn(LECTURE_ID);

		// when & then
		mockMvc.perform(post("/baskets/{lectureId}", LECTURE_ID)
				.cookie(sessionCookie))
			.andDo(print())
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.data.lectureId").value(LECTURE_ID))
			.andDo(document("basket-save-success",
				preprocessRequest(prettyPrint()),
				preprocessResponse(prettyPrint()),
				responseFields(
					fieldWithPath("code").type(JsonFieldType.NUMBER).description("코드"),
					fieldWithPath("status").type(JsonFieldType.STRING).description("상태"),
					fieldWithPath("message").type(JsonFieldType.STRING).description("메시지"),
					fieldWithPath("data").type(JsonFieldType.OBJECT).description("응답 데이터"),
					fieldWithPath("data.lectureId").type(JsonFieldType.NUMBER).description("담은 강의 id")
				)
			));
	}

	@Test
	@DisplayName("수강 바구니 담기 : 실패 - 중복 과목 담기")
	void addDuplicateSubjectToBasket() throws Exception {
		// given
		String COOKIE_NAME = "SESSIONID";
		String COOKIE_VALUE = "03166dc4-2c82-4e55-85f5-f47919f367a6";
		Cookie sessionCookie = new Cookie(COOKIE_NAME, COOKIE_VALUE);

		Long LECTURE_ID = 1L;

		given(basketService.addLectureToBasket(anyLong(), anyLong()))
			.willThrow(new DuplicateBasketException());

		// when & then
		mockMvc.perform(post("/baskets/{lectureId}", LECTURE_ID)
				.cookie(sessionCookie))
			.andDo(print())
			.andExpect(status().isBadRequest())
			.andDo(document("basket-save-duplicate-fail",
				preprocessRequest(prettyPrint()),
				preprocessResponse(prettyPrint()),
				responseFields(
					fieldWithPath("code").type(JsonFieldType.NUMBER).description("코드"),
					fieldWithPath("status").type(JsonFieldType.STRING).description("상태"),
					fieldWithPath("message").type(JsonFieldType.STRING).description("메시지"),
					fieldWithPath("data").type(JsonFieldType.NULL).description("응답 데이터")
				)
			));
	}

	@Test
	@DisplayName("수강 바구니 담기 : 실패 - 한 학기 제한 학점 초과")
	void exceededDefaultCreditLimit() throws Exception {
		// given
		String COOKIE_NAME = "SESSIONID";
		String COOKIE_VALUE = "03166dc4-2c82-4e55-85f5-f47919f367a6";
		Cookie sessionCookie = new Cookie(COOKIE_NAME, COOKIE_VALUE);

		Long LECTURE_ID = 1L;

		given(basketService.addLectureToBasket(anyLong(), anyLong()))
			.willThrow(new CreditLimitExceededException());

		// when & then
		mockMvc.perform(post("/baskets/{lectureId}", LECTURE_ID)
				.cookie(sessionCookie))
			.andDo(print())
			.andExpect(status().isBadRequest())
			.andDo(document("basket-save-exceeded-fail",
				preprocessRequest(prettyPrint()),
				preprocessResponse(prettyPrint()),
				responseFields(
					fieldWithPath("code").type(JsonFieldType.NUMBER).description("코드"),
					fieldWithPath("status").type(JsonFieldType.STRING).description("상태"),
					fieldWithPath("message").type(JsonFieldType.STRING).description("메시지"),
					fieldWithPath("data").type(JsonFieldType.NULL).description("응답 데이터")
				)
			));
	}

	@Test
	@DisplayName("수강 바구니 담기 : 실패 - 시간표 겹침")
	void conflictSchedule() throws Exception {
		// given
		String COOKIE_NAME = "SESSIONID";
		String COOKIE_VALUE = "03166dc4-2c82-4e55-85f5-f47919f367a6";
		Cookie sessionCookie = new Cookie(COOKIE_NAME, COOKIE_VALUE);

		Long LECTURE_ID = 1L;

		given(basketService.addLectureToBasket(anyLong(), anyLong()))
			.willThrow(new ScheduleConflictException());

		// when & then
		mockMvc.perform(post("/baskets/{lectureId}", LECTURE_ID)
				.cookie(sessionCookie))
			.andDo(print())
			.andExpect(status().isBadRequest())
			.andDo(document("basket-save-conflict-fail",
				preprocessRequest(prettyPrint()),
				preprocessResponse(prettyPrint()),
				responseFields(
					fieldWithPath("code").type(JsonFieldType.NUMBER).description("코드"),
					fieldWithPath("status").type(JsonFieldType.STRING).description("상태"),
					fieldWithPath("message").type(JsonFieldType.STRING).description("메시지"),
					fieldWithPath("data").type(JsonFieldType.NULL).description("응답 데이터")
				)
			));
	}

	@Test
	@DisplayName("수강 바구니 목록 조회 : 성공")
	void fetchBasketList() throws Exception {
		// given
		String COOKIE_NAME = "SESSIONID";
		String COOKIE_VALUE = "03166dc4-2c82-4e55-85f5-f47919f367a6";
		Cookie sessionCookie = new Cookie(COOKIE_NAME, COOKIE_VALUE);

		List<BasketDetail> baskets = List.of(
			createBasketDetail("선형대수학", 5839, "공A401", DayOfWeek.MON),
			createBasketDetail("미분적분학", 3891, "공B103", DayOfWeek.FRI)
		);
		BasketList basketList = new BasketList(baskets);

		given(basketService.fetchBaskets(any()))
			.willReturn(basketList);

		// when & then
		mockMvc.perform(get("/baskets")
				.cookie(sessionCookie))
			.andDo(print())
			.andExpect(status().isOk())
			.andDo(document("basket-fetch-success",
				preprocessRequest(prettyPrint()),
				preprocessResponse(prettyPrint()),
				responseFields(
					fieldWithPath("code").type(JsonFieldType.NUMBER).description("코드"),
					fieldWithPath("status").type(JsonFieldType.STRING).description("상태"),
					fieldWithPath("message").type(JsonFieldType.STRING).description("메시지"),
					fieldWithPath("data").type(JsonFieldType.OBJECT).description("응답 데이터"),
					fieldWithPath("data.baskets").type(JsonFieldType.ARRAY).description("수강 바구니 목록"),
					fieldWithPath("data.baskets[].targetGrade").type(JsonFieldType.NUMBER).description("수강 학년"),
					fieldWithPath("data.baskets[].subjectDivision").type(JsonFieldType.STRING).description("과목 분류"),
					fieldWithPath("data.baskets[].lectureNumber").type(JsonFieldType.NUMBER).description("과목 번호"),
					fieldWithPath("data.baskets[].subjectName").type(JsonFieldType.STRING).description("과목명"),
					fieldWithPath("data.baskets[].credits").type(JsonFieldType.NUMBER).description("학점"),
					fieldWithPath("data.baskets[].hoursPerWeek").type(JsonFieldType.NUMBER).description("주 수업 시간"),
					fieldWithPath("data.baskets[].schedule").type(JsonFieldType.STRING).description("강의 시간"),
					fieldWithPath("data.baskets[].professorName").type(JsonFieldType.STRING).description("교수명"),
					fieldWithPath("data.baskets[].totalCapacity").type(JsonFieldType.NUMBER).description("전체 수강 인원")
				)
			));
	}

	@Test
	@DisplayName("수강 바구니 삭제 : 성공")
	void deleteBasket() throws Exception {
		// given
		String COOKIE_NAME = "SESSIONID";
		String COOKIE_VALUE = "03166dc4-2c82-4e55-85f5-f47919f367a6";
		Cookie sessionCookie = new Cookie(COOKIE_NAME, COOKIE_VALUE);

		Long DELETED_BASKET_ID = 1L;

		given(basketService.deleteBasket(anyLong(), anyLong()))
			.willReturn(DELETED_BASKET_ID);

		// when & then
		mockMvc.perform(delete("/baskets/{basketId}", DELETED_BASKET_ID)
				.cookie(sessionCookie))
			.andDo(print())
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.data.deletedBasketId").value(DELETED_BASKET_ID))
			.andDo(document("basket-delete-success",
				preprocessRequest(prettyPrint()),
				preprocessResponse(prettyPrint()),
				responseFields(
					fieldWithPath("code").type(JsonFieldType.NUMBER).description("코드"),
					fieldWithPath("status").type(JsonFieldType.STRING).description("상태"),
					fieldWithPath("message").type(JsonFieldType.STRING).description("메시지"),
					fieldWithPath("data").type(JsonFieldType.OBJECT).description("응답 데이터"),
					fieldWithPath("data.deletedBasketId").type(JsonFieldType.NUMBER).description("삭제한 수강 바구니 id")
				)
			));
	}

	@Test
	@DisplayName("수강 바구니 삭제 : 실패 - 수강 바구니에 존재하지 않는 강의")
	void deleteNonexistenceBasket() throws Exception {
		// given
		String COOKIE_NAME = "SESSIONID";
		String COOKIE_VALUE = "03166dc4-2c82-4e55-85f5-f47919f367a6";
		Cookie sessionCookie = new Cookie(COOKIE_NAME, COOKIE_VALUE);

		Long DELETED_BASKET_ID = 1L;

		given(basketService.deleteBasket(anyLong(), anyLong()))
			.willThrow(new NonexistenceBasketException());

		// when & then
		mockMvc.perform(delete("/baskets/{basketId}", DELETED_BASKET_ID)
				.cookie(sessionCookie))
			.andDo(print())
			.andExpect(status().isBadRequest())
			.andDo(document("basket-delete-fail",
				preprocessRequest(prettyPrint()),
				preprocessResponse(prettyPrint()),
				responseFields(
					fieldWithPath("code").type(JsonFieldType.NUMBER).description("코드"),
					fieldWithPath("status").type(JsonFieldType.STRING).description("상태"),
					fieldWithPath("message").type(JsonFieldType.STRING).description("메시지"),
					fieldWithPath("data").type(JsonFieldType.NULL).description("응답 데이터")
				)
			));
	}

	private BasketDetail createBasketDetail(String subjectName, int lectureNumber, String lectureRoom, DayOfWeek dayOfWeek) {
		Professor professor = new Professor("김서연");

		Department department = new Department("전기전자공학부");

		Subject subject = Subject.builder()
			.name(subjectName)
			.subjectDivision(SubjectDivision.ME)
			.credits(3)
			.targetGrade(Grade.SENIOR)
			.hoursPerWeek(3)
			.build();

		Lecture lecture = Lecture.builder()
			.lectureNumber(lectureNumber)
			.professor(professor)
			.lectureRoom(lectureRoom)
			.department(department)
			.totalCapacity(40)
			.subject(subject)
			.build();

		Schedule schedule = Schedule.builder()
			.dayOfWeek(dayOfWeek)
			.firstPeriod(Period.FOUR)
			.lastPeriod(Period.NINE)
			.build();

		lecture.addSchedule(schedule);

		Basket basket = Basket.builder()
			.lecture(lecture)
			.build();

		return new BasketDetail(basket);
	}

}
