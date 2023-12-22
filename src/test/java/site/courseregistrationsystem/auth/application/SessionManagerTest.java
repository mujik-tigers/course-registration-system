package site.courseregistrationsystem.auth.application;

import static org.assertj.core.api.Assertions.*;

import org.assertj.core.data.Offset;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import site.courseregistrationsystem.IntegrationTestSupport;
import site.courseregistrationsystem.auth.StudentSession;
import site.courseregistrationsystem.exception.ErrorType;
import site.courseregistrationsystem.exception.auth.NonexistenceSessionException;

class SessionManagerTest extends IntegrationTestSupport {

	@Autowired
	private SessionManager sessionManager;

	@Test
	void generate() {
		// given
		Long studentPk = 100L;

		// when
		StudentSession session = sessionManager.generate(studentPk);

		// then
		assertThat(session.getStudentPk()).isEqualTo(studentPk);
		assertThat(session.getId()).hasSize(36);
		assertThat(session.getExpiration()).isCloseTo(3600L, Offset.offset(10L));
	}

	@Test
	void fetch() {
		// given
		Long studentPk = 100L;
		StudentSession session = sessionManager.generate(studentPk);

		// when
		StudentSession fetched = sessionManager.fetch(session.getId());

		// then
		assertThat(fetched.getId()).isEqualTo(session.getId());
		assertThat(fetched.getStudentPk()).isEqualTo(session.getStudentPk());
	}

	@Test
	void invalidate() {
		// given
		Long studentPk = 100L;
		StudentSession session = sessionManager.generate(studentPk);

		// when
		sessionManager.invalidate(session.getId());

		// then
		assertThatThrownBy(() -> sessionManager.fetch(session.getId()))
			.isInstanceOf(NonexistenceSessionException.class)
			.hasMessage(ErrorType.NONEXISTENT_SESSION.getMessage());
	}

	@Test
	void renew() {
		// given
		Long studentPk = 100L;
		StudentSession session = sessionManager.generate(studentPk);

		// when
		StudentSession renewed = sessionManager.renew(session.getId());

		// then
		assertThat(renewed.getId()).isNotEqualTo(session.getId());
		assertThat(renewed.getStudentPk()).isEqualTo(session.getStudentPk());
	}

}
