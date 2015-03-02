package io.rouz.hpack;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.junit.Test;

import java.util.Optional;

import io.rouz.hpack.field.HeaderField;
import io.rouz.hpack.field.HeaderFieldCollectors;
import io.rouz.hpack.field.HeaderFields;
import io.rouz.hpack.field.HeaderName;

import static io.rouz.hpack.field.HeaderField.HeaderFieldVisitor;
import static io.rouz.hpack.field.HeaderField.IndexedName;
import static io.rouz.hpack.field.HeaderField.LiteralName;
import static io.rouz.hpack.field.HeaderField.LiteralValue;
import static io.rouz.hpack.field.HeaderFieldType.INDEXED;
import static io.rouz.hpack.field.HeaderFieldType.LITERAL_INDEX;
import static io.rouz.hpack.field.HeaderFieldType.LITERAL_NEVER_INDEX;
import static io.rouz.hpack.field.HeaderFieldType.LITERAL_NO_INDEX;

import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

public class HeaderFieldTest {

  final HeaderName name = HeaderFields.name("foo");
  final HeaderName indx = HeaderFields.name(42);

  /*
   * [li][li][inx]
   *
   * l = literal field/name
   * i = indexed field/name
   *
   * i = indexed
   * n = not indexed
   * x = never indexed
   */
  final HeaderField iii = HeaderFields.indexedField(42);
  final HeaderField lli = HeaderFields.indexedField(name, "bar");
  final HeaderField lii = HeaderFields.indexedField(indx, "bar");
  final HeaderField lln = HeaderFields.nonIndexedField(name, "bar");
  final HeaderField lin = HeaderFields.nonIndexedField(indx, "bar");
  final HeaderField llx = HeaderFields.neverIndexedField(name, "bar");
  final HeaderField lix = HeaderFields.neverIndexedField(indx, "bar");

  @Test
  public void shouldBeExpectedTypes() throws Exception {
    assertThat(iii.type(), is(INDEXED));
    assertThat(lli.type(), is(LITERAL_INDEX));
    assertThat(lii.type(), is(LITERAL_INDEX));
    assertThat(lln.type(), is(LITERAL_NO_INDEX));
    assertThat(lin.type(), is(LITERAL_NO_INDEX));
    assertThat(llx.type(), is(LITERAL_NEVER_INDEX));
    assertThat(lix.type(), is(LITERAL_NEVER_INDEX));
  }

  @Test
  public void shouldContainExpectedNameAndValues() throws Exception {
    assertPattern(iii, is(true),  index(42),   nullValue(), nullValue());
    assertPattern(lli, is(false), nullValue(), name("foo"), value("bar"));
    assertPattern(lii, is(false), index(42),   nullValue(), value("bar"));
    assertPattern(lln, is(false), nullValue(), name("foo"), value("bar"));
    assertPattern(lin, is(false), index(42),   nullValue(), value("bar"));
    assertPattern(llx, is(false), nullValue(), name("foo"), value("bar"));
    assertPattern(lix, is(false), index(42),   nullValue(), value("bar"));
  }

  @Test
  public void should() throws Exception {
    final Optional<Integer> index = HeaderFieldCollectors.index(iii);
    assertThat(index.isPresent(), is(true));
    assertThat(index.get(), is(42));

    final Optional<Integer> noIndex = HeaderFieldCollectors.index(lli);
    assertThat(noIndex.isPresent(), is(false));

    final Optional<Integer> index2 = HeaderFieldCollectors.index(lii);
    assertThat(index2.isPresent(), is(true));
    assertThat(index2.get(), is(42));
  }

  private void assertPattern(
      HeaderField field,
      Matcher indexedFieldMatcher, Matcher indexedNameMatcher,
      Matcher literalNameMatcher, Matcher literalValueMatcher) {

    TestVisitor visitor = new TestVisitor();
    field.visit(visitor);
    assertThat(visitor.indexedField, indexedFieldMatcher);
    assertThat(visitor.indexedName, indexedNameMatcher);
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
