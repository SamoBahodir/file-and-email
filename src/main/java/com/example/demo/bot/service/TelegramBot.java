package com.example.demo.bot.service;

import com.example.demo.bot.config.BotConfig;
import com.example.demo.email.EmailSenderService;
import com.example.demo.entity.user.User;
import com.example.demo.entity.user.UserRepository;
import com.vdurmont.emoji.EmojiParser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScopeDefault;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Slf4j
@Component
public class TelegramBot extends TelegramLongPollingBot {
    private final BotConfig botConfig;
    private final EmailSenderService emailSenderService;
    private final UserRepository userRepository;
    static final String HELP_TEXT = " This bot is created to demonstrate Spring capabilities\n\n" +
            " You can execute commands from the main menu on the left or by typing a commands:\n\n" +
            " Type /start to see e welcome message\n\n" +
            " Type /mydata to see data stored about tourself\n\n" +
            " Type /help to see this message again";

    public TelegramBot(BotConfig botConfig, EmailSenderService emailSenderService, UserRepository userRepository) {
        this.botConfig = botConfig;
        this.emailSenderService = emailSenderService;
        this.userRepository = userRepository;
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
                    registerUser(update.getMessage());
                    startCommandReceived(chatId, update.getMessage().getChat().getFirstName());
                    break;
                case "/help":
                    sendMessage(chatId, HELP_TEXT);
                    break;
                case "/register":
                    register(chatId);
                    break;
                default:
                    sendMessage(chatId, "Kechirasiz, buyruq tan olinmadi");
            }
        } else if (update.hasCallbackQuery()) {
            String callBackData = update.getCallbackQuery().getData();
            var messageId = update.getCallbackQuery().getMessage().getMessageId();
            var chatId = update.getCallbackQuery().getMessage().getChatId();
            if (callBackData.equals("YES_BUTTON")) {
                String text = "You pressed YES button";
                EditMessageText message = new EditMessageText();
                message.setChatId(String.valueOf(chatId));
                message.setText(text);
                message.setMessageId(messageId);
                try {
                    execute(message);
                } catch (TelegramApiException e) {
                    log.error("Error occurred: " + e.getMessage());

                }
            } else if (callBackData.equals("NO_BUTTON")) {
                String text = "You pressed NO button";
                EditMessageText message = new EditMessageText();
                message.setChatId(String.valueOf(chatId));
                message.setText(text);
                message.setMessageId(messageId);
                try {
                    execute(message);
                } catch (TelegramApiException e) {
                    log.error("Error occurred: " + e.getMessage());

                }
            }
        }
    }

    public void register(Long chatId) {
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText("Do yu really want to register");
        InlineKeyboardMarkup markupInLine = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInLine = new ArrayList<>();
        List<InlineKeyboardButton> rowInLine = new ArrayList<>();
        InlineKeyboardButton yesButton = new InlineKeyboardButton();
        yesButton.setText("Yes");
        yesButton.setCallbackData("YES_BUTTON");

        InlineKeyboardButton noButton = new InlineKeyboardButton();
        noButton.setText("NO");
        noButton.setCallbackData("NO_BUTTON");
        rowInLine.add(yesButton);
        rowInLine.add(noButton);
        rowsInLine.add(rowInLine);
        markupInLine.setKeyboard(rowsInLine);
        message.setReplyMarkup(markupInLine);
        try {
            execute(message);
        } catch (TelegramApiException e) {
            log.error("Error occurred: " + e.getMessage());

        }

    }

    private void registerUser(Message message) {
        if (userRepository.findById(message.getChatId()).isEmpty()) {
            var chatId = message.getChatId();
            var chat = message.getChat();
            User user = new User();
            user.setChatId(chatId);
            user.setFirstname(chat.getFirstName());
            user.setLastname(chat.getLastName());
            user.setUsername(chat.getUserName());
            user.setRegisteredAt(new Date());
            emailSenderService.sendEmail("turayev.bahodir95@gmail.com","telegram","Sizning telegramizdan "+chat.getFirstName()+" ro'xatdan o'tdi");
            userRepository.save(user);
            log.info("User saved");
        }
    }

    public void startCommandReceived(Long chatId, String name) {
        String answer = EmojiParser.parseToUnicode("Salom, " + name + " siz bilan tanishganimdan xursandman" + " :blush:");
//        String answer = "Salom, " + name + " siz bilan tanishganimdan xursandman";
        log.info("Replied to user {}", name);
        sendMessage(chatId, answer);
    }

    private void sendMessage(Long chatId, String textToSend) {
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText(textToSend);
        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
        List<KeyboardRow> keyboardRows = new ArrayList<>();
        KeyboardRow row = new KeyboardRow();
        row.add("weather");
        row.add("get random joke");
        keyboardRows.add(row);
        row = new KeyboardRow();
        row.add("/register");
        row.add("check my data");
        row.add("delete my data");
        keyboardRows.add(row);
        keyboardMarkup.setKeyboard(keyboardRows);
        message.setReplyMarkup(keyboardMarkup);
        try {
            execute(message);
        } catch (TelegramApiException e) {
            log.error("Error occurred: " + e.getMessage());

        }
    }
}
