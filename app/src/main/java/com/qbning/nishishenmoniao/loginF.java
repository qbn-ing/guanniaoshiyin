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

import java.util.Objects;
import androidx.fragment.app.Fragment;

import org.json.JSONException;
import org.json.JSONObject;

//test1
//Test12345678
public class loginF extends Fragment {
    TextView tv;
    EditText zh,pwd,idtcode;
    Button btn;
    ImageView idcode;
    String corstr;
    String name,pwd0,pwd1,code;
    String target = "https://127.0.0.1:5000/login";
    JSONObject receives,sends;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.login, container, false);
        tv = view.findViewById(R.id.textView2);
        zh = view.findViewById(R.id.editTextText);
        pwd = view.findViewById(R.id.editTextTextPassword);
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
                code = String.valueOf(idtcode.getText()).toLowerCase();
                if(check_empty(context))
                {
                    if(check_code(context))
                    {
                        try {
                            sends = new JSONObject();
                            sends.put("name", name);
                            sends.put("pwd", pwd0);
                            Thread thread = new Thread(new Runnable() {
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
                            else
                            {
                                Toast.makeText(context,"昵称或密码错误 ",Toast.LENGTH_SHORT).show();
                                return;
                            }
                        } catch (JSONException | InterruptedException e) {
                            throw new RuntimeException(e);
                        }

                    }
                }
            }
        });
        return view;
    }
    Boolean check_empty(Context context)
    {
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

        return  Boolean.TRUE;
    }
    Boolean check_code(Context context)
    {
        if(code.isEmpty())
        {
            Toast.makeText(context,"请输入验证码",Toast.LENGTH_SHORT).show();
            idcode.setImageBitmap(identitifyCode.getInstance().createBitmap());
            corstr = identitifyCode.getInstance().getCode().toLowerCase();
            return Boolean.FALSE;
        }
        if(Objects.equals(code,corstr)==Boolean.FALSE)
        {
            Toast.makeText(context,"验证码错误",Toast.LENGTH_SHORT).show();
            return Boolean.FALSE;
        }
        return Boolean.TRUE;
    }
}
