package com.qbning.nishishenmoniao.util;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

public class identitifyCode {
    private static final char[] CHARS={
            '0','1','2', '3', '4', '5', '6', '7', '8', '9',
            'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'j', 'k', 'm',
            'n', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z',
            'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M',
            'N', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z'
    };
    private static identitifyCode identitifyCode;

    public static identitifyCode getInstance(){
        if(identitifyCode  == null){
            identitifyCode = new identitifyCode();
        }
        return identitifyCode;
    }
    //验证码个数
    private static final int CODE_LENGTH = 4;
    //字体大小
    private static final int FONT_SIZE = 50;
    //线条数
    private static final int LINE_NUMBER = 5;
    //padding，其中base的意思是初始值，而range是变化范围。数值根据自己想要的大小来设置
    private static final int BASE_PADDING_LEFT=10,RANGE_PADDING_LEFT=100,BASE_PADDING_TOP=75,RANGE_PADDING_TOP=50;
    //验证码默认宽高
    private static final int DEFAULT_WIDTH=400,DEFAULT_HEIGHT=150;

    //画布的长宽
    private int width=DEFAULT_WIDTH,height=DEFAULT_HEIGHT;

    //字体的随机位置
    private int base_padding_left=BASE_PADDING_LEFT,range_padding_left=RANGE_PADDING_LEFT,
            base_padding_top=BASE_PADDING_TOP,range_padding_top=RANGE_PADDING_TOP;
    //验证码个数，线条数，字体大小
    private int codeLength=CODE_LENGTH,line_number=LINE_NUMBER,font_size=FONT_SIZE;

    private String code;
    private int padding_left,padding_top;
    private Random random=new Random();

    //验证码图片(生成一个用位图)
    public Bitmap createBitmap(){
        padding_left=0;
        padding_top=0;
        //创建指定格式，大小的位图//Config.ARGB_8888是一种色彩的存储方法
        Bitmap bp=Bitmap.createBitmap(width,height, Bitmap.Config.ARGB_8888);
        Canvas c=new Canvas(bp);

        code =createCode();
        //将画布填充为白色
        c.drawColor(Color.WHITE);
        //新建一个画笔
        Paint paint =new Paint();
        //设置画笔抗锯齿
        paint.setAntiAlias(true);
        paint.setTextSize(font_size);
        //在画布上画上验证码
//        Paint.FontMetrics fontMetrics = paint.getFontMetrics();
        for(int i=0;i<code.length();i++){
            randomTextStyle(paint);
            randomPadding();
            //这里的padding_left,padding_top是文字的基线
            c.drawText(code.charAt(i)+"",padding_left,padding_top,paint);
        }
        //画干扰线
        for(int i = 0;i<line_number;i++){
            drawLine(c,paint);
        }
        //保存一下画布
        c.save();
        c.restore();
        return bp;
    }

    //生成验证码
    private String createCode(){
        StringBuilder sb=new StringBuilder();
        //利用random生成随机下标
        for(int i=0;i<codeLength;i++){
            sb.append(CHARS[random.nextInt(CHARS.length)]);
        }
        return sb.toString();
    }

    //画线
    private void drawLine(Canvas canvas,Paint paint){
        int color=randomColor();
        int startX=random.nextInt(width);
        int startY=random.nextInt(height);
        int stopX=random.nextInt(width);
        int stopY=random.nextInt(height);
        paint.setStrokeWidth(1);
        paint.setColor(color);
        canvas.drawLine(startX,startY,stopX,stopY,paint);
    }
    //随机文字样式，颜色，文字粗细与倾斜度
    private void randomTextStyle(Paint paint){
        int color=randomColor();
        paint.setColor(color);
        paint.setFakeBoldText(random.nextBoolean());//true为粗体，false为非粗体
        double skew =random.nextInt(11)/10;
        //随机ture或者false来生成正数或者负数，来表示文字的倾斜度，负数右倾，正数左倾
        skew=random.nextBoolean()?skew:-skew;
        //   paint.setUnderlineText(true);//下划线
        // paint.setStrikeThruText(true);//删除线
    }
    //生成随机颜色，利用RGB
    private int randomColor(){
        return randomColor(1);
    }
    private int randomColor(int rate){
        int red=random.nextInt(256)/rate;
        int green=random.nextInt(256)/rate;
        int blue = random.nextInt(256)/rate;
        return Color.rgb(red,green,blue);
    }
    //验证码位置随机
    private void randomPadding(){

        padding_left+=base_padding_left+random.nextInt(range_padding_left);
        padding_top=base_padding_top+random.nextInt(range_padding_top);
    }

    public String getCode(){
        return code;
    }
    public static String md5Java(String content) {
        byte[] hash;
        try {
            hash = MessageDigest.getInstance("MD5").digest(content.getBytes());
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("NoSuchAlgorithmException", e);
        }

        StringBuilder hex = new StringBuilder(hash.length * 2);
        for (byte b : hash) {
            if ((b & 0xFF) < 0x10) {
                hex.append(0);
            }
            hex.append(Integer.toHexString(b & 0xff));
        }

        return hex.toString();
    }
}
