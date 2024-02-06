package site.courseregistrationsystem.auth.presentation;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@ConfigurationProperties("cookie")
@RequiredArgsConstructor
@Getter
public class CookieProperties {

	private final String name;
	private final String domain;
	private final String path;
	private final int expiry;
	private final String sameSite;

}
