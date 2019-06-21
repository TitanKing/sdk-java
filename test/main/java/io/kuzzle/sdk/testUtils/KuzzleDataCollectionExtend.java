package main.java.io.kuzzle.sdk.testUtils;

import org.json.JSONObject;

import io.kuzzle.sdk.core.Kuzzle;
import io.kuzzle.sdk.core.Collection;
import io.kuzzle.sdk.core.Options;
import io.kuzzle.sdk.listeners.ResponseListener;

public class KuzzleDataCollectionExtend extends Collection {
  public KuzzleDataCollectionExtend(final Kuzzle kuzzle, final String index, final String collection) {
    super(kuzzle, collection, index);
  }

  public Collection deleteDocument(final String documentId, final JSONObject filter, final Options options, final ResponseListener<String> listener, final ResponseListener<String[]> listener2) {
    return super.deleteDocument(documentId, filter, options, listener, listener2);
  }
}
