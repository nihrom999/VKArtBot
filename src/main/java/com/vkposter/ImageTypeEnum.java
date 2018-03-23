package com.vkposter;

public enum ImageTypeEnum
{
  IMAGE_TYPE_JPG( "jpg" ),
  IMAGE_TYPE_PNG( "png" ),
  IMAGE_TYPE_UNKNOWN( "unknown" );

  private String type;

  ImageTypeEnum( String type )
  {
    this.type = type;
  }

  public String getType()
  {
    return type;
  }
}
