package com.sitp.activities;

import android.Manifest;
import android.app.AlertDialog;
import android.app.KeyguardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Build;
import android.preference.PreferenceManager;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyPermanentlyInvalidatedException;
import android.security.keystore.KeyProperties;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.joker.annotation.PermissionsCustomRationale;
import com.joker.annotation.PermissionsDenied;
import com.joker.api.Permissions4M;
import com.sitp.cipherpart.PasswordStore;
import com.sitp.cipherpart.R;
import com.sitp.fingerprint_verify.FingerprintAuthenticationDialogFragment;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;

/**
 * password store 用于存取用户字符串口令 实际上是先取出保存在服务器的 用于加密用户字符串口令的 密钥
 *                  再通过password store中的内部操作将字符串口令解密出来
 * password manager 用于口令/指纹验证后 存取保存在本地的PBE加密/解密的真实口令
 */
public class MainActivity extends AppCompatActivity {

    ImageButton btnFingerPrint;
    Context mContext;

    PasswordStore passwordStore;
    //String password;
    //String UID = "user";

    private static final String TAG = MainActivity.class.getSimpleName();

    private static final String DIALOG_FRAGMENT_TAG = "myFragment";
    private static final String SECRET_MESSAGE = "Very secret message";
    private static final String KEY_NAME_NOT_INVALIDATED = "key_not_invalidated";
    public static final String DEFAULT_KEY_NAME = "default_key";

    public static final int READ_EXTERNAL_CODE = 1;
    public static final int WRITE_EXTERNAL_CODE = 2;

    private KeyStore mKeyStore;
    private KeyGenerator mKeyGenerator;
    private SharedPreferences mSharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContext = this;

        firstRun();

        btnFingerPrint = (ImageButton)findViewById(R.id.ibtn_fingerprint);
        //btnPasswordStr = (ImageButton)findViewById(R.id.ibtn_password);

        try {
            mKeyStore = KeyStore.getInstance("AndroidKeyStore");
        } catch (KeyStoreException e) {
            throw new RuntimeException("Failed to get an instance of KeyStore", e);
        }
        try {
            mKeyGenerator = KeyGenerator
                    .getInstance(KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore");
        } catch (NoSuchAlgorithmException | NoSuchProviderException e) {
            throw new RuntimeException("Failed to get an instance of KeyGenerator", e);
        }
        Cipher defaultCipher;
        try {
            defaultCipher = Cipher.getInstance(KeyProperties.KEY_ALGORITHM_AES + "/"
                    + KeyProperties.BLOCK_MODE_CBC + "/"
                    + KeyProperties.ENCRYPTION_PADDING_PKCS7);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
            throw new RuntimeException("Failed to get an instance of Cipher", e);
        }
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        KeyguardManager keyguardManager = getSystemService(KeyguardManager.class);
        FingerprintManager fingerprintManager = getSystemService(FingerprintManager.class);

        if (!keyguardManager.isKeyguardSecure()) {
            // Show a message that the user hasn't set up a fingerprint or lock screen.
            Toast.makeText(this,
                    "Secure lock screen hasn't set up.\n"
                            + "Go to 'Settings -> Security -> Fingerprint' to set up a fingerprint",
                    Toast.LENGTH_LONG).show();
            btnFingerPrint.setEnabled(false);
            return;
        }

        // Now the protection level of USE_FINGERPRINT permission is normal instead of dangerous.
        // See http://developer.android.com/reference/android/Manifest.permission.html#USE_FINGERPRINT
        // The line below prevents the false positive inspection from Android Studio
        // noinspection ResourceType
        if (!fingerprintManager.hasEnrolledFingerprints()) {
            btnFingerPrint.setEnabled(false);
            // This happens when no fingerprints are registered.
            Toast.makeText(this,
                    "Go to 'Settings -> Security -> Fingerprint' and register at least one" +
                            " fingerprint",
                    Toast.LENGTH_LONG).show();
            return;
        }

        btnFingerPrint.setEnabled(true);
        createKey(DEFAULT_KEY_NAME, true);
        createKey(KEY_NAME_NOT_INVALIDATED, false);

        btnFingerPrint.setOnClickListener(new VerifyButtonClickListener(defaultCipher, DEFAULT_KEY_NAME));
//        btnPasswordStr.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(getApplicationContext(),PasswordLoginActivity.class);
//                startActivity(intent);
//            }
//        });

        //String password = "root1234";

//        passwordStore = PasswordStore.getInstance();
//        //password="user_password";
//        //runAddThread();
//        runQueryThread();

        /*PasswordManager passwordManager = PasswordManager.getInstance();
        try {
            passwordManager.createPasswordAuto("aaa", password, 10, true, true, true, true);
            String str = passwordManager.getPassword("aaa", password);
            boolean verifyResult = passwordManager.verifyPassword("aaa",password);
            if(verifyResult == true){
                System.out.println("验证成功！"+str);
            }else {
                System.out.println("验证失败！"+str);
            }
            System.out.println("解密后："+str);
            System.out.println("修改密码：");
            passwordManager.changePasswordManual("aaa",password,"mymessage");
            str = passwordManager.getPassword("aaa", password);
            verifyResult = passwordManager.verifyPassword("aaa",password);
            if(verifyResult == true){
                System.out.println("验证成功！"+str);
            }else {
                System.out.println("验证失败！"+str);
            }
            System.out.println("解密后："+str);
            passwordManager.deletePassword("aaa",password);
        } catch (Exception e) {
            e.printStackTrace();
        }*/
    }

