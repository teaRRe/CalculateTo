package com.example.calculatrice;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Build;
import android.provider.Telephony;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.Printer;
import android.view.Display;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;

public class MainActivity extends AppCompatActivity {

    private int BTN_WIDTH;
    private int BTN_HEIGHT;
    private int BTN_CALCULATE_HEIGHT;
    private LinearLayout linearLayout;
    private String mainText;
    private TextView text_display;
    private Stack mainText2 = new Stack();
    private TextView text_minor;
    private int stateFlag;
    //数字链表
    private LinkedList<Double> optItem = new LinkedList<Double>();

    private Stack optStack = new Stack();
    //运算符号链表
    private LinkedList<String> symList = new LinkedList<String>();

    //M运算
    private Button btn_mc;private Button btn_mc_in;private Button btn_mc_de;private Button btn_mr;
    //0~9
    private Button btn_0;private  Button btn_1;private  Button btn_2;private  Button btn_3;private  Button btn_4;
    private Button btn_5;private  Button btn_6;private  Button btn_7;private  Button btn_8;private  Button btn_9;
    //加减乘除模
    private Button btn_add;private Button btn_min;private Button btn_mul;private Button btn_div;private Button btn_left;
    //删、点、等于、归零
    private Button btn_del;private Button btn_dot;private Button btn_cal;private Button btn_c;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //全屏
        hideBottomUIMenu();

        //获取窗口管理器
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        //获取所有按钮实例
        getButtonInstance();
        text_display = (TextView) findViewById(R.id.text_display);
        text_minor = (TextView) findViewById(R.id.text_minor);

        //动态获取高度
        BTN_CALCULATE_HEIGHT = convertDpToPixel((convertPixelToDp(dm.heightPixels)-150)/6 * 2);
        BTN_WIDTH = dm.widthPixels/4;
        BTN_HEIGHT = convertDpToPixel(((convertPixelToDp(dm.heightPixels)-150)/6));

        //设置高度
        setBtnHeight(BTN_HEIGHT);
        btn_cal.setHeight(BTN_CALCULATE_HEIGHT);

        //初始化显示字符串
        text_display.setText("");

        //设置数字触摸事件
        setOnTouch();
        //【util】区、M行触摸事件
        setUtilTouch();
        //【等于】按钮触摸、点击事件
        btn_cal.setOnTouchListener(listener_btn_cal);
        btn_cal.setOnClickListener(lsn_clk_btn_cal);

