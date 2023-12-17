package site.courseregistrationsystem.util.encryption;

import org.mindrot.jbcrypt.BCrypt;

public final class BCryptManager {

	public static String encrypt(String plainData) {
		return BCrypt.hashpw(plainData, BCrypt.gensalt());
	}

	public static boolean isMatch(String plainData, String hashData) {
		return BCrypt.checkpw(plainData, hashData);
	}

}
