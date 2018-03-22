package ru.msav.passwordgenerator;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import ru.msav.passwordgenerator.workers.BaseWorker;
import ru.msav.passwordgenerator.workers.RandomPasswordWorker;


public class RandomPasswordFragment extends Fragment {

    /**
     * Класс генерации пароля
     */
    private RandomPasswordWorker worker;

    /**
     * Поле пароля
     */
    private TextView editTextPassword;

    /**
     * Элементы управления длиной пароля
     */
    private EditText editTextLength;
    private SeekBar seekBarLength;

    /**
     * Параметры генерации пароля
     */
    private Switch switchUpperCase;
    private Switch switchLowerCase;
    private Switch switchNumbers;
    private Switch switchSpecialChars;

    /**
     * Кнопки
     */
    private ImageButton buttonCopy;
    private ImageButton buttonRefresh;

    private SeekBar.OnSeekBarChangeListener seekBarLengthListener = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
            if (worker.getLength() != i) {
                // Сохранение параметров
                //
                Editor editor = getPreferencesEditor();
                editor.putInt(MainActivity.SETTINGS_DOMAIN + ".length", i);
                editor.commit();

                // Обработка изменения значения
                //
                worker.setLength(i);
                refreshFormData();
            }
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {

        }
    };

    private TextWatcher editTextLengthListener = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            int length;
            try { length = Integer.parseInt(charSequence.toString()); }
            catch (NumberFormatException ex) { length = BaseWorker.MIN_LENGTH; }

            if (worker.getLength() != length) {
                // Сохранение параметров
                //
                Editor editor = getPreferencesEditor();
                editor.putInt(MainActivity.SETTINGS_DOMAIN + ".length", length);
                editor.commit();

                // Обработка изменения значения
                //
                worker.setLength(length);
                refreshFormData();
            }
        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    };

    private CompoundButton.OnCheckedChangeListener switchSpecialCharsListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
            if (worker.isSpecialChars() != b) {
                // Сохранение параметров
                //
                Editor editor = getPreferencesEditor();
                editor.putBoolean(MainActivity.SETTINGS_DOMAIN + ".specialChars", b);
                editor.commit();

                // Обработка изменения значения
                //
                worker.setSpecialChars(b);
                refreshFormData();
            }
        }
    };

    private CompoundButton.OnCheckedChangeListener switchUpperCaseListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
            if (worker.isUpperCase() != b) {
                // Сохранение параметров
                //
                Editor editor = getPreferencesEditor();
                editor.putBoolean(MainActivity.SETTINGS_DOMAIN + ".upperCase", b);
                editor.commit();

                // Обработка изменения значения
                //
                worker.setUpperCase(b);
                refreshFormData();
            }
        }
    };

    private CompoundButton.OnCheckedChangeListener switchLowerCaseListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
            if (worker.isLowerCase() != b) {
                // Сохранение параметров
                //
                Editor editor = getPreferencesEditor();
                editor.putBoolean(MainActivity.SETTINGS_DOMAIN + ".lowerCase", b);
                editor.commit();

                // Обработка изменения значения
                //
                worker.setLowerCase(b);
                refreshFormData();
            }
        }
    };

    private CompoundButton.OnCheckedChangeListener switchNumbersListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
            if (worker.isNumbersChars() != b) {
                // Сохранение параметров
                //
                Editor editor = getPreferencesEditor();
                editor.putBoolean(MainActivity.SETTINGS_DOMAIN + ".numbersChars", b);
                editor.commit();

                // Обработка изменения значения
                //
                worker.setNumbersChars(b);
                refreshFormData();
            }
        }
    };

    private View.OnClickListener buttonRefreshOnClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            worker.updatePassword();
            refreshFormData();

            Toast toast = Toast.makeText(getContext(), R.string.label_refresh_password_action, Toast.LENGTH_SHORT);
            toast.show();
        }
    };

    private View.OnClickListener buttonCopyOnClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            ClipboardManager clipboardManager =
                    (ClipboardManager) getContext().getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clipData = ClipData.newPlainText(getString(R.string.label_textViewPassword),
                    worker.getPassword());
            clipboardManager.setPrimaryClip(clipData);

            Toast toast = Toast.makeText(getContext(), R.string.label_copy_action, Toast.LENGTH_SHORT);
            toast.show();
        }
    };

    public RandomPasswordFragment() {
        super();

        this.worker = new RandomPasswordWorker();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_random_password, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Элементы управления длиной пароля
        try {
            this.editTextLength = getView().findViewById(R.id.editTextLength);
        } finally {
            this.editTextLength.addTextChangedListener(editTextLengthListener);
        }

        try {
            this.seekBarLength = getView().findViewById(R.id.seekBarLength);
        } finally {
            this.seekBarLength.setOnSeekBarChangeListener(seekBarLengthListener);
        }


        // Поле пароля
        this.editTextPassword = getView().findViewById(R.id.textViewHashValue);


        // Параметры генерации пароля
        try {
            this.switchUpperCase = getView().findViewById(R.id.switchUpperCase);
        } finally {
            this.switchUpperCase.setOnCheckedChangeListener(switchUpperCaseListener);
        }

        try {
            this.switchLowerCase = getView().findViewById(R.id.switchLowerCase);
        } finally {
            this.switchLowerCase.setOnCheckedChangeListener(switchLowerCaseListener);
        }

        try {
            this.switchNumbers = getView().findViewById(R.id.switchNumbers);
        } finally {
            this.switchNumbers.setOnCheckedChangeListener(switchNumbersListener);
        }

        try {
            this.switchSpecialChars = getView().findViewById(R.id.switchSpecial);
        } finally {
            this.switchSpecialChars.setOnCheckedChangeListener(switchSpecialCharsListener);
        }


        // Кнопки действий
        this.buttonCopy = getView().findViewById(R.id.imageButtonCopy);
        this.buttonCopy.setOnClickListener(buttonCopyOnClick);

        this.buttonRefresh = getView().findViewById(R.id.imageButtonRefresh);
        this.buttonRefresh.setOnClickListener(buttonRefreshOnClick);


        // Инициализация параметров
        //
        SharedPreferences preferences = getPreferences();

        worker.setLength(preferences.getInt(MainActivity.SETTINGS_DOMAIN + ".length", 8));
        worker.setUpperCase(preferences.getBoolean(MainActivity.SETTINGS_DOMAIN + ".upperCase", true));
        worker.setLowerCase(preferences.getBoolean(MainActivity.SETTINGS_DOMAIN + ".lowerCase", true));
        worker.setNumbersChars(preferences.getBoolean(MainActivity.SETTINGS_DOMAIN + ".numbersChars", true));
        worker.setSpecialChars(preferences.getBoolean(MainActivity.SETTINGS_DOMAIN + ".specialChars", true));

        refreshFormData();
    }

    /**
     * Получает контекст текущих параметров
     * @return Контекст параметров
     */
    private SharedPreferences getPreferences() {
        SharedPreferences preferences = getContext().getSharedPreferences(MainActivity.SETTINGS, Context.MODE_PRIVATE);
        return preferences;
    }

    /**
     * Получает контекст редактирования текущих параметров
     * @return Контекст редактирования параметров
     */
    private Editor getPreferencesEditor() {
        SharedPreferences preferences = getPreferences();
        Editor editor = preferences.edit();
        return editor;
    }

    /**
     * Обновление данных формы из данных класса генерации пароля
     */
    private void refreshFormData() {
        this.editTextPassword.setText(worker.getPassword());

        this.editTextLength.setText(String.valueOf(worker.getLength()));
        this.seekBarLength.setProgress(worker.getLength());

        // Расстановка параметров генерации пароля
        this.switchUpperCase.setChecked(worker.isUpperCase());
        this.switchLowerCase.setChecked(worker.isLowerCase());
        this.switchNumbers.setChecked(worker.isNumbersChars());
        this.switchSpecialChars.setChecked(worker.isSpecialChars());
    }

}
