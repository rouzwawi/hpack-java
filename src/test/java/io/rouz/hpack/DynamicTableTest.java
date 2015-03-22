package io.rouz.hpack;

import org.junit.Test;

import io.rouz.hpack.table.DynamicTable;
import io.rouz.hpack.field.HeaderField;
import io.rouz.hpack.table.Tables;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class DynamicTableTest {

  DynamicTable table = Tables.dynamic(64);

  @Test
  public void shouldInsertAtHead() throws Exception {
    table.put("foo", "foo");
    table.put("bar", "bar");

    HeaderField entry1 = table.get(1);
    HeaderField entry2 = table.get(2);

    assertThat(entry1.name(), is("bar"));
    assertThat(entry2.name(), is("foo"));
  }

  @Test(expected = IndexOutOfBoundsException.class)
  public void shouldThrowIndexOutOfBoundsException() throws Exception {
    table.put("foo", "foo");
    table.get(2);
  }
}
