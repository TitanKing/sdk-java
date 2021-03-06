package io.kuzzle.test.API.Controllers.DocumentTest;

import com.google.gson.internal.LazilyParsedNumber;
import io.kuzzle.sdk.CoreClasses.Maps.KuzzleMap;
import io.kuzzle.sdk.CoreClasses.Responses.Response;
import io.kuzzle.sdk.Exceptions.InternalException;
import io.kuzzle.sdk.Exceptions.NotConnectedException;
import io.kuzzle.sdk.Kuzzle;

import io.kuzzle.sdk.Options.SearchOptions;
import io.kuzzle.sdk.Options.UpdateOptions;
import io.kuzzle.sdk.Options.CreateOptions;

import io.kuzzle.sdk.Protocol.AbstractProtocol;
import io.kuzzle.sdk.Protocol.ProtocolState;
import io.kuzzle.sdk.Protocol.WebSocket;

import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.mockito.stubbing.Answer;

import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;

import java.lang.Thread;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

public class DocumentTest {

  private AbstractProtocol networkProtocol = Mockito.mock(WebSocket.class);

  @Test
  public void getDocumentTest() throws NotConnectedException, InternalException {

    Kuzzle kuzzleMock = spy(new Kuzzle(networkProtocol));
    String index = "nyc-open-data";
    String collection = "yellow-taxi";

    ArgumentCaptor arg = ArgumentCaptor.forClass(KuzzleMap.class);

    kuzzleMock.getDocumentController().get(index, collection, "some-id");
    Mockito.verify(kuzzleMock, Mockito.times(1)).query((KuzzleMap) arg.capture());

    assertEquals(((KuzzleMap) arg.getValue()).getString("controller"), "document");
    assertEquals(((KuzzleMap) arg.getValue()).getString("action"), "get");
    assertEquals(((KuzzleMap) arg.getValue()).getString("index"), "nyc-open-data");
    assertEquals(((KuzzleMap) arg.getValue()).getString("_id"), "some-id");
  }

  @Test(expected = NotConnectedException.class)
  public void getDocumentShouldThrowWhenNotConnected() throws NotConnectedException, InternalException {
    AbstractProtocol fakeNetworkProtocol = Mockito.mock(WebSocket.class);
    Mockito.when(fakeNetworkProtocol.getState()).thenAnswer((Answer<ProtocolState>) invocation -> ProtocolState.CLOSE);

    Kuzzle kuzzleMock = spy(new Kuzzle(fakeNetworkProtocol));
    String index = "nyc-open-data";
    String collection = "yellow-taxi";

    kuzzleMock.getDocumentController().get(index, collection, "some-id");
  }


  @Test
  public void createDocumentTestA() throws NotConnectedException, InternalException {

    Kuzzle kuzzleMock = spy(new Kuzzle(networkProtocol));
    String index = "nyc-open-data";
    String collection = "yellow-taxi";

    ConcurrentHashMap<String, Object> document = new ConcurrentHashMap<>();
    document.put("name", "Yoann");
    document.put("nickname", "El angel de la muerte que hace el JAVA");

    ArgumentCaptor arg = ArgumentCaptor.forClass(KuzzleMap.class);

    CreateOptions options = new CreateOptions();
    options.setId("some-id");
    options.setWaitForRefresh(true);

    kuzzleMock.getDocumentController().create(index, collection, document, options);
    Mockito.verify(kuzzleMock, Mockito.times(1)).query((KuzzleMap) arg.capture());

    assertEquals(((KuzzleMap) arg.getValue()).getString("controller"), "document");
    assertEquals(((KuzzleMap) arg.getValue()).getString("action"), "create");
    assertEquals(((KuzzleMap) arg.getValue()).getString("index"), "nyc-open-data");
    assertEquals(((KuzzleMap) arg.getValue()).getString("_id"), "some-id");
    assertEquals(((KuzzleMap) arg.getValue()).getString("refresh"), "wait_for");
    assertEquals(((ConcurrentHashMap<String, Object>) (((KuzzleMap) arg.getValue()).get("body"))).get("name").toString(), "Yoann");
    assertEquals(((ConcurrentHashMap<String, Object>) (((KuzzleMap) arg.getValue()).get("body"))).get("nickname").toString(), "El angel de la muerte que hace el JAVA");
  }

  @Test
  public void createDocumentTestB() throws NotConnectedException, InternalException {

    Kuzzle kuzzleMock = spy(new Kuzzle(networkProtocol));
    String index = "nyc-open-data";
    String collection = "yellow-taxi";

    ConcurrentHashMap<String, Object> document = new ConcurrentHashMap<>();
    document.put("name", "Yoann");
    document.put("nickname", "El angel de la muerte que hace el JAVA");

    ArgumentCaptor arg = ArgumentCaptor.forClass(KuzzleMap.class);

    kuzzleMock.getDocumentController().create(index, collection, document);
    Mockito.verify(kuzzleMock, Mockito.times(1)).query((KuzzleMap) arg.capture());

    assertEquals(((KuzzleMap) arg.getValue()).getString("controller"), "document");
    assertEquals(((KuzzleMap) arg.getValue()).getString("action"), "create");
    assertEquals(((KuzzleMap) arg.getValue()).getString("index"), "nyc-open-data");
    assertEquals(((KuzzleMap) arg.getValue()).getString("_id"), null);
    assertEquals(((KuzzleMap) arg.getValue()).getBoolean("waitForRefresh"), null);
    assertEquals(((ConcurrentHashMap<String, Object>) (((KuzzleMap) arg.getValue()).get("body"))).get("name").toString(), "Yoann");
    assertEquals(((ConcurrentHashMap<String, Object>) (((KuzzleMap) arg.getValue()).get("body"))).get("nickname").toString(), "El angel de la muerte que hace el JAVA");
  }

  @Test(expected = NotConnectedException.class)
  public void createDocumentThrowWhenNotConnected() throws NotConnectedException, InternalException {
    AbstractProtocol fakeNetworkProtocol = Mockito.mock(WebSocket.class);
    Mockito.when(fakeNetworkProtocol.getState()).thenAnswer((Answer<ProtocolState>) invocation -> ProtocolState.CLOSE);

    Kuzzle kuzzleMock = spy(new Kuzzle(fakeNetworkProtocol));
    String index = "nyc-open-data";
    String collection = "yellow-taxi";

    ConcurrentHashMap<String, Object> document = new ConcurrentHashMap<>();
    document.put("name", "Yoann");
    document.put("nickname", "El angel de la muerte que hace el JAVA");

    kuzzleMock.getDocumentController().create(index, collection, document);
  }

  @Test
  public void createOrReplaceDocumentTestA() throws NotConnectedException, InternalException {

    Kuzzle kuzzleMock = spy(new Kuzzle(networkProtocol));
    String index = "nyc-open-data";
    String collection = "yellow-taxi";
    ArgumentCaptor arg = ArgumentCaptor.forClass(KuzzleMap.class);
    ConcurrentHashMap<String, Object> document = new ConcurrentHashMap<>();
    document.put("name", "Yoann");
    document.put("nickname", "El angel de la muerte que hace el JAVA");

    kuzzleMock.getDocumentController().createOrReplace(index, collection, "some-id", document, true);
    Mockito.verify(kuzzleMock, Mockito.times(1)).query((KuzzleMap) arg.capture());

    assertEquals(((KuzzleMap) arg.getValue()).getString("controller"), "document");
    assertEquals(((KuzzleMap) arg.getValue()).getString("action"), "createOrReplace");
    assertEquals(((KuzzleMap) arg.getValue()).getString("index"), "nyc-open-data");
    assertEquals(((KuzzleMap) arg.getValue()).getString("_id"), "some-id");
    assertEquals(((KuzzleMap) arg.getValue()).getString("refresh"), "wait_for");
    assertEquals(((ConcurrentHashMap<String, Object>) (((KuzzleMap) arg.getValue()).get("body"))).get("name").toString(), "Yoann");
    assertEquals(((ConcurrentHashMap<String, Object>) (((KuzzleMap) arg.getValue()).get("body"))).get("nickname").toString(), "El angel de la muerte que hace el JAVA");
  }

