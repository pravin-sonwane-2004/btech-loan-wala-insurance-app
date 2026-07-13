package com.pravin.demo.auth;

import java.util.Optional;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.util.StringUtils;

@ConfigurationProperties(prefix = "app.auth")
public class AuthTokenProperties {

	private String adminToken = "SAWAI_ADMIN_TOKEN_2026";
	private String agentToken = "SAWAI_AGENT_TOKEN_2026";

	public Optional<UserRole> resolveRole(String token) {
		if (!StringUtils.hasText(token)) {
			return Optional.empty();
		}
		if (token.equals(adminToken)) {
			return Optional.of(UserRole.ADMIN);
		}
		if (token.equals(agentToken)) {
			return Optional.of(UserRole.AGENT);
		}
		return Optional.empty();
	}

	public String getAdminToken() {
		return adminToken;
	}

	public void setAdminToken(String adminToken) {
		this.adminToken = adminToken;
	}

	public String getAgentToken() {
		return agentToken;
	}

	public void setAgentToken(String agentToken) {
		this.agentToken = agentToken;
	}
}
