package com.example.wish.bean;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by huanhuan on 2016/5/15.
 */
public class FestivalLab {
    public static FestivalLab mInstance;

    private List<Festival> mFestival = new ArrayList<Festival>();
    private List<Msg> mMsgs = new ArrayList<Msg>();

    private FestivalLab(){
        mFestival.add(new Festival(1,"国庆节"));
        mFestival.add(new Festival(2,"中秋节"));
        mFestival.add(new Festival(3,"元旦"));
        mFestival.add(new Festival(4,"春节"));
        mFestival.add(new Festival(5,"端午节"));
        mFestival.add(new Festival(6,"七夕节"));
        mFestival.add(new Festival(7,"圣诞节"));
        mFestival.add(new Festival(8,"儿童节"));

        mMsgs.add(new Msg(1,1,"你们俩可真会算计，国庆大假连婚假，这个月就让你俩休过去了，你们不在，可怜我要替你俩做多少事情啊，你们准备怎么补偿我？不如随礼的钱就算了。"));
        mMsgs.add(new Msg(2,1,"为缓解工作压力而出游的摩羯，选择云南中甸，新疆塞理木湖，阳光普照，僻静的夜市，花花草草，小桥流水，内心得以宁静，感受到大自然的力量。"));
        mMsgs.add(new Msg(3,1,"请注意，顺风快递有你一个幸福邮包，请于国庆节当天查收。第一层是喜庆，祝你喜气洋洋；第二层是幸运，祝你心想事成；第三层是吉祥，祝你鸿福齐天！"));
        mMsgs.add(new Msg(4,1,"山清水秀神州踏行，物华天宝人杰地灵，峥嵘岁月国家繁盛，江山不老祖国长春，愿伟大的祖国更加昌盛，祝亲爱的朋友国庆快乐，心想事成！"));
        mMsgs.add(new Msg(5,1,"过节，最要紧就是开心。祝福的事呢是不能强求的。别人没祝福你是他们不懂得珍惜，发生这种事，大家都不想的。乐不乐？我提前发个给你：国庆快乐！"));
        mMsgs.add(new Msg(6,1,"思念的心想了很久，牵挂的情念了很久，问候的笔写了很久，祝福的信传了很久，今天是十一黄金周，特地把祝福来送上，外出旅游，一路保重，国庆快乐！"));
        mMsgs.add(new Msg(7,1,"转眼又是这个熟悉的国庆节，我的祝福分分秒秒，我的关心时时刻刻，就在你的身边！愿我的祝福随风飘送，顺水漂流，直达你的心扉，温馨我们的友谊！"));
        mMsgs.add(new Msg(8,1,"家，不需要奢华，温馨就好；生活，不需要精彩，平安就好；朋友，不需要热情，真诚就好；祝福，不需要华丽，收到就好。国庆到，愿你一切都好。"));

    }
    public List<Msg> getMsgsByFestivalId(int fesId){
        List<Msg> msgs = new ArrayList<Msg>();
        for (Msg msg :mMsgs){
            if(msg.getFestivalId()==fesId){
                msgs.add(msg);
            }
        }
        return msgs;
    }
    public Msg getMsgById(int id) {
        for (Msg msg : mMsgs) {
            if (msg.getId() == id) {
                return msg;
            }
        }
        return null;
    }

    public List<Festival> getmFestivals(){
        return new ArrayList<Festival>(mFestival);
    }
    public Festival getFestivalById(int fesId){
        for(Festival festival :mFestival){
            if(festival.getId()==fesId){
                return festival;
            }
        }
        return null;
    }

    public static FestivalLab getInstance(){
        if(mInstance==null){
            synchronized (FestivalLab.class){
                if(mInstance==null){
                    mInstance=new FestivalLab();
                }
            }
        }
        return mInstance;
    }
}