  @Test
  public void createOrReplaceDocumentTestB() throws NotConnectedException, InternalException {

    Kuzzle kuzzleMock = spy(new Kuzzle(networkProtocol));
    String index = "nyc-open-data";
    String collection = "yellow-taxi";
    ArgumentCaptor arg = ArgumentCaptor.forClass(KuzzleMap.class);
    ConcurrentHashMap<String, Object> document = new ConcurrentHashMap<>();
    document.put("name", "Yoann");
    document.put("nickname", "El angel de la muerte que hace el JAVA");

    kuzzleMock.getDocumentController().createOrReplace(index, collection, "some-id", document);
    Mockito.verify(kuzzleMock, Mockito.times(1)).query((KuzzleMap) arg.capture());

    assertEquals(((KuzzleMap) arg.getValue()).getString("controller"), "document");
    assertEquals(((KuzzleMap) arg.getValue()).getString("action"), "createOrReplace");
    assertEquals(((KuzzleMap) arg.getValue()).getString("index"), "nyc-open-data");
    assertEquals(((KuzzleMap) arg.getValue()).getString("_id"), "some-id");
    assertEquals(((KuzzleMap) arg.getValue()).getString("waitForRefresh"), null);
    assertEquals(((ConcurrentHashMap<String, Object>) (((KuzzleMap) arg.getValue()).get("body"))).get("name").toString(), "Yoann");
    assertEquals(((ConcurrentHashMap<String, Object>) (((KuzzleMap) arg.getValue()).get("body"))).get("nickname").toString(), "El angel de la muerte que hace el JAVA");
  }

  @Test(expected = NotConnectedException.class)
  public void createOrReplaceDocumentShouldThrowWhenNotConnected() throws NotConnectedException, InternalException {
    AbstractProtocol fakeNetworkProtocol = Mockito.mock(WebSocket.class);
    Mockito.when(fakeNetworkProtocol.getState()).thenAnswer((Answer<ProtocolState>) invocation -> ProtocolState.CLOSE);

    Kuzzle kuzzleMock = spy(new Kuzzle(fakeNetworkProtocol));
    String index = "nyc-open-data";
    String collection = "yellow-taxi";

    ConcurrentHashMap<String, Object> document = new ConcurrentHashMap<>();
    document.put("name", "Yoann");
    document.put("nickname", "El angel de la muerte que hace el JAVA");

    kuzzleMock.getDocumentController().createOrReplace(index, collection, "some-id", document);
  }

  @Test
  public void udpateDocumentTestA() throws NotConnectedException, InternalException {

    Kuzzle kuzzleMock = spy(new Kuzzle(networkProtocol));
    String index = "nyc-open-data";
    String collection = "yellow-taxi";

    ConcurrentHashMap<String, Object> document = new ConcurrentHashMap<>();
    document.put("name", "Yoann");

    UpdateOptions options = new UpdateOptions();
    options.setWaitForRefresh(true);
    options.setSource(true);
    options.setRetryOnConflict(1);

    ArgumentCaptor arg = ArgumentCaptor.forClass(KuzzleMap.class);

    kuzzleMock.getDocumentController().update(index, collection, "some-id", document, options);
    Mockito.verify(kuzzleMock, Mockito.times(1)).query((KuzzleMap) arg.capture());

    assertEquals(((KuzzleMap) arg.getValue()).getString("controller"), "document");
    assertEquals(((KuzzleMap) arg.getValue()).getString("action"), "update");
    assertEquals(((KuzzleMap) arg.getValue()).getString("index"), "nyc-open-data");
    assertEquals(((KuzzleMap) arg.getValue()).getString("_id"), "some-id");
    assertEquals(((KuzzleMap) arg.getValue()).getNumber("retryOnConflict"), 1);
    assertEquals(((KuzzleMap) arg.getValue()).getString("refresh"), "wait_for");
    assertEquals(((KuzzleMap) arg.getValue()).getBoolean("source"), true);
    assertEquals(((ConcurrentHashMap<String, Object>) (((KuzzleMap) arg.getValue()).get("body"))).get("name").toString(), "Yoann");
  }

  @Test
  public void udpateDocumentTestB() throws NotConnectedException, InternalException {

    Kuzzle kuzzleMock = spy(new Kuzzle(networkProtocol));
    String index = "nyc-open-data";
    String collection = "yellow-taxi";

    ConcurrentHashMap<String, Object> document = new ConcurrentHashMap<>();

    document.put("name", "Yoann");
    ArgumentCaptor arg = ArgumentCaptor.forClass(KuzzleMap.class);

    kuzzleMock.getDocumentController().update(index, collection, "some-id", document);
    Mockito.verify(kuzzleMock, Mockito.times(1)).query((KuzzleMap) arg.capture());

    assertEquals(((KuzzleMap) arg.getValue()).getString("controller"), "document");
    assertEquals(((KuzzleMap) arg.getValue()).getString("action"), "update");
    assertEquals(((KuzzleMap) arg.getValue()).getString("index"), "nyc-open-data");
    assertEquals(((KuzzleMap) arg.getValue()).getString("_id"), "some-id");
    assertEquals(((KuzzleMap) arg.getValue()).getNumber("retryOnConflict"), null);
    assertEquals(((KuzzleMap) arg.getValue()).getBoolean("waitForRefresh"), null);
    assertEquals(((KuzzleMap) arg.getValue()).getBoolean("source"), null);
    assertEquals(((ConcurrentHashMap<String, Object>) (((KuzzleMap) arg.getValue()).get("body"))).get("name").toString(), "Yoann");
  }

  @Test(expected = NotConnectedException.class)
  public void updateShouldThrowWhenNotConnected() throws NotConnectedException, InternalException {

    AbstractProtocol fakeNetworkProtocol = Mockito.mock(WebSocket.class);
    Mockito.when(fakeNetworkProtocol.getState()).thenAnswer((Answer<ProtocolState>) invocation -> ProtocolState.CLOSE);

    Kuzzle kuzzleMock = spy(new Kuzzle(fakeNetworkProtocol));
    String index = "nyc-open-data";
    String collection = "yellow-taxi";
    String id = "some-id";

    ConcurrentHashMap<String, Object> document = new ConcurrentHashMap<>();
    document.put("name", "Yoann");

    UpdateOptions options = new UpdateOptions();
    options.setWaitForRefresh(false);
    options.setSource(true);
    options.setRetryOnConflict(1);

    ArgumentCaptor arg = ArgumentCaptor.forClass(KuzzleMap.class);

    kuzzleMock.getDocumentController().update(index, collection, "some-id", document, options);
    Mockito.verify(kuzzleMock, Mockito.times(1)).query((KuzzleMap) arg.capture());

    assertEquals(((KuzzleMap) arg.getValue()).getString("controller"), "document");
    assertEquals(((KuzzleMap) arg.getValue()).getString("action"), "update");
    assertEquals(((KuzzleMap) arg.getValue()).getString("index"), "nyc-open-data");
    assertEquals(((KuzzleMap) arg.getValue()).getString("_id"), "some-id");
    assertEquals(((KuzzleMap) arg.getValue()).getNumber("retryOnConflict"), 1);
    assertEquals(((KuzzleMap) arg.getValue()).getBoolean("waitForRefresh"), false);
    assertEquals(((KuzzleMap) arg.getValue()).getBoolean("source"), true);
    assertEquals(((ConcurrentHashMap<String, Object>) (((KuzzleMap) arg.getValue()).get("body"))).get("name").toString(), "Yoann");
  }

  @Test
  public void mCreateDocumentTestA() throws NotConnectedException, InternalException {

    Kuzzle kuzzleMock = spy(new Kuzzle(networkProtocol));
    String index = "nyc-open-data";
    String collection = "yellow-taxi";

    ConcurrentHashMap<String, Object> document1 = new ConcurrentHashMap<>();
    ConcurrentHashMap<String, Object> document2 = new ConcurrentHashMap<>();
    ConcurrentHashMap<String, Object> body1 = new ConcurrentHashMap<>();
    ConcurrentHashMap<String, Object> body2 = new ConcurrentHashMap<>();

    document1.put("_id", "some-id1");
    body1.put("key1", "value1");
    document1.put("body", body1);

    document2.put("_id", "some-id2");
    body2.put("key2", "value2");
    document2.put("body", body2);

    final ArrayList<ConcurrentHashMap<String, Object>> documents = new ArrayList<>();
    documents.add(document1);
    documents.add(document2);

    ArgumentCaptor<KuzzleMap> arg = ArgumentCaptor.forClass(KuzzleMap.class);

    kuzzleMock.getDocumentController().mCreate(index, collection, documents);
    Mockito.verify(kuzzleMock, Mockito.times(1)).query(arg.capture());

    assertEquals((arg.getValue()).getString("controller"), "document");
    assertEquals((arg.getValue()).getString("action"), "mCreate");
    assertEquals((arg.getValue()).getString("index"), "nyc-open-data");
    assertEquals((arg.getValue()).getBoolean("waitForRefresh"), null);
    assertEquals(((ArrayList<ConcurrentHashMap<String, Object>>) (((KuzzleMap) (arg.getValue()).get("body"))).get("documents")).get(0).get("_id").toString(), "some-id1");
    assertEquals(((ArrayList<ConcurrentHashMap<String, Object>>) (((KuzzleMap) (arg.getValue()).get("body"))).get("documents")).get(1).get("_id").toString(), "some-id2");
  }

