package com.vkposter.bot;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import javax.imageio.ImageIO;

import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import com.vk.api.sdk.client.VkApiClient;
import com.vk.api.sdk.client.actors.UserActor;
import com.vk.api.sdk.exceptions.ApiException;
import com.vk.api.sdk.exceptions.ClientException;
import com.vk.api.sdk.httpclient.HttpTransportClient;
import com.vk.api.sdk.objects.UserAuthResponse;
import com.vk.api.sdk.objects.photos.Photo;
import com.vk.api.sdk.objects.photos.PhotoUpload;
import com.vk.api.sdk.objects.photos.responses.WallUploadResponse;
import com.vk.api.sdk.objects.wall.responses.PostResponse;
import com.vkposter.HttpRequestClient;
import com.vkposter.ImageInfo;
import com.vkposter.ImageTypeEnum;
import com.vkposter.util.UrlExtractor;

public class VKPoster extends TelegramLongPollingBot
{
  private static final String BOT_USER_NAME = "VKArtBot";
  private static final String BOT_TOKEN = "539855889:AAHT0xbKc9_Ozivf0IyxDcOMssJEkEazEVY";
  private static final String MASTER_CHAT_ID = "403968874";

  private static final String NOT_THE_MASTER_ERROR_TEXT = "I obey only the master!";
  private static final String NOT_THE_MASTER_INFO_TEXT = "The user @%s is trying to rule the bot. \n Message: %s";
  private static final String MALFORMED_URL_ERROR_TEXT = "Malformed URL: %s\n%s";
  private static final String CREATING_FILE_ERROR_TEXT = "Error creating file: %s\n%s";
  private static final String UNSUPPORTED_IMAGE_FORMAT_ERROR_TEXT = "Unsupported file format: %s";
  private static final String SENDING_TO_VK_ERROR_TEXT = "Error sending image to VK:\n%s";

  private static final String UPLOAD_SUCCESSFUL_MESSAGE_TEXT = "Upload successful, post id: %s";

  private static final Integer VK_GROUP_ID = Integer.parseInt( "163516182" );

  private static final String VK_POST_URL = "https://vk.com/public%s?w=wall-%s_%s"; // https://vk.com/public163516182?w=wall-163516182_53
  private static final String VK_APP_ID = "6415642";
  private static final String VK_CLIENT_SECRET = "XTv6X6X1LpGOyQnkAb0U";
  private static final String VK_REDIRECT_URL = "https://vk.com/public163516182";
  private static final String VK_CODE = "ea313ed1c8344cce46";


  private VkApiClient vkApiClient;
  private UserActor actor;

  public VKPoster()
  {
    super();
    initVKApi();
  }

  public VKPoster( DefaultBotOptions options )
  {
    super( options );
    initVKApi();
  }

