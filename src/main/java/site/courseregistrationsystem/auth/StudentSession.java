package site.courseregistrationsystem.auth;

import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;

import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@RedisHash("session")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class StudentSession {

	@Id
	private String id;

	private Long studentPk;

	@TimeToLive
	private Long expiration;

	@Builder
	private StudentSession(String id, Long studentPk, Long expiration) {
		this.id = id;
		this.studentPk = studentPk;
		this.expiration = expiration;
	}

}
