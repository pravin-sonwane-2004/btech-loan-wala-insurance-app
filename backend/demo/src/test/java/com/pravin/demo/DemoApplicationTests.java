package com.pravin.demo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pravin.demo.auth.AuthTokenProperties;
import com.pravin.demo.auth.TokenAuthorizationInterceptor;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

class DemoApplicationTests {

	@Test
	void missingTokenReturnsUnauthorized() throws Exception {
		TokenAuthorizationInterceptor interceptor = interceptor();
		MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/customers");
		MockHttpServletResponse response = new MockHttpServletResponse();

		boolean allowed = interceptor.preHandle(request, response, new Object());

		assertFalse(allowed);
		assertEquals(401, response.getStatus());
	}

	@Test
	void agentCannotDelete() throws Exception {
		TokenAuthorizationInterceptor interceptor = interceptor();
		MockHttpServletRequest request = new MockHttpServletRequest("DELETE", "/api/customers/1");
		request.addHeader(TokenAuthorizationInterceptor.TOKEN_HEADER, "agent-token");
		MockHttpServletResponse response = new MockHttpServletResponse();

		boolean allowed = interceptor.preHandle(request, response, new Object());

		assertFalse(allowed);
		assertEquals(403, response.getStatus());
	}

	@Test
	void adminCanDelete() throws Exception {
		TokenAuthorizationInterceptor interceptor = interceptor();
		MockHttpServletRequest request = new MockHttpServletRequest("DELETE", "/api/customers/1");
		request.addHeader(TokenAuthorizationInterceptor.TOKEN_HEADER, "admin-token");
		MockHttpServletResponse response = new MockHttpServletResponse();

		boolean allowed = interceptor.preHandle(request, response, new Object());

		assertTrue(allowed);
		assertEquals(200, response.getStatus());
	}

	private TokenAuthorizationInterceptor interceptor() {
		AuthTokenProperties properties = new AuthTokenProperties();
		properties.setAdminToken("admin-token");
		properties.setAgentToken("agent-token");
		return new TokenAuthorizationInterceptor(properties, new ObjectMapper());
	}
}
