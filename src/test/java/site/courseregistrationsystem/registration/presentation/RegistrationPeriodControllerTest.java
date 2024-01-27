package site.courseregistrationsystem.registration.presentation;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.*;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDateTime;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;

import jakarta.servlet.http.Cookie;
import site.courseregistrationsystem.RestDocsSupport;
import site.courseregistrationsystem.exception.registration_period.StartTimeAfterEndTimeException;
import site.courseregistrationsystem.exception.registration_period.StartTimeBeforeCurrentTimeException;
import site.courseregistrationsystem.registration.BasketRegistrationPeriod;
import site.courseregistrationsystem.registration.dto.BasketRegistrationPeriodSaveForm;
import site.courseregistrationsystem.registration.dto.EnrollmentRegistrationPeriodSaveForm;
import site.courseregistrationsystem.student.Grade;

class RegistrationPeriodControllerTest extends RestDocsSupport {

	@Test
	@DisplayName("수강 신청 기간 추가 : 성공")
	void saveEnrollmentRegistrationPeriod() throws Exception {
		// given
		LocalDateTime startTime = LocalDateTime.of(2024, 1, 17, 9, 30, 0);
		LocalDateTime endTime = LocalDateTime.of(2024, 1, 17, 10, 0, 0);

		EnrollmentRegistrationPeriodSaveForm saveForm = new EnrollmentRegistrationPeriodSaveForm(Grade.FRESHMAN, startTime, endTime);

		// when & then
		mockMvc.perform(post("/registration-period/enrollments")
				.content(objectMapper.writeValueAsString(saveForm))
				.contentType(MediaType.APPLICATION_JSON))
			.andDo(print())
			.andExpect(status().isCreated())
			.andDo(document("enrollment-registration-period-save-success",
				preprocessRequest(prettyPrint()),
				preprocessResponse(prettyPrint()),
				requestFields(
					fieldWithPath("grade").type(JsonFieldType.STRING).description("타겟 학년"),
					fieldWithPath("startTime").type(JsonFieldType.VARIES).description("시작 시간"),
					fieldWithPath("endTime").type(JsonFieldType.VARIES).description("종료 시간")
				),
				responseFields(
					fieldWithPath("code").type(JsonFieldType.NUMBER).description("코드"),
					fieldWithPath("status").type(JsonFieldType.STRING).description("상태"),
					fieldWithPath("message").type(JsonFieldType.STRING).description("메시지"),
					fieldWithPath("data").type(JsonFieldType.NULL).description("응답 데이터")
				)
			));
	}

	@Test
	@DisplayName("수강 신청 기간 추가 : 실패 - 현재 시간보다 수강 신청 시작 시간이 더 일찍임")
	void invalidTimeEnrollmentRegistrationPeriod() throws Exception {
		// given
		doThrow(new StartTimeBeforeCurrentTimeException()).when(enrollmentRegistrationPeriodService)
			.saveEnrollmentRegistrationPeriod(any(), any(), any(), any());

		LocalDateTime startTime = LocalDateTime.of(2024, 1, 17, 9, 30, 0);
		LocalDateTime endTime = LocalDateTime.of(2024, 1, 17, 10, 0, 0);

		EnrollmentRegistrationPeriodSaveForm saveForm = new EnrollmentRegistrationPeriodSaveForm(Grade.FRESHMAN, startTime, endTime);

		// when & then
		mockMvc.perform(post("/registration-period/enrollments")
				.content(objectMapper.writeValueAsString(saveForm))
				.contentType(MediaType.APPLICATION_JSON))
			.andDo(print())
			.andExpect(status().isBadRequest())
			.andDo(document("enrollment-registration-period-save-fail-early-start-time",
				preprocessRequest(prettyPrint()),
				preprocessResponse(prettyPrint()),
				requestFields(
					fieldWithPath("grade").type(JsonFieldType.STRING).description("타겟 학년"),
					fieldWithPath("startTime").type(JsonFieldType.VARIES).description("시작 시간"),
					fieldWithPath("endTime").type(JsonFieldType.VARIES).description("종료 시간")
				),
				responseFields(
					fieldWithPath("code").type(JsonFieldType.NUMBER).description("코드"),
					fieldWithPath("status").type(JsonFieldType.STRING).description("상태"),
					fieldWithPath("message").type(JsonFieldType.STRING).description("메시지"),
					fieldWithPath("data").type(JsonFieldType.NULL).description("응답 데이터")
				)
			));
	}