  @Test
  public void mCreateDocumentTestB() throws NotConnectedException, InternalException {

    Kuzzle kuzzleMock = spy(new Kuzzle(networkProtocol));
    String index = "nyc-open-data";
    String collection = "yellow-taxi";

    ConcurrentHashMap<String, Object> document1 = new ConcurrentHashMap<>();
    ConcurrentHashMap<String, Object> document2 = new ConcurrentHashMap<>();
    ConcurrentHashMap<String, Object> body = new ConcurrentHashMap<>();

    document1.put("_id", "some-id1");
    body.put("key1", "value1");
    document1.put("body", body);

    document2.put("_id", "some-id2");
    body.put("key2", "value2");
    document2.put("body", body);

    final ArrayList<ConcurrentHashMap<String, Object>> documents = new ArrayList<>();
    documents.add(document1);
    documents.add(document2);

    ArgumentCaptor<KuzzleMap> arg = ArgumentCaptor.forClass(KuzzleMap.class);

    kuzzleMock.getDocumentController().mCreate(index, collection, documents, true);
    Mockito.verify(kuzzleMock, Mockito.times(1)).query(arg.capture());

    assertEquals((arg.getValue()).getString("controller"), "document");
    assertEquals((arg.getValue()).getString("action"), "mCreate");
    assertEquals((arg.getValue()).getString("index"), "nyc-open-data");
    assertEquals((arg.getValue()).getString("refresh"), "wait_for");
    assertEquals(((ArrayList<ConcurrentHashMap<String, Object>>) (((KuzzleMap) (arg.getValue()).get("body"))).get("documents")).get(0).get("_id").toString(), "some-id1");
    assertEquals(((ArrayList<ConcurrentHashMap<String, Object>>) (((KuzzleMap) (arg.getValue()).get("body"))).get("documents")).get(1).get("_id").toString(), "some-id2");
  }

  @Test(expected = NotConnectedException.class)
  public void mCreateDocumentShouldThrowWhenNotConnected() throws NotConnectedException, InternalException {
    AbstractProtocol fakeNetworkProtocol = Mockito.mock(WebSocket.class);
    Mockito.when(fakeNetworkProtocol.getState()).thenAnswer((Answer<ProtocolState>) invocation -> ProtocolState.CLOSE);

    Kuzzle kuzzleMock = spy(new Kuzzle(fakeNetworkProtocol));
    String index = "nyc-open-data";
    String collection = "yellow-taxi";

    ConcurrentHashMap<String, Object> document1 = new ConcurrentHashMap<>();
    ConcurrentHashMap<String, Object> document2 = new ConcurrentHashMap<>();
    ConcurrentHashMap<String, Object> body = new ConcurrentHashMap<>();

    document1.put("_id", "some-id1");
    body.put("key1", "value1");
    document1.put("body", body);

    document2.put("_id", "some-id2");
    body.put("key2", "value2");
    document2.put("body", body);

    final ArrayList<ConcurrentHashMap<String, Object>> documents = new ArrayList<>();
    documents.add(document1);
    documents.add(document2);

    kuzzleMock.getDocumentController().mCreate(index, collection, documents);
  }

  @Test
  public void mDeleteDocumentTestA() throws NotConnectedException, InternalException {

    Kuzzle kuzzleMock = spy(new Kuzzle(networkProtocol));
    String index = "nyc-open-data";
    String collection = "yellow-taxi";

    final ArrayList<String> ids = new ArrayList<>();
    ids.add("some-id1");
    ids.add("some-id2");
    ArgumentCaptor<KuzzleMap> arg = ArgumentCaptor.forClass(KuzzleMap.class);

    kuzzleMock.getDocumentController().mDelete(index, collection, ids);
    Mockito.verify(kuzzleMock, Mockito.times(1)).query(arg.capture());

    assertEquals((arg.getValue()).getString("controller"), "document");
    assertEquals((arg.getValue()).getString("action"), "mDelete");
    assertEquals((arg.getValue()).getString("index"), "nyc-open-data");
    assertEquals((arg.getValue()).getBoolean("waitForRefresh"), null);
    assertEquals(((ArrayList<String>) (((KuzzleMap) (arg.getValue()).get("body"))).get("ids")).get(0), "some-id1");
    assertEquals(((ArrayList<String>) (((KuzzleMap) (arg.getValue()).get("body"))).get("ids")).get(1), "some-id2");
  }

  @Test
  public void mDeleteDocumentTestB() throws NotConnectedException, InternalException {

    Kuzzle kuzzleMock = spy(new Kuzzle(networkProtocol));
    String index = "nyc-open-data";
    String collection = "yellow-taxi";

    final ArrayList<String> ids = new ArrayList<>();
    ids.add("some-id1");
    ids.add("some-id2");
    ArgumentCaptor<KuzzleMap> arg = ArgumentCaptor.forClass(KuzzleMap.class);

    kuzzleMock.getDocumentController().mDelete(index, collection, ids, true);
    Mockito.verify(kuzzleMock, Mockito.times(1)).query(arg.capture());

    assertEquals((arg.getValue()).getString("controller"), "document");
    assertEquals((arg.getValue()).getString("action"), "mDelete");
    assertEquals((arg.getValue()).getString("index"), "nyc-open-data");
    assertEquals((arg.getValue()).getString("refresh"), "wait_for");
    assertEquals(((ArrayList<String>) (((KuzzleMap) (arg.getValue()).get("body"))).get("ids")).get(0), "some-id1");
    assertEquals(((ArrayList<String>) (((KuzzleMap) (arg.getValue()).get("body"))).get("ids")).get(1), "some-id2");
  }

  @Test(expected = NotConnectedException.class)
  public void mDeleteDocumentShouldThrowWhenNotConnected() throws NotConnectedException, InternalException {
    AbstractProtocol fakeNetworkProtocol = Mockito.mock(WebSocket.class);
    Mockito.when(fakeNetworkProtocol.getState()).thenAnswer((Answer<ProtocolState>) invocation -> ProtocolState.CLOSE);

    Kuzzle kuzzleMock = spy(new Kuzzle(fakeNetworkProtocol));
    String index = "nyc-open-data";
    String collection = "yellow-taxi";

    final ArrayList<String> ids = new ArrayList<>();
    ids.add("some-id1");
    ids.add("some-id2");

    kuzzleMock.getDocumentController().mDelete(index, collection, ids);
  }

  @Test
  public void replaceDocumentTestA() throws NotConnectedException, InternalException {

    Kuzzle kuzzleMock = spy(new Kuzzle(networkProtocol));
    String index = "nyc-open-data";
    String collection = "yellow-taxi";

    ConcurrentHashMap<String, Object> document = new ConcurrentHashMap<>();
    document.put("name", "Yoann");
    document.put("nickname", "El angel de la muerte que hace el JAVA");

    ArgumentCaptor arg = ArgumentCaptor.forClass(KuzzleMap.class);

    kuzzleMock.getDocumentController().replace(index, collection, "some-id", document, true);
    Mockito.verify(kuzzleMock, Mockito.times(1)).query((KuzzleMap) arg.capture());

    assertEquals(((KuzzleMap) arg.getValue()).getString("controller"), "document");
    assertEquals(((KuzzleMap) arg.getValue()).getString("action"), "replace");
    assertEquals(((KuzzleMap) arg.getValue()).getString("index"), "nyc-open-data");
    assertEquals(((KuzzleMap) arg.getValue()).getString("_id"), "some-id");
    assertEquals(((KuzzleMap) arg.getValue()).getString("refresh"), "wait_for");
    assertEquals(((ConcurrentHashMap<String, Object>) (((KuzzleMap) arg.getValue()).get("body"))).get("name").toString(), "Yoann");
    assertEquals(((ConcurrentHashMap<String, Object>) (((KuzzleMap) arg.getValue()).get("body"))).get("nickname").toString(), "El angel de la muerte que hace el JAVA");
  }

