package net.teamcadi.angelbrowser.Activity_Back.database;

public class JoinDBControl
{
    // 아이디 중복확인을 수행하도록 하는 메소드.
    static public void checkID(String id)
    {
        ControlMySQL checkMyID = new ControlMySQL(id, 2);
        ControlMySQL.active = true;
        checkMyID.start();
    }

    // 회원가입 동작을 수행하는 메소드.
    static public void userRegister(String id)
    {
        ControlMySQL registerUserInfo = new ControlMySQL(id);
        ControlMySQL.active = true;
        registerUserInfo.start();
    }
}