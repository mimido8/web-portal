package com.healthware.routes;

import com.healthware.Session;
import com.healthware.base.http.HTTPRequest;
import com.healthware.base.http.HTTPResponse;
import com.healthware.models.Account;
import org.junit.Assert;
import org.junit.Test;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

public class AuthenticationFilterTest {
    @Test
    public void returnsForbiddenForRequestsWithoutSession() {
        //Create filter with no sessions, since none are required
        AuthenticationFilter filter = new AuthenticationFilter(Collections.emptyMap());

        //Create request with no body and no session
        HTTPRequest request = new HTTPRequest(null, null);

        HTTPResponse<String> response = filter.getResponse(request);

        assertNotNull(response);
        assertEquals(403, response.status);
    }

    @Test
    public void returnsForbiddenForRequestsWithInvalidSession() {
        //Create filter with some session token
        AuthenticationFilter filter = new AuthenticationFilter(Collections.singletonMap("some session token", null));

        //Create request with no body and different (invalid) session token
        HTTPRequest request = new HTTPRequest(null, "invalid session token");

        HTTPResponse<String> response = filter.getResponse(request);

        assertNotNull(response);
        assertEquals(403, response.status);
    }

    @Test
    public void invalidatesExpiredSessions() {
        //Create filter with an expired session
        String sessionToken = "some session token";
        long januaryFirst1970 = 0;
        Session session = new Session(januaryFirst1970, 0, Account.Type.PATIENT);
        Map<String, Session> sessions = new HashMap<>(Collections.singletonMap(sessionToken, session));
        AuthenticationFilter filter = new AuthenticationFilter(sessions);

        //Create request with no body and the expired session token
        HTTPRequest request = new HTTPRequest(null, sessionToken);

        HTTPResponse<String> response = filter.getResponse(request);

        assertNotNull(response);
        assertEquals(403, response.status);

        //Assert that the filter removes the expired session
        assertTrue(!sessions.containsKey(sessionToken));
    }

    @Test
    public void performsNoActionForUnexpiredSessions() {
        //Create filter with a valid session
        String sessionToken = "some session token";
        Session session = new Session(System.currentTimeMillis(), 0, Account.Type.PATIENT);
        Map<String, Session> sessions = new HashMap<>(Collections.singletonMap(sessionToken, session));
        AuthenticationFilter filter = new AuthenticationFilter(sessions);

        //Create request with no body and the valid session token
        HTTPRequest request = new HTTPRequest(null, sessionToken);

        HTTPResponse<String> response = filter.getResponse(request);

        assertNull(response);
        assertTrue(sessions.containsKey(sessionToken));
    }
}