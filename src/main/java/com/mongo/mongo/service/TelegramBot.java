package com.mongo.mongo.service;


import com.mongo.mongo.configuration.BotConfig;
import com.mongo.mongo.model.BotUser;
import com.mongo.mongo.model.LastSearch;
import com.mongo.mongo.model.ModelPoi;
import com.mongo.mongo.repository.Storage;



import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.time.LocalDate;
import java.util.*;


@Component
@Slf4j
public class TelegramBot extends TelegramLongPollingBot {

    private final BotConfig botConfig;
    private final Service service;
    private final Storage storage;
    private final Map<Long, BotUser> userList = new HashMap<>();


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
            int end = message.length();
            log.info("поиск по запросу "+message);

            switch (message) {
                case "/start" -> start(chatId, update.getMessage().getChat().getFirstName());
                case "/data" -> sendMessage(chatId, LocalDate.now().toString());
                case "/menu" -> sendMessage(chatId, menu());
                case "/query" -> query(chatId);
            }


            if (message.charAt(0) == '!' && message.charAt(1) != '!' && !message.contains("#")) {

                find(update, message, chatId, end);



            } else if (message.startsWith("!") && !message.startsWith("!!") && message.contains("#")
                    && !message.substring(message.indexOf("#"), message.indexOf("#") + 2).equals("##")) {

                findNElements(update, message, chatId, end);

            } else if (message.startsWith("!") && !message.startsWith("!!") && message.contains("#")
                    && message.substring(message.indexOf("#"), message.indexOf("#") + 2).equals("##")) {

                nextElements(update, message, chatId, end);

            } else if (message.startsWith("!!")) {

                clarification(message, chatId, end);

            } else if (message.startsWith("$")) {

                sortedSearch(message, chatId, end);

            }
        }

    }


    private void start(Long chatId, String firstName) {
        String stringBuilder = "HI, " +
                firstName
                +
                "набери /menu если хочешь узнать как мной пользоваться";
        sendMessage(chatId, stringBuilder);

    }

    private void query(Long chatId) {
        int start = service.query().toString().indexOf("[");
        int end = service.query().toString().indexOf("]") + 1;
        String message = " возможные параметра поиска \n" +
                service.query().toString().substring(start, end) + "\n";
        sendMessage(chatId, message);
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

    private String menu() {
        return """
                /query возможные параметра поиска\s
                !ключ:значение поиск по параметрам\s
                !!ключ:значение уточнение предыдущего поиска по дополнителным параметрам\s
                !ключ:значение#n поиск по параметрам выдать n элементов\s
                !ключ:значение##n поиск по параметрам выдать следующие n элементов\s
                $ключ&a#n выдать n элементов отсортированных по возрастанию ключа\s 
                $ключ&d#n выдать n элементов отсортированных по убыванию ключа\s
                """;
    }

    private void sortedSearch(String message, Long chatId, int end) {
        //$ключ&a#n выдать n элементов отсортированных по возрастанию ключа
        //$ключ&d#n выдать n элементов отсортированных по убыванию ключа
        String[] param = message.substring(1, end).split("&|#");
        Long limit = 3L;
        if (param.length > 1) {
            limit = Long.parseLong(param[2]);
        }
        List<ModelPoi> l;
        if (Objects.equals(param[1], "a")) {
            l = storage.findAll()
                    .stream()
                    .sorted(((o1, o2) -> o1.getKeyValueMap().get(param[0]).compareTo(o2.getKeyValueMap().get(param[0]))))
                    .limit(limit).toList();
            l.forEach(p -> sendMessage(chatId, p.getKeyValueMap().toString()));
        } else {
            l = storage.findAll()
                    .stream()
                    .sorted(((o1, o2) -> o2.getKeyValueMap().get(param[0]).compareTo(o1.getKeyValueMap().get(param[0]))))
                    .limit(limit).toList();
            l.forEach(p -> sendMessage(chatId, p.getKeyValueMap().toString()));
        }
    }

    private void clarification(String message, Long chatId, int end) {
        //!!ключ:значение уточнение предыдущего поиска по дополнителным параметрам
        String[] param = message.substring(2, end).split(":");

        if (!userList.containsKey(chatId)) {
            throw new RuntimeException("запросов еще не было");
        }
        List<ModelPoi> l = userList.get(chatId).getLastSearch().getLastSearch()
                .stream().filter(p -> p.getKeyValueMap().get(param[0]).equals(param[1]))
                .toList();
        l.forEach(p -> sendMessage(chatId, p.getKeyValueMap().toString()));
    }

    private void nextElements(Update update, String message, Long chatId, int end) {
        //!ключ:значение##n поиск по параметрам выдать следующие n элементов
        String[] param = message.trim().substring(1, end).split(":|#");
        Long n = Long.parseLong(param[param.length - 1]);

        if (!userList.containsKey(chatId)) {
            throw new RuntimeException("запросов еще не было");
        }
        List<ModelPoi> l = userList.get(update.getMessage().getChatId()).getLastSearch()
                .getLastSearch().stream().skip(userList.get(chatId).getLastSearch().getN()).limit(n).toList();
        if (l.size() == 0) {
            sendMessage(chatId, "все элементы были показаны, повторите запрос");
        }
        l.forEach(p -> sendMessage(chatId, p.getKeyValueMap().toString()));
        userList.get(chatId).getLastSearch().setN(userList.get(chatId).getLastSearch().getN() + n);
    }

    private void find(Update update, String message, Long chatId, int end) {
        //!ключ:значение поиск по параметрам
        BotUser user = new BotUser();
        user.setName(update.getMessage().getChat().getFirstName());
        user.setChatId(update.getMessage().getChatId());
        String[] param = message.substring(1, end).split(":");
        List<ModelPoi> l = storage.findBy(param[0], param[1]);
        user.setLastSearch(LastSearch.builder().lastSearch(l).build());
        userList.put(update.getMessage().getChatId(), user);
        for (ModelPoi modelPoi : l) {
            sendMessage(chatId, modelPoi.getKeyValueMap().toString());
        }
    }

    private void findNElements(Update update, String message, Long chatId, int end) {
        //!ключ:значение#n поиск по параметрам выдать n элементов
        BotUser user = new BotUser();
        update.getMessage().getChat().getFirstName();
        user.setChatId(update.getMessage().getChatId());
        String[] param = message.substring(1, end).split(":|#");
        List<ModelPoi> l = storage.findBy(param[0], param[1]);
        long n = Long.parseLong(param[2]);
        user.setLastSearch(LastSearch.builder().n(n).lastSearch(l).build());
        userList.put(update.getMessage().getChatId(), user);
        l.stream().limit(n).forEach(p -> sendMessage(chatId, p.getKeyValueMap().toString()));
    }
}
