package com.vkposter;

import static com.vkposter.ImageTypeEnum.IMAGE_TYPE_UNKNOWN;

import org.apache.commons.lang3.StringUtils;

public class ImageInfo
{
  private String url;
  private ImageTypeEnum type;

  public ImageInfo()
  {
  }

  public ImageInfo( String url, ImageTypeEnum type )
  {
    this.url = url;
    this.type = type;
  }

  public static ImageTypeEnum getImageTypeByUrl( String url )
  {
    if ( StringUtils.isEmpty( url ) ) {
      return IMAGE_TYPE_UNKNOWN;
    }

    for( ImageTypeEnum type : ImageTypeEnum.values() ) {
      if ( type.getType().equals( StringUtils.right( url, 3 ) ) ) {
        return type;
      }
    }

    return IMAGE_TYPE_UNKNOWN;
  }

  public static ImageInfo getImageInfoFromUrl( String url )
  {
    return new ImageInfo( url, getImageTypeByUrl( url ) );
  }
}
