package site.courseregistrationsystem.enrollment.presentation;

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
import site.courseregistrationsystem.enrollment.dto.EnrolledLecture;
import site.courseregistrationsystem.enrollment.dto.EnrolledLectureDetail;
import site.courseregistrationsystem.enrollment.dto.EnrolledLectures;
import site.courseregistrationsystem.enrollment.dto.EnrollmentCapacity;
import site.courseregistrationsystem.exception.credit.CreditLimitExceededException;
import site.courseregistrationsystem.exception.enrollment.DuplicateEnrollmentException;
import site.courseregistrationsystem.exception.enrollment.EnrollmentNotFoundException;
import site.courseregistrationsystem.exception.enrollment.LectureNotInCurrentSemesterException;
import site.courseregistrationsystem.exception.registration_period.InvalidEnrollmentTimeException;
import site.courseregistrationsystem.exception.schedule.ScheduleConflictException;
import site.courseregistrationsystem.subject.SubjectDivision;

class EnrollmentControllerTest extends RestDocsSupport {

	@Test
	@DisplayName("빠른 수강 신청 : 성공")
	void fastEnrollmentSuccess() throws Exception {
		// given
		String COOKIE_NAME = "SESSIONID";
		String COOKIE_VALUE = "03166dc4-2c82-4e55-85f5-f47919f367a6";
		Cookie sessionCookie = new Cookie(COOKIE_NAME, COOKIE_VALUE);

		Long lectureId = 1L;
		given(enrollmentService.enrollLectureByNumber(any(), anyLong(), anyInt()))
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
		given(enrollmentService.enrollLecture(any(), anyLong(), anyLong()))
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
	@DisplayName("수강 신청 : 실패 - 수강 신청 기간 아님")
	void enrollmentFail() throws Exception {
		// given
		String COOKIE_NAME = "SESSIONID";
		String COOKIE_VALUE = "03166dc4-2c82-4e55-85f5-f47919f367a6";
		Cookie sessionCookie = new Cookie(COOKIE_NAME, COOKIE_VALUE);

		Long lectureId = 1L;
		given(enrollmentService.enrollLecture(any(), anyLong(), anyLong()))
			.willThrow(new InvalidEnrollmentTimeException());

		// when & then
		mockMvc.perform(post("/enrollments/" + lectureId)
				.cookie(sessionCookie))
			.andDo(print())
			.andExpect(status().isBadRequest())
			.andDo(document("enrollment-fail-invalid-semester",
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
	@DisplayName("수강 신청 : 지난 학기 강의 오류")
	void enrollmentFailPastLecture() throws Exception {
		// given
		String COOKIE_NAME = "SESSIONID";
		String COOKIE_VALUE = "03166dc4-2c82-4e55-85f5-f47919f367a6";
		Cookie sessionCookie = new Cookie(COOKIE_NAME, COOKIE_VALUE);

		Long lectureId = 1L;
		given(enrollmentService.enrollLecture(any(), anyLong(), anyLong()))
			.willThrow(new LectureNotInCurrentSemesterException());

		// when & then
		mockMvc.perform(post("/enrollments/" + lectureId)
				.cookie(sessionCookie))
			.andDo(print())
			.andExpect(status().isBadRequest())
			.andDo(document("enrollment-fail-past-lecture",
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
	@DisplayName("수강 신청 : 학점 초과 실패")
	void enrollmentFailCreditsExceeded() throws Exception {
		// given
		String COOKIE_NAME = "SESSIONID";
		String COOKIE_VALUE = "03166dc4-2c82-4e55-85f5-f47919f367a6";
		Cookie sessionCookie = new Cookie(COOKIE_NAME, COOKIE_VALUE);

		Long lectureId = 1L;
		given(enrollmentService.enrollLecture(any(), anyLong(), anyLong()))
			.willThrow(new CreditLimitExceededException());

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
		given(enrollmentService.enrollLecture(any(), anyLong(), anyLong()))
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
		given(enrollmentService.enrollLecture(any(), anyLong(), anyLong()))
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

	@Test
	@DisplayName("수강 신청 취소 : 성공")
	void cancelEnrollmentSuccess() throws Exception {
		// given
		String COOKIE_NAME = "SESSIONID";
		String COOKIE_VALUE = "03166dc4-2c82-4e55-85f5-f47919f367a6";
		Cookie sessionCookie = new Cookie(COOKIE_NAME, COOKIE_VALUE);

		Long enrollmentId = 1L;

		// when & then
		mockMvc.perform(delete("/enrollments/" + enrollmentId)
				.cookie(sessionCookie))
			.andDo(print())
			.andExpect(status().isOk())
			.andDo(document("cancel-enrollment-success",
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
	@DisplayName("수강 신청 취소 : 미신청 강의 오류")
	void cancelEnrollmentFail() throws Exception {
		// given
		String COOKIE_NAME = "SESSIONID";
		String COOKIE_VALUE = "03166dc4-2c82-4e55-85f5-f47919f367a6";
		Cookie sessionCookie = new Cookie(COOKIE_NAME, COOKIE_VALUE);

		Long enrollmentId = 1L;

		doThrow(new EnrollmentNotFoundException()).when(enrollmentService).cancel(anyLong(), anyLong());

		// when & then
		mockMvc.perform(delete("/enrollments/" + enrollmentId)
				.cookie(sessionCookie))
			.andDo(print())
			.andExpect(status().isBadRequest())
			.andDo(document("cancel-enrollment-fail",
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
	@DisplayName("수강 신청 내역 조회 : 성공")
	void fetchEnrollmentsSuccess() throws Exception {
		// given
		String COOKIE_NAME = "SESSIONID";
		String COOKIE_VALUE = "03166dc4-2c82-4e55-85f5-f47919f367a6";
		Cookie sessionCookie = new Cookie(COOKIE_NAME, COOKIE_VALUE);

		List<EnrolledLectureDetail> enrolledLectures = List.of(
			new EnrolledLectureDetail(
				10L, 1L, 100101, SubjectDivision.MR.getDescription(), "금속공예기초", 4, 3, 1, "남유진", "목(6-9)"),
			new EnrolledLectureDetail(
				11L, 2L, 500401, SubjectDivision.GR.getDescription(), "미술사", 3, 2, 1, "노준", "화(2-4)"),
			new EnrolledLectureDetail(
				12L, 3L, 900401, SubjectDivision.GE.getDescription(), "봉사활동", 1, 1, 1, "유재석", "금(1-1)")
		);

		given(enrollmentService.fetchAll(anyLong()))
			.willReturn(new EnrolledLectures(enrolledLectures));

		// when & then
		mockMvc.perform(get("/enrollments")
				.cookie(sessionCookie))
			.andDo(print())
			.andExpect(status().isOk())
			.andDo(document("fetch-enrollments-success",
				preprocessRequest(prettyPrint()),
				preprocessResponse(prettyPrint()),
				responseFields(
					fieldWithPath("code").type(JsonFieldType.NUMBER).description("코드"),
					fieldWithPath("status").type(JsonFieldType.STRING).description("상태"),
					fieldWithPath("message").type(JsonFieldType.STRING).description("메시지"),
					fieldWithPath("data").type(JsonFieldType.OBJECT).description("응답 데이터"),
					fieldWithPath("data.enrolledLectures").type(JsonFieldType.ARRAY).description("수강 신청 내역"),
					fieldWithPath("data.enrolledLectures[].id").type(JsonFieldType.NUMBER).description("수강 신청 PK"),
					fieldWithPath("data.enrolledLectures[].lectureId").type(JsonFieldType.NUMBER).description("강의 PK"),
					fieldWithPath("data.enrolledLectures[].lectureNumber").type(JsonFieldType.NUMBER)
						.description("강의 번호"),
					fieldWithPath("data.enrolledLectures[].subjectDivision").type(JsonFieldType.STRING)
						.description("이수 구분"),
					fieldWithPath("data.enrolledLectures[].subjectName").type(JsonFieldType.STRING)
						.description("강의 이름"),
					fieldWithPath("data.enrolledLectures[].hoursPerWeek").type(JsonFieldType.NUMBER)
						.description("강의 시간"),
					fieldWithPath("data.enrolledLectures[].credits").type(JsonFieldType.NUMBER).description("학점"),
					fieldWithPath("data.enrolledLectures[].targetGrade").type(JsonFieldType.NUMBER)
						.description("대상 학년"),
					fieldWithPath("data.enrolledLectures[].professorName").type(JsonFieldType.STRING)
						.description("교강사"),
					fieldWithPath("data.enrolledLectures[].schedule").type(JsonFieldType.STRING).description("강의 요시")
				)
			));
	}

	@Test
	@DisplayName("수강 신청 인원 조회 : 성공")
	void countEnrollmentsSuccess() throws Exception {
		// given
		String COOKIE_NAME = "SESSIONID";
		String COOKIE_VALUE = "03166dc4-2c82-4e55-85f5-f47919f367a6";
		Cookie sessionCookie = new Cookie(COOKIE_NAME, COOKIE_VALUE);

		Long lectureId = 1L;

		given(enrollmentService.fetchCountBy(any(), any(), anyLong()))
			.willReturn(new EnrollmentCapacity(20, 12));

		// when & then
		mockMvc.perform(get("/enrollments/" + lectureId + "/enrollment-count")
				.cookie(sessionCookie))
			.andDo(print())
			.andExpect(status().isOk())
			.andDo(document("count-enrollments-success",
				preprocessRequest(prettyPrint()),
				preprocessResponse(prettyPrint()),
				responseFields(
					fieldWithPath("code").type(JsonFieldType.NUMBER).description("코드"),
					fieldWithPath("status").type(JsonFieldType.STRING).description("상태"),
					fieldWithPath("message").type(JsonFieldType.STRING).description("메시지"),
					fieldWithPath("data").type(JsonFieldType.OBJECT).description("응답 데이터"),
					fieldWithPath("data.capacity").type(JsonFieldType.NUMBER).description("정원"),
					fieldWithPath("data.currentEnrollmentCount").type(JsonFieldType.NUMBER).description("현재 신청 인원")
				)
			));
	}

}
