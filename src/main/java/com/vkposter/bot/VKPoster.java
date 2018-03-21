package com.vkposter.bot;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;

import javax.imageio.ImageIO;

import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Update;
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

public class VKPoster extends TelegramLongPollingBot
{
  private static final String BOT_USER_NAME = "VKArtBot";
  private static final String BOT_TOKEN = "539855889:AAHT0xbKc9_Ozivf0IyxDcOMssJEkEazEVY";
  private static final String MASTER_CHAT_ID = "403968874";
  private static final String NOT_THE_MASTER_ERROR_TEXT = "I obey only the master!";

  private static final String VK_APP_ID = "6415642";
  private static final String VK_CLIENT_SECRET = "XTv6X6X1LpGOyQnkAb0U";
  private static final String VK_REDIRECT_URL = "https://vk.com/public163516182";
  private static final String VK_CODE = "2f30371aecf5af8798";
  private static final Integer VK_GROUP_ID = Integer.parseInt( "163516182" );


  private VkApiClient vkApiClient;
  private UserActor actor;

  public VKPoster()
  {
    super();

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

  public void onUpdateReceived( Update update ) {
    if (update.hasMessage() && update.getMessage().hasText()) {
      System.out.println("Chat id = [" + update.getMessage().getChatId() + "]");



      File file = new File("/home/drapeza/pictures/1.jpg");

      if (MASTER_CHAT_ID.equals(update.getMessage().getChatId().toString())) {
        BufferedImage image = null;
        try {
          URL url = new URL(update.getMessage().getText());
          image = ImageIO.read(url);
          ImageIO.write(image, "jpg", file);
        } catch (IOException e) {
        }

        try {
          PhotoUpload serverResponse = vkApiClient.photos()
                  .getWallUploadServer(actor)
                  .groupId(VK_GROUP_ID)
                  .execute();

          WallUploadResponse uploadResponse = vkApiClient.upload()
                  .photoWall(serverResponse.getUploadUrl(), file)
                  .execute();
          List<Photo> photoList = vkApiClient.photos()
                  .saveWallPhoto(actor, uploadResponse.getPhoto())
                  .groupId(VK_GROUP_ID)
                  .server(uploadResponse.getServer())
                  .hash(uploadResponse.getHash())
                  .execute();

          Photo photo = photoList.get(0);
          String attachId = "photo" + photo.getOwnerId() + "_" + photo.getId();
          PostResponse postResponse = vkApiClient.wall().post(actor).ownerId(-VK_GROUP_ID).attachments(attachId).execute();
        } catch (ApiException e) {
          e.printStackTrace();
        } catch (ClientException e) {
          e.printStackTrace();
        }


      } else {
        SendMessage message = new SendMessage().setChatId(update.getMessage().getChatId()).setText(NOT_THE_MASTER_ERROR_TEXT);
        try {
          execute(message);
        } catch (TelegramApiException e) {
          e.printStackTrace();
        }
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
}
