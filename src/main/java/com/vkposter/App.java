package com.vkposter;

import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.TelegramBotsApi;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import com.vkposter.bot.VKPoster;


public class App
{
  public static void main( String[] args )
  {
    ApiContextInitializer.init();

    TelegramBotsApi botsApi = new TelegramBotsApi();

    try {
      botsApi.registerBot( new VKPoster() );
    } catch( TelegramApiException e ) {
      e.printStackTrace();
    }
  }
}
