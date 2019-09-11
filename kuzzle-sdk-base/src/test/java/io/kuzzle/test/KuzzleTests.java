package io.kuzzle.test;

import io.kuzzle.runner.Helpers.IJObjectHelper;
import io.kuzzle.runner.Protocol.WebSocket;
import io.kuzzle.sdk.CoreClasses.Json.IJObject;
import io.kuzzle.sdk.CoreClasses.Task;
import io.kuzzle.sdk.Events.EventListener;
import io.kuzzle.sdk.Exceptions.InternalException;
import io.kuzzle.sdk.Exceptions.NotConnectedException;
import io.kuzzle.sdk.Protocol.AbstractProtocol;
import io.kuzzle.sdk.Protocol.ProtocolState;
import io.kuzzle.sdk.CoreClasses.Responses.ErrorResponse;
import io.kuzzle.sdk.CoreClasses.Responses.Response;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mockito;
import org.mockito.stubbing.Answer;

import java.net.URISyntaxException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

public class KuzzleTests <T> {
    private AbstractProtocol<T> networkProtocol = Mockito.mock(WebSocket.class);
    private EventListener
            tokenExpiredEventListener = Mockito.mock(EventListener.class);
    private EventListener<Response<T>>
            unhandledResponseEventListener = Mockito.mock(EventListener.class);
    private TestableKuzzle<T> kuzzle;

    @Before
    public void setup() throws URISyntaxException {
        kuzzle = new TestableKuzzle(networkProtocol);
        kuzzle.setTokenExpiredEventListener(tokenExpiredEventListener);
        kuzzle.setUnhandledResponseEventListener(unhandledResponseEventListener);
    }

    @Test
    public void connect() throws Exception {
        kuzzle.connect();
        Mockito.verify(
                networkProtocol,
                Mockito.times(1)
        ).connect();
    }

    @Test
    public void disconnect() throws Exception {
        kuzzle.disconnect();
        Mockito.verify(
                networkProtocol,
                Mockito.times(1)
        ).disconnect();
    }


    @Test
    public void registerTokenExpiredEvent() throws Exception {
        kuzzle.registerTokenExpiredEvent(() -> {});
        Mockito.verify(tokenExpiredEventListener,
               Mockito.times(1)
        ).register(Matchers.any(Runnable.class));
    }

    @Test
    public void unregisterTokenExpiredEvent() throws Exception {
        kuzzle.unregisterTokenExpiredEvent(() -> {});
        Mockito.verify(
                tokenExpiredEventListener,
                Mockito.times(1)
        ).unregister(Matchers.any(Runnable.class));
    }

    @Test
    public void onStateChanged() {
        ConcurrentHashMap<String, Task<Response<T>>>
                requests = kuzzle.getRequests();

        Task<Response<T>> response = new Task<>();
        requests.put("foobar", response);
        kuzzle.onStateChanged(ProtocolState.CLOSE);
        Assert.assertEquals(0, requests.size());
        Assert.assertTrue(response.isCompletedExceptionally());
    }

    @Test(expected = NotConnectedException.class)
    public void queryShouldThrowWhenNotConnected()
            throws NotConnectedException, InternalException {
        Mockito.when(networkProtocol.getState())
            .thenAnswer(
                (Answer<ProtocolState>) invocation -> ProtocolState.CLOSE
            );

        kuzzle.query(IJObjectHelper.<T>newIJObject().toNative());
    }

    @Test
    public void querySuccess() throws NotConnectedException, InternalException {
        Mockito.when(networkProtocol.getState())
            .thenAnswer(
                (Answer<ProtocolState>) invocation -> ProtocolState.OPEN
            );

        CompletableFuture<Response<T>>
                response = kuzzle.query(
                        IJObjectHelper.<T>newIJObject().toNative()
                );

        Assert.assertNotNull(response);
        Mockito.verify(
                networkProtocol,
                Mockito.times(1)
        ).send(Matchers.any(IJObject.class));
    }

    @Test(expected = InternalException.class)
    public void queryShouldThrowWhenVolatileIsNotIJObject()
            throws NotConnectedException, InternalException {
        Mockito.when(networkProtocol.getState())
            .thenAnswer(
                (Answer<ProtocolState>) invocation -> ProtocolState.OPEN
            );

        IJObject<T> payload = IJObjectHelper.<T>newIJObject();
        payload.put("volatile", "foobar");

        kuzzle.query(payload.toNative());
    }

    @Test
    public void onResponseReceivedAndTokenIsExpired() {
        ConcurrentHashMap<String, Task<Response<T>>>
                requests = kuzzle.getRequests();

        Response<T> response = new Response<T>();
        response.error = new ErrorResponse();
        response.error.message = "Token expired";
        response.error.status = 42;
        response.room = "room-id";

        Task<Response<T>> task = new Task<Response<T>>();

        requests.put("room-id", task);

        kuzzle.onResponseReceived(response.toIJObject().toJsonString());

        Mockito.verify(
                tokenExpiredEventListener,
                Mockito.times(1)
        ).trigger();
    }

    @Test
    public void onResponseReceivedAndResponseIsUnhandled() {
        ConcurrentHashMap<String, Task<Response<T>>>
                requests = kuzzle.getRequests();

        AtomicBoolean success = new AtomicBoolean(false);
        Response response = new Response();
        response.requestId = "foobar";

        Task<Response<T>> task = new Task<Response<T>>();

        requests.put("request-id", task);

        kuzzle.onResponseReceived(response.toIJObject().toJsonString());

        Mockito.verify(
                unhandledResponseEventListener,
                Mockito.times(1)
        ).trigger(Matchers.any(Response.class));
    }

}