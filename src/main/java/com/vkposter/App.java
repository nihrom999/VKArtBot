package com.vkposter;

import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.TelegramBotsApi;

import com.vkposter.bot.VKPoster;
import org.telegram.telegrambots.exceptions.TelegramApiRequestException;


public class App
{
  public static void main( String[] args ) throws TelegramApiRequestException {
    ApiContextInitializer.init();

    TelegramBotsApi botsApi = new TelegramBotsApi();

      botsApi.registerBot( new VKPoster() );
  }
}