package com.vkposter.bot;

import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.exceptions.TelegramApiException;

public class VKPoster extends TelegramLongPollingBot
{

  public static final String botUserName = "VKArtBot";
  public static final String botToken = "539855889:AAHT0xbKc9_Ozivf0IyxDcOMssJEkEazEVY";

  public void onUpdateReceived( Update update )
  {
    // We check if the update has a message and the message has text
    if ( update.hasMessage() && update.getMessage().hasText() ) {
      SendMessage message = new SendMessage() // Create a SendMessage object with mandatory fields
        .setChatId( update.getMessage().getChatId() )
        .setText( update.getMessage().getText() );
      try {
        execute( message ); // Call method to send the message
      } catch( TelegramApiException e ) {
        e.printStackTrace();
      }
    }
  }

  public String getBotUsername()
  {
    return botUserName;
  }

  public String getBotToken()
  {
    return botToken;
  }
}