  @Test
  public void replaceDocumentTestB() throws NotConnectedException, InternalException {

    Kuzzle kuzzleMock = spy(new Kuzzle(networkProtocol));
    String index = "nyc-open-data";
    String collection = "yellow-taxi";

    ConcurrentHashMap<String, Object> document = new ConcurrentHashMap<>();
    document.put("name", "Yoann");
    document.put("nickname", "El angel de la muerte que hace el JAVA");

    ArgumentCaptor arg = ArgumentCaptor.forClass(KuzzleMap.class);

    kuzzleMock.getDocumentController().replace(index, collection, "some-id", document);
    Mockito.verify(kuzzleMock, Mockito.times(1)).query((KuzzleMap) arg.capture());

    assertEquals(((KuzzleMap) arg.getValue()).getString("controller"), "document");
    assertEquals(((KuzzleMap) arg.getValue()).getString("action"), "replace");
    assertEquals(((KuzzleMap) arg.getValue()).getString("index"), "nyc-open-data");
    assertEquals(((KuzzleMap) arg.getValue()).getString("_id"), "some-id");
    assertEquals(((KuzzleMap) arg.getValue()).getBoolean("waitForRefresh"), null);
    assertEquals(((ConcurrentHashMap<String, Object>) (((KuzzleMap) arg.getValue()).get("body"))).get("name").toString(), "Yoann");
    assertEquals(((ConcurrentHashMap<String, Object>) (((KuzzleMap) arg.getValue()).get("body"))).get("nickname").toString(), "El angel de la muerte que hace el JAVA");
  }

  @Test(expected = NotConnectedException.class)
  public void replaceDocumentShouldThrowWhenNotConnected() throws NotConnectedException, InternalException {
    AbstractProtocol fakeNetworkProtocol = Mockito.mock(WebSocket.class);
    Mockito.when(fakeNetworkProtocol.getState()).thenAnswer((Answer<ProtocolState>) invocation -> ProtocolState.CLOSE);

    Kuzzle kuzzleMock = spy(new Kuzzle(fakeNetworkProtocol));
    String index = "nyc-open-data";
    String collection = "yellow-taxi";

    ConcurrentHashMap<String, Object> document = new ConcurrentHashMap<>();
    document.put("name", "Yoann");
    document.put("nickname", "El angel de la muerte que hace el JAVA");

    kuzzleMock.getDocumentController().replace(index, collection, "some-id", document);
  }

  @Test
  public void deleteDocumentTestA() throws NotConnectedException, InternalException {

    Kuzzle kuzzleMock = spy(new Kuzzle(networkProtocol));
    String index = "nyc-open-data";
    String collection = "yellow-taxi";

    ArgumentCaptor arg = ArgumentCaptor.forClass(KuzzleMap.class);

    kuzzleMock.getDocumentController().delete(index, collection, "some-id", true);
    Mockito.verify(kuzzleMock, Mockito.times(1)).query((KuzzleMap) arg.capture());

    assertEquals(((KuzzleMap) arg.getValue()).getString("controller"), "document");
    assertEquals(((KuzzleMap) arg.getValue()).getString("action"), "delete");
    assertEquals(((KuzzleMap) arg.getValue()).getString("index"), "nyc-open-data");
    assertEquals(((KuzzleMap) arg.getValue()).getString("_id"), "some-id");
    assertEquals(((KuzzleMap) arg.getValue()).getString("refresh"), "wait_for");
  }

  @Test
  public void deleteDocumentTestB() throws NotConnectedException, InternalException {

    Kuzzle kuzzleMock = spy(new Kuzzle(networkProtocol));
    String index = "nyc-open-data";
    String collection = "yellow-taxi";

    ArgumentCaptor arg = ArgumentCaptor.forClass(KuzzleMap.class);

    kuzzleMock.getDocumentController().delete(index, collection, "some-id");
    Mockito.verify(kuzzleMock, Mockito.times(1)).query((KuzzleMap) arg.capture());

    assertEquals(((KuzzleMap) arg.getValue()).getString("controller"), "document");
    assertEquals(((KuzzleMap) arg.getValue()).getString("action"), "delete");
    assertEquals(((KuzzleMap) arg.getValue()).getString("index"), "nyc-open-data");
    assertEquals(((KuzzleMap) arg.getValue()).getString("_id"), "some-id");
    assertEquals(((KuzzleMap) arg.getValue()).getBoolean("waitForRefresh"), null);
  }

  @Test(expected = NotConnectedException.class)
  public void deleteDocumentShouldThrowWhenNotConnected() throws NotConnectedException, InternalException {
    AbstractProtocol fakeNetworkProtocol = Mockito.mock(WebSocket.class);
    Mockito.when(fakeNetworkProtocol.getState()).thenAnswer((Answer<ProtocolState>) invocation -> ProtocolState.CLOSE);

    Kuzzle kuzzleMock = spy(new Kuzzle(fakeNetworkProtocol));
    String index = "nyc-open-data";
    String collection = "yellow-taxi";

    kuzzleMock.getDocumentController().delete(index, collection, "some-id");
  }

  @Test
  public void mGetDocumentTest() throws NotConnectedException, InternalException {

    Kuzzle kuzzleMock = spy(new Kuzzle(networkProtocol));
    String index = "nyc-open-data";
    String collection = "yellow-taxi";

    final ArrayList<String> ids = new ArrayList<>();
    ids.add("some-id1");
    ids.add("some-id2");
    ArgumentCaptor<KuzzleMap> arg = ArgumentCaptor.forClass(KuzzleMap.class);

    kuzzleMock.getDocumentController().mGet(index, collection, ids);
    Mockito.verify(kuzzleMock, Mockito.times(1)).query(arg.capture());

    assertEquals((arg.getValue()).getString("controller"), "document");
    assertEquals((arg.getValue()).getString("action"), "mGet");
    assertEquals((arg.getValue()).getString("index"), "nyc-open-data");
    assertEquals(((ArrayList<String>) (((KuzzleMap) (arg.getValue()).get("body"))).get("ids")).get(0), "some-id1");
    assertEquals(((ArrayList<String>) (((KuzzleMap) (arg.getValue()).get("body"))).get("ids")).get(1), "some-id2");
  }

  @Test(expected = NotConnectedException.class)
  public void mGetDocumentShouldThrowWhenNotConnected() throws NotConnectedException, InternalException {
    AbstractProtocol fakeNetworkProtocol = Mockito.mock(WebSocket.class);
    Mockito.when(fakeNetworkProtocol.getState()).thenAnswer((Answer<ProtocolState>) invocation -> ProtocolState.CLOSE);

    Kuzzle kuzzleMock = spy(new Kuzzle(fakeNetworkProtocol));
    String index = "nyc-open-data";
    String collection = "yellow-taxi";

    final ArrayList<String> ids = new ArrayList<>();
    ids.add("some-id1");
    ids.add("some-id2");

    kuzzleMock.getDocumentController().mGet(index, collection, ids);
  }

  @Test
  public void mReplaceDocumentTestA() throws NotConnectedException, InternalException {

    Kuzzle kuzzleMock = spy(new Kuzzle(networkProtocol));
    String index = "nyc-open-data";
    String collection = "yellow-taxi";

    ConcurrentHashMap<String, Object> document1 = new ConcurrentHashMap<>();
    ConcurrentHashMap<String, Object> document2 = new ConcurrentHashMap<>();
    ConcurrentHashMap<String, Object> body1 = new ConcurrentHashMap<>();
    ConcurrentHashMap<String, Object> body2 = new ConcurrentHashMap<>();

    document1.put("_id", "some-id1");
    body1.put("key1", "value1");
    document1.put("body", body1);

    document2.put("_id", "some-id2");
    body2.put("key2", "value2");
    document2.put("body", body2);

    final ArrayList<ConcurrentHashMap<String, Object>> documents = new ArrayList<>();
    documents.add(document1);
    documents.add(document2);

    ArgumentCaptor<KuzzleMap> arg = ArgumentCaptor.forClass(KuzzleMap.class);

    kuzzleMock.getDocumentController().mReplace(index, collection, documents);
    Mockito.verify(kuzzleMock, Mockito.times(1)).query(arg.capture());

    assertEquals((arg.getValue()).getString("controller"), "document");
    assertEquals((arg.getValue()).getString("action"), "mReplace");
    assertEquals((arg.getValue()).getString("index"), "nyc-open-data");
    assertEquals((arg.getValue()).getBoolean("waitForRefresh"), null);
    assertEquals(((ArrayList<ConcurrentHashMap<String, Object>>) (((KuzzleMap) (arg.getValue()).get("body"))).get("documents")).get(0).get("_id").toString(), "some-id1");
    assertEquals(((ArrayList<ConcurrentHashMap<String, Object>>) (((KuzzleMap) (arg.getValue()).get("body"))).get("documents")).get(1).get("_id").toString(), "some-id2");
  }

