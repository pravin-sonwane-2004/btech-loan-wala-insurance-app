package com.pravin.demo.auth;

import java.io.IOException;
import java.time.Instant;

import com.pravin.demo.common.ApiError;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import tools.jackson.databind.ObjectMapper;

@Component
public class TokenAuthorizationInterceptor implements HandlerInterceptor {

	public static final String TOKEN_HEADER = "X-Auth-Token";
	public static final String ROLE_ATTRIBUTE = "authenticatedRole";

	private final AuthTokenProperties authTokenProperties;
	private final ObjectMapper objectMapper;

	public TokenAuthorizationInterceptor(AuthTokenProperties authTokenProperties, ObjectMapper objectMapper) {
		this.authTokenProperties = authTokenProperties;
		this.objectMapper = objectMapper;
	}

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws IOException {
		if (HttpMethod.OPTIONS.matches(request.getMethod())) {
			return true;
		}

		var role = authTokenProperties.resolveRole(request.getHeader(TOKEN_HEADER));
		if (role.isEmpty()) {
			writeError(response, request, HttpStatus.UNAUTHORIZED, "Missing or invalid X-Auth-Token");
			return false;
		}

		if (HttpMethod.DELETE.matches(request.getMethod()) && role.get() != UserRole.ADMIN) {
			writeError(response, request, HttpStatus.FORBIDDEN, "DELETE operations require ADMIN authorization");
			return false;
		}

		request.setAttribute(ROLE_ATTRIBUTE, role.get());
		return true;
	}

	private void writeError(HttpServletResponse response, HttpServletRequest request, HttpStatus status, String message)
			throws IOException {
		response.setStatus(status.value());
		response.setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
		objectMapper.writeValue(response.getOutputStream(),
				new ApiError(Instant.now(), status.value(), status.getReasonPhrase(), message, request.getRequestURI()));
	}
}