    private boolean initCipher(Cipher cipher, String keyName) {
        try {
            mKeyStore.load(null);
            SecretKey key = (SecretKey) mKeyStore.getKey(keyName, null);
            cipher.init(Cipher.ENCRYPT_MODE, key);
            return true;
        } catch (KeyPermanentlyInvalidatedException e) {
            return false;
        } catch (KeyStoreException | CertificateException | UnrecoverableKeyException | IOException
                | NoSuchAlgorithmException | InvalidKeyException e) {
            throw new RuntimeException("Failed to init Cipher", e);
        }
    }

    /**
     * Proceed the verification operation
     *
     * @param withFingerprint {@code true} if the verification was made by using a fingerprint
     * @param cryptoObject the Crypto object
     */
    public void onVerified(boolean withFingerprint,
                            @Nullable FingerprintManager.CryptoObject cryptoObject) {
        if (withFingerprint) {
            // If the user has authenticated with fingerprint, verify that using cryptography and
            // then show the confirmation message.
            assert cryptoObject != null;
            tryEncrypt(cryptoObject.getCipher());
        } else {
            // Authentication happened with backup password. Just show the confirmation message.
            Toast.makeText(this,"Str verified successfully!",Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this,InquiryActivity.class);
            startActivity(intent);
        }
    }

    public void onNotVerified(){
        Toast.makeText(mContext,"密码错误！",Toast.LENGTH_LONG).show();
    }

    /**
     * Tries to encrypt some data with the generated key in {@link #createKey} which is
     * only works if the user has just authenticated via fingerprint.
     */
    private void tryEncrypt(Cipher cipher) {
        try {
            byte[] encrypted = cipher.doFinal(SECRET_MESSAGE.getBytes());
            Toast.makeText(this,"Fingerprint verified successfully!",Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this,InquiryActivity.class);
            startActivity(intent);
        } catch (BadPaddingException | IllegalBlockSizeException e) {
            Toast.makeText(this, "Failed to encrypt the data with the generated key. "
                    + "Retry the verification", Toast.LENGTH_LONG).show();
            Log.e(TAG, "Failed to encrypt the data with the generated key." + e.getMessage());
        }
    }

