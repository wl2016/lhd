package com.entity;

public class UserInfo
{
  private String userName;
  private String passwd;

  public String getPasswd()
  {
    return this.passwd;
  }

  public void setPasswd(String passwd) {
    this.passwd = passwd;
  }

  public String getUserName() {
    return this.userName;
  }

  public void setUserName(String userName) {
    this.userName = userName;
  }
}