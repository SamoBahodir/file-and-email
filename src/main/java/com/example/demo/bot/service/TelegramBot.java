package com.example.demo.bot.service;

import com.example.demo.bot.config.BotConfig;
import com.example.demo.email.EmailSenderService;
import com.example.demo.entity.ads.AdsRepository;
import com.example.demo.entity.file.File;
import com.example.demo.entity.file.FileRepository;
import com.example.demo.entity.user.User;
import com.example.demo.entity.user.UserRepository;
import com.vdurmont.emoji.EmojiParser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
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
import java.util.Optional;

@Slf4j
@Component
public class TelegramBot extends TelegramLongPollingBot {
    private final BotConfig botConfig;
    private final AdsRepository adsRepository;
    private final FileRepository fileRepository;
    private final EmailSenderService emailSenderService;
    private final UserRepository userRepository;
    static final String HELP_TEXT = " Bu bot Spring imkoniyatlarini namoyish qilish uchun yaratilgan\n\n" +
            " Siz buyruqlarni chapdagi asosiy menyudan yoki buyruqlarni kiritish orqali bajarishingiz mumkin:\n\n" +
            " Xush kelibsiz xabarni ko'rish uchun /start ni kiriting\n\n" +
            " Oʻzingiz haqingizda saqlangan maʼlumotlarni koʻrish uchun /mydata ni kiriting\n\n" +
            " Ushbu xabarni qayta ko'rish uchun /help ni kiriting";
    static final String YES_BUTTON = "YES_BUTTON";
    static final String NO_BUTTON = "NO_BUTTON";
    static final String ERROR_TEXT = "Error occurred: ";
    static final String DELETED = "Sizning malumotlariz o'chirildi ";

    public TelegramBot(BotConfig botConfig, AdsRepository adsRepository, FileRepository fileRepository, EmailSenderService emailSenderService, UserRepository userRepository) {
        this.botConfig = botConfig;
        this.adsRepository = adsRepository;
        this.fileRepository = fileRepository;
        this.emailSenderService = emailSenderService;
        this.userRepository = userRepository;
        extracted();
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
            if (message.startsWith("?")) {
                var textToSend = EmojiParser.parseToUnicode(message.substring(1));
                var users = userRepository.findAll();
                for (User user : users) {
                    prepareMessage(user.getChatId(), textToSend);
                }
            } else {
                switch (message) {
                    case "/start":
                        registerUser(update.getMessage());
                        startCommandReceived(chatId, update.getMessage().getChat().getFirstName());
                        break;
                    case "/help":
                        prepareMessage(chatId, HELP_TEXT);
                        break;
                    case "/maʼlumotlarimni oʻchirib tashlang":
                        delete(chatId, DELETED);
                        break;
                    case "/register":
                        register(chatId);
                        break;
                    default:
                        prepareMessage(chatId, "https://" + message);
                }
            }
        } else if (update.hasCallbackQuery()) {
            String callBackData = update.getCallbackQuery().getData();
            var messageId = update.getCallbackQuery().getMessage().getMessageId();
            var chatId = update.getCallbackQuery().getMessage().getChatId();
            if (callBackData.equals(YES_BUTTON)) {
                String text = "Siz bosdingiz YES button";
                executeEditMessageText(text, chatId, messageId);
            } else if (callBackData.equals(NO_BUTTON)) {
                String text = "Siz bosdingiz NO button";
                executeEditMessageText(text, chatId, messageId);
            }
        }
    }

    public void delete(Long chatId, String name) {
        Optional<User> byId = userRepository.findById(chatId);
        User user = byId.get();
        userRepository.delete(user);
        sendMessage(chatId, name);
    }

    public void register(Long chatId) {
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText("Haqiqatan ham ro'yxatdan o'tmoqchimisiz");
        InlineKeyboardMarkup markupInLine = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInLine = new ArrayList<>();
        List<InlineKeyboardButton> rowInLine = new ArrayList<>();
        InlineKeyboardButton yesButton = new InlineKeyboardButton();
        yesButton.setText("Yes");
        yesButton.setCallbackData(YES_BUTTON);

        InlineKeyboardButton noButton = new InlineKeyboardButton();
        noButton.setText("NO");
        noButton.setCallbackData(NO_BUTTON);
        rowInLine.add(yesButton);
        rowInLine.add(noButton);
        rowsInLine.add(rowInLine);
        markupInLine.setKeyboard(rowsInLine);
        message.setReplyMarkup(markupInLine);
        executeMessage(message);

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
            emailSenderService.sendEmail("turayev.bahodir95@gmail.com", "telegram", "Sizning telegramizdan " + chat.getFirstName() + " ro'xatdan o'tdi");
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
        row.add("/ob-havo");
        row.add("/tasodifiy hazil oling");
        keyboardRows.add(row);
        row = new KeyboardRow();
        row.add("/register");
        row.add("/ma'lumotlarimni tekshiring");
        row.add("/maʼlumotlarimni oʻchirib tashlang");
        keyboardRows.add(row);
        keyboardMarkup.setKeyboard(keyboardRows);
        message.setReplyMarkup(keyboardMarkup);
        executeMessage(message);
    }

    private void executeEditMessageText(String text, Long chatId, long messageId) {
        EditMessageText message = new EditMessageText();
        message.setChatId(String.valueOf(chatId));
        message.setText(text);
        message.setMessageId((int) messageId);
        try {
            execute(message);
        } catch (TelegramApiException e) {
            log.error(ERROR_TEXT + e.getMessage());

        }
    }

    private void executeMessage(SendMessage message) {
        try {
            execute(message);
        } catch (TelegramApiException e) {
            log.error(ERROR_TEXT + e.getMessage());

        }
    }

    private void extracted() {
        List<BotCommand> botCommands = new ArrayList<>();
        botCommands.add(new BotCommand("/start", "xush kelibsiz xabar oling"));
        botCommands.add(new BotCommand("/mydata", "ma'lumotlaringizni saqlang"));
        botCommands.add(new BotCommand("/deletedata", "maʼlumotlarimni oʻchirib tashlang"));
        botCommands.add(new BotCommand("/help", "Ushbu botodan qanday foydalanish haqida ma'lumot"));
        botCommands.add(new BotCommand("/setting", "afzalliklaringizni belgilang"));
        try {
            this.execute(new SetMyCommands(botCommands, new BotCommandScopeDefault(), null));
        } catch (TelegramApiException e) {
            log.error("Error setting bots command list {}", e.getMessage());
        }
    }

    private void prepareMessage(Long chatId, String textToSend) {
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText(textToSend);
        executeMessage(message);
    }

    @Scheduled(cron = "${cron.scheduler}")
    private void sendAds() {
        var ads = fileRepository.findAll();
        var users = userRepository.findAll();
        for (File ad : ads) {
            for (User user : users) {
                prepareMessage(user.getChatId(), ad.getExtension());
            }
        }
    }
}
