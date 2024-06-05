package com.qbning.nishishenmoniao;

import static com.qbning.nishishenmoniao.util.netWork.performHttpsPostRequest;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.qbning.nishishenmoniao.util.identitifyCode;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import androidx.fragment.app.Fragment;

import org.json.JSONException;
import org.json.JSONObject;

import javax.net.ssl.HttpsURLConnection;

public class signinF extends Fragment {
    TextView tv;
    EditText zh, pwd, pwd2, idtcode;
    Button btn;
    ImageView idcode;
    String corstr;
    String name, pwd0, pwd1, code;
    JSONObject sends, receives;
    URL url;
    String target = "https://127.0.0.1:5000/register";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.signin, container, false);

        tv = view.findViewById(R.id.textView2);
        zh = view.findViewById(R.id.editTextText);
        pwd = view.findViewById(R.id.editTextTextPassword);
        pwd2 = view.findViewById(R.id.editTextTextPassword2);
        idtcode = view.findViewById(R.id.editTextText2);
        btn = view.findViewById(R.id.button);
        idcode = view.findViewById(R.id.imageView2);
        idcode.setImageBitmap(identitifyCode.getInstance().createBitmap());
        corstr = identitifyCode.getInstance().getCode().toLowerCase();
        idcode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                idcode.setImageBitmap(identitifyCode.getInstance().createBitmap());
                corstr = identitifyCode.getInstance().getCode().toLowerCase();
            }
        });
        Context context = getActivity();
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                name = String.valueOf(zh.getText());
                pwd0 = String.valueOf(pwd.getText());
                pwd1 = String.valueOf(pwd2.getText());
                code = String.valueOf(idtcode.getText()).toLowerCase();
                if (check_empty(context)) {
                    if(pwd1.isEmpty())
                    {
                        Toast.makeText(context,"请再次输入密码",Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if(Objects.equals(pwd1,pwd0)==Boolean.FALSE)
                    {
                        Toast.makeText(context,"两次密码不一致",Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if(name.length()<3)
                    {
                        Toast.makeText(context,"昵称过短",Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if(pwd0.length()<8)
                    {
                        Toast.makeText(context,"密码过短",Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if(!pwdCheck(pwd0))
                    {
                        Toast.makeText(context,"密码请包含大写字母、小写字母、数字",Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (check_code(context)) {
                        sends = new JSONObject();
                        try {
                            sends.put("name", name);
                            sends.put("pwd", pwd0);
                            url = new URL(target);
                             Thread thread=new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        receives =  new JSONObject(Objects.requireNonNull(performHttpsPostRequest(target, sends.toString())));

                                    } catch (JSONException e) {
                                        throw new RuntimeException(e);
                                    }
                                }
                            });
                             thread.start();
                             thread.join();
                            if(Objects.equals(receives.get("State"),"OK"))
                            {
                                Toast.makeText(context,"欢迎 "+name,Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(getActivity(), MainActivity2.class);
                                intent.putExtra("name",name);
                                startActivity(intent);
                                requireActivity().finish();
                            }
                            else {
                                Toast.makeText(context,"用户名已存在 ",Toast.LENGTH_SHORT).show();
                                return;
                            }
                        } catch (JSONException | IOException | InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }
            }
        });


        return view;
    }

    Boolean check_empty(Context context) {
        if(name.isEmpty())
        {
            Toast.makeText(context,"请输入昵称",Toast.LENGTH_SHORT).show();
            return Boolean.FALSE;
        }
        if(pwd0.isEmpty())
        {
            Toast.makeText(context,"请输入密码",Toast.LENGTH_SHORT).show();
            return Boolean.FALSE;
        }

        return Boolean.TRUE;
    }

    Boolean check_code(Context context) {
        if(code.isEmpty())
        {
            Toast.makeText(context,"请输入验证码",Toast.LENGTH_SHORT).show();
            return Boolean.FALSE;
        }
        if(Objects.equals(code,corstr)==Boolean.FALSE)
        {
            Toast.makeText(context,"验证码错误",Toast.LENGTH_SHORT).show();
            idcode.setImageBitmap(identitifyCode.getInstance().createBitmap());
            corstr = identitifyCode.getInstance().getCode().toLowerCase();
            return Boolean.FALSE;
        }
        return Boolean.TRUE;
    }

    Boolean pwdCheck(String input) {
        // 正则表达式匹配至少包含一个数字、一个小写字母、一个大写字母
        Pattern pattern = Pattern.compile("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z]).*$");
        Matcher matcher = pattern.matcher(input);
        return matcher.matches();
    }


}
