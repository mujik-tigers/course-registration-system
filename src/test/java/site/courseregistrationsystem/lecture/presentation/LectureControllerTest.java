package site.courseregistrationsystem.lecture.presentation;

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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.restdocs.payload.JsonFieldType;

import jakarta.servlet.http.Cookie;
import site.courseregistrationsystem.RestDocsSupport;
import site.courseregistrationsystem.lecture.dto.LectureDetail;
import site.courseregistrationsystem.lecture.dto.LectureDetailPage;

class LectureControllerTest extends RestDocsSupport {

	@Test
	@DisplayName("종합 강의 시간표 조회 : 성공")
	void fetchSuccess() throws Exception {
		// given
		Page<LectureDetail> fetchedLectures = new PageImpl<>(List.of(
			new LectureDetail(1L, 100101, "교필", "세계미술사", 3, 2, 1, "조형대학", "노준", "월(2-4)", 25),
			new LectureDetail(2L, 300101, "전필", "금속공예기초", 4, 3, 1, "금속공예디자인학과", "남유진", "목(6-9)", 20)
		), PageRequest.of(0, 20), 2);

		given(lectureService.fetch(any(), any(), any(), any()))
			.willReturn(new LectureDetailPage(fetchedLectures));

		String COOKIE_NAME = "SESSIONID";
		String COOKIE_VALUE = "03166dc4-2c82-4e55-85f5-f47919f367a6";
		Cookie sessionCookie = new Cookie(COOKIE_NAME, COOKIE_VALUE);

		// when & then
		mockMvc.perform(get("/lectures")
				.cookie(sessionCookie))
			.andDo(print())
			.andExpect(status().isOk())
			.andDo(document("lecture-fetch-success",
				preprocessRequest(prettyPrint()),
				preprocessResponse(prettyPrint()),
				responseFields(
					fieldWithPath("code").type(JsonFieldType.NUMBER).description("코드"),
					fieldWithPath("status").type(JsonFieldType.STRING).description("상태"),
					fieldWithPath("message").type(JsonFieldType.STRING).description("메시지"),
					fieldWithPath("data").type(JsonFieldType.OBJECT).description("응답 데이터"),
					fieldWithPath("data.lectures").type(JsonFieldType.ARRAY).description("강의 목록"),
					fieldWithPath("data.lectures[].id").type(JsonFieldType.NUMBER).description("강의 PK"),
					fieldWithPath("data.lectures[].lectureNumber").type(JsonFieldType.NUMBER).description("강의 번호"),
					fieldWithPath("data.lectures[].subjectDivision").type(JsonFieldType.STRING).description("이수 구분"),
					fieldWithPath("data.lectures[].subjectName").type(JsonFieldType.STRING).description("강의 이름"),
					fieldWithPath("data.lectures[].hoursPerWeek").type(JsonFieldType.NUMBER).description("강의 시간"),
					fieldWithPath("data.lectures[].credits").type(JsonFieldType.NUMBER).description("학점"),
					fieldWithPath("data.lectures[].targetGrade").type(JsonFieldType.NUMBER).description("대상 학년"),
					fieldWithPath("data.lectures[].departmentName").type(JsonFieldType.STRING).description("개설 학부"),
					fieldWithPath("data.lectures[].professorName").type(JsonFieldType.STRING).description("교강사"),
					fieldWithPath("data.lectures[].schedule").type(JsonFieldType.STRING).description("강의 요시"),
					fieldWithPath("data.lectures[].totalCapacity").type(JsonFieldType.NUMBER).description("전체 정원"),
					fieldWithPath("data.number").type(JsonFieldType.NUMBER).description("현재 페이지"),
					fieldWithPath("data.size").type(JsonFieldType.NUMBER).description("페이지 크기"),
					fieldWithPath("data.first").type(JsonFieldType.BOOLEAN).description("첫 페이지라면 true"),
					fieldWithPath("data.last").type(JsonFieldType.BOOLEAN).description("마지막 페이지라면 true"),
					fieldWithPath("data.totalPages").type(JsonFieldType.NUMBER).description("총 페이지 개수"),
					fieldWithPath("data.totalElements").type(JsonFieldType.NUMBER).description("총 데이터 개수")
				)
			));
	}

	@Test
	@DisplayName("종합 강의 시간표 조회 : 쿠키 인증 실패")
	void fetchFail() throws Exception {
		// when & then
		mockMvc.perform(get("/lectures"))
			.andDo(print())
			.andExpect(status().isUnauthorized())
			.andDo(document("lecture-fetch-fail",
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
