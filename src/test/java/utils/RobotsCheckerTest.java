package utils;

import org.junit.jupiter.api.Test;

import java.net.MalformedURLException;
import java.net.URL;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RobotsCheckerTest {
  RobotsChecker checker = new RobotsChecker("TestAgent");

  @Test
  public void testValidUrl() throws MalformedURLException {
    assertTrue(checker.obeysRobotTxt(new URL("https://www.youtube.com/watch?v=dQw4w9WgXcQ")));
  }

  @Test
  public void testInvalidUrl() throws MalformedURLException {
    assertFalse(checker.obeysRobotTxt(new URL("https://www.youtube.com/comment")));
  }

  @Test
  public void testInvalidSubDirectoryUrl() throws MalformedURLException {
    assertFalse(checker.obeysRobotTxt(new URL("https://www.github.com/gist/arbitrary")));
  }

  @Test
  public void testInvalidEndPathUrl() throws MalformedURLException {
    assertFalse(checker.obeysRobotTxt(new URL("https://www.github.com/arbitrary/comments")));
  }
}