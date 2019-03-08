package com.sitp.activities;

import android.content.Intent;
import android.support.annotation.IdRes;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.ImageButton;
import android.widget.Toast;

import com.roughike.bottombar.BottomBar;
import com.roughike.bottombar.BottomBarTab;
import com.roughike.bottombar.OnTabReselectListener;
import com.roughike.bottombar.OnTabSelectListener;
import com.sitp.adapters.MyExtendableListViewAdapter;
import com.sitp.cipherpart.R;
//import com.roughike.bottombar.TabSelectionInterceptor;


//注意标注为“ＬＯＯＫ　ＨＥＡＲ”的两个地方
public class InquiryActivity extends AppCompatActivity {

    private ImageButton mIbtnReturn;
    private ExpandableListView expandableListView;
    private MyExtendableListViewAdapter myExtendableListViewAdapter;
    private BottomBar bottomBar;
    private BottomBarTab nearby;
    //用于在子选项显示时平移对齐(或许可以精简掉)
    int item_translation=10;
    //条目名称
    private String item="";
    //密码
    private String password="";
    //限定条目长度，与item_translation搭配用于从item+password的string中提取出password
    int item_length=10;
    //组名称
    private String group="";
    private String string="";
    //数据库名称
    private static final String DATABASE_NAME="jscsd.db";

    //数据库版本号
    private static final int DATABASE_VERSION=1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inquiry);

        mIbtnReturn=(ImageButton)findViewById(R.id.ibtn_return);
        expandableListView = (ExpandableListView)findViewById(R.id.expanded_menu);
        bottomBar = (BottomBar) findViewById(R.id.bottomBar);

        string=item;
        while(string.length()<=10)
            string+=" ";
        string+=password;


        String[] groupString = {"射手","辅助","坦克","法师"};
        String[][] childString = {
                {"孙尚香", "后羿", "马可波罗", "狄仁杰"},
                {"孙膑", "蔡文姬", "鬼谷子", "杨玉环"},
                {"张飞", "廉颇", "牛魔", "项羽"},
                {"诸葛亮", "王昭君", "安琪拉", "干将"}
        };
        /*
        groupString=Arrays.copyOf(groupString,groupString.length+1);
        groupString[groupString.length-1]="a";
        String[][] citys;
        System.out.println("groupString.length"+groupString.length);
        childString=Arrays.copyOf(childString,childString.length+1);
        */
        /*
        System.out.println("childString.length"+childString.length);
        childString[childString.length-1]=Arrays.copyOf(childString[childString.length-1],childString[childString.length-1].length+1);
        childString[childString.length-1][childString[childString.length-1].length-1]="a";
        */
/*
        citys = new String[jsonArray.length()][];
        for (int i = 0; i < jsonArray.length(); i++) {

            JSONObject jsonObject = jsonArray.getJSONObject(i);

            //市
            JSONArray cityArrJson = jsonObject.getJSONArray("city");

            String[] citysCol = new String[cityArrJson.length()];

            for(int n = 0; n<cityArrJson.length();n++){
                JSONObject cityObject = cityArrJson.getJSONObject(n);
                citysCol[n] = cityObject.getString("name");

            }

            citys[i] = citysCol;

            }
            */
/*
        childString=Arrays.copyOf(childString,childString.length+1);
        childString[groupString.length-1][0]="aa";
        */


        //ＬＯＯＫ　ＨＥＡＲ！使用下面语句帮助搭建页面，groupString为组名字符串组，childString为二维条目字符串组，item_translation为显示时的辅助变量，已经设置好
        myExtendableListViewAdapter=new MyExtendableListViewAdapter(groupString,childString,item_translation);


        mIbtnReturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(InquiryActivity.this,MainActivity.class);
                startActivity(intent);
            }
        });

        bottomBar.setOnTabSelectListener(new OnTabSelectListener() {
            @Override
            public void onTabSelected(@IdRes int tabId) {
                if (tabId == R.id.tab_search) {
                    // 选择指定 id 的标签
                    nearby = bottomBar.getTabWithId(R.id.tab_search);
                    //nearby.setBadgeCount(4);
                }
                //ＬＯＯＫ　ＨＥＡＲ！以下switch语句用于实现相应功能，但目前没有写完
                switch(tabId){
                    case R.id.tab_search:

                        Toast.makeText(getApplicationContext(), "tab_search", Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.tab_add:

                        Toast.makeText(getApplicationContext(), "tab_add", Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.tab_delete:

                        Toast.makeText(getApplicationContext(), "tab_delete", Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.tab_setting:

                        Toast.makeText(getApplicationContext(), "tab_setting", Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        });
        bottomBar.setOnTabReselectListener(new OnTabReselectListener() {
            @Override
            public void onTabReSelected(@IdRes int tabId) {
                if (tabId == R.id.tab_search) {
                    // 已经选择了这个标签，又点击一次。即重选。
                    nearby.removeBadge();
                }
            }
        });
        /*bottomBar.setTabSelectionInterceptor(new TabSelectionInterceptor() {
            @Override
            public boolean shouldInterceptTabSelection(@IdRes int oldTabId, @IdRes int newTabId) {
                // 点击无效
                if (newTabId == R.id.tab_setting ) {
                    // ......
                    // 返回 true 。代表：这里我处理了，你不用管了。
                    return true;
                }

                return false;
            }
        });
        */

        expandableListView.setAdapter(myExtendableListViewAdapter);
        //设置分组的监听
        expandableListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
                return false;
            }
        });
        //设置子项布局监听
        expandableListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                Toast.makeText(getApplicationContext(), getString(R.string.copy_success), Toast.LENGTH_SHORT).show();
                return true;
            }
        });
        expandableListView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
            @Override
            public void onGroupExpand(int groupPosition) {
                int count = myExtendableListViewAdapter.getGroupCount();
                for(int i = 0;i < count;i++){
                    if (i!=groupPosition){
                        expandableListView.collapseGroup(i);
                    }
                }
            }
        });
    }
}


