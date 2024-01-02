package site.courseregistrationsystem.basket.presentation;

import static org.mockito.BDDMockito.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.*;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.restdocs.payload.JsonFieldType;

import jakarta.servlet.http.Cookie;
import site.courseregistrationsystem.RestDocsSupport;
import site.courseregistrationsystem.exception.basket.DuplicateBasketException;
import site.courseregistrationsystem.exception.basket.ExceededCreditLimitException;

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
			.willThrow(new ExceededCreditLimitException());

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
}