  public void onUpdateReceived( Update update )
  {
    if ( update.hasMessage() && update.getMessage().hasText() ) {
      System.out.println( "Chat id = [" + update.getMessage().getChatId() + "]" );

      String chatId = update.getMessage().getChatId().toString();
      String userName = update.getMessage().getFrom().getUserName();
      String messageText = update.getMessage().getText();

      if ( MASTER_CHAT_ID.equals( chatId ) ) {

        String stringUrl = UrlExtractor.getFirstUrlFromText( messageText );

        if ( !( stringUrl.contains( ImageTypeEnum.IMAGE_TYPE_JPG.getType() )
          || stringUrl.contains( ImageTypeEnum.IMAGE_TYPE_PNG.getType() ) ) ) {

          if ( stringUrl.contains( "deviantart.com" ) ) {
            stringUrl = UrlExtractor.getUrlFromDeviantArtPage( HttpRequestClient.httpGet( stringUrl ) );
          }
        }

        URL url = null;

        try {
          url = new URL( stringUrl );
        } catch( MalformedURLException e ) {
          sendTextMessageToUser( String.format( MALFORMED_URL_ERROR_TEXT, messageText, e.getMessage() ), chatId );
          e.printStackTrace();
          return;
        }

        ImageTypeEnum imageType = ImageInfo.getImageTypeByUrl( stringUrl );
        if ( ImageTypeEnum.IMAGE_TYPE_UNKNOWN.equals( imageType ) ) {
          sendTextMessageToUser( String.format( UNSUPPORTED_IMAGE_FORMAT_ERROR_TEXT, stringUrl ), chatId );
          return;
        }

        String fileName = "/home/drapeza/pictures/1." + imageType.getType();

        File file = new File( fileName );

        BufferedImage image = null;

        try {
          image = ImageIO.read( url );
          ImageIO.write( image, imageType.getType(), file );
        } catch( IOException e ) {
          sendTextMessageToUser( String.format( CREATING_FILE_ERROR_TEXT, fileName, e.getMessage() ), MASTER_CHAT_ID );
        }

        Integer postId = null;
        try {
          postId = uploadToVK( file );
          if ( postId != null ) {
            sendTextMessageToUser(
              String.format(
                UPLOAD_SUCCESSFUL_MESSAGE_TEXT,
                String.format( VK_POST_URL, VK_GROUP_ID.toString(), VK_GROUP_ID.toString(), postId ) ),
              chatId );
          }
        } catch( ClientException e ) {
          sendTextMessageToUser( String.format( SENDING_TO_VK_ERROR_TEXT, e.getMessage() ), MASTER_CHAT_ID );
          e.printStackTrace();
        } catch( ApiException e ) {
          sendTextMessageToUser( String.format( SENDING_TO_VK_ERROR_TEXT, e.getMessage() ), MASTER_CHAT_ID );
          e.printStackTrace();
        }

      } else {
        sendTextMessageToUser( NOT_THE_MASTER_ERROR_TEXT, chatId );
        sendTextMessageToUser( String.format( NOT_THE_MASTER_INFO_TEXT, userName, messageText ), MASTER_CHAT_ID );
      }
    }
  }

  public String getBotUsername()
  {
    return BOT_USER_NAME;
  }

  public String getBotToken()
  {
    return BOT_TOKEN;
  }

  private void initVKApi()
  {
    vkApiClient = new VkApiClient( new HttpTransportClient() );
    UserAuthResponse authResponse = null;
    try {
      authResponse = vkApiClient.oauth()
        .userAuthorizationCodeFlow( Integer.parseInt( VK_APP_ID ), VK_CLIENT_SECRET, VK_REDIRECT_URL, VK_CODE )
        .execute();

      actor = new UserActor( authResponse.getUserId(), authResponse.getAccessToken() );
    } catch( ApiException e ) {
      e.printStackTrace();
    } catch( ClientException e ) {
      e.printStackTrace();
    }
  }

  private Integer uploadToVK( File file ) throws ClientException, ApiException
  {

    PhotoUpload serverResponse = vkApiClient.photos().getWallUploadServer( actor ).groupId( VK_GROUP_ID ).execute();

    WallUploadResponse uploadResponse = vkApiClient.upload().photoWall( serverResponse.getUploadUrl(), file ).execute();
    List<Photo> photoList = vkApiClient.photos()
      .saveWallPhoto( actor, uploadResponse.getPhoto() )
      .groupId( VK_GROUP_ID )
      .server( uploadResponse.getServer() )
      .hash( uploadResponse.getHash() )
      .execute();

    Photo photo = photoList.get( 0 );
    String attachId = "photo" + photo.getOwnerId() + "_" + photo.getId();
    PostResponse postResponse = vkApiClient.wall()
      .post( actor )
      .ownerId( -VK_GROUP_ID )
      .attachments( attachId )
      .execute();

    return postResponse.getPostId();
  }

  void sendTextMessageToUser( String text, String chatId )
  {
    SendMessage message = new SendMessage().setChatId( chatId ).setText( text );
    try {
      execute( message );
    } catch( TelegramApiException e ) {
      e.printStackTrace();
    }
  }
}
