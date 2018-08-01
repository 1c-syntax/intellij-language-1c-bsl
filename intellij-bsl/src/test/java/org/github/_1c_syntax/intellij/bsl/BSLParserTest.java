package org.github._1c_syntax.intellij.bsl;

import com.intellij.testFramework.ParsingTestCase;
import org.github._1c_syntax.intellij.bsl.util.TestUtils;

public class BSLParserTest extends ParsingTestCase {

  public BSLParserTest() {
    super("parser", "bsl", new BSLParserDefinition());
  }

  public void testHello() {
    doTest(true);
  }

  @Override
  protected String getTestDataPath() {
    return TestUtils.BASE_TEST_DATA_PATH;
  }

}
