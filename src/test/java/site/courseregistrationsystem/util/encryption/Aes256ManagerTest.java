package site.courseregistrationsystem.util.encryption;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import site.courseregistrationsystem.IntegrationTestSupport;

class Aes256ManagerTest extends IntegrationTestSupport {

	@Autowired
	private Aes256Manager aes256Manager;

	@Test
	void encrypt() {
		// given
		String plainData = "20221219";

		// when
		String encrypted = aes256Manager.encrypt(plainData);

		// then
		assertThat(encrypted).isNotEqualTo(plainData);
	}

	@Test
	void decrypt() {
		// given
		String plainData = "20221219";
		String encrypted = aes256Manager.encrypt(plainData);

		// when
		String decrypted = aes256Manager.decrypt(encrypted);

		// then
		assertThat(decrypted).isEqualTo(plainData);
		assertThat(decrypted).isNotEqualTo(encrypted);
	}

}
