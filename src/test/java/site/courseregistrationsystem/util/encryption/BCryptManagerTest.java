package site.courseregistrationsystem.util.encryption;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.Test;

class BCryptManagerTest {

	@Test
	void encrypt() {
		// given
		String plainData = "this is plain data";

		// when
		String hashData = BCryptManager.encrypt(plainData);

		// then
		assertThat(hashData).isNotEqualTo(plainData);
		assertThat(hashData).startsWith("$2");
		assertThat(hashData).hasSize(60);
	}

	@Test
	void isMatch() {
		// given
		String plainData = "this is plain data";
		String wrongData = "this is wrong data";

		// when
		String hashData = BCryptManager.encrypt(plainData);

		// then
		assertThat(BCryptManager.isMatch(plainData, hashData)).isTrue();
		assertThat(BCryptManager.isMatch(wrongData, hashData)).isFalse();
	}

}
