package ru.msav.passwordgenerator;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.SharedPreferences;
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
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import ru.msav.passwordgenerator.workers.HashAlgorithm;
import ru.msav.passwordgenerator.workers.HashPasswordWorker;

import static android.content.SharedPreferences.*;


public class HashPhraseFragment extends Fragment {

    private EditText editTextPassphrase;

    private TextView textViewHashValue;
    private ImageButton imageButtonCopy;

    private RadioGroup radioGroupAlgorithm;
    private RadioButton radioButtonAlgoMD5;
    private RadioButton radioButtonAlgoSHA256;

    private HashPasswordWorker Worker;


    /**
     * Копирование хэша в буфер обмена
     */
    private View.OnClickListener imageButtonCopyOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

            ClipboardManager clipboardManager =
                    (ClipboardManager) getContext().getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clipData = ClipData.newPlainText(getString(R.string.label_textViewHashLabel),
                    Worker.getPassword());
            if (clipboardManager != null)
                clipboardManager.setPrimaryClip(clipData);

            Toast toast = Toast.makeText(getContext(), R.string.label_copy_action, Toast.LENGTH_SHORT);
            toast.show();

        }
    };


    /**
     * Обработка изменения текста фразы для хэширования
     */
    private TextWatcher editTextPassphraseOnTextChanged = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            String newValue = charSequence.toString();
            if (!newValue.equals(Worker.getPassphrase())) {
                Worker.setPassphrase(newValue);

                // Сохранение фразы в параметрах
                //
                Editor editor = getPreferencesEditor();
                editor.putString(MainActivity.SETTINGS_DOMAIN + ".passphrase", Worker.getPassphrase());
                editor.commit();

                refreshFormData();
            }
        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    };

    /**
     * Обработка выбора алгоритма
     */
    private CompoundButton.OnCheckedChangeListener radioButtonAlgoOnCheckedChangeListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
            // Установка выбора алгоритма
            //
            if (radioButtonAlgoMD5.isChecked())
                Worker.setAlgorithm(HashAlgorithm.MD5);
            else if (radioButtonAlgoSHA256.isChecked())
                Worker.setAlgorithm(HashAlgorithm.SHA256);

            // Сохранение алгоритма в настройках
            //
            Editor editor = getPreferencesEditor();
            editor.putInt(MainActivity.SETTINGS_DOMAIN + ".algorithm", Worker.getAlgorithm().getValue());
            editor.commit();

            refreshFormData();
        }
    };

    /**
     * Конструктор класса
     */
    public HashPhraseFragment() {
        super();
        Worker = new HashPasswordWorker();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_hash_phrase, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        /**
         * Результат вычисления
         */
        textViewHashValue = getView().findViewById(R.id.textViewHashValue);
        imageButtonCopy = getView().findViewById(R.id.imageButtonCopy);
        imageButtonCopy.setOnClickListener(imageButtonCopyOnClickListener);

        /**
         * Кнопки выбора алгритма
         */
        radioGroupAlgorithm = getView().findViewById(R.id.radioGroupAlgorithm);
        radioButtonAlgoMD5 = getView().findViewById(R.id.radioButtonAlgoMD5);
        radioButtonAlgoSHA256 = getView().findViewById(R.id.radioButtonAlgoSHA256);
        radioButtonAlgoMD5.setOnCheckedChangeListener(radioButtonAlgoOnCheckedChangeListener);
        radioButtonAlgoSHA256.setOnCheckedChangeListener(radioButtonAlgoOnCheckedChangeListener);

        // Установка начальных значений из параметров
        //
        SharedPreferences preferences = getPreferences();
        Worker.setPassphrase(preferences.getString(MainActivity.SETTINGS_DOMAIN + ".passphrase", ""));

        // Определение алгоритма
        HashAlgorithm algorithm = HashAlgorithm.valueOf(preferences.getInt(MainActivity.SETTINGS_DOMAIN + ".algorithm", 0));
        Worker.setAlgorithm(algorithm);

        /**
         * Входящее значение для шифрования
         */
        editTextPassphrase = getView().findViewById(R.id.editTextPassphrase);
        editTextPassphrase.setText(Worker.getPassphrase());
        editTextPassphrase.addTextChangedListener(editTextPassphraseOnTextChanged);

        refreshFormData();
    }

    /**
     * Установка значений алгоритма выполняется при показе закладки пользователю.
     * В противном случае выделение именно элемента управления происходит не корректно.
     * @param isVisibleToUser
     */
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

        if (isVisibleToUser) {
            refreshAlgorithm();
        }
    }

    /**
     * Обновление данных формы из данных класса генерации пароля
     */
    public void refreshFormData() {
        //editTextPassphrase.setText(Worker.getPassphrase());
        textViewHashValue.setText(Worker.getPassword());
    }

    private void refreshAlgorithm() {
        switch (Worker.getAlgorithm()) {
            case MD5:
                if (!radioButtonAlgoMD5.isChecked())
                    radioGroupAlgorithm.check(R.id.radioButtonAlgoMD5);
                break;
            case SHA256:
                if (!radioButtonAlgoSHA256.isChecked())
                    radioGroupAlgorithm.check(R.id.radioButtonAlgoSHA256);
                break;
        }
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

}
