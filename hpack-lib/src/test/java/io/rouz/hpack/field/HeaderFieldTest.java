package io.rouz.hpack.field;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class HeaderFieldTest {

  @Test
  public void nameValueAndSize() throws Exception {
    HeaderField headerField = HeaderField.create("foo", "bar");

    assertThat(headerField.name(), is("foo"));
    assertThat(headerField.value(), is("bar"));
    assertThat(headerField.size(), is(32 + 6));
  }

  @Test
  public void nameValueAndSizeWithNoValue() throws Exception {
    HeaderField headerField = HeaderField.create("foo");

    assertThat(headerField.name(), is("foo"));
    assertThat(headerField.value(), is(""));
    assertThat(headerField.size(), is(32 + 3));
  }
}