	@Test
	@DisplayName("수강 신청 기간 추가 : 실패 - 시작 시간보다 종료 시간이 더 일찍임")
	void earlyEndTimeEnrollmentRegistrationPeriod() throws Exception {
		// given
		doThrow(new StartTimeAfterEndTimeException()).when(enrollmentRegistrationPeriodService)
			.saveEnrollmentRegistrationPeriod(any(), any(), any(), any());

		LocalDateTime startTime = LocalDateTime.of(2024, 1, 17, 9, 30, 0);
		LocalDateTime endTime = LocalDateTime.of(2024, 1, 17, 10, 0, 0);

		EnrollmentRegistrationPeriodSaveForm saveForm = new EnrollmentRegistrationPeriodSaveForm(Grade.FRESHMAN, startTime, endTime);

		// when & then
		mockMvc.perform(post("/registration-period/enrollments")
				.content(objectMapper.writeValueAsString(saveForm))
				.contentType(MediaType.APPLICATION_JSON))
			.andDo(print())
			.andExpect(status().isBadRequest())
			.andDo(document("enrollment-registration-period-save-fail-early-end-time",
				preprocessRequest(prettyPrint()),
				preprocessResponse(prettyPrint()),
				requestFields(
					fieldWithPath("grade").type(JsonFieldType.STRING).description("타겟 학년"),
					fieldWithPath("startTime").type(JsonFieldType.VARIES).description("시작 시간"),
					fieldWithPath("endTime").type(JsonFieldType.VARIES).description("종료 시간")
				),
				responseFields(
					fieldWithPath("code").type(JsonFieldType.NUMBER).description("코드"),
					fieldWithPath("status").type(JsonFieldType.STRING).description("상태"),
					fieldWithPath("message").type(JsonFieldType.STRING).description("메시지"),
					fieldWithPath("data").type(JsonFieldType.NULL).description("응답 데이터")
				)
			));
	}

	@Test
	@DisplayName("수강 바구니 신청 기간 조회 : 성공")
	void fetchBasketRegistrationPeriod() throws Exception {
		// given
		LocalDateTime startTime = LocalDateTime.of(2024, 1, 17, 9, 30, 1);
		LocalDateTime endTime = LocalDateTime.of(2024, 1, 17, 10, 0, 1);
		BasketRegistrationPeriod basketRegistrationPeriod = createBasketRegistrationPeriod(startTime, endTime);

		String COOKIE_NAME = "SESSIONID";
		String COOKIE_VALUE = "03166dc4-2c82-4e55-85f5-f47919f367a6";
		Cookie sessionCookie = new Cookie(COOKIE_NAME, COOKIE_VALUE);

		given(basketRegistrationPeriodService.fetchBasketRegistrationPeriod())
			.willReturn(basketRegistrationPeriod);

		// when & then
		mockMvc.perform(get("/registration-period/baskets")
				.cookie(sessionCookie))
			.andDo(print())
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.data.targetGrade").value(Grade.COMMON.name()))
			.andExpect(jsonPath("$.data.startTime").value(basketRegistrationPeriod.getStartTime().toString()))
			.andExpect(jsonPath("$.data.endTime").value(basketRegistrationPeriod.getEndTime().toString()))
			.andDo(document("basket-registration-period-fetch-success",
				preprocessRequest(prettyPrint()),
				preprocessResponse(prettyPrint()),
				responseFields(
					fieldWithPath("code").type(JsonFieldType.NUMBER).description("코드"),
					fieldWithPath("status").type(JsonFieldType.STRING).description("상태"),
					fieldWithPath("message").type(JsonFieldType.STRING).description("메시지"),
					fieldWithPath("data").type(JsonFieldType.OBJECT).description("응답 데이터"),
					fieldWithPath("data.targetGrade").type(JsonFieldType.STRING).description("타겟 학년"),
					fieldWithPath("data.startTime").type(JsonFieldType.VARIES).description("시작 시간"),
					fieldWithPath("data.endTime").type(JsonFieldType.VARIES).description("종료 시간")
				)
			));
	}

