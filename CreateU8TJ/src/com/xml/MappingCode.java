package com.xml;

import java.util.List;
import java.util.Map;

public class MappingCode
{
  public static String inputMappingCode(String src)
  {
    List lst = Constant.BASE_MESSAGE_MAPPING;
    if (lst == null)
      return src;
    if (src == null)
      return "";
    src = src.trim();
    for (int i = 0; i < lst.size(); i++)
    {
      Map codeMap = (Map)lst.get(i);
      if (codeMap.get("title").toString().equals("出库类型"))
        continue;
      if (codeMap.get("e3_code").toString().equals(src))
      {
        return codeMap.get("u8_code").toString();
      }
    }

    return src;
  }

  public static String outputMappingCode(String src)
  {
    List lst = Constant.BASE_MESSAGE_MAPPING;
    if (lst == null)
      return src;
    if (src == null)
      return "";
    src = src.trim();
    for (int i = 0; i < lst.size(); i++)
    {
      Map codeMap = (Map)lst.get(i);
      if (codeMap.get("title").toString().equals("入库类型"))
        continue;
      if (codeMap.get("e3_code").toString().equals(src))
        return codeMap.get("u8_code").toString();
    }
    return src;
  }
}