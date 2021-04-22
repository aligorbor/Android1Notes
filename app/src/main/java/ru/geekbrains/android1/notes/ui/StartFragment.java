package ru.geekbrains.android1.notes.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.Profile;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;

import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;

import java.util.Arrays;

import ru.geekbrains.android1.notes.MainActivity;
import ru.geekbrains.android1.notes.Navigation;
import ru.geekbrains.android1.notes.R;


public class StartFragment extends Fragment {
    // Используется, чтобы определить результат activity регистрации через Google
    private static final int RC_SIGN_IN = 40404;
    private static final String TAG = "GoogleAuth";
    private static final String TAG_FB = "FBAuth";

    private Navigation navigation;
    // Клиент для регистрации пользователя через Google
    private GoogleSignInClient googleSignInClient;
    // Кнопка регистрации через Google
    private SignInButton buttonSignIn;
    private LoginButton loginButton;  //FB
    private TextView emailView;
    private TextView textFB;
    private MaterialButton continue_;

    private CallbackManager callbackManager;   //FB


    public StartFragment() {
        // Required empty public constructor
    }

    public static StartFragment newInstance() {
        return new StartFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        callbackManager = CallbackManager.Factory.create();  //FB
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        MainActivity activity = (MainActivity) context;
        navigation = activity.getNavigation();
    }

    @Override
    public void onDetach() {
        navigation = null;
        super.onDetach();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_start, container, false);
        initFBLogin(view);
        initGoogleSign();
        initView(view);
        enableSign(true);
        enableLoginFB(true);
        return view;
    }

    private void initFBLogin(View view) {
        textFB = view.findViewById(R.id.textFB);
        loginButton = view.findViewById(R.id.login_button);
        loginButton.setReadPermissions("email");
        // If using in a fragment
        loginButton.setFragment(this);

        // Callback registration
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                //   String string = "FB User Id: "+loginResult.getAccessToken().getUserId();
                if (Profile.getCurrentProfile() != null)
                    updateUIFB(Profile.getCurrentProfile().getName());
                enableLoginFB(false);
            }

            @Override
            public void onCancel() {
                // App code
            }

            @Override
            public void onError(FacebookException exception) {
                Log.w(TAG_FB, "FB Login failed");
            }
        });
    }


    // Инициализация запроса на аутентификацию
    private void initGoogleSign() {
        // Конфигурация запроса на регистрацию пользователя, чтобы получить
        // идентификатор пользователя, его почту и основной профайл
        // (регулируется параметром)
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail().build();
        // Получаем клиента для регистрации и данные по клиенту
        googleSignInClient = GoogleSignIn.getClient(getContext(), gso);
    }

    private void initView(View view) {
        buttonSignIn = view.findViewById(R.id.sign_in_button);
        buttonSignIn.setOnClickListener(v -> signIn());
        emailView = view.findViewById(R.id.email);
        // Кнопка «Продолжить», будем показывать главный фрагмент
        continue_ = view.findViewById(R.id.continue_);
        continue_.setOnClickListener(v -> navigation.addFragment(ListNotesFragment.newInstance(), false));
    }

    @Override
    public void onStart() {
        super.onStart();
        // Проверим, входил ли пользователь в это приложение через Google
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(getContext());
        if (account != null) {
            // Пользователь уже входил, сделаем кнопку недоступной
            enableSign(false);
            // Обновим почтовый адрес этого пользователя и выведем его на экран
            updateUI(account.getEmail());
        }
        //FB
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        if (accessToken != null && !accessToken.isExpired()) {
            LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("public_profile"));
        }
    }

    private void updateUI(String email) {
        emailView.setText(email);
    }

    private void updateUIFB(String string) {
        textFB.setText(string);
    }

    // Инициируем регистрацию пользователя
    private void signIn() {
        Intent signInIntent = googleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    // Здесь получим ответ от системы, что пользователь вошёл
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {// Когда сюда возвращается Task, результаты аутентификации уже готовы
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    //https://developers.google.com/identity/sign-in/android/backend-auth?authuser=1
    // Получаем данные пользователя
    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            // Регистрация прошла успешно
            enableSign(false);
            if (account != null)
                updateUI(account.getEmail());
        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure
            // reason. Please refer to the GoogleSignInStatusCodes class
            // reference for more information.
            Log.w(TAG, "signInResult:failed code=" + e.getStatusCode());
        }
    }

    private void enableSign(boolean enable) {
        buttonSignIn.setEnabled(enable);
        continue_.setEnabled(!enable);
    }

    private void enableLoginFB(boolean enable) {
        loginButton.setEnabled(enable);
        continue_.setEnabled(!enable);
    }

}