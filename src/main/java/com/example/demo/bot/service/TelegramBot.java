package com.example.demo.bot.service;

import com.example.demo.bot.config.BotConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScopeDefault;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class TelegramBot extends TelegramLongPollingBot {
    private final BotConfig botConfig;
    static final String HELP_TEXT=" This bot is created to demonstrate Spring capabilities\n\n"+
            " You can execute commands from the main menu on the left or by typing a commands:\n\n"+
            " Type /start to see e welcome message\n\n"+
            " Type /mydata to see data stored about tourself\n\n"+
            " Type /help to see this message again";

    public TelegramBot(BotConfig botConfig) {
        this.botConfig = botConfig;
        List<BotCommand> botCommands = new ArrayList<>();
        botCommands.add(new BotCommand("/start", "get a welcome message"));
        botCommands.add(new BotCommand("/mydata", "get your data stored"));
        botCommands.add(new BotCommand("/deletedata", "delete my data"));
        botCommands.add(new BotCommand("/help", "info how to use this boto"));
        botCommands.add(new BotCommand("/setting", "set your preferences"));
        try {
            this.execute(new SetMyCommands(botCommands, new BotCommandScopeDefault(), null));
        } catch (TelegramApiException e) {
            log.error("Error setting bots command list {}", e.getMessage());
        }
    }

    @Override
    public String getBotUsername() {
        return botConfig.getName();
    }

    @Override
    public String getBotToken() {
        return botConfig.getToken();
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            String message = update.getMessage().getText();
            Long chatId = update.getMessage().getChatId();
            switch (message) {
                case "/start":
                    startCommandReceived(chatId, update.getMessage().getChat().getFirstName());
                    break;
                case "/help":
                    sendMessage(chatId,HELP_TEXT);
                default:
                    sendMessage(chatId, "Kechirasiz, buyruq tan olinmadi");
            }
        }
    }

    public void startCommandReceived(Long chatId, String name) {
        String answer = "Salom, " + name + " siz bilan tanishganimdan xursandman";
        log.info("Replied to user {}", name);
        sendMessage(chatId, answer);
    }

    private void sendMessage(Long chatId, String textToSend) {
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText(textToSend);
        try {
            execute(message);
        } catch (TelegramApiException e) {
            log.error("Error occurred: " + e.getMessage());

        }
    }
}
