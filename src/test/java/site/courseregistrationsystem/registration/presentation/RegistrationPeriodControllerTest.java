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

import site.courseregistrationsystem.RestDocsSupport;
import site.courseregistrationsystem.exception.registration_period.StartTimeAfterEndTimeException;
import site.courseregistrationsystem.exception.registration_period.StartTimeBeforeCurrentTimeException;
import site.courseregistrationsystem.lecture.Semester;
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

		EnrollmentRegistrationPeriodSaveForm saveForm = new EnrollmentRegistrationPeriodSaveForm(Grade.FRESHMAN, startTime, endTime, Semester.FIRST);

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
					fieldWithPath("endTime").type(JsonFieldType.VARIES).description("종료 시간"),
					fieldWithPath("semester").type(JsonFieldType.STRING).description("학기")
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
			.saveEnrollmentRegistrationPeriod(any(), any(), any(), any(), any());

		LocalDateTime startTime = LocalDateTime.of(2024, 1, 17, 9, 30, 0);
		LocalDateTime endTime = LocalDateTime.of(2024, 1, 17, 10, 0, 0);

		EnrollmentRegistrationPeriodSaveForm saveForm = new EnrollmentRegistrationPeriodSaveForm(Grade.FRESHMAN, startTime, endTime, Semester.FIRST);

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
					fieldWithPath("endTime").type(JsonFieldType.VARIES).description("종료 시간"),
					fieldWithPath("semester").type(JsonFieldType.STRING).description("학기")
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
			.saveEnrollmentRegistrationPeriod(any(), any(), any(), any(), any());

		LocalDateTime startTime = LocalDateTime.of(2024, 1, 17, 9, 30, 0);
		LocalDateTime endTime = LocalDateTime.of(2024, 1, 17, 10, 0, 0);

		EnrollmentRegistrationPeriodSaveForm saveForm = new EnrollmentRegistrationPeriodSaveForm(Grade.FRESHMAN, startTime, endTime, Semester.FIRST);

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
					fieldWithPath("endTime").type(JsonFieldType.VARIES).description("종료 시간"),
					fieldWithPath("semester").type(JsonFieldType.STRING).description("학기")
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
	@DisplayName("수강 바구니 신청 기간 추가 : 성공")
	void saveBasketRegistrationPeriod() throws Exception {
		// given
		LocalDateTime startTime = LocalDateTime.of(2024, 1, 17, 9, 30, 0);
		LocalDateTime endTime = LocalDateTime.of(2024, 1, 17, 10, 0, 0);

		BasketRegistrationPeriodSaveForm saveForm = new BasketRegistrationPeriodSaveForm(startTime, endTime, Semester.FIRST);

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
					fieldWithPath("endTime").type(JsonFieldType.VARIES).description("종료 시간"),
					fieldWithPath("semester").type(JsonFieldType.STRING).description("학기")
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
			.saveBasketRegistrationPeriod(any(), any(), any(), any());

		LocalDateTime startTime = LocalDateTime.of(2024, 1, 17, 9, 30, 0);
		LocalDateTime endTime = LocalDateTime.of(2024, 1, 17, 10, 0, 0);

		BasketRegistrationPeriodSaveForm saveForm = new BasketRegistrationPeriodSaveForm(startTime, endTime, Semester.FIRST);

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
					fieldWithPath("endTime").type(JsonFieldType.VARIES).description("종료 시간"),
					fieldWithPath("semester").type(JsonFieldType.STRING).description("학기")
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
			.saveBasketRegistrationPeriod(any(), any(), any(), any());

		LocalDateTime startTime = LocalDateTime.of(2024, 1, 17, 9, 30, 0);
		LocalDateTime endTime = LocalDateTime.of(2024, 1, 17, 10, 0, 0);

		BasketRegistrationPeriodSaveForm saveForm = new BasketRegistrationPeriodSaveForm(startTime, endTime, Semester.FIRST);

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
					fieldWithPath("endTime").type(JsonFieldType.VARIES).description("종료 시간"),
					fieldWithPath("semester").type(JsonFieldType.STRING).description("학기")
				),
				responseFields(
					fieldWithPath("code").type(JsonFieldType.NUMBER).description("코드"),
					fieldWithPath("status").type(JsonFieldType.STRING).description("상태"),
					fieldWithPath("message").type(JsonFieldType.STRING).description("메시지"),
					fieldWithPath("data").type(JsonFieldType.NULL).description("응답 데이터")
				)
			));
	}

}
