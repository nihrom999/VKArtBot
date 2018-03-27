package com.vkposter;

import com.vkposter.util.UrlExtractor;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Unit test for simple App.
 */
public class AppTest 
    extends TestCase
{
    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public AppTest( String testName )
    {
        super( testName );
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite()
    {
        return new TestSuite( AppTest.class );
    }

    /**
     * Rigourous Test :-)
     */
    public void testApp() throws IOException {
        assertTrue( true );

        HttpClient client = new DefaultHttpClient();
        HttpGet request = new HttpGet("https://straban.deviantart.com/art/ECHO-mutant-bounty-hunter-736535478");
        HttpResponse response = client.execute(request);
        BufferedReader rd = new BufferedReader (new InputStreamReader(response.getEntity().getContent()));
        String line = "";
        StringBuilder stringBuilder = new StringBuilder();
        while ((line = rd.readLine()) != null) {
            stringBuilder.append(line);
        }

        System.out.println(UrlExtractor.getUrlFromDeviantArtPage(stringBuilder.toString()));
    }
}