	@Test
	@DisplayName("수강 바구니 신청 기간 추가 : 성공")
	void saveBasketRegistrationPeriod() throws Exception {
		// given
		LocalDateTime startTime = LocalDateTime.of(2024, 1, 17, 9, 30, 0);
		LocalDateTime endTime = LocalDateTime.of(2024, 1, 17, 10, 0, 0);

		BasketRegistrationPeriodSaveForm saveForm = new BasketRegistrationPeriodSaveForm(startTime, endTime);

		// when & then
		mockMvc.perform(post("/registration-period/baskets")
				.content(objectMapper.writeValueAsString(saveForm))
				.contentType(MediaType.APPLICATION_JSON))
			.andDo(print())
			.andExpect(status().isCreated())
			.andDo(document("basket-registration-period-save-success",
				preprocessRequest(prettyPrint()),
				preprocessResponse(prettyPrint()),
				requestFields(
					fieldWithPath("startTime").type(JsonFieldType.VARIES).description("시작 시간"),
					fieldWithPath("endTime").type(JsonFieldType.VARIES).description("종료 시간")
				),
				responseFields(
					fieldWithPath("code").type(JsonFieldType.NUMBER).description("코드"),
					fieldWithPath("status").type(JsonFieldType.STRING).description("상태"),
					fieldWithPath("message").type(JsonFieldType.STRING).description("메시지"),
					fieldWithPath("data").type(JsonFieldType.NULL).description("응답 데이터")
				)
			));
	}

	@Test
	@DisplayName("수강 바구니 신청 기간 추가 : 실패 - 현재 시간보다 수강 바구니 신청 시작 시간이 더 일찍임")
	void invalidTimeBasketRegistrationPeriod() throws Exception {
		// given
		doThrow(new StartTimeBeforeCurrentTimeException()).when(basketRegistrationPeriodService)
			.saveBasketRegistrationPeriod(any(), any(), any());

		LocalDateTime startTime = LocalDateTime.of(2024, 1, 17, 9, 30, 0);
		LocalDateTime endTime = LocalDateTime.of(2024, 1, 17, 10, 0, 0);

		BasketRegistrationPeriodSaveForm saveForm = new BasketRegistrationPeriodSaveForm(startTime, endTime);

		// when & then
		mockMvc.perform(post("/registration-period/baskets")
				.content(objectMapper.writeValueAsString(saveForm))
				.contentType(MediaType.APPLICATION_JSON))
			.andDo(print())
			.andExpect(status().isBadRequest())
			.andDo(document("basket-registration-period-save-fail-early-start-time",
				preprocessRequest(prettyPrint()),
				preprocessResponse(prettyPrint()),
				requestFields(
					fieldWithPath("startTime").type(JsonFieldType.VARIES).description("시작 시간"),
					fieldWithPath("endTime").type(JsonFieldType.VARIES).description("종료 시간")
				),
				responseFields(
					fieldWithPath("code").type(JsonFieldType.NUMBER).description("코드"),
					fieldWithPath("status").type(JsonFieldType.STRING).description("상태"),
					fieldWithPath("message").type(JsonFieldType.STRING).description("메시지"),
					fieldWithPath("data").type(JsonFieldType.NULL).description("응답 데이터")
				)
			));
	}

	@Test
	@DisplayName("수강 바구니 신청 기간 추가 : 실패 - 시작 시간보다 종료 시간이 더 일찍임")
	void earlyEndTimeBasketRegistrationPeriod() throws Exception {
		// given
		doThrow(new StartTimeAfterEndTimeException()).when(basketRegistrationPeriodService)
			.saveBasketRegistrationPeriod(any(), any(), any());

		LocalDateTime startTime = LocalDateTime.of(2024, 1, 17, 9, 30, 0);
		LocalDateTime endTime = LocalDateTime.of(2024, 1, 17, 10, 0, 0);

		BasketRegistrationPeriodSaveForm saveForm = new BasketRegistrationPeriodSaveForm(startTime, endTime);

		// when & then
		mockMvc.perform(post("/registration-period/baskets")
				.content(objectMapper.writeValueAsString(saveForm))
				.contentType(MediaType.APPLICATION_JSON))
			.andDo(print())
			.andExpect(status().isBadRequest())
			.andDo(document("basket-registration-period-save-fail-early-end-time",
				preprocessRequest(prettyPrint()),
				preprocessResponse(prettyPrint()),
				requestFields(
					fieldWithPath("startTime").type(JsonFieldType.VARIES).description("시작 시간"),
					fieldWithPath("endTime").type(JsonFieldType.VARIES).description("종료 시간")
				),
				responseFields(
					fieldWithPath("code").type(JsonFieldType.NUMBER).description("코드"),
					fieldWithPath("status").type(JsonFieldType.STRING).description("상태"),
					fieldWithPath("message").type(JsonFieldType.STRING).description("메시지"),
					fieldWithPath("data").type(JsonFieldType.NULL).description("응답 데이터")
				)
			));
	}

	private static BasketRegistrationPeriod createBasketRegistrationPeriod(LocalDateTime startTime, LocalDateTime endTime) {
		return BasketRegistrationPeriod.builder()
			.startTime(startTime)
			.endTime(endTime)
			.build();
	}

}
