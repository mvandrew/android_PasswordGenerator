package ru.msav.passwordgenerator;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.widget.TextView;

public class AboutActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        // Установка заголовка
        try {
            PackageInfo packageInfo = this.getPackageManager().getPackageInfo(getPackageName(), 0);
            String version = packageInfo.versionName;

            TextView textViewAppName = findViewById(R.id.textViewAppName);
            String titleValue = getString(R.string.text_about_header,
                    getString(R.string.app_name),
                    version);
            textViewAppName.setText(titleValue);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        // Ссылка на обратную связь
        TextView textViewFeedback = findViewById(R.id.textViewFeedback);
        textViewFeedback.setText(
                Html.fromHtml("<a href='mailto:info@msav.ru?subject=Android%20Password%20Generator'>" + getString(R.string.text_about_feedback) + "</a>")
        );
        textViewFeedback.setMovementMethod(LinkMovementMethod.getInstance());

        // Ссылка на сайт автора
        TextView textViewSite = findViewById(R.id.textViewSite);
        textViewSite.setText(
                Html.fromHtml("<a href='http://www.msav.ru/?utm_source=google_play&utm_medium=app&utm_campaign=password_generator&utm_content=about'>" + getString(R.string.text_about_site) + "</a>")
        );
        textViewSite.setMovementMethod(LinkMovementMethod.getInstance());
    }
}