  @Test
  public void mReplaceDocumentTestB() throws NotConnectedException, InternalException {

    Kuzzle kuzzleMock = spy(new Kuzzle(networkProtocol));
    String index = "nyc-open-data";
    String collection = "yellow-taxi";

    ConcurrentHashMap<String, Object> document1 = new ConcurrentHashMap<>();
    ConcurrentHashMap<String, Object> document2 = new ConcurrentHashMap<>();
    ConcurrentHashMap<String, Object> body = new ConcurrentHashMap<>();

    document1.put("_id", "some-id1");
    body.put("key1", "value1");
    document1.put("body", body);

    document2.put("_id", "some-id2");
    body.put("key2", "value2");
    document2.put("body", body);

    final ArrayList<ConcurrentHashMap<String, Object>> documents = new ArrayList<>();
    documents.add(document1);
    documents.add(document2);

    ArgumentCaptor<KuzzleMap> arg = ArgumentCaptor.forClass(KuzzleMap.class);

    kuzzleMock.getDocumentController().mReplace(index, collection, documents, true);
    Mockito.verify(kuzzleMock, Mockito.times(1)).query(arg.capture());

    assertEquals((arg.getValue()).getString("controller"), "document");
    assertEquals((arg.getValue()).getString("action"), "mReplace");
    assertEquals((arg.getValue()).getString("index"), "nyc-open-data");
    assertEquals((arg.getValue()).getString("refresh"), "wait_for");
    assertEquals(((ArrayList<ConcurrentHashMap<String, Object>>) (((KuzzleMap) (arg.getValue()).get("body"))).get("documents")).get(0).get("_id").toString(), "some-id1");
    assertEquals(((ArrayList<ConcurrentHashMap<String, Object>>) (((KuzzleMap) (arg.getValue()).get("body"))).get("documents")).get(1).get("_id").toString(), "some-id2");
  }

  @Test(expected = NotConnectedException.class)
  public void mReplaceDocumentShouldThrowWhenNotConnected() throws NotConnectedException, InternalException {
    AbstractProtocol fakeNetworkProtocol = Mockito.mock(WebSocket.class);
    Mockito.when(fakeNetworkProtocol.getState()).thenAnswer((Answer<ProtocolState>) invocation -> ProtocolState.CLOSE);

    Kuzzle kuzzleMock = spy(new Kuzzle(fakeNetworkProtocol));
    String index = "nyc-open-data";
    String collection = "yellow-taxi";

    ConcurrentHashMap<String, Object> document1 = new ConcurrentHashMap<>();
    ConcurrentHashMap<String, Object> document2 = new ConcurrentHashMap<>();
    ConcurrentHashMap<String, Object> body = new ConcurrentHashMap<>();

    document1.put("_id", "some-id1");
    body.put("key1", "value1");
    document1.put("body", body);

    document2.put("_id", "some-id2");
    body.put("key2", "value2");
    document2.put("body", body);

    final ArrayList<ConcurrentHashMap<String, Object>> documents = new ArrayList<>();
    documents.add(document1);
    documents.add(document2);

    kuzzleMock.getDocumentController().mReplace(index, collection, documents);
  }

  @Test
  public void existsDocumentTest() throws NotConnectedException, InternalException {

    Kuzzle kuzzleMock = spy(new Kuzzle(networkProtocol));
    String index = "nyc-open-data";
    String collection = "yellow-taxi";


    ArgumentCaptor<KuzzleMap> arg = ArgumentCaptor.forClass(KuzzleMap.class);

    kuzzleMock.getDocumentController().exists(index, collection, "some-id");
    Mockito.verify(kuzzleMock, Mockito.times(1)).query(arg.capture());

    assertEquals((arg.getValue()).getString("controller"), "document");
    assertEquals((arg.getValue()).getString("action"), "exists");
    assertEquals((arg.getValue()).getString("index"), "nyc-open-data");
    assertEquals((arg.getValue()).getString("_id"), "some-id");
  }

  @Test(expected = NotConnectedException.class)
  public void existsDocumentShouldThrowWhenNotConnected() throws NotConnectedException, InternalException {
    AbstractProtocol fakeNetworkProtocol = Mockito.mock(WebSocket.class);
    Mockito.when(fakeNetworkProtocol.getState()).thenAnswer((Answer<ProtocolState>) invocation -> ProtocolState.CLOSE);

    Kuzzle kuzzleMock = spy(new Kuzzle(fakeNetworkProtocol));
    String index = "nyc-open-data";
    String collection = "yellow-taxi";

    kuzzleMock.getDocumentController().exists(index, collection, "some-id");
  }

  @Test
  public void mUpdateDocumentTestA() throws NotConnectedException, InternalException {
    Kuzzle kuzzleMock = spy(new Kuzzle(networkProtocol));
    String index = "nyc-open-data";
    String collection = "yellow-taxi";

    ConcurrentHashMap<String, Object> document1 = new ConcurrentHashMap<>();
    ConcurrentHashMap<String, Object> document2 = new ConcurrentHashMap<>();
    ConcurrentHashMap<String, Object> body1 = new ConcurrentHashMap<>();
    ConcurrentHashMap<String, Object> body2 = new ConcurrentHashMap<>();

    document1.put("_id", "some-id1");
    body1.put("key1", "value1");
    document1.put("body", body1);

    document2.put("_id", "some-id2");
    body2.put("key2", "value2");
    document2.put("body", body2);

    final ArrayList<ConcurrentHashMap<String, Object>> documents = new ArrayList<>();
    documents.add(document1);
    documents.add(document2);

    ArgumentCaptor<KuzzleMap> arg = ArgumentCaptor.forClass(KuzzleMap.class);

    kuzzleMock.getDocumentController().mUpdate(index, collection, documents);
    Mockito.verify(kuzzleMock, Mockito.times(1)).query(arg.capture());

    assertEquals((arg.getValue()).getString("controller"), "document");
    assertEquals((arg.getValue()).getString("action"), "mUpdate");
    assertEquals((arg.getValue()).getString("index"), "nyc-open-data");
    assertEquals((arg.getValue()).getNumber("retryOnConflict"), null);
    assertEquals((arg.getValue()).getBoolean("waitForRefresh"), null);
    assertEquals(((ArrayList<ConcurrentHashMap<String, Object>>) (((KuzzleMap) (arg.getValue()).get("body"))).get("documents")).get(0).get("_id").toString(), "some-id1");
    assertEquals(((ArrayList<ConcurrentHashMap<String, Object>>) (((KuzzleMap) (arg.getValue()).get("body"))).get("documents")).get(1).get("_id").toString(), "some-id2");
  }

  @Test
  public void mUpdateDocumentTestB() throws NotConnectedException, InternalException {

    Kuzzle kuzzleMock = spy(new Kuzzle(networkProtocol));
    String index = "nyc-open-data";
    String collection = "yellow-taxi";

    ConcurrentHashMap<String, Object> document1 = new ConcurrentHashMap<>();
    ConcurrentHashMap<String, Object> document2 = new ConcurrentHashMap<>();
    ConcurrentHashMap<String, Object> body1 = new ConcurrentHashMap<>();
    ConcurrentHashMap<String, Object> body2 = new ConcurrentHashMap<>();

    document1.put("_id", "some-id1");
    body1.put("key1", "value1");
    document1.put("body", body1);

    document2.put("_id", "some-id2");
    body2.put("key2", "value2");
    document2.put("body", body2);

    final ArrayList<ConcurrentHashMap<String, Object>> documents = new ArrayList<>();
    documents.add(document1);
    documents.add(document2);

    UpdateOptions options = new UpdateOptions();
    options.setRetryOnConflict(0);
    options.setWaitForRefresh(true);

    ArgumentCaptor<KuzzleMap> arg = ArgumentCaptor.forClass(KuzzleMap.class);

    kuzzleMock.getDocumentController().mUpdate(index, collection, documents, options);
    Mockito.verify(kuzzleMock, Mockito.times(1)).query(arg.capture());

    assertEquals((arg.getValue()).getString("controller"), "document");
    assertEquals((arg.getValue()).getString("action"), "mUpdate");
    assertEquals((arg.getValue()).getString("index"), "nyc-open-data");
    assertEquals((arg.getValue()).getNumber("retryOnConflict"), 0);
    assertEquals((arg.getValue()).getString("refresh"), "wait_for");
    assertEquals(((ArrayList<ConcurrentHashMap<String, Object>>) (((KuzzleMap) (arg.getValue()).get("body"))).get("documents")).get(0).get("_id").toString(), "some-id1");
    assertEquals(((ArrayList<ConcurrentHashMap<String, Object>>) (((KuzzleMap) (arg.getValue()).get("body"))).get("documents")).get(1).get("_id").toString(), "some-id2");
  }

