package com.craft3r.matrx;

public class Data {
    public static final String[][] commands = new String[][]
            {{"час", "время"},
            {"дела", "самочувствие"},
            {"привет", "здравствуй", "хай", "Hello"},
            {"имя", "название", "зовут"},
            {"поиск", "поищи", "найди", "найти"},
            {"геолокацию", "геолокация", "карта", "местоположение", "карту"},
            {"программу", "экзешник", "прогу"},
            {"отключись", "выключись"},
            {"спс", "спасибо"},
            {"таймер"}};

    public static final String[] cmd_clas = new String[]
            {"clock", "SomeQuestion", "hello", "AskName", "search", "geolocation", "pr", "end", "sps", "timer"};

    public static final String[] msges = new String[]
            {"Матрица говорит какое сейчас время", "простой вопрос", "Простой вопрос", "Простой вопрос", "Матрица выполняет поиск в гугле",
            "Матрица выполняет поиск в гугл карте", "Открывает программу\n(В разработке!)", "Закрытие программы", "Простой диалог", "Запускает таймер"};
}