    /**
     * Creates a symmetric key in the Android Key Store which can only be used after the user has
     * authenticated with fingerprint.
     *
     * @param keyName the name of the key to be created
     * @param invalidatedByBiometricEnrollment if {@code false} is passed, the created key will not
     *                                         be invalidated even if a new fingerprint is enrolled.
     *                                         The default value is {@code true}, so passing
     *                                         {@code true} doesn't change the behavior
     *                                         (the key will be invalidated if a new fingerprint is
     *                                         enrolled.). Note that this parameter is only valid if
     *                                         the app works on Android N developer preview.
     *
     */
    public void createKey(String keyName, boolean invalidatedByBiometricEnrollment) {
        // The enrolling flow for fingerprint. This is where you ask the user to set up fingerprint
        // for your flow. Use of keys is necessary if you need to know if the set of
        // enrolled fingerprints has changed.
        try {
            mKeyStore.load(null);
            // Set the alias of the entry in Android KeyStore where the key will appear
            // and the constrains (purposes) in the constructor of the Builder

            KeyGenParameterSpec.Builder builder = new KeyGenParameterSpec.Builder(keyName,
                    KeyProperties.PURPOSE_ENCRYPT |
                            KeyProperties.PURPOSE_DECRYPT)
                    .setBlockModes(KeyProperties.BLOCK_MODE_CBC)
                    // Require the user to authenticate with a fingerprint to authorize every use
                    // of the key
                    .setUserAuthenticationRequired(true)
                    .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_PKCS7);

            // This is a workaround to avoid crashes on devices whose API level is < 24
            // because KeyGenParameterSpec.Builder#setInvalidatedByBiometricEnrollment is only
            // visible on API level +24.
            // Ideally there should be a compat library for KeyGenParameterSpec.Builder but
            // which isn't available yet.
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                builder.setInvalidatedByBiometricEnrollment(invalidatedByBiometricEnrollment);
            }
            mKeyGenerator.init(builder.build());
            mKeyGenerator.generateKey();
        } catch (NoSuchAlgorithmException | InvalidAlgorithmParameterException
                | CertificateException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    /*private void runAddThread(){
        new Thread(){
            @Override
            public void run(){
                passwordStore.set(getApplicationContext(),UID,password);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(MainActivity.this,"add successfully!",Toast.LENGTH_LONG).show();
                    }
                });
            }
        }.start();
    }*/

    /*private void runQueryThread(){
        new Thread(){
            @Override
            public void run(){
                try {
                    password = passwordStore.getPassword(getApplicationContext(),UID);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                } catch (GeneralSecurityException e) {
                    e.printStackTrace();
                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(MainActivity.this,"password:"+password,Toast.LENGTH_LONG).show();
                    }
                });
            }
        }.start();
    }*/

    private class VerifyButtonClickListener implements View.OnClickListener {

        Cipher mCipher;
        String mKeyName;

        VerifyButtonClickListener(Cipher cipher, String keyName) {
            mCipher = cipher;
            mKeyName = keyName;
        }

        @Override
        public void onClick(View view) {

            // Set up the crypto object for later. The object will be authenticated by use
            // of the fingerprint.
            if (initCipher(mCipher, mKeyName)) {

                // Show the fingerprint dialog. The user has the option to use the fingerprint with
                // crypto, or you can fall back to using a server-side verified password.
                FingerprintAuthenticationDialogFragment fragment
                        = new FingerprintAuthenticationDialogFragment();
                fragment.setCryptoObject(new FingerprintManager.CryptoObject(mCipher));
                fragment.setCancelable(false);
                boolean useFingerprintPreference = mSharedPreferences
                        .getBoolean(getString(R.string.use_fingerprint_to_authenticate_key),
                                true);
                if (useFingerprintPreference) {
                    fragment.setStage(
                            FingerprintAuthenticationDialogFragment.Stage.FINGERPRINT);
                } else {
                    fragment.setStage(
                            FingerprintAuthenticationDialogFragment.Stage.PASSWORD);
                }
                fragment.show(getFragmentManager(), DIALOG_FRAGMENT_TAG);
            } else {
                // This happens if the lock screen has been disabled or or a fingerprint got
                // enrolled. Thus show the dialog to authenticate with their password first
                // and ask the user if they want to authenticate with fingerprints in the
                // future
                FingerprintAuthenticationDialogFragment fragment
                        = new FingerprintAuthenticationDialogFragment();
                fragment.setCancelable(false);
                fragment.setCryptoObject(new FingerprintManager.CryptoObject(mCipher));
                fragment.setStage(
                        FingerprintAuthenticationDialogFragment.Stage.NEW_FINGERPRINT_ENROLLED);
                fragment.show(getFragmentManager(), DIALOG_FRAGMENT_TAG);
            }
        }
    }

    private void firstRun() {
        SharedPreferences sharedPreferences = getSharedPreferences("User",Context.MODE_PRIVATE);
        String UID = sharedPreferences.getString("UID","");
        if (UID.isEmpty()){
            AlertDialog.Builder dialog = new AlertDialog.Builder(this);//新建一个对话框
            dialog.setMessage("检测到当前未有用户登录，请根据提示进行注册。");//设置提示信息
            //设置确定按钮并监听
            dialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    LayoutInflater factory = LayoutInflater.from(mContext);
                    View textEntryView = factory.inflate(R.layout.double_edit_dialog, null);
                    final EditText editTextUID = (EditText) textEntryView.findViewById(R.id.editTextUID);
                    final EditText editTextPass = (EditText)textEntryView.findViewById(R.id.editTextPass);
                    final AlertDialog ad1 = new AlertDialog.Builder(mContext).
                            setView(textEntryView).setTitle("用户注册:")
                            .setPositiveButton("确定",null)
                            .setCancelable(false)
                            .create();
                    ad1.show();// 显示对话框
                    ad1.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if(editTextPass.getText().length()<8){
                                Toast.makeText(mContext,"密码长度不能少于8位！",Toast.LENGTH_LONG).show();
                            }
                            else{
                                Toast.makeText(mContext,"请牢记您的UID和密码！",Toast.LENGTH_LONG).show();
                                PasswordStore store = PasswordStore.getInstance();
                                store.set(mContext,editTextUID.getText().toString(),editTextPass.getText().toString());
                                ad1.dismiss();
                            }
                        }
                    });
                }
            });
            dialog.show();//最后不要忘记把对话框显示出来
        }
        else {
            Toast.makeText(this,"当前用户：" + UID,Toast.LENGTH_LONG).show();
        }
    }

    public void checkPermissions(){
        Permissions4M.get(MainActivity.this)
                .requestForce(true)
                .requestUnderM(true)
                .requestPermissions(Manifest.permission.READ_EXTERNAL_STORAGE)
                .requestCodes(READ_EXTERNAL_CODE)
                .requestPageType(Permissions4M.PageType.MANAGER_PAGE)
                .request();

        Permissions4M.get(MainActivity.this)
                .requestForce(true)
                .requestUnderM(true)
                .requestPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .requestCodes(WRITE_EXTERNAL_CODE)
                .requestPageType(Permissions4M.PageType.MANAGER_PAGE)
                .request();
    }

    @PermissionsDenied({READ_EXTERNAL_CODE,WRITE_EXTERNAL_CODE})
    public void denied(int code){
        switch (code){
            case READ_EXTERNAL_CODE:
                Toast.makeText(this,"读取存储卡权限授权失败",Toast.LENGTH_LONG);
                break;
            case WRITE_EXTERNAL_CODE:
                Toast.makeText(this,"写入存储卡权限授权失败",Toast.LENGTH_LONG);
                break;
        }
    }

    @PermissionsCustomRationale({READ_EXTERNAL_CODE,WRITE_EXTERNAL_CODE})
    public void cusRationale(int code){
        switch (code){
            case READ_EXTERNAL_CODE:
                new android.support.v7.app.AlertDialog.Builder(this)
                        .setMessage("读取存储卡权限申请：\n我们需要您开启读取存储卡权限(in activity with annotation)")
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Permissions4M.get(MainActivity.this)
                                        // 注意添加 .requestOnRationale()
                                        .requestOnRationale()
                                        .requestPermissions(Manifest.permission.READ_EXTERNAL_STORAGE)
                                        .requestCodes(READ_EXTERNAL_CODE)
                                        .request();
                            }
                        })
                        .show();
                break;
            case WRITE_EXTERNAL_CODE:
                new android.support.v7.app.AlertDialog.Builder(this)
                        .setMessage("写入存储卡权限申请：\n我们需要您开启写入存储卡权限(in activity with annotation)")
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Permissions4M.get(MainActivity.this)
                                        // 注意添加 .requestOnRationale()
                                        .requestOnRationale()
                                        .requestPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                                        .requestCodes(WRITE_EXTERNAL_CODE)
                                        .request();
                            }
                        })
                        .show();
                break;
        }
    }
}
