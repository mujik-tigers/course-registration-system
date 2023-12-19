package site.courseregistrationsystem.clock.presentation;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.*;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.restdocs.payload.JsonFieldType;

import site.courseregistrationsystem.RestDocsSupport;

class ClockControllerTest extends RestDocsSupport {

	@Test
	@DisplayName("현재 서버시간 조회 : 성공")
	void checkCurrentServerTime() throws Exception {
		// when & then
		mockMvc.perform(get("/clock/server"))
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

}