package ru.msav.passwordgenerator.workers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

/**
 * Created by msav on 27.12.2017.
 */

public class RandomPasswordWorker extends BaseWorker {

    /**
     * Длина пароля
     */
    protected int length;
    public void setLength(int value) {
        this.length = value;

        // Проверка минимальной длины пароля
        if (this.length < BaseWorker.MIN_LENGTH)
            this.length = BaseWorker.MIN_LENGTH;

        // Проверка максимальной длины
        if (this.length > BaseWorker.MAX_LENGTH)
            this.length = BaseWorker.MAX_LENGTH;

        updatePassword();
    }
    public int getLength() {
        return this.length;
    }

    /**
     * Прописные буквы
     */
    protected boolean upperCase;
    public void setUpperCase(boolean value) {
        this.upperCase = value;
        updatePassword();
    }
    public boolean isUpperCase() { return upperCase; }

    /**
     * Строчные буквы
     */
    protected boolean lowerCase;
    public void setLowerCase(boolean value) {
        this.lowerCase = value;
        updatePassword();
    }
    public boolean isLowerCase() { return this.lowerCase; }

    /**
     * Цифры
     */
    protected boolean numbersChars;
    public void setNumbersChars(boolean value) {
        this.numbersChars = value;
        updatePassword();
    }
    public boolean isNumbersChars() { return this.numbersChars; }

    /**
     * Спецсимволы
     */
    protected boolean specialChars;
    public void setSpecialChars(boolean value) {
        this.specialChars = value;
        updatePassword();
    }
    public boolean isSpecialChars() { return this.specialChars; }

    /**
     * Конструктор
     */
    public RandomPasswordWorker() {
        super();

        setLength(8);

        setUpperCase(true);
        setLowerCase(true);
        setNumbersChars(true);
        setSpecialChars(true);
    }

    /**
     * Обновление пароля
     *
     * @return признак успешности
     */
    @Override
    public boolean updatePassword() {
        boolean res = false;

        String passwordValue = "";
        String alphabet = "abcdefghijklmnopqrstuvwxyz";

        List<Character> charsList = new ArrayList<>();

        // Добавление чисел в список генерации
        if (isNumbersChars()) {
            Character[] numbersList = new Character[] {'1', '2', '3', '4', '5', '6', '7', '8', '9', '0'};
            charsList.addAll(new ArrayList<>(Arrays.asList(numbersList)));
        }

        // Добавление символов в нижнем регистре
        if (isLowerCase()) {
            char[] arLowerChars = alphabet.toCharArray();
            Character[] lowerCaseList = new Character[arLowerChars.length];
            for (int i = 0; i < arLowerChars.length; i++)
                lowerCaseList[i] = arLowerChars[i];
            charsList.addAll(new ArrayList<>(Arrays.asList(lowerCaseList)));
        }

        // Добавление символов в ВЕРХНЕМ регистре
        if (isUpperCase()) {
            char[] arUpperChars = alphabet.toUpperCase().toCharArray();
            Character[] upperCaseList = new Character[arUpperChars.length];
            for (int i = 0; i < arUpperChars.length; i++)
                upperCaseList[i] = arUpperChars[i];
            charsList.addAll(new ArrayList<>(Arrays.asList(upperCaseList)));
        }

        // Добавление специальных символов
        if (isSpecialChars()) {
            String specialChars = "~!@#$%^&*+-/.,\\{}[]();:";
            char[] arSpecialChars = specialChars.toCharArray();
            Character[] specialCharsList = new Character[arSpecialChars.length];
            for (int i = 0; i < arSpecialChars.length; i++)
                specialCharsList[i] = arSpecialChars[i];
            charsList.addAll(new ArrayList<>(Arrays.asList(specialCharsList)));
        }

        if (charsList.size() > 0) {
            // Формирование пароля
            Random random = new Random(System.currentTimeMillis());
            for (int i = 0; i < getLength(); i++) {
                int index = random.nextInt(charsList.size());
                passwordValue += charsList.get(index).toString();
            }

            setPassword(passwordValue);
        }

        return res;
    }
}
