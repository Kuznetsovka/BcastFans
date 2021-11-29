package com.systemair.bcastfans;

import org.openqa.selenium.WebDriver;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.Duration;
public class HttpCodeResponse{
   public static int responseCode(WebDriver driver, String url) throws MalformedURLException, IOException {
      // wait of 5 seconds
      driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(5));
      // establish and open connection with URL
      HttpURLConnection c = (HttpURLConnection)new URL(url).openConnection();
      // set the HEAD request with setRequestMethod
      c.setRequestMethod("HEAD");
      // connection started and get response code
      c.connect();
      return c.getResponseCode();
   }
}