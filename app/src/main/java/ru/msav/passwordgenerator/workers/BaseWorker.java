package ru.msav.passwordgenerator.workers;

/**
 * Created by msav on 27.12.2017.
 */

public abstract class BaseWorker {

    /**
     * Минимальная длина пароля
     */
    public static final int MIN_LENGTH = 3;

    /**
     * Максимальная длина пароля
     */
    public static final int MAX_LENGTH = 32;

    /**
     * Итоговое значение пароля
     */
    private String mPassword;

    public String getPassword() {
        return mPassword;
    }

    public void setPassword(String password) {
        mPassword = password;
    }

    /**
     * Обновление пароля
     *
     * @return признак успешности
     */
    abstract public boolean updatePassword();

    public BaseWorker() {
        setPassword("");
    }

}