  @Test(expected = NotConnectedException.class)
  public void mUpdateDocumentShouldThrowWhenNotConnected() throws NotConnectedException, InternalException {
    AbstractProtocol fakeNetworkProtocol = Mockito.mock(WebSocket.class);
    Mockito.when(fakeNetworkProtocol.getState()).thenAnswer((Answer<ProtocolState>) invocation -> ProtocolState.CLOSE);

    Kuzzle kuzzleMock = spy(new Kuzzle(fakeNetworkProtocol));
    String index = "nyc-open-data";
    String collection = "yellow-taxi";

    ConcurrentHashMap<String, Object> document1 = new ConcurrentHashMap<>();
    ConcurrentHashMap<String, Object> document2 = new ConcurrentHashMap<>();
    ConcurrentHashMap<String, Object> body1 = new ConcurrentHashMap<>();
    ConcurrentHashMap<String, Object> body2 = new ConcurrentHashMap<>();

    document1.put("_id", "some-id1");
    body1.put("key1", "value1");
    document1.put("body", body1);

    document2.put("_id", "some-id2");
    body2.put("key2", "value2");
    document2.put("body", body2);

    final ArrayList<ConcurrentHashMap<String, Object>> documents = new ArrayList<>();
    documents.add(document1);
    documents.add(document2);

    kuzzleMock.getDocumentController().mUpdate(index, collection, documents);
  }

  @Test
  public void mCreateOrReplaceDocumentTestA() throws NotConnectedException, InternalException {

    Kuzzle kuzzleMock = spy(new Kuzzle(networkProtocol));
    String index = "nyc-open-data";
    String collection = "yellow-taxi";

    ConcurrentHashMap<String, Object> document1 = new ConcurrentHashMap<>();
    ConcurrentHashMap<String, Object> document2 = new ConcurrentHashMap<>();
    ConcurrentHashMap<String, Object> body1 = new ConcurrentHashMap<>();
    ConcurrentHashMap<String, Object> body2 = new ConcurrentHashMap<>();

    document1.put("_id", "some-id1");
    body1.put("key1", "value1");
    document1.put("body", body1);

    document2.put("_id", "some-id2");
    body2.put("key2", "value2");
    document2.put("body", body2);

    final ArrayList<ConcurrentHashMap<String, Object>> documents = new ArrayList<>();
    documents.add(document1);
    documents.add(document2);

    ArgumentCaptor<KuzzleMap> arg = ArgumentCaptor.forClass(KuzzleMap.class);

    kuzzleMock.getDocumentController().mCreateOrReplace(index, collection, documents);
    Mockito.verify(kuzzleMock, Mockito.times(1)).query(arg.capture());

    assertEquals((arg.getValue()).getString("controller"), "document");
    assertEquals((arg.getValue()).getString("action"), "mCreateOrReplace");
    assertEquals((arg.getValue()).getString("index"), "nyc-open-data");
    assertEquals((arg.getValue()).getBoolean("waitForRefresh"), null);
    assertEquals(((ArrayList<ConcurrentHashMap<String, Object>>) (((KuzzleMap) (arg.getValue()).get("body"))).get("documents")).get(0).get("_id").toString(), "some-id1");
    assertEquals(((ArrayList<ConcurrentHashMap<String, Object>>) (((KuzzleMap) (arg.getValue()).get("body"))).get("documents")).get(1).get("_id").toString(), "some-id2");
  }

  @Test
  public void mCreateOrReplaceDocumentTestB() throws NotConnectedException, InternalException {

    Kuzzle kuzzleMock = spy(new Kuzzle(networkProtocol));
    String index = "nyc-open-data";
    String collection = "yellow-taxi";

    ConcurrentHashMap<String, Object> document1 = new ConcurrentHashMap<>();
    ConcurrentHashMap<String, Object> document2 = new ConcurrentHashMap<>();
    ConcurrentHashMap<String, Object> body = new ConcurrentHashMap<>();

    document1.put("_id", "some-id1");
    body.put("key1", "value1");
    document1.put("body", body);

    document2.put("_id", "some-id2");
    body.put("key2", "value2");
    document2.put("body", body);

    final ArrayList<ConcurrentHashMap<String, Object>> documents = new ArrayList<>();
    documents.add(document1);
    documents.add(document2);

    ArgumentCaptor<KuzzleMap> arg = ArgumentCaptor.forClass(KuzzleMap.class);

    kuzzleMock.getDocumentController().mCreateOrReplace(index, collection, documents, true);
    Mockito.verify(kuzzleMock, Mockito.times(1)).query(arg.capture());

    assertEquals((arg.getValue()).getString("controller"), "document");
    assertEquals((arg.getValue()).getString("action"), "mCreateOrReplace");
    assertEquals((arg.getValue()).getString("index"), "nyc-open-data");
    assertEquals((arg.getValue()).getString("refresh"), "wait_for");
    assertEquals(((ArrayList<ConcurrentHashMap<String, Object>>) (((KuzzleMap) (arg.getValue()).get("body"))).get("documents")).get(0).get("_id").toString(), "some-id1");
    assertEquals(((ArrayList<ConcurrentHashMap<String, Object>>) (((KuzzleMap) (arg.getValue()).get("body"))).get("documents")).get(1).get("_id").toString(), "some-id2");
  }

  @Test(expected = NotConnectedException.class)
  public void mCreateOrReplaceDocumentShouldThrowWhenNotConnected() throws NotConnectedException, InternalException {
    AbstractProtocol fakeNetworkProtocol = Mockito.mock(WebSocket.class);
    Mockito.when(fakeNetworkProtocol.getState()).thenAnswer((Answer<ProtocolState>) invocation -> ProtocolState.CLOSE);

    Kuzzle kuzzleMock = spy(new Kuzzle(fakeNetworkProtocol));
    String index = "nyc-open-data";
    String collection = "yellow-taxi";

    ConcurrentHashMap<String, Object> document1 = new ConcurrentHashMap<>();
    ConcurrentHashMap<String, Object> document2 = new ConcurrentHashMap<>();
    ConcurrentHashMap<String, Object> body = new ConcurrentHashMap<>();

    document1.put("_id", "some-id1");
    body.put("key1", "value1");
    document1.put("body", body);

    document2.put("_id", "some-id2");
    body.put("key2", "value2");
    document2.put("body", body);

    final ArrayList<ConcurrentHashMap<String, Object>> documents = new ArrayList<>();
    documents.add(document1);
    documents.add(document2);

    kuzzleMock.getDocumentController().mCreateOrReplace(index, collection, documents);
  }

  @Test
  public void deleteByQueryDocumentTestA() throws NotConnectedException, InternalException {

    Kuzzle kuzzleMock = spy(new Kuzzle(networkProtocol));
    String index = "nyc-open-data";
    String collection = "yellow-taxi";

    ConcurrentHashMap<String, Object> searchQuery = new ConcurrentHashMap<>();
    ConcurrentHashMap<String, Object> query = new ConcurrentHashMap<>();
    ConcurrentHashMap<String, Object> match = new ConcurrentHashMap<>();
    match.put("Hello", "Clarisse");
    query.put("match", match);
    searchQuery.put("query", query);

    ArgumentCaptor<KuzzleMap> arg = ArgumentCaptor.forClass(KuzzleMap.class);

    kuzzleMock.getDocumentController().deleteByQuery(index, collection, searchQuery);
    Mockito.verify(kuzzleMock, Mockito.times(1)).query(arg.capture());

    assertEquals((arg.getValue()).getString("controller"), "document");
    assertEquals((arg.getValue()).getString("action"), "deleteByQuery");
    assertEquals((arg.getValue()).getString("index"), "nyc-open-data");
    assertEquals((arg.getValue()).getBoolean("waitForRefresh"), null);
    assertEquals(((ConcurrentHashMap<String, Object>) ((ConcurrentHashMap<String, Object>) (((KuzzleMap) (arg.getValue()).get("body"))).get("query")).get("match")).get("Hello"), "Clarisse");
  }

