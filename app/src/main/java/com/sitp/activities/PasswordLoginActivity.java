package com.sitp.activities;

import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.duma.ld.mylibrary.SwitchView;
import com.sitp.cipherpart.R;

import pl.droidsonroids.gif.GifDrawable;
import pl.droidsonroids.gif.GifImageButton;

public class PasswordLoginActivity extends AppCompatActivity {

    private ImageButton mIbtnReturn;
    private TextView mTvOrder;
    private SwitchView mSvVisiable;
    private EditText mEtPassword;
    private Button mBtnSure;
    private GifImageButton mGibSure;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password_login);

        mIbtnReturn=(ImageButton)findViewById(R.id.ibtn_return);
        mTvOrder=(TextView)findViewById(R.id.tv_order);
        mSvVisiable=(SwitchView)findViewById(R.id.sv_visiable);
        mEtPassword=(EditText)findViewById(R.id.et_password);
        mGibSure=(GifImageButton)findViewById(R.id.gib_sure);
        mBtnSure=(Button)findViewById(R.id.btn_sure);

        mIbtnReturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(PasswordLoginActivity.this,MainActivity.class);
                startActivity(intent);
            }
        });
        //切换密码是否可见
        mSvVisiable.setOnClickCheckedListener(new SwitchView.onClickCheckedListener() {
            @Override
            public void onClick() {
                int cursorPosition = mEtPassword.length();
                if(mEtPassword.getInputType()== (InputType.TYPE_CLASS_TEXT |InputType.TYPE_TEXT_VARIATION_PASSWORD)){
                    mEtPassword.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                    mEtPassword.setSelection(cursorPosition);
                }
                else{
                    mEtPassword.setInputType(InputType.TYPE_CLASS_TEXT |InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    mEtPassword.setSelection(cursorPosition);
                }
            }
        });
        mGibSure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    // 如果加载的是gif动图，第一步需要先将gif动图资源转化为GifDrawable
                    // 将gif图资源转化为GifDrawable
                    GifDrawable gifDrawable = new GifDrawable(getResources(), R.drawable.btn_sure_chosen);
                    // gif1加载一个动态图gif
                    mGibSure.setBackground(gifDrawable);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });
        mBtnSure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        Typeface typeFace = Typeface.createFromAsset(getAssets(),"font/debiao.ttf");

        mTvOrder.setTypeface(typeFace);
        mBtnSure.setTypeface(typeFace);


    }
}
