package com.gmail.mbotyuk.parserhtml.configuration;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

@Slf4j
@Configuration
public class EchoBotConfiguration extends TelegramLongPollingBot {

    private static final String CHAT_ID = "-787060670";

    @Value("${bot.bot_username}")
    private String botUsername;
    @Value("${bot.bot_token}")
    private String botToken;

    @Override
    public String getBotUsername() {
        return botUsername;
    }

    @Override
    public String getBotToken() {
        return botToken;
    }

    @SneakyThrows
    @Override
    public void onUpdateReceived(Update update) {

    }

    @SneakyThrows
    public void sendExchangeRateToGroup(String exchangeRate) {
        execute(new SendMessage(CHAT_ID, exchangeRate));
    }
}
