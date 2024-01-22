package site.courseregistrationsystem.clock.presentation;

import static org.mockito.BDDMockito.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.*;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.Year;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.restdocs.payload.JsonFieldType;

import jakarta.servlet.http.Cookie;
import site.courseregistrationsystem.RestDocsSupport;
import site.courseregistrationsystem.clock.Clock;
import site.courseregistrationsystem.clock.dto.CurrentYearAndSemester;
import site.courseregistrationsystem.exception.clock.NonexistenceClockException;
import site.courseregistrationsystem.lecture.Semester;

class ClockControllerTest extends RestDocsSupport {

	@Test
	@DisplayName("현재 서버시간 조회 : 성공")
	void checkCurrentServerTime() throws Exception {
		// given
		String COOKIE_NAME = "SESSIONID";
		String COOKIE_VALUE = "03166dc4-2c82-4e55-85f5-f47919f367a6";
		Cookie sessionCookie = new Cookie(COOKIE_NAME, COOKIE_VALUE);

		// when & then
		mockMvc.perform(get("/clock/server")
				.cookie(sessionCookie))
			.andDo(print())
			.andExpect(status().isOk())
			.andDo(document("current-server-time-fetch-success",
				preprocessRequest(prettyPrint()),
				preprocessResponse(prettyPrint()),
				responseFields(
					fieldWithPath("code").type(JsonFieldType.NUMBER).description("코드"),
					fieldWithPath("status").type(JsonFieldType.STRING).description("상태"),
					fieldWithPath("message").type(JsonFieldType.STRING).description("메시지"),
					fieldWithPath("data").type(JsonFieldType.OBJECT).description("응답 데이터"),
					fieldWithPath("data.currentServerTime").type(JsonFieldType.STRING).description("날짜 및 시간")
				)
			));
	}

	@Test
	@DisplayName("현재 서버시간 조회 : 실패")
	void checkFailCurrentServerTime() throws Exception {
		// when & then
		mockMvc.perform(get("/clock/server"))
			.andDo(print())
			.andExpect(status().isUnauthorized())
			.andDo(document("current-server-time-fetch-fail",
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
	@DisplayName("남은 세션 시간 조회 : 성공")
	void fetchSeverRemainingTime() throws Exception {
		// given
		String COOKIE_NAME = "SESSIONID";
		String COOKIE_VALUE = "03166dc4-2c82-4e55-85f5-f47919f367a6";
		Cookie sessionCookie = new Cookie(COOKIE_NAME, COOKIE_VALUE);

		// when & then
		mockMvc.perform(get("/clock/session")
				.cookie(sessionCookie))
			.andDo(print())
			.andExpect(status().isOk())
			.andDo(document("server-remaining-time-fetch-success",
				preprocessRequest(prettyPrint()),
				preprocessResponse(prettyPrint()),
				responseFields(
					fieldWithPath("code").type(JsonFieldType.NUMBER).description("코드"),
					fieldWithPath("status").type(JsonFieldType.STRING).description("상태"),
					fieldWithPath("message").type(JsonFieldType.STRING).description("메시지"),
					fieldWithPath("data").type(JsonFieldType.OBJECT).description("응답 데이터"),
					fieldWithPath("data.sessionRemainingTime").type(JsonFieldType.NUMBER).description("남은 세션 시간")
				)
			));
	}

	@Test
	@DisplayName("현재 년도와 학기 조회 : 성공")
	void fetchCurrentYearAndSemester() throws Exception {
		// given
		String COOKIE_NAME = "SESSIONID";
		String COOKIE_VALUE = "03166dc4-2c82-4e55-85f5-f47919f367a6";
		Cookie sessionCookie = new Cookie(COOKIE_NAME, COOKIE_VALUE);

		Clock clock = Clock.builder()
			.year(Year.of(2024))
			.semester(Semester.FIRST)
			.build();
		CurrentYearAndSemester currentYearAndSemester = new CurrentYearAndSemester(clock);

		given(clockService.fetchCurrentClock())
			.willReturn(currentYearAndSemester);

		// when & then
		mockMvc.perform(get("/clock/current-year-and-semester")
				.cookie(sessionCookie))
			.andDo(print())
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.data.year").value(clock.getYear()))
			.andExpect(jsonPath("$.data.semester").value(clock.getSemester()))
			.andDo(document("current-year-and-semester-fetch-success",
				preprocessRequest(prettyPrint()),
				preprocessResponse(prettyPrint()),
				responseFields(
					fieldWithPath("code").type(JsonFieldType.NUMBER).description("코드"),
					fieldWithPath("status").type(JsonFieldType.STRING).description("상태"),
					fieldWithPath("message").type(JsonFieldType.STRING).description("메시지"),
					fieldWithPath("data").type(JsonFieldType.OBJECT).description("응답 데이터"),
					fieldWithPath("data.year").type(JsonFieldType.NUMBER).description("현재 년도"),
					fieldWithPath("data.semester").type(JsonFieldType.STRING).description("현재 학기")
				)
			));
	}

	@Test
	@DisplayName("현재 년도와 학기 조회 : 실패 - 등록 되지 않은 현재 시간 정보")
	void nonexistenceCurrentYearAndSemester() throws Exception {
		// given
		String COOKIE_NAME = "SESSIONID";
		String COOKIE_VALUE = "03166dc4-2c82-4e55-85f5-f47919f367a6";
		Cookie sessionCookie = new Cookie(COOKIE_NAME, COOKIE_VALUE);

		given(clockService.fetchCurrentClock())
			.willThrow(new NonexistenceClockException());

		// when & then
		mockMvc.perform(get("/clock/current-year-and-semester")
				.cookie(sessionCookie))
			.andDo(print())
			.andExpect(status().isBadRequest())
			.andDo(document("current-year-and-semester-fetch-fail",
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
