package com.mongo.mongo.service;


import com.mongo.mongo.configuration.BotConfig;
import com.mongo.mongo.model.ModelPoi;
import com.mongo.mongo.repository.Storage;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.time.LocalDate;
import java.util.List;


@Component
public class TelegramBot extends TelegramLongPollingBot {

    final private BotConfig botConfig;
    private final Service service;
    private final Storage storage;

    public TelegramBot(BotConfig botConfig, Service service, Storage storage) {
        this.botConfig = botConfig;
        this.service = service;
        this.storage = storage;
    }

    @Override
    public String getBotUsername() {
        return botConfig.getBotName();
    }

    @Override
    public String getBotToken() {
        return botConfig.getToken();
    }

    @Override
    public void onUpdateReceived(Update update) {

        if (update.hasMessage() && update.getMessage().hasText()) {
            var message = update.getMessage().getText();
            var chatId = update.getMessage().getChatId();

            switch (message) {
                case "/start" -> start(chatId, update.getMessage().getChat().getFirstName());
                case "/data" -> sendMessage(chatId, LocalDate.now().toString());
            }

            if(message.charAt(0) == '!') {
                int end = message.length();
                String[] param = message.substring(1, end).split(":");
                List<ModelPoi> l = storage.findBy(param[0],param[1]);
                for (ModelPoi modelPoi : l) {
                    sendMessage(chatId,modelPoi.getKeyValueMap().toString());
                }



            }

        }

    }

    private void start(Long chatId, String firstName) {
        int start = service.query().toString().indexOf("[");
        int end = service.query().toString().indexOf("]")+1;

        String stringBuilder = "HI, " +
                firstName +
                " возможные параметра поиска \n" +
                service.query().toString().substring(start, end) + "\n" +
                "задавай поиск по типу $ параметр : значение";
        sendMessage(chatId, stringBuilder);

    }

    private void sendMessage(Long chatId, String text) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText(text);

        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }
}
