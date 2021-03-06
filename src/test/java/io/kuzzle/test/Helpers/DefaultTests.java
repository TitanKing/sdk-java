package io.kuzzle.test.Helpers;

import io.kuzzle.sdk.Helpers.Default;
import org.junit.Assert;
import org.junit.Test;

public class DefaultTests {

  @Test
  public void notNullTest() {
    String str1 = Default.defaultValue(null, "foobar");
    String str2 = Default.defaultValue("SomeString", "foobar");

    Integer int1 = Default.defaultValue(null, 42);
    Integer int2 = Default.defaultValue(10, 42);

    Assert.assertEquals("foobar", str1);
    Assert.assertEquals("SomeString", str2);

    Assert.assertEquals(42, int1.intValue());
    Assert.assertEquals(10, int2.intValue());
  }

}
