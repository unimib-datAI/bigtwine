package it.unimib.disco.bigtwine.services.socials.security.jwt;

import org.junit.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;

import static org.mockito.Mockito.*;
import static org.junit.Assert.*;

public class JWTAuthRestInterceptorTest {

    private final static String FAKE_SYSTEM_TOKEN = "aabbccdd";

    @Test
    public void testIntercept() throws Exception {
        HttpHeaders headers = new HttpHeaders();
        HttpRequest request = mock(HttpRequest.class);

        when(request.getHeaders()).thenReturn(headers);

        ClientHttpRequestExecution execution = mock(ClientHttpRequestExecution.class);
        JWTAuthRestInterceptor interceptor = new JWTAuthRestInterceptor(FAKE_SYSTEM_TOKEN);
        byte[] body = {};

        interceptor.intercept(request, body, execution);

        assertTrue(headers.size() > 0);
        assertTrue(headers.containsKey(HttpHeaders.AUTHORIZATION));
        assertEquals("Bearer " + FAKE_SYSTEM_TOKEN, headers.get(HttpHeaders.AUTHORIZATION).get(0));
    }
}
