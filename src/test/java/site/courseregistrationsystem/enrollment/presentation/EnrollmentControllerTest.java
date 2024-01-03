package site.courseregistrationsystem.enrollment.presentation;

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
import site.courseregistrationsystem.enrollment.dto.EnrolledLecture;
import site.courseregistrationsystem.exception.enrollment.CreditsLimitExceededException;
import site.courseregistrationsystem.exception.enrollment.DuplicateEnrollmentException;
import site.courseregistrationsystem.exception.enrollment.ScheduleConflictException;

class EnrollmentControllerTest extends RestDocsSupport {

	@Test
	@DisplayName("빠른 수강 신청 : 성공")
	void fastEnrollmentSuccess() throws Exception {
		// given
		String COOKIE_NAME = "SESSIONID";
		String COOKIE_VALUE = "03166dc4-2c82-4e55-85f5-f47919f367a6";
		Cookie sessionCookie = new Cookie(COOKIE_NAME, COOKIE_VALUE);

		Long lectureId = 1L;
		given(enrollmentService.enrollLectureByNumber(anyLong(), anyLong()))
			.willReturn(new EnrolledLecture(lectureId));

		// when & then
		Integer lectureNumber = 100101;
		mockMvc.perform(post("/enrollments/fast/" + lectureNumber)
				.cookie(sessionCookie))
			.andDo(print())
			.andExpect(status().isCreated())
			.andDo(document("fast-enrollment-success",
				preprocessRequest(prettyPrint()),
				preprocessResponse(prettyPrint()),
				responseFields(
					fieldWithPath("code").type(JsonFieldType.NUMBER).description("코드"),
					fieldWithPath("status").type(JsonFieldType.STRING).description("상태"),
					fieldWithPath("message").type(JsonFieldType.STRING).description("메시지"),
					fieldWithPath("data").type(JsonFieldType.OBJECT).description("응답 데이터"),
					fieldWithPath("data.enrolledLectureId").type(JsonFieldType.NUMBER).description("수강 신청된 강의 PK")
				)
			));
	}

	@Test
	@DisplayName("수강 신청 : 성공")
	void enrollmentSuccess() throws Exception {
		// given
		String COOKIE_NAME = "SESSIONID";
		String COOKIE_VALUE = "03166dc4-2c82-4e55-85f5-f47919f367a6";
		Cookie sessionCookie = new Cookie(COOKIE_NAME, COOKIE_VALUE);

		Long lectureId = 1L;
		given(enrollmentService.enrollLecture(anyLong(), anyLong()))
			.willReturn(new EnrolledLecture(lectureId));

		// when & then
		mockMvc.perform(post("/enrollments/" + lectureId)
				.cookie(sessionCookie))
			.andDo(print())
			.andExpect(status().isCreated())
			.andDo(document("enrollment-success",
				preprocessRequest(prettyPrint()),
				preprocessResponse(prettyPrint()),
				responseFields(
					fieldWithPath("code").type(JsonFieldType.NUMBER).description("코드"),
					fieldWithPath("status").type(JsonFieldType.STRING).description("상태"),
					fieldWithPath("message").type(JsonFieldType.STRING).description("메시지"),
					fieldWithPath("data").type(JsonFieldType.OBJECT).description("응답 데이터"),
					fieldWithPath("data.enrolledLectureId").type(JsonFieldType.NUMBER).description("수강 신청된 강의 PK")
				)
			));
	}

	@Test
	@DisplayName("수강 신청 : 학점 초과 실패")
	void enrollmentFailCreditsExceeded() throws Exception {
		// given
		String COOKIE_NAME = "SESSIONID";
		String COOKIE_VALUE = "03166dc4-2c82-4e55-85f5-f47919f367a6";
		Cookie sessionCookie = new Cookie(COOKIE_NAME, COOKIE_VALUE);

		Long lectureId = 1L;
		given(enrollmentService.enrollLecture(anyLong(), anyLong()))
			.willThrow(new CreditsLimitExceededException());

		// when & then
		mockMvc.perform(post("/enrollments/" + lectureId)
				.cookie(sessionCookie))
			.andDo(print())
			.andExpect(status().isBadRequest())
			.andDo(document("enrollment-fail-credits-exceeded",
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
	@DisplayName("수강 신청 : 과목 중복 실패")
	void enrollmentFailDuplicatedSubject() throws Exception {
		// given
		String COOKIE_NAME = "SESSIONID";
		String COOKIE_VALUE = "03166dc4-2c82-4e55-85f5-f47919f367a6";
		Cookie sessionCookie = new Cookie(COOKIE_NAME, COOKIE_VALUE);

		Long lectureId = 1L;
		given(enrollmentService.enrollLecture(anyLong(), anyLong()))
			.willThrow(new DuplicateEnrollmentException());

		// when & then
		mockMvc.perform(post("/enrollments/" + lectureId)
				.cookie(sessionCookie))
			.andDo(print())
			.andExpect(status().isBadRequest())
			.andDo(document("enrollment-fail-duplicated-subject",
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
	@DisplayName("수강 신청 : 시간 충돌 실패")
	void enrollmentFailScheduleConflict() throws Exception {
		// given
		String COOKIE_NAME = "SESSIONID";
		String COOKIE_VALUE = "03166dc4-2c82-4e55-85f5-f47919f367a6";
		Cookie sessionCookie = new Cookie(COOKIE_NAME, COOKIE_VALUE);

		Long lectureId = 1L;
		given(enrollmentService.enrollLecture(anyLong(), anyLong()))
			.willThrow(new ScheduleConflictException());

		// when & then
		mockMvc.perform(post("/enrollments/" + lectureId)
				.cookie(sessionCookie))
			.andDo(print())
			.andExpect(status().isBadRequest())
			.andDo(document("enrollment-fail-schedule-conflict",
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
	@DisplayName("수강 신청 : 쿠키 인증 실패")
	void enrollmentFailCookie() throws Exception {
		// given
		Long lectureId = 1L;

		// when & then
		mockMvc.perform(post("/enrollments/" + lectureId))
			.andDo(print())
			.andExpect(status().isUnauthorized())
			.andDo(document("enrollment-fail-cookie",
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
