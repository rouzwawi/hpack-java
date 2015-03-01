package io.rouz;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.junit.Test;

import static io.rouz.HeaderFields.HeaderField;
import static io.rouz.HeaderFields.HeaderFieldType.INDEXED;
import static io.rouz.HeaderFields.HeaderFieldType.LITERAL_INDEX;
import static io.rouz.HeaderFields.HeaderFieldType.LITERAL_NEVER_INDEX;
import static io.rouz.HeaderFields.HeaderFieldType.LITERAL_NO_INDEX;
import static io.rouz.HeaderFields.HeaderFieldVisitor;
import static io.rouz.HeaderFields.HeaderName;
import static io.rouz.HeaderFields.IndexedName;
import static io.rouz.HeaderFields.LiteralName;
import static io.rouz.HeaderFields.LiteralValue;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

public class HeaderFieldTest {

  @Test
  public void testName() throws Exception {
    final HeaderName name = HeaderFields.name("foo");
    final HeaderName indx = HeaderFields.name(1337);

    /*
     * l[li][inx]
     *
     * l = literal
     *
     * l = literal name
     * i = indexed name
     *
     * i = indexed
     * n = not indexed
     * x = never indexed
     */
    final HeaderField indexed = HeaderFields.createIndexed(1337);
    final HeaderField lli = HeaderFields.createIndexed(name, "bar");
    final HeaderField lii = HeaderFields.createIndexed(indx, "bar");
    final HeaderField lln = HeaderFields.createNonIndexed(name, "bar");
    final HeaderField lin = HeaderFields.createNonIndexed(indx, "bar");
    final HeaderField llx = HeaderFields.createNonIndexed(name, "bar", true);
    final HeaderField lix = HeaderFields.createNonIndexed(indx, "bar", true);

    assertThat(indexed.type(), is(INDEXED));
    assertThat(lli.type(), is(LITERAL_INDEX));
    assertThat(lii.type(), is(LITERAL_INDEX));
    assertThat(lln.type(), is(LITERAL_NO_INDEX));
    assertThat(lin.type(), is(LITERAL_NO_INDEX));
    assertThat(llx.type(), is(LITERAL_NEVER_INDEX));
    assertThat(lix.type(), is(LITERAL_NEVER_INDEX));

    assertThat(indexed.type().indexPrefix(), is(7));
    assertThat(lli.type().indexPrefix(), is(6));
    assertThat(lii.type().indexPrefix(), is(6));
    assertThat(lln.type().indexPrefix(), is(4));
    assertThat(lin.type().indexPrefix(), is(4));
    assertThat(llx.type().indexPrefix(), is(4));
    assertThat(lix.type().indexPrefix(), is(4));

    assertPattern(indexed, index(1337), is(true),  nullValue(), nullValue());
    assertPattern(lli,     nullValue(), is(false), name("foo"), value("bar"));
    assertPattern(lii,     index(1337), is(false), nullValue(), value("bar"));
    assertPattern(lln,     nullValue(), is(false), name("foo"), value("bar"));
    assertPattern(lin,     index(1337), is(false), nullValue(), value("bar"));
    assertPattern(llx,     nullValue(), is(false), name("foo"), value("bar"));
    assertPattern(lix,     index(1337), is(false), nullValue(), value("bar"));
  }

  private void assertPattern(
      HeaderField field,
      Matcher indexedNameMatcher,
      Matcher indexedFieldMatcher,
      Matcher literalNameMatcher,
      Matcher literalValueMatcher) {

    TestVisitor visitor = new TestVisitor();
    field.visit(visitor);
    assertThat(visitor.indexedName, indexedNameMatcher);
    assertThat(visitor.indexedField, indexedFieldMatcher);
    assertThat(visitor.literalName, literalNameMatcher);
    assertThat(visitor.literalValue, literalValueMatcher);
  }

  private Matcher<? extends IndexedName> index(final int index) {
    return allOf(notNullValue(), new BaseMatcher<IndexedName>() {
      @Override
      public boolean matches(Object item) {
        IndexedName indexedName = (IndexedName) item;
        return indexedName.index() == index;
      }

      @Override
      public void describeTo(Description description) {
        description.appendText("index == " + index);
      }
    });
  }

  private Matcher<? extends LiteralName> name(final String name) {
    return allOf(notNullValue(), new BaseMatcher<LiteralName>() {
      @Override
      public boolean matches(Object item) {
        LiteralName literalName = (LiteralName) item;
        return name.equals(literalName.name());
      }

      @Override
      public void describeTo(Description description) {
        description.appendText("name == " + name);
      }
    });
  }

  private Matcher<? extends LiteralValue> value(final String value) {
    return allOf(notNullValue(), new BaseMatcher<LiteralValue>() {
      @Override
      public boolean matches(Object item) {
        LiteralValue literalValue = (LiteralValue) item;
        return value.equals(literalValue.value());
      }

      @Override
      public void describeTo(Description description) {
        description.appendText("value == " + value);
      }
    });
  }

  private static class TestVisitor implements HeaderFieldVisitor {

    private boolean indexedField;
    private IndexedName indexedName;
    private LiteralName literalName;
    private LiteralValue literalValue;

    @Override
    public void indexedName(IndexedName indexedName, boolean indexedField) {
      this.indexedName = indexedName;
      this.indexedField = indexedField;
    }

    @Override
    public void literalName(LiteralName literalName) {
      this.literalName = literalName;
    }

    @Override
    public void literalValue(LiteralValue literalValue) {
      this.literalValue = literalValue;
    }
  }
}