  @Test
  public void deleteByQueryDocumentTestB() throws NotConnectedException, InternalException {

    Kuzzle kuzzleMock = spy(new Kuzzle(networkProtocol));
    String index = "nyc-open-data";
    String collection = "yellow-taxi";

    ConcurrentHashMap<String, Object> searchQuery = new ConcurrentHashMap<>();
    ConcurrentHashMap<String, Object> query = new ConcurrentHashMap<>();
    ConcurrentHashMap<String, Object> match = new ConcurrentHashMap<>();
    match.put("Hello", "Clarisse");
    query.put("match", match);
    searchQuery.put("query", query);

    ArgumentCaptor<KuzzleMap> arg = ArgumentCaptor.forClass(KuzzleMap.class);

    kuzzleMock.getDocumentController().deleteByQuery(index, collection, searchQuery, true);
    Mockito.verify(kuzzleMock, Mockito.times(1)).query(arg.capture());

    assertEquals((arg.getValue()).getString("controller"), "document");
    assertEquals((arg.getValue()).getString("action"), "deleteByQuery");
    assertEquals((arg.getValue()).getString("index"), "nyc-open-data");
    assertEquals((arg.getValue()).getString("refresh"), "wait_for");
    assertEquals(((ConcurrentHashMap<String, Object>) ((ConcurrentHashMap<String, Object>) (((KuzzleMap) (arg.getValue()).get("body"))).get("query")).get("match")).get("Hello"), "Clarisse");
  }

  @Test(expected = NotConnectedException.class)
  public void deleteByQueryDocumentShouldThrowWhenNotConnected() throws NotConnectedException, InternalException {
    AbstractProtocol fakeNetworkProtocol = Mockito.mock(WebSocket.class);
    Mockito.when(fakeNetworkProtocol.getState()).thenAnswer((Answer<ProtocolState>) invocation -> ProtocolState.CLOSE);

    Kuzzle kuzzleMock = spy(new Kuzzle(fakeNetworkProtocol));
    String index = "nyc-open-data";
    String collection = "yellow-taxi";

    ConcurrentHashMap<String, Object> searchQuery = new ConcurrentHashMap<>();
    ConcurrentHashMap<String, Object> match = new ConcurrentHashMap<>();
    match.put("Hello", "Clarisse");
    searchQuery.put("match", match);

    kuzzleMock.getDocumentController().deleteByQuery(index, collection, searchQuery);
  }

  @Test
  public void validateDocumentTest() throws NotConnectedException, InternalException {

    Kuzzle kuzzleMock = spy(new Kuzzle(networkProtocol));
    String index = "nyc-open-data";
    String collection = "yellow-taxi";

    ConcurrentHashMap<String, Object> document = new ConcurrentHashMap<>();

    document.put("Tirion", "Fordring");

    ArgumentCaptor<KuzzleMap> arg = ArgumentCaptor.forClass(KuzzleMap.class);

    kuzzleMock.getDocumentController().validate(index, collection, document);
    Mockito.verify(kuzzleMock, Mockito.times(1)).query(arg.capture());

    assertEquals((arg.getValue()).getString("controller"), "document");
    assertEquals((arg.getValue()).getString("action"), "validate");
    assertEquals((arg.getValue()).getString("index"), "nyc-open-data");
    assertEquals(((ConcurrentHashMap<String, Object>) (((KuzzleMap) arg.getValue()).get("body"))).get("Tirion").toString(), "Fordring");

  }

  @Test(expected = NotConnectedException.class)
  public void validateDocumentShouldThrowWhenNotConnected() throws NotConnectedException, InternalException {
    AbstractProtocol fakeNetworkProtocol = Mockito.mock(WebSocket.class);
    Mockito.when(fakeNetworkProtocol.getState()).thenAnswer((Answer<ProtocolState>) invocation -> ProtocolState.CLOSE);

    Kuzzle kuzzleMock = spy(new Kuzzle(fakeNetworkProtocol));
    String index = "nyc-open-data";
    String collection = "yellow-taxi";

    ConcurrentHashMap<String, Object> document = new ConcurrentHashMap<>();

    document.put("Tirion", "Fordring");

    kuzzleMock.getDocumentController().validate(index, collection, document);
  }

  @Test
  public void countDocumentTestA() throws NotConnectedException, InternalException {

    Kuzzle kuzzleMock = spy(new Kuzzle(networkProtocol));
    String index = "nyc-open-data";
    String collection = "yellow-taxi";

    ArgumentCaptor<KuzzleMap> arg = ArgumentCaptor.forClass(KuzzleMap.class);

    kuzzleMock.getDocumentController().count(index, collection);
    Mockito.verify(kuzzleMock, Mockito.times(1)).query(arg.capture());

    assertEquals((arg.getValue()).getString("controller"), "document");
    assertEquals((arg.getValue()).getString("action"), "count");
    assertEquals((arg.getValue()).getString("index"), "nyc-open-data");
    assertEquals((arg.getValue()).getString("collection"), "yellow-taxi");
  }

  @Test
  public void countDocumentTestB() throws NotConnectedException, InternalException {

    Kuzzle kuzzleMock = spy(new Kuzzle(networkProtocol));
    String index = "nyc-open-data";
    String collection = "yellow-taxi";

    ArgumentCaptor<KuzzleMap> arg = ArgumentCaptor.forClass(KuzzleMap.class);

    ConcurrentHashMap<String, Object> searchQuery = new ConcurrentHashMap<>();
    ConcurrentHashMap<String, Object> match = new ConcurrentHashMap<>();
    match.put("Hello", "Clarisse");
    searchQuery.put("match", match);

    kuzzleMock.getDocumentController().count(index, collection, searchQuery);
    Mockito.verify(kuzzleMock, Mockito.times(1)).query(arg.capture());

    assertEquals((arg.getValue()).getString("controller"), "document");
    assertEquals((arg.getValue()).getString("action"), "count");
    assertEquals((arg.getValue()).getString("index"), "nyc-open-data");
    assertEquals((arg.getValue()).getString("collection"), "yellow-taxi");
    assertEquals(((ConcurrentHashMap<String, Object>) ((ConcurrentHashMap<String, Object>) (((KuzzleMap) (arg.getValue()).get("body"))).get("query")).get("match")).get("Hello"), "Clarisse");

  }

  @Test(expected = NotConnectedException.class)
  public void countDocumentShouldThrowWhenNotConnected() throws NotConnectedException, InternalException {
    AbstractProtocol fakeNetworkProtocol = Mockito.mock(WebSocket.class);
    Mockito.when(fakeNetworkProtocol.getState()).thenAnswer((Answer<ProtocolState>) invocation -> ProtocolState.CLOSE);

    Kuzzle kuzzleMock = spy(new Kuzzle(fakeNetworkProtocol));
    String index = "nyc-open-data";
    String collection = "yellow-taxi";

    kuzzleMock.getDocumentController().count(index, collection);
  }

  @Test
  public void updateByQueryDocumentTestA() throws NotConnectedException, InternalException {

    Kuzzle kuzzleMock = spy(new Kuzzle(networkProtocol));
    String index = "nyc-open-data";
    String collection = "yellow-taxi";

    ConcurrentHashMap<String, Object> searchQuery = new ConcurrentHashMap<>();
    ConcurrentHashMap<String, Object> match = new ConcurrentHashMap<>();
    match.put("Hello", "Clarisse");
    searchQuery.put("match", match);

    ArgumentCaptor<KuzzleMap> arg = ArgumentCaptor.forClass(KuzzleMap.class);

    ConcurrentHashMap<String, Object> changes = new ConcurrentHashMap<>();
    changes.put("Hello", "Mister Anderson");

    kuzzleMock.getDocumentController().updateByQuery(index, collection, searchQuery, changes);
    Mockito.verify(kuzzleMock, Mockito.times(1)).query(arg.capture());

    assertEquals((arg.getValue()).getString("controller"), "document");
    assertEquals((arg.getValue()).getString("action"), "updateByQuery");
    assertEquals((arg.getValue()).getString("index"), "nyc-open-data");
    assertEquals((arg.getValue()).getBoolean("waitForRefresh"), null);
    assertEquals(((ConcurrentHashMap<String, Object>) ((ConcurrentHashMap<String, Object>) (((KuzzleMap) (arg.getValue()).get("body"))).get("query")).get("match")).get("Hello"), "Clarisse");
    assertEquals((((ConcurrentHashMap<String, Object>) (((KuzzleMap) (arg.getValue()).get("body"))).get("changes")).get("Hello")), "Mister Anderson");

  }

