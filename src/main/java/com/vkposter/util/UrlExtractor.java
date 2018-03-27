package com.vkposter.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UrlExtractor
{
  private static final String urlRegex = "(https:\\/\\/)(www.)?([a-zA-Z0-9]+).[a-zA-Z0-9]*.[a-z]{3}.?(([-A-Za-z0-9_.]+)?\\/?)+";
  private static final Pattern urlPattern = Pattern.compile( urlRegex, Pattern.MULTILINE );

  private static final String daUrlRegex = "(<meta property=\"og:image\" content=\")[^>]*(\">)";
  private static final Pattern daUrlPattern = Pattern.compile( daUrlRegex, Pattern.MULTILINE );

  public static String getFirstUrlFromText( String text )
  {
    Matcher matcher = urlPattern.matcher( text );

    if ( matcher.find() ) {
      return matcher.group( 0 );
    }

    return null;
  }

  public static String getUrlFromDeviantArtPage(String text )
  {
    Matcher matcher = daUrlPattern.matcher( text );

    if ( matcher.find() ) {
      return  getFirstUrlFromText(matcher.group( 0 ));
    }

    return null;
  }
}
