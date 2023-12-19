package site.courseregistrationsystem.util.encryption;

import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import site.courseregistrationsystem.exception.CustomException;
import site.courseregistrationsystem.exception.ErrorType;

@Component
public class Aes256Manager {

	private final Cipher cipher;
	private final SecretKeySpec keySpec;
	private final IvParameterSpec ivParameterSpec;

	@Autowired
	public Aes256Manager(Aes256Properties properties) throws NoSuchPaddingException, NoSuchAlgorithmException {
		this.cipher = Cipher.getInstance(properties.getAlgorithm());
		this.keySpec = new SecretKeySpec(properties.getKey().getBytes(), "AES");
		this.ivParameterSpec = new IvParameterSpec(properties.getInitialVector().getBytes());
	}

	/**
	 *
	 * @param plainInformation
	 * 평문을 입력받는다.
	 *
	 * 평문 -> 바이트 배열
	 * 바이트 배열 -> 암호화 된 바이트 배열
	 * 암호화 된 바이트 배열 -> Base64 인코딩한 암호화 된 문자열
	 *
	 * Base64 인코딩한 암호화 된 문자열을 반환한다.
	 */
	public String encrypt(String plainInformation) {
		byte[] encryptedInformation;
		try {
			cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivParameterSpec);
			encryptedInformation = cipher.doFinal(plainInformation.getBytes(StandardCharsets.UTF_8));
		} catch (InvalidAlgorithmParameterException | InvalidKeyException |
				 IllegalBlockSizeException | BadPaddingException e) {
			throw new CustomException(ErrorType.AES256_SETTING_FAIL);
		}

		return Base64.getEncoder().encodeToString(encryptedInformation);
	}

	/**
	 *
	 * @param encryptedInformation
	 * Base64 인코딩한 암호화 된 문자열을 입력받는다.
	 *
	 * Base64 인코딩한 암호화 된 문자열 -> 암호화 된 바이트 배열
	 * 암호화 된 바이트 배열 -> 해독된 바이트 배열
	 * 해독된 바이트 배열 -> 평문
	 *
	 * 평문을 반환한다.
	 */
	public String decrypt(String encryptedInformation) {
		byte[] decodedPlainInformation;
		try {
			cipher.init(Cipher.DECRYPT_MODE, keySpec, ivParameterSpec);
			byte[] decodedAndEncryptedInformation = Base64.getDecoder().decode(encryptedInformation);
			decodedPlainInformation = cipher.doFinal(decodedAndEncryptedInformation);
		} catch (InvalidAlgorithmParameterException | InvalidKeyException |
				 IllegalBlockSizeException | BadPaddingException e) {
			throw new CustomException(ErrorType.AES256_SETTING_FAIL);
		}

		return new String(decodedPlainInformation, StandardCharsets.UTF_8);
	}

}