  @Test
  public void updateByQueryDocumentTestB() throws NotConnectedException, InternalException {

    Kuzzle kuzzleMock = spy(new Kuzzle(networkProtocol));
    String index = "nyc-open-data";
    String collection = "yellow-taxi";

    ConcurrentHashMap<String, Object> searchQuery = new ConcurrentHashMap<>();
    ConcurrentHashMap<String, Object> match = new ConcurrentHashMap<>();
    match.put("Hello", "Clarisse");
    searchQuery.put("match", match);

    ArgumentCaptor<KuzzleMap> arg = ArgumentCaptor.forClass(KuzzleMap.class);

    UpdateOptions options = new UpdateOptions();
    options.setWaitForRefresh(true);
    options.setSource(true);
    options.setRetryOnConflict(1);
    ConcurrentHashMap<String, Object> changes = new ConcurrentHashMap<>();
    changes.put("Hello", "Mister Anderson");

    kuzzleMock.getDocumentController().updateByQuery(index, collection, searchQuery, changes, options);
    Mockito.verify(kuzzleMock, Mockito.times(1)).query(arg.capture());

    assertEquals((arg.getValue()).getString("controller"), "document");
    assertEquals((arg.getValue()).getString("action"), "updateByQuery");
    assertEquals((arg.getValue()).getString("index"), "nyc-open-data");
    assertEquals((arg.getValue()).getNumber("retryOnConflict"), 1);
    assertEquals((arg.getValue()).getString("refresh"), "wait_for");
    assertEquals((arg.getValue()).getBoolean("source"), true);
    assertEquals(((ConcurrentHashMap<String, Object>) ((ConcurrentHashMap<String, Object>) (((KuzzleMap) (arg.getValue()).get("body"))).get("query")).get("match")).get("Hello"), "Clarisse");
    assertEquals((((ConcurrentHashMap<String, Object>) (((KuzzleMap) (arg.getValue()).get("body"))).get("changes")).get("Hello")), "Mister Anderson");
  }

  @Test(expected = NotConnectedException.class)
  public void updateByQueryDocumentShouldThrowWhenNotConnected() throws NotConnectedException, InternalException {
    AbstractProtocol fakeNetworkProtocol = Mockito.mock(WebSocket.class);
    Mockito.when(fakeNetworkProtocol.getState()).thenAnswer((Answer<ProtocolState>) invocation -> ProtocolState.CLOSE);

    Kuzzle kuzzleMock = spy(new Kuzzle(fakeNetworkProtocol));
    String index = "nyc-open-data";
    String collection = "yellow-taxi";

    ConcurrentHashMap<String, Object> searchQuery = new ConcurrentHashMap<>();
    ConcurrentHashMap<String, Object> match = new ConcurrentHashMap<>();
    match.put("Hello", "Clarisse");
    searchQuery.put("match", match);

    ConcurrentHashMap<String, Object> changes = new ConcurrentHashMap<>();
    changes.put("Hello", "Mister Anderson");

    kuzzleMock.getDocumentController().updateByQuery(index, collection, searchQuery, changes);
  }

  @Test
  public void searchDocumentTestA() throws NotConnectedException, InternalException, ExecutionException, InterruptedException {

    Kuzzle kuzzleMock = spy(new Kuzzle(networkProtocol));
    String index = "nyc-open-data";
    String collection = "yellow-taxi";

    ConcurrentHashMap<String, Object> searchQuery = new ConcurrentHashMap<>();
    ConcurrentHashMap<String, Object> match = new ConcurrentHashMap<>();
    ConcurrentHashMap<String, Object> query = new ConcurrentHashMap<>();
    match.put("Hello", "Clarisse");
    query.put("match", match);
    searchQuery.put("query", query);

    ArgumentCaptor<KuzzleMap> arg = ArgumentCaptor.forClass(KuzzleMap.class);
    CompletableFuture<Response> queryResponse = new CompletableFuture<>();

    Response r = new Response();
    r.result = new ConcurrentHashMap<String, Object>();
    ((ConcurrentHashMap<String, Object>) r.result).put("aggregations", new ConcurrentHashMap<String, Object>());
    ((ConcurrentHashMap<String, Object>) r.result).put("total", new LazilyParsedNumber("1"));
    ((ConcurrentHashMap<String, Object>) r.result).put("scrollId", "");
    ((ConcurrentHashMap<String, Object>) r.result).put("hits", new ArrayList<ConcurrentHashMap<String, Object>>());
    doReturn(queryResponse).when(kuzzleMock).query(any(ConcurrentHashMap.class));

    new Thread(() -> {
      try {
        kuzzleMock.getDocumentController().search(index, collection, searchQuery);
      } catch (Exception e) {
        e.printStackTrace();
      }
    }).start();
    queryResponse.complete(r);
    Thread.sleep(1000);
    Mockito.verify(kuzzleMock, Mockito.times(1)).query(arg.capture());

    assertEquals((arg.getValue()).getString("controller"), "document");
    assertEquals((arg.getValue()).getString("action"), "search");
    assertEquals((arg.getValue()).getString("index"), "nyc-open-data");
    assertEquals(((ConcurrentHashMap<String, Object>) ((ConcurrentHashMap<String, Object>) (((KuzzleMap) (arg.getValue()).get("body"))).get("query")).get("match")).get("Hello"), "Clarisse");

  }

  @Test
  public void searchDocumentTestB() throws NotConnectedException, InternalException, ExecutionException, InterruptedException {

    Kuzzle kuzzleMock = spy(new Kuzzle(networkProtocol));
    String index = "nyc-open-data";
    String collection = "yellow-taxi";
    SearchOptions options = new SearchOptions();
    options.setSize(10);
    options.setFrom(0);
    options.setScroll("30s");

    ConcurrentHashMap<String, Object> searchQuery = new ConcurrentHashMap<>();
    ConcurrentHashMap<String, Object> match = new ConcurrentHashMap<>();
    ConcurrentHashMap<String, Object> query = new ConcurrentHashMap<>();
    match.put("Hello", "Clarisse");
    query.put("match", match);
    searchQuery.put("query", query);

    ArgumentCaptor<KuzzleMap> arg = ArgumentCaptor.forClass(KuzzleMap.class);
    CompletableFuture<Response> queryResponse = new CompletableFuture<>();

    Response r = new Response();
    r.result = new ConcurrentHashMap<String, Object>();
    ((ConcurrentHashMap<String, Object>) r.result).put("aggregations", new ConcurrentHashMap<String, Object>());
    ((ConcurrentHashMap<String, Object>) r.result).put("total", new LazilyParsedNumber("1"));
    ((ConcurrentHashMap<String, Object>) r.result).put("scrollId", "");
    ((ConcurrentHashMap<String, Object>) r.result).put("hits", new ArrayList<ConcurrentHashMap<String, Object>>());
    doReturn(queryResponse).when(kuzzleMock).query(any(ConcurrentHashMap.class));

    new Thread(() -> {
      try {
        kuzzleMock.getDocumentController().search(index, collection, searchQuery, options);
      } catch (Exception e) {
        e.printStackTrace();
      }
    }).start();
    queryResponse.complete(r);
    Thread.sleep(1000);
    Mockito.verify(kuzzleMock, Mockito.times(1)).query(arg.capture());

    assertEquals((arg.getValue()).getString("controller"), "document");
    assertEquals((arg.getValue()).getString("action"), "search");
    assertEquals((arg.getValue()).getString("index"), "nyc-open-data");
    assertEquals((arg.getValue()).getNumber("from"), 0);
    assertEquals((arg.getValue()).getNumber("size"), 10);
    assertEquals((arg.getValue()).getString("scroll"), "30s");

    assertEquals(((ConcurrentHashMap<String, Object>) ((ConcurrentHashMap<String, Object>) (((KuzzleMap) (arg.getValue()).get("body"))).get("query")).get("match")).get("Hello"), "Clarisse");

  }

  @Test(expected = NotConnectedException.class)
  public void searchDocumentShouldThrowWhenNotConnected() throws NotConnectedException, InternalException, ExecutionException, InterruptedException {
    AbstractProtocol fakeNetworkProtocol = Mockito.mock(WebSocket.class);
    Mockito.when(fakeNetworkProtocol.getState()).thenAnswer((Answer<ProtocolState>) invocation -> ProtocolState.CLOSE);

    Kuzzle kuzzleMock = spy(new Kuzzle(fakeNetworkProtocol));
    String index = "nyc-open-data";
    String collection = "yellow-taxi";

    ConcurrentHashMap<String, Object> searchQuery = new ConcurrentHashMap<>();
    ConcurrentHashMap<String, Object> match = new ConcurrentHashMap<>();
    match.put("Hello", "Clarisse");
    searchQuery.put("match", match);

    kuzzleMock.getDocumentController().search(index, collection, searchQuery);
  }
}