        /**
         * 【DEL】按钮点击事件
         */
        btn_del.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mainText2.empty())
                    mainText2.clear();
                if (!optItem.isEmpty())
                    optItem.clear();
                if (!symList.isEmpty())
                    symList.clear();

                text_display.setText("");
            }
        });
        /**
         * 【C】按钮点击事件
         */
        btn_c.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!mainText2.empty()){
                    mainText2.pop();    //移除栈顶元素
                    String s = "";
                    for (int i = 0; i<mainText2.size(); i++){
                        s = s + mainText2.get(i);
                    }
                    text_display.setText(s);

                    Log.i("Log","【C】button click：" + s);
                }
            }
        });

    }

    /**
     * 隐藏虚拟按键，并且全屏
     * 感谢网友！
     */
    protected void hideBottomUIMenu() {

        if (Build.VERSION.SDK_INT > 11 && Build.VERSION.SDK_INT < 19) { // lower api
            View v = this.getWindow().getDecorView();
            v.setSystemUiVisibility(View.GONE);
        } else if (Build.VERSION.SDK_INT >= 19) {
            //for new api versions.
            View decorView = getWindow().getDecorView();
            int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY | View.SYSTEM_UI_FLAG_FULLSCREEN;
            decorView.setSystemUiVisibility(uiOptions);
        }
    }
    /**
     * px转dp
     * 感谢网友！
     * @param pixel
     * @return
     */
    private int convertPixelToDp(int pixel) {
        DisplayMetrics displayMetrics = this.getResources().getDisplayMetrics();
        return (int)(pixel/displayMetrics.density);
    }

    /**
     * dp转px
     * 感谢网友！
     * @param dp
     * @return
     */
    private int convertDpToPixel(int dp) {
        DisplayMetrics displayMetrics = this.getResources().getDisplayMetrics();
        return (int)(dp*displayMetrics.density);
    }

    /**
     * 获取所有按钮实例
     */
    private void getButtonInstance(){
        //0~9
        btn_0 = (Button) findViewById(R.id.btn_0); btn_1 = (Button) findViewById(R.id.btn_1);
        btn_2 = (Button) findViewById(R.id.btn_2); btn_3 = (Button) findViewById(R.id.btn_3);
        btn_4 = (Button) findViewById(R.id.btn_4); btn_5 = (Button) findViewById(R.id.btn_5);
        btn_6 = (Button) findViewById(R.id.btn_6); btn_7 = (Button) findViewById(R.id.btn_7);
        btn_8 = (Button) findViewById(R.id.btn_8); btn_9 = (Button) findViewById(R.id.btn_9);
        //加减乘除模
        btn_add = (Button) findViewById(R.id.btn_add);
        btn_min = (Button) findViewById(R.id.btn_min);
        btn_mul = (Button) findViewById(R.id.btn_mul);
        btn_div = (Button) findViewById(R.id.btn_div);
        btn_left = (Button) findViewById(R.id.btn_left);
        //删、点、等于、归零
        btn_del = (Button) findViewById(R.id.btn_del);
        btn_dot = (Button) findViewById(R.id.btn_dot);
        btn_cal = (Button) findViewById(R.id.btn_cal);
        btn_c = (Button) findViewById(R.id.btn_c);
        //
        btn_mc = (Button) findViewById(R.id.btn_mc);
        btn_mc_in = (Button) findViewById(R.id.btn_mc_in);
        btn_mc_de = (Button) findViewById(R.id.btn_mc_de);
        btn_mr = (Button) findViewById(R.id.btn_mr);
    }

    /**
     * 设置按钮高度
     * @param height
     */
    private void setBtnHeight(int height){
        btn_0.setHeight(height);btn_1.setHeight(height);btn_2.setHeight(height);btn_3.setHeight(height);
        btn_4.setHeight(height);btn_5.setHeight(height);btn_6.setHeight(height);btn_7.setHeight(height);
        btn_8.setHeight(height);btn_9.setHeight(height);

        btn_add.setHeight(height);btn_min.setHeight(height);btn_mul.setHeight(height);btn_div.setHeight(height);
        btn_left.setHeight(height);

        btn_del.setHeight(height);btn_dot.setHeight(height);btn_cal.setHeight(height);btn_c.setHeight(height);

        btn_mc.setHeight(height);btn_mc_in.setHeight(height);btn_mc_de.setHeight(height);btn_mr.setHeight(height);

    }

    /**
     * 【数字】按钮监触摸事件调用
     */
    private void setOnTouch(){
        btn_1.setOnTouchListener(listener_btn_num);btn_2.setOnTouchListener(listener_btn_num);btn_3.setOnTouchListener(listener_btn_num);
        btn_4.setOnTouchListener(listener_btn_num);btn_5.setOnTouchListener(listener_btn_num);btn_6.setOnTouchListener(listener_btn_num);
        btn_7.setOnTouchListener(listener_btn_num);btn_8.setOnTouchListener(listener_btn_num);btn_9.setOnTouchListener(listener_btn_num);
        btn_left.setOnTouchListener(lsn_tou_btn_util);btn_0.setOnTouchListener(listener_btn_num);btn_dot.setOnTouchListener(listener_btn_num);

    }
    /**
     * 【加减乘除】区触摸事件调用
     */
    private void setUtilTouch(){
        btn_c.setOnTouchListener(lsn_tou_btn_util);btn_div.setOnTouchListener(lsn_tou_btn_util);
        btn_mul.setOnTouchListener(lsn_tou_btn_util);btn_del.setOnTouchListener(lsn_tou_btn_util);
        btn_min.setOnTouchListener(lsn_tou_btn_util);btn_add.setOnTouchListener(lsn_tou_btn_util);
        //M行一起用这个效果
        btn_mc.setOnTouchListener(lsn_tou_btn_util);btn_mc_in.setOnTouchListener(lsn_tou_btn_util);
        btn_mc_de.setOnTouchListener(lsn_tou_btn_util);btn_mr.setOnTouchListener(lsn_tou_btn_util);
    }
    /**
     * 【数字】按钮触摸监听事件实现
     */
    View.OnTouchListener listener_btn_num = new View.OnTouchListener(){
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch (event.getAction()){
                case MotionEvent.ACTION_DOWN:
                    v.setBackgroundColor(0xFF007def);
                    Log.i("Log","【number button】 Action down : " + v.getTag() + "");

                    if (stateFlag ==1){
                        text_display.setText("");

                        stateFlag =0;
                    }
                    /**
                     * 首先保证%和.不是第一个输入的字符
                     * 再保证不能重复输入这两个字符
                     * 【模 % 是一个运算符！】
                     */
                    if ( !".".equals(v.getTag()) && !"%".equals(v.getTag())){

                        text_display.setText(text_display.getText().toString() + v.getTag());//主窗口显示

                        mainText2.add(v.getTag());  //入栈
                        optStack.add(v.getTag());   //入运算栈

                    }else if ( !mainText2.empty() && !".".equals(mainText2.get(mainText2.size() - 1))
                            && !"%".equals(mainText2.get(mainText2.size() - 1))) {

                        text_display.setText(text_display.getText().toString() + v.getTag());//主窗口显示

                        mainText2.add(v.getTag());  //入栈
                        optStack.add(v.getTag());   //入运算栈

                    }

                    break;
                case MotionEvent.ACTION_UP:
                    v.setBackgroundColor(0xffffffff);
                    break;
            }
            return false;   //响应点击事件
        }
    };
    /**
     * 【等于】按钮触摸事件实现
     */
    View.OnTouchListener listener_btn_cal = new View.OnTouchListener(){

        public boolean onTouch(View v, MotionEvent event) {
            switch (event.getAction()){
                case MotionEvent.ACTION_DOWN:
                    v.setBackgroundColor(0xFFf5f5f5);
                    btn_cal.setTextColor(0xff000000);
                    break;
                case MotionEvent.ACTION_UP:
                    v.setBackgroundColor(0xff007dff);
                    btn_cal.setTextColor(0xffffffff);
                    break;
            }

            return false;
        }
    };

    String sym;
    Double calNum1;
    Double calNum2;
    int symLocation;
    Stack temp1 = new Stack();

    /**
     * 构建最后一个操作数
     * @return
     */
    private double getLastNum(){
        StringBuilder lastOpt = new StringBuilder();

        String temp = "";
        //Log.i("eq","The Symbol: "+symList.getLast());
        while (true){

            temp = mainText2.pop().toString();
            Log.i("eq","After pop: " +temp);

            if (temp.equals(symList.getLast())){
                break;
            }else{
                lastOpt.append(temp);
            }
        }
        lastOpt.reverse();
        double lastOptDb = Double.parseDouble(lastOpt.toString());
        return lastOptDb;
    }
    /**
     * 【等于】按钮点击事件
     */
    View.OnClickListener lsn_clk_btn_cal = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            Log.i("Log","【calculate】button click");

                //次窗口
            text_minor.setText(text_display.getText() + "=");

            try {
                //获取最后一个输入数
                double lastOptDb = getLastNum();
                //添加进链表
                optItem.add(lastOptDb);
            }catch (Exception e){
                e.printStackTrace();
            }

                //double s = Calculate(optItem, symList);
                double s2 = CalculateByOrder(optItem, symList);
                //Log.i("Log", "结果是：" + s);
                Log.i("Log", "四则顺序算法结果是：" + s2);

                String display = String.valueOf(s2);

                text_display.setText(display);
                stateFlag = 1;

                mainText2.clear();
                optItem.clear();
                symList.clear();
                optStack.clear();


        }
    };
    /**
     * 【加减乘除】区、M行触摸事件实现
     */
    View.OnTouchListener lsn_tou_btn_util = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch (event.getAction()){
                case MotionEvent.ACTION_DOWN:
                    v.setBackgroundColor(0xFFffffff);
                    Log.i("Log","【operating button】Action down--" + v.getTag().toString());
                    /**
                     * 同时能且只能有一个运算符号(+-x/)显示在主窗口和入栈
                     */
                    try {

                        if ( !mainText2.empty() && isCalSymbol(v.getTag().toString())){
                            text_display.setText(text_display.getText().toString()+v.getTag()); //主窗口显示
                            mainText2.add(v.getTag().toString());   //字符入主栈
                            optStack.add(v.getTag().toString());    //入运算栈
                            //获取并移除栈顶元素
                            sym = optStack.pop().toString();

                            String operating = "";
                            //StringBuilder stringBuilder = new StringBuilder();
                            for (int i = 0; i < optStack.size(); i++){
                                operating = operating + optStack.get(i);
                                //stringBuilder.append(optStack.get(i));
                            }
                            calNum1 =  Double.parseDouble(operating);

                            //操作数入列
                            optItem.add(calNum1);
                            //运算符号入队列
                            symList.add(sym);

                            Log.i("Log","入队列："+ calNum1 + " 符号：" + sym);

                        }

                    }catch (Exception e){ }
                    finally {
                        //清空运算栈
                        if (!optStack.empty())
                            optStack.clear();
                    }

                    break;
                case MotionEvent.ACTION_UP:
                    v.setBackgroundColor(0xFFF5F5F5);
                    break;
            }
            return false;
        }
    };
    /**
     * 主窗口显示符号判别
     * @param sym
     * @return
     */
    private boolean isCalSymbol(String sym){
        if ("/".equals(sym) || "x".equals(sym) || "-".equals(sym) || "+".equals(sym) || "%".equals(sym)){
            if ( !"/".equals(mainText2.get(mainText2.size()-1)) &&
                    !"x".equals(mainText2.get(mainText2.size()-1)) &&
                    !"-".equals(mainText2.get(mainText2.size()-1)) &&
                    !"+".equals(mainText2.get(mainText2.size()-1)) &&
                    !"%".equals(mainText2.get(mainText2.size()-1))){
                return true;
            }
        }else{
            return false;
        }
        return false;
    }

    /**
     * 基本运算
     * @param calnum1
     * @param calnum2
     * @param sym
     * @return
     */
    private Double calByNum(Double calnum1, Double calnum2, String sym){
        double result = 0.0;
        switch (sym){
            case "x":
                result = calnum1 * calnum2;
                break;
            case "+":
                result = calnum1 + calnum2;
                break;
            case "-":
                result = calnum1 - calnum2;
                break;
            case "/":
                result = calnum1 / calnum2;
                break;
        }
        return result;
    }

    /**
     * 不考虑四则运算规则计算
     * @param q
     * @param s
     * @return
     */
    double Calculate(LinkedList<Double> q, LinkedList<String> s)
    {
        double ans = q.getFirst();
        q.pop();
        while(!q.isEmpty())
        {
            String x = s.getFirst();
            double y = q.getFirst();
            if(x.equals("-"))
            {
                ans = ans-y;
            }
            else if(x.equals("+"))
            {
                ans = ans+y;
            }
            else if(x.equals("x"))
            {
                ans = ans*y;
            }
            else if(x.equals("/"))
            {
                ans = ans/y;
            }
            else if(x.equals("%"))
            {
                int temp = (int)ans % (int)y;
                ans = temp;
            }
            q.pop();
            s.pop();
        }
        return ans;
    }

    /**
     * 四则运算规则运算
     * @param nums
     * @param ops
     * @return
     */
    private double CalculateByOrder(LinkedList<Double> nums, LinkedList<String> ops){
        double t = 0.0;
        double [] position;
        /**
         * 看一下运算式里的乘除法和加减法数量
         */
        int Fcount = 0;
        int Scount = 0;
        for (String s:ops ){
            if (s.equals("x") || s.equals("/")){
                Fcount++;
            }
        }
        for (String s:ops){
            if (s.equals("+") || s.equals("-")){
                Scount++;
            }
        }
        Log.i("Log", "Fcount=" + Fcount+" Scount=" + Scount);
        /**
         * 如果是单项式
         */
        if (Fcount == 1 && Scount == 0){
            t = calByNum(nums.getFirst(), nums.getLast(), ops.getFirst());
        } else if (Scount == 1 && Fcount == 0){
            t = calByNum(nums.getFirst(), nums.getLast(), ops.getFirst());
        }else {
            /**
             * 如果是多项式
             */
            int i = 0;
            while (true){
                /**
                 * 先把乘除计算完
                 */
                if (ops.get(i).equals("x") || ops.get(i).equals("/")){
                    double c1 = nums.get(i);
                    double c2 = nums.get(i+1);
                    String op = ops.get(i);
                    nums.remove(i);
                    nums.remove(i);
                    ops.remove(i);

                    t=calByNum(c1, c2, op);

                    nums.add(i, t);

                    Log.i("Log",">>>>>>>i=" + i + "  nums:" + nums + "  ops.size()=" + ops.size() +
                    "  ops:" + ops);
                    i=0;
                }else{
                    i++;
                }
                /**
                 * 乘除计算完以后计算加减
                 */
                if (Scount == ops.size()){
                    int j = 0;
                    while (true){
                        double c1 = nums.get(j);
                        double c2 = nums.get(j+1);
                        String op = ops.get(j);
                        nums.remove(j);
                        nums.remove(j);
                        ops.remove(j);

                        t=calByNum(c1, c2, op);

                        nums.add(i, t);

                        if (ops.size() == 0)
                            break;
                    }
                }
                if (ops.size() == 0)
                    break;
            }
        }

        return t;
    }
}